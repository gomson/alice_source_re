package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import java.io.IOException;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;













public class ColorProperty
  extends ObjectProperty
{
  public ColorProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, Color defaultValue)
  {
    super(owner, name, defaultValue, Color.class);
  }
  
  public Color getColorValue() { return (Color)getValue(); }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version)
    throws IOException
  {
    Node redNode = node.getElementsByTagName("red").item(0);
    Node greenNode = node.getElementsByTagName("green").item(0);
    Node blueNode = node.getElementsByTagName("blue").item(0);
    Node alphaNode = node.getElementsByTagName("alpha").item(0);
    float red = Float.parseFloat(getNodeText(redNode));
    float green = Float.parseFloat(getNodeText(greenNode));
    float blue = Float.parseFloat(getNodeText(blueNode));
    float alpha = Float.parseFloat(getNodeText(alphaNode));
    set(new Color(red, green, blue, alpha));
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    Color color = getColorValue();
    
    org.w3c.dom.Element redNode = document.createElement("red");
    redNode.appendChild(createNodeForString(document, Float.toString(color.getRed())));
    node.appendChild(redNode);
    
    org.w3c.dom.Element greenNode = document.createElement("green");
    greenNode.appendChild(createNodeForString(document, Float.toString(color.getGreen())));
    node.appendChild(greenNode);
    
    org.w3c.dom.Element blueNode = document.createElement("blue");
    blueNode.appendChild(createNodeForString(document, Float.toString(color.getBlue())));
    node.appendChild(blueNode);
    
    org.w3c.dom.Element alphaNode = document.createElement("alpha");
    alphaNode.appendChild(createNodeForString(document, Float.toString(color.getAlpha())));
    node.appendChild(alphaNode);
  }
}
