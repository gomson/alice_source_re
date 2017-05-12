package edu.cmu.cs.stage3.caitlin.personbuilder;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;





























public class XMLDirectoryUtilities
{
  public XMLDirectoryUtilities() {}
  
  public static Node loadURL(URL url)
  {
    Document document = null;
    if (url != null) {
      try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(url.openStream());
      } catch (IOException ioe) {
        document = null;
        ioe.printStackTrace();
      } catch (ParserConfigurationException pce) {
        document = null;
        pce.printStackTrace();
      } catch (SAXException se) {
        document = null;
        se.printStackTrace();
      }
      
      return document;
    }
    return null;
  }
  
  public static String getPath(Node node) {
    NamedNodeMap nodeMap = node.getAttributes();
    Node pathNode = nodeMap.getNamedItem("path");
    if (pathNode != null) {
      return pathNode.getNodeValue();
    }
    return null;
  }
  
  public static Node loadFile(String fileName) {
    URL url = XMLDirectoryUtilities.class.getResource(fileName);
    return loadURL(url);
  }
  
  public static Vector getDirectories(Node node) {
    NodeList nList = node.getChildNodes();
    Vector directoryNodes = new Vector();
    for (int i = 0; i < nList.getLength(); i++) {
      Node kidNode = nList.item(i);
      if (kidNode.getNodeName().equals("directory")) {
        directoryNodes.addElement(kidNode);
      }
    }
    return directoryNodes;
  }
  
  public static Vector getSetColorNodes(Node node) {
    NodeList nList = node.getChildNodes();
    Vector propertySetNodes = new Vector();
    for (int i = 0; i < nList.getLength(); i++) {
      Node kidNode = nList.item(i);
      if (kidNode.getNodeName().equals("setColor")) {
        propertySetNodes.addElement(kidNode);
      }
    }
    return propertySetNodes;
  }
  
  protected static Vector getFilesOfType(String nodeType, Node node) {
    NodeList nList = node.getChildNodes();
    Vector files = new Vector();
    for (int i = 0; i < nList.getLength(); i++) {
      Node kidNode = nList.item(i);
      if (kidNode.getNodeName().equals(nodeType)) {
        NamedNodeMap nodeMap = kidNode.getAttributes();
        Node pathNode = nodeMap.getNamedItem("path");
        String path = null;
        if (pathNode != null) {
          path = pathNode.getNodeValue();
          URL url = PersonBuilder.class.getResource(path);
          if (url != null)
            files.addElement(url);
        }
      }
    }
    return files;
  }
  
  public static Vector getImageURLs(Node node) {
    return getFilesOfType("image", node);
  }
  
  public static Vector getImages(Node node) {
    Vector urls = getImageURLs(node);
    Vector images = new Vector();
    Toolkit tk = Toolkit.getDefaultToolkit();
    for (int i = 0; i < urls.size(); i++) {
      URL url = (URL)urls.elementAt(i);
      try {
        Image img = tk.createImage(url);
        if (img != null)
          images.addElement(img);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return images;
  }
  
  public static Vector getModelURLs(Node node) {
    return getFilesOfType("model", node);
  }
  
  public static Vector getXMLURLs(Node node) {
    return getFilesOfType("xml", node);
  }
  
  public static Vector getPropertySets(Node node) {
    return getFilesOfType("propertySet", node);
  }
}
