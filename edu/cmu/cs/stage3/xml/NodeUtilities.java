package edu.cmu.cs.stage3.xml;

import org.w3c.dom.CDATASection;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;














public class NodeUtilities
{
  public NodeUtilities() {}
  
  public static String getNodeText(Node node)
  {
    StringBuffer propertyTextBuffer = new StringBuffer();
    NodeList children = node.getChildNodes();
    for (int j = 0; j < children.getLength(); j++) {
      Node childNode = children.item(j);
      if ((childNode instanceof CDATASection)) {
        propertyTextBuffer.append(((CDATASection)childNode).getData());
      } else if ((childNode instanceof Text)) {
        propertyTextBuffer.append(((Text)childNode).getData().trim());
      } else if ((childNode instanceof EntityReference)) {
        NodeList grandchildren = childNode.getChildNodes();
        for (int k = 0; k < grandchildren.getLength(); k++) {
          Node grandchildNode = grandchildren.item(k);
          if ((grandchildNode instanceof Text)) {
            propertyTextBuffer.append(((Text)grandchildNode).getData());
          }
        }
      }
    }
    return propertyTextBuffer.toString();
  }
}
