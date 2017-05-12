package edu.cmu.cs.stage3.caitlin.personbuilder;

import java.awt.FlowLayout;
import java.io.PrintStream;
import java.net.URL;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

















public class StringPropertySetter
  extends JPanel
  implements DocumentListener
{
  String propertyName = "";
  String labelText = "";
  String defaultValue = "";
  
  ModelWrapper modelWrapper = null;
  
  JTextField valueField = null;
  
  public StringPropertySetter(Node propNode, ModelWrapper modelWrapper) {
    this.modelWrapper = modelWrapper;
    
    Node propDetailsNode = loadPropertyXML(propNode);
    getPropertyInfo(propDetailsNode);
    createGUI();
  }
  
  public void changedUpdate(DocumentEvent de)
  {
    modelWrapper.setProperty(propertyName, valueField.getText(), labelText);
  }
  
  public void insertUpdate(DocumentEvent de)
  {
    modelWrapper.setProperty(propertyName, valueField.getText(), labelText);
  }
  
  public void removeUpdate(DocumentEvent de)
  {
    String value = valueField.getText();
    if (value.equals("")) {
      value = "noname";
    }
    modelWrapper.setProperty(propertyName, value, labelText);
  }
  
  protected Node loadPropertyXML(Node propNode) {
    String path = "";
    URL propSetURL = null;
    Node propSetNode = propNode;
    










    URL xmlURL = null;
    Vector xmlFiles = XMLDirectoryUtilities.getXMLURLs(propSetNode);
    if (xmlFiles.size() == 1) {
      xmlURL = (URL)xmlFiles.elementAt(0);
    } else {
      System.out.println("Zero or Multiple xml files found");
    }
    
    if (xmlURL != null) {
      org.w3c.dom.Document doc = (org.w3c.dom.Document)XMLDirectoryUtilities.loadURL(xmlURL);
      return doc.getDocumentElement();
    }
    
    return null;
  }
  
  protected void getPropertyInfo(Node propNode)
  {
    NodeList nList = propNode.getChildNodes();
    for (int i = 0; i < nList.getLength(); i++) {
      Node curNode = nList.item(i);
      if (curNode.getNodeName().equals("setProperty")) {
        NamedNodeMap attrs = curNode.getAttributes();
        for (int j = 0; j < attrs.getLength(); j++) {
          Node attr = attrs.item(j);
          if (attr.getNodeName().equals("name")) {
            propertyName = attr.getNodeValue();
          } else if (attr.getNodeName().equals("description")) {
            labelText = attr.getNodeValue();
          } else if (attr.getNodeName().equals("defaultValue")) {
            defaultValue = attr.getNodeValue();
          }
        }
      }
    }
  }
  
  protected void createGUI() {
    setLayout(new FlowLayout());
    JLabel label = new JLabel(labelText);
    valueField = new JTextField(defaultValue);
    
    valueField.getDocument().addDocumentListener(this);
    
    add(label);
    add(valueField);
  }
}
