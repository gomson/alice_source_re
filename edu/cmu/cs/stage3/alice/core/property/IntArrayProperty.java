package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ExceptionWrapper;
import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.scenegraph.io.VFB;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.io.KeepFileDoesNotExistException;
import edu.cmu.cs.stage3.io.KeepFileNotSupportedException;
import edu.cmu.cs.stage3.lang.Messages;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.w3c.dom.Document;









public class IntArrayProperty
  extends ObjectProperty
{
  public IntArrayProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, int[] defaultValue)
  {
    super(owner, name, defaultValue, [I.class);
  }
  
  public int[] getIntArrayValue() { return (int[])getValue(); }
  
  public int size() {
    int[] value = getIntArrayValue();
    if (value != null) {
      return value.length;
    }
    return 0;
  }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException
  {
    m_associatedFileKey = null;
    String filename = getFilename(getNodeText(node));
    String extension = filename.substring(filename.lastIndexOf('.') + 1);
    
    InputStream is = loader.readFile(filename);
    int[] indicesValue; int[] indicesValue; if (extension.equalsIgnoreCase("vfb")) {
      indicesValue = VFB.loadIndices(is);
    } else {
      indicesValue = IndexedTriangleArray.loadIndices(is);
    }
    loader.closeCurrentFile();
    set(indicesValue);
    try {
      m_associatedFileKey = loader.getKeepKey(filename);
    } catch (KeepFileNotSupportedException kfnse) {
      m_associatedFileKey = null;
    }
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    int[] indicesValue = getIntArrayValue();
    String filename = "indices.bin";
    Object associatedFileKey;
    try {
      associatedFileKey = storer.getKeepKey(filename);
    } catch (KeepFileNotSupportedException kfnse) { Object associatedFileKey;
      associatedFileKey = null;
    }
    if ((m_associatedFileKey == null) || (!m_associatedFileKey.equals(associatedFileKey))) {
      m_associatedFileKey = null;
      OutputStream os = storer.createFile(filename, true);
      IndexedTriangleArray.storeIndices(indicesValue, os);
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
    String filename = "indices.bin";
    storer.keepFile(filename);
  }
}
