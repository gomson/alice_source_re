package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ExceptionWrapper;
import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.image.ImageIO;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.io.KeepFileDoesNotExistException;
import edu.cmu.cs.stage3.io.KeepFileNotSupportedException;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.w3c.dom.Document;






public class ImageProperty
  extends ObjectProperty
{
  public ImageProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, Image defaultValue)
  {
    super(owner, name, defaultValue, Image.class);
  }
  
  public Image getImageValue() { return (Image)getValue(); }
  
  private String getCodecName(String path) {
    String extension = path.substring(path.lastIndexOf('.') + 1);
    return ImageIO.mapExtensionToCodecName(extension);
  }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException {
    m_associatedFileKey = null;
    String filename = getFilename(getNodeText(node));
    InputStream is = loader.readFile(filename);
    set(ImageIO.load(getCodecName(filename), is));
    loader.closeCurrentFile();
    try {
      m_associatedFileKey = loader.getKeepKey(filename);
    } catch (KeepFileNotSupportedException kfnse) {
      m_associatedFileKey = null;
    }
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    Image imageValue = getImageValue();
    String codecName = "png";
    String extension = "png";
    String filename = getName() + "." + extension;
    Object associatedFileKey;
    try {
      associatedFileKey = storer.getKeepKey(filename);
    } catch (KeepFileNotSupportedException kfnse) { Object associatedFileKey;
      associatedFileKey = null;
    }
    if ((m_associatedFileKey == null) || (!m_associatedFileKey.equals(associatedFileKey))) {
      m_associatedFileKey = null;
      OutputStream os = storer.createFile(filename, false);
      BufferedOutputStream bos = new BufferedOutputStream(os);
      DataOutputStream dos = new DataOutputStream(bos);
      try {
        ImageIO.store(codecName, dos, imageValue);
      }
      catch (InterruptedException localInterruptedException) {}
      
      dos.flush();
      storer.closeCurrentFile();
      m_associatedFileKey = associatedFileKey;
    }
    else if (storer.isKeepFileSupported()) {
      try {
        storer.keepFile(filename);
      } catch (KeepFileNotSupportedException kfnse) {
        throw new Error(storer + " " + Messages.getString("returns_true_for_isKeepFileSupported____but_then_throws_") + kfnse);
      } catch (KeepFileDoesNotExistException kfdne) {
        throw new ExceptionWrapper(kfdne, filename);
      }
    }
    
    node.appendChild(createNodeForString(document, "java.io.File[" + filename + "]"));
  }
  
  public void keepAnyAssociatedFiles(DirectoryTreeStorer storer) throws KeepFileNotSupportedException, KeepFileDoesNotExistException {
    super.keepAnyAssociatedFiles(storer);
    Image imageValue = getImageValue();
    if ((imageValue instanceof RenderedImage)) {
      String extension = "png";
      String filename = getName() + "." + extension;
      storer.keepFile(filename);
    }
  }
}
