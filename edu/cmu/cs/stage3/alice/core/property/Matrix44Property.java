package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.math.Matrix44;
import java.io.IOException;
import java.util.Vector;
import javax.vecmath.Matrix4d;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;












public class Matrix44Property
  extends ObjectProperty
{
  public Matrix44Property(edu.cmu.cs.stage3.alice.core.Element owner, String name, Matrix4d defaultValue)
  {
    super(owner, name, defaultValue, Matrix4d.class);
  }
  
  public Matrix4d getMatrix4dValue() { return (Matrix4d)getValue(); }
  
  public Matrix44 getMatrix44Value() {
    Matrix4d m = getMatrix4dValue();
    if ((m == null) || ((m instanceof Matrix44))) {
      return (Matrix44)m;
    }
    return new Matrix44(m);
  }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException
  {
    Matrix4d m = new Matrix4d();
    NodeList rowNodeList = node.getElementsByTagName("row");
    for (int i = 0; i < 4; i++) {
      org.w3c.dom.Element rowNode = (org.w3c.dom.Element)rowNodeList.item(i);
      NodeList itemNodeList = rowNode.getElementsByTagName("item");
      for (int j = 0; j < 4; j++) {
        Node itemNode = itemNodeList.item(j);
        m.setElement(i, j, Double.parseDouble(getNodeText(itemNode)));
      }
    }
    set(m);
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    Matrix4d m = getMatrix44Value();
    for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
      org.w3c.dom.Element rowNode = document.createElement("row");
      
      for (int colIndex = 0; colIndex < 4; colIndex++) {
        org.w3c.dom.Element itemNode = document.createElement("item");
        
        itemNode.appendChild(createNodeForString(document, Double.toString(m.getElement(rowIndex, colIndex))));
        rowNode.appendChild(itemNode);
      }
      node.appendChild(rowNode);
    }
  }
}
