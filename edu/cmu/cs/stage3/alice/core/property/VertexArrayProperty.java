package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ExceptionWrapper;
import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry;
import edu.cmu.cs.stage3.alice.scenegraph.io.VFB;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.io.KeepFileDoesNotExistException;
import edu.cmu.cs.stage3.io.KeepFileNotSupportedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.w3c.dom.Document;








public class VertexArrayProperty
  extends ObjectArrayProperty
{
  public VertexArrayProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, Vertex3d[] defaultValue)
  {
    super(owner, name, defaultValue, [Ledu.cmu.cs.stage3.alice.scenegraph.Vertex3d.class);
  }
  
  public Vertex3d[] getVertexArrayValue() { return (Vertex3d[])getArrayValue(); }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException
  {
    m_associatedFileKey = null;
    String filename = getFilename(getNodeText(node));
    String extension = filename.substring(filename.lastIndexOf('.') + 1);
    
    InputStream is = loader.readFile(filename);
    Vertex3d[] verticesValue; Vertex3d[] verticesValue; if (extension.equalsIgnoreCase("vfb")) {
      verticesValue = VFB.loadVertices(is);
    } else {
      verticesValue = VertexGeometry.loadVertices(is);
    }
    loader.closeCurrentFile();
    set(verticesValue);
    try {
      m_associatedFileKey = loader.getKeepKey(filename);
    } catch (KeepFileNotSupportedException kfnse) {
      m_associatedFileKey = null;
    }
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    Vertex3d[] verticesValue = getVertexArrayValue();
    String filename = "vertices.bin";
    Object associatedFileKey;
    try {
      associatedFileKey = storer.getKeepKey(filename);
    } catch (KeepFileNotSupportedException kfnse) { Object associatedFileKey;
      associatedFileKey = null;
    }
    if ((m_associatedFileKey == null) || (!m_associatedFileKey.equals(associatedFileKey))) {
      m_associatedFileKey = null;
      OutputStream os = storer.createFile(filename, true);
      VertexGeometry.storeVertices(verticesValue, os);
      storer.closeCurrentFile();
      m_associatedFileKey = associatedFileKey;
    }
    else if (storer.isKeepFileSupported()) {
      try {
        storer.keepFile(filename);
      } catch (KeepFileNotSupportedException kfnse) {
        throw new Error(storer + " returns true for isKeepFileSupported(), but then throws " + kfnse);
      } catch (KeepFileDoesNotExistException kfdne) {
        throw new ExceptionWrapper(kfdne, filename);
      }
    }
    
    node.appendChild(createNodeForString(document, "java.io.File[" + filename + "]"));
  }
  
  public void keepAnyAssociatedFiles(DirectoryTreeStorer storer) throws KeepFileNotSupportedException, KeepFileDoesNotExistException {
    super.keepAnyAssociatedFiles(storer);
    String filename = "vertices.bin";
    storer.keepFile(filename);
  }
}
