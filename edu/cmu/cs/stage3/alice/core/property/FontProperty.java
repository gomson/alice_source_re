package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import java.awt.Font;
import java.io.IOException;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;













public class FontProperty
  extends ObjectProperty
{
  public FontProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, Font defaultValue)
  {
    super(owner, name, defaultValue, Font.class);
  }
  
  public Font getFontValue() { return (Font)getValue(); }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException
  {
    Node familyNode = node.getElementsByTagName("family").item(0);
    Node nameNode = node.getElementsByTagName("name").item(0);
    Node styleNode = node.getElementsByTagName("style").item(0);
    Node sizeNode = node.getElementsByTagName("size").item(0);
    String family = getNodeText(familyNode);
    String name = getNodeText(nameNode);
    int style = Integer.parseInt(getNodeText(styleNode));
    int size = Integer.parseInt(getNodeText(sizeNode));
    set(new Font(name, style, size));
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    Font f = getFontValue();
    
    org.w3c.dom.Element familyNode = document.createElement("family");
    familyNode.appendChild(createNodeForString(document, f.getFamily()));
    node.appendChild(familyNode);
    
    org.w3c.dom.Element nameNode = document.createElement("name");
    nameNode.appendChild(createNodeForString(document, f.getName()));
    node.appendChild(nameNode);
    
    org.w3c.dom.Element styleNode = document.createElement("style");
    styleNode.appendChild(createNodeForString(document, Integer.toString(f.getStyle())));
    node.appendChild(styleNode);
    
    org.w3c.dom.Element sizeNode = document.createElement("size");
    sizeNode.appendChild(createNodeForString(document, Integer.toString(f.getSize())));
    node.appendChild(sizeNode);
  }
}
