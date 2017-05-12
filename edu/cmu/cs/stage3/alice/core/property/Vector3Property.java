package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.math.Vector3;
import java.io.IOException;
import java.util.Vector;
import javax.vecmath.Vector3d;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;












public class Vector3Property
  extends ObjectProperty
{
  public Vector3Property(edu.cmu.cs.stage3.alice.core.Element owner, String name, Vector3d defaultValue)
  {
    super(owner, name, defaultValue, Vector3d.class);
  }
  
  public Vector3d getVector3dValue() { return (Vector3d)getValue(); }
  
  public Vector3 getVector3Value() {
    Vector3d v = getVector3dValue();
    if ((v == null) || ((v instanceof Vector3))) {
      return (Vector3)v;
    }
    return new Vector3(v);
  }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException
  {
    Node xNode = node.getElementsByTagName("x").item(0);
    Node yNode = node.getElementsByTagName("y").item(0);
    Node zNode = node.getElementsByTagName("z").item(0);
    float x = Float.parseFloat(getNodeText(xNode));
    float y = Float.parseFloat(getNodeText(yNode));
    float z = Float.parseFloat(getNodeText(zNode));
    set(new Vector3d(x, y, z));
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    Vector3d v = getVector3Value();
    
    org.w3c.dom.Element xNode = document.createElement("x");
    xNode.appendChild(createNodeForString(document, Double.toString(x)));
    node.appendChild(xNode);
    
    org.w3c.dom.Element yNode = document.createElement("y");
    yNode.appendChild(createNodeForString(document, Double.toString(y)));
    node.appendChild(yNode);
    
    org.w3c.dom.Element zNode = document.createElement("z");
    zNode.appendChild(createNodeForString(document, Double.toString(z)));
    node.appendChild(zNode);
  }
}
