package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import java.io.IOException;
import java.util.Vector;
import org.w3c.dom.Document;
















public class ClassProperty
  extends ObjectProperty
{
  public ClassProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, Class defaultValue)
  {
    super(owner, name, defaultValue, Class.class);
  }
  
  public Class getClassValue() { return (Class)getValue(); }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version)
    throws IOException
  {
    String text = getNodeText(node);
    try {
      set(Class.forName(text));
    } catch (ClassNotFoundException cnfe) {
      throw new RuntimeException(text);
    }
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    node.appendChild(createNodeForString(document, getClassValue().getName()));
  }
}
