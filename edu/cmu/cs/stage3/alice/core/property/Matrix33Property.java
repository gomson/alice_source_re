package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.math.Matrix33;
import java.io.IOException;
import java.util.Vector;
import javax.vecmath.Matrix3d;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;












public class Matrix33Property
  extends ObjectProperty
{
  public Matrix33Property(edu.cmu.cs.stage3.alice.core.Element owner, String name, Matrix3d defaultValue)
  {
    super(owner, name, defaultValue, Matrix3d.class);
  }
  
  public Matrix3d getMatrix3dValue() { return (Matrix3d)getValue(); }
  
  public Matrix33 getMatrix33Value() {
    Matrix3d m = getMatrix3dValue();
    if ((m == null) || ((m instanceof Matrix33))) {
      return (Matrix33)m;
    }
    return new Matrix33(m);
  }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException
  {
    Matrix3d m = new Matrix3d();
    NodeList rowNodeList = node.getElementsByTagName("row");
    for (int i = 0; i < 3; i++) {
      org.w3c.dom.Element rowNode = (org.w3c.dom.Element)rowNodeList.item(i);
      NodeList itemNodeList = rowNode.getElementsByTagName("item");
      for (int j = 0; j < 3; j++) {
        Node itemNode = itemNodeList.item(j);
        m.setElement(i, j, Double.parseDouble(getNodeText(itemNode)));
      }
    }
    set(m);
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    Matrix3d m = getMatrix33Value();
    for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
      org.w3c.dom.Element rowNode = document.createElement("row");
      
      for (int colIndex = 0; colIndex < 3; colIndex++) {
        org.w3c.dom.Element itemNode = document.createElement("item");
        
        itemNode.appendChild(createNodeForString(document, Double.toString(m.getElement(rowIndex, colIndex))));
        rowNode.appendChild(itemNode);
      }
      node.appendChild(rowNode);
    }
  }
}
