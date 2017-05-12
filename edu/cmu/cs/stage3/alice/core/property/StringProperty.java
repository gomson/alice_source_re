package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import java.io.IOException;
import java.util.Vector;
import org.w3c.dom.Document;
















public class StringProperty
  extends ObjectProperty
{
  public StringProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, String defaultValue)
  {
    super(owner, name, defaultValue, String.class);
  }
  
  public String getStringValue() { return (String)getValue(); }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException
  {
    set(getNodeText(node));
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    node.appendChild(createNodeForString(document, getStringValue()));
  }
}
