package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class Configuration
{
  public static final int VIS_OPEN = 1;
  public static final int VIS_ADVANCED = 2;
  public static final int VIS_HIDDEN = 4;
  public static final int VIS_ALL = 7;
  private String keyPrefix;
  
  public static String getValue(Package p, String relativeKey)
  {
    return _getValue(p.getName() + "." + relativeKey);
  }
  
  public static String[] getValueList(Package p, String relativeKey) {
    return _getValueList(p.getName() + "." + relativeKey);
  }
  
  public static void setValue(Package p, String relativeKey, String value) {
    _setValue(p.getName() + "." + relativeKey, value);
  }
  
  public static void setValueList(Package p, String relativeKey, String[] values) {
    _setValueList(p.getName() + "." + relativeKey, values);
  }
  
  public static void addToValueList(Package p, String relativeKey, String item) {
    _addToValueList(p.getName() + "." + relativeKey, item);
  }
  
  public static void removeFromValueList(Package p, String relativeKey, String item) {
    _removeFromValueList(p.getName() + "." + relativeKey, item);
  }
  
  public static boolean isList(Package p, String relativeKey) {
    return _isList(p.getName() + "." + relativeKey);
  }
  
  public static boolean keyExists(Package p, String relativeKey) {
    return _keyExists(p.getName() + "." + relativeKey);
  }
  
  public static void deleteKey(Package p, String relativeKey) {
    _deleteKey(p.getName() + "." + relativeKey);
  }
  
  public static String[] getSubKeys(Package p, String relativeKey, int visibility) {
    return _getSubKeys(p.getName() + "." + relativeKey, visibility);
  }
  
  public static void setVisibility(Package p, String relativeKey, int visibility) {
    _setVisibility(p.getName() + "." + relativeKey, visibility);
  }
  



  private Configuration(Package p)
  {
    keyPrefix = (p.getName() + ".");
  }
  
  public static Configuration getLocalConfiguration(Package p) {
    return new Configuration(p);
  }
  
  public String getValue(String relativeKey) {
    return _getValue(keyPrefix + relativeKey);
  }
  
  public String[] getValueList(String relativeKey) {
    return _getValueList(keyPrefix + relativeKey);
  }
  
  public void setValue(String relativeKey, String value) {
    _setValue(keyPrefix + relativeKey, value);
  }
  
  public void setValueList(String relativeKey, String[] values) {
    _setValueList(keyPrefix + relativeKey, values);
  }
  
  public void addToValueList(String relativeKey, String item) {
    _addToValueList(keyPrefix + relativeKey, item);
  }
  
  public void removeFromValueList(String relativeKey, String item) {
    _removeFromValueList(keyPrefix + relativeKey, item);
  }
  
  public boolean isList(String relativeKey) {
    return _isList(keyPrefix + relativeKey);
  }
  
  public boolean keyExists(String relativeKey) {
    return _keyExists(keyPrefix + relativeKey);
  }
  
  public void deleteKey(String relativeKey) {
    _deleteKey(keyPrefix + relativeKey);
  }
  
  public String[] getSubKeys(String relativeKey, int visibility) {
    return _getSubKeys(keyPrefix + relativeKey, visibility);
  }
  
  public void setVisibility(String relativeKey, int visibility) {
    _setVisibility(keyPrefix + relativeKey, visibility);
  }
  

  private static final File configLocation = new File(JAlice.getAliceUserDirectory(), "AlicePreferences.xml").getAbsoluteFile();
  


  private static Key root = new Key(null);
  static { rootname = "<root>";
    rootsubKeys = new HashMap();
    File aliceHasNotExitedFile = new File(JAlice.getAliceUserDirectory(), "aliceHasNotExited.txt");
    






    try
    {
      loadConfig(configLocation);
    } catch (Exception e) {
      try {
        storeConfig();
      } catch (IOException e1) {
        AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_new_preferences_file_"), e1);
      }
    }
  }
  
  private static class Key {
    public String name;
    public int visibility;
    public String value;
    public ArrayList valueList;
    public HashMap subKeys;
    
    private Key() {}
    
    public Key getSubKey(String name) {
      if (subKeys != null) {
        int i = name.indexOf('.');
        if (i == -1) {
          return (Key)subKeys.get(name);
        }
        Key subKey = (Key)subKeys.get(name.substring(0, i));
        if (subKey != null) {
          return subKey.getSubKey(name.substring(i + 1));
        }
      }
      
      return null;
    }
    
    public Key createSubKey(String name) {
      if (subKeys == null) {
        subKeys = new HashMap();
      }
      
      int i = name.indexOf('.');
      if (i == -1) {
        Key subKey = (Key)subKeys.get(name);
        if (subKey == null) {
          subKey = new Key();
          name = name;
          subKeys.put(name, subKey);
        }
        return subKey;
      }
      Key subKey = (Key)subKeys.get(name.substring(0, i));
      if (subKey == null) {
        subKey = new Key();
        name = name.substring(0, i);
        subKeys.put(name, subKey);
      }
      return subKey.createSubKey(name.substring(i + 1));
    }
    
    public void deleteSubKey(String name)
    {
      if (subKeys != null) {
        int i = name.indexOf('.');
        if (i == -1) {
          subKeys.remove(name);
        } else {
          Key subKey = (Key)subKeys.get(name.substring(0, i));
          if (subKey != null) {
            subKey.deleteSubKey(name.substring(i + 1));
          }
        }
      }
    }
    
    public String toString() {
      StringBuffer s = new StringBuffer();
      s.append(Messages.getString("_nname__") + name + "\n");
      s.append(Messages.getString("visibility__") + visibility + "\n");
      s.append(Messages.getString("value__") + value + "\n");
      s.append(Messages.getString("valueList__") + valueList + "\n");
      s.append(Messages.getString("subKeys__") + subKeys + "\n");
      return s.toString();
    }
  }
  



  private static String _getValue(String keyName)
  {
    Key key = root.getSubKey(keyName);
    if (key != null) {
      return value;
    }
    return null;
  }
  
  private static String[] _getValueList(String keyName) {
    Key key = root.getSubKey(keyName);
    if ((key != null) && 
      (valueList != null)) {
      return (String[])valueList.toArray(new String[0]);
    }
    
    return null;
  }
  
  private static void _setValue(String keyName, String value) {
    Key key = root.getSubKey(keyName);
    
    if (key == null) {
      key = root.createSubKey(keyName);
    }
    
    String oldValue = value;
    String[] oldValueList = _getValueList(keyName);
    fireChanging(keyName, _isList(keyName), oldValue, value, oldValueList, null);
    
    if (valueList != null) {
      valueList = null;
    }
    value = value;
    
    fireChanged(keyName, _isList(keyName), oldValue, value, oldValueList, null);
  }
  
  private static void _setValueList(String keyName, String[] values) {
    Key key = root.getSubKey(keyName);
    
    if (key == null) {
      key = root.createSubKey(keyName);
    }
    
    String oldValue = value;
    String[] oldValueList = _getValueList(keyName);
    fireChanging(keyName, _isList(keyName), oldValue, null, oldValueList, values);
    
    if (value != null) {
      value = null;
    }
    
    if (valueList == null) {
      valueList = new ArrayList(values == null ? 0 : values.length);
    } else {
      valueList.clear();
    }
    if (values != null) {
      for (int i = 0; i < values.length; i++) {
        valueList.add(values[i]);
      }
    }
    
    fireChanged(keyName, _isList(keyName), oldValue, null, oldValueList, values);
  }
  
  private static void _addToValueList(String keyName, String item) {
    Key key = root.getSubKey(keyName);
    
    if (key == null) {
      key = root.createSubKey(keyName);
    }
    
    String oldValue = value;
    String[] oldValueList = _getValueList(keyName);
    fireChanging(keyName, _isList(keyName), oldValue, null, oldValueList, null);
    
    if (value != null) {
      value = null;
    }
    
    if (valueList == null) {
      valueList = new ArrayList();
    }
    if (item != null) {
      valueList.add(item);
    }
    
    fireChanged(keyName, _isList(keyName), oldValue, null, oldValueList, _getValueList(keyName));
  }
  
  private static void _removeFromValueList(String keyName, String item) {
    Key key = root.getSubKey(keyName);
    
    if (key == null) {
      key = root.createSubKey(keyName);
    }
    
    String oldValue = value;
    String[] oldValueList = _getValueList(keyName);
    fireChanging(keyName, _isList(keyName), oldValue, null, oldValueList, null);
    
    if (value != null) {
      value = null;
    }
    
    if (valueList == null) {
      valueList = new ArrayList();
    }
    else if (item != null) {
      valueList.remove(item);
    }
    

    fireChanged(keyName, _isList(keyName), oldValue, null, oldValueList, _getValueList(keyName));
  }
  
  private static boolean _isList(String keyName) {
    Key key = root.getSubKey(keyName);
    if (key != null) {
      return valueList != null;
    }
    return false;
  }
  
  private static boolean _keyExists(String keyName) {
    return root.getSubKey(keyName) != null;
  }
  
  private static void _deleteKey(String keyName) {
    root.deleteSubKey(keyName);
  }
  
  private static String[] _getSubKeys(String keyName, int visibility) {
    Key key = root.getSubKey(keyName);
    if (key != null) {
      ArrayList list = new ArrayList(subKeys.size());
      for (Iterator iter = subKeys.keySet().iterator(); iter.hasNext();) {
        Key subKey = (Key)iter.next();
        if ((visibility & visibility) > 0) {
          list.add(name);
        }
      }
      return (String[])list.toArray(new String[0]);
    }
    return null;
  }
  
  private static void _setVisibility(String keyName, int visibility) {
    Key key = root.getSubKey(keyName);
    if (key != null) {
      visibility = visibility;
    }
  }
  
  private static void loadConfig(File file) throws IOException
  {
    loadConfig(file.toURL());
  }
  
  private static void loadConfig(URL url) throws IOException {
    BufferedInputStream bis = new BufferedInputStream(url.openStream());
    loadConfig(bis);
    bis.close();
  }
  
  private static void loadConfig(InputStream is) throws IOException {
    rootsubKeys = new HashMap();
    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(is);
      
      Element rootElement = document.getDocumentElement();
      rootElement.normalize();
      
      NodeList childNodes = rootElement.getChildNodes();
      for (int i = 0; i < childNodes.getLength(); i++) {
        Node childNode = childNodes.item(i);
        if ((childNode instanceof Element)) {
          Element childElement = (Element)childNode;
          String tagName = childElement.getTagName();
          if (tagName.equals("key")) {
            Key subKey = loadKey(childElement);
            if ((subKey != null) && (name != null)) {
              rootsubKeys.put(name, subKey);


            }
            


          }
          

        }
        

      }
      


    }
    catch (Exception e)
    {


      DialogManager.showMessageDialog(
        Messages.getString("Alice_had_trouble_reading_your_preferences_but_will_continue_to_run_normally"), 
        Messages.getString("Unable_to_load_preferences"), 2);
    }
  }
  
  private static Key loadKey(Element keyElement) {
    Key key = new Key(null);
    
    String visibility = keyElement.getAttribute("visibility").trim();
    if (visibility.equals("open")) {
      visibility = 1;
    } else if (visibility.equals("advanced")) {
      visibility = 2;
    } else if (visibility.equals("hidden")) {
      visibility = 4;
    }
    
    HashMap map = parseSingleNode(keyElement);
    
    Element nameElement = (Element)map.get("name");
    if (nameElement != null) {
      Text textNode = (Text)parseSingleNode(nameElement).get("text");
      if (textNode != null) {
        name = textNode.getData().trim();
      }
    }
    
    Element valueElement = (Element)map.get("value");
    if (valueElement != null) {
      Element listElement = (Element)parseSingleNode(valueElement).get("list");
      if (listElement != null) {
        valueList = new ArrayList();
        ArrayList items = (ArrayList)parseSingleNode(listElement).get("items");
        if (items != null) {
          for (Iterator iter = items.iterator(); iter.hasNext();) {
            Element itemElement = (Element)iter.next();
            if (itemElement != null) {
              Text textNode = (Text)parseSingleNode(itemElement).get("text");
              if (textNode != null) {
                valueList.add(textNode.getData().trim());
              }
            }
          }
        }
      } else {
        Text textNode = (Text)parseSingleNode(valueElement).get("text");
        if (textNode != null) {
          value = textNode.getData().trim();
        }
      }
    }
    
    ArrayList keys = (ArrayList)map.get("keys");
    if (keys != null) {
      for (Iterator iter = keys.iterator(); iter.hasNext();) {
        Element subKeyElement = (Element)iter.next();
        if (subKeyElement != null) {
          if (subKeys == null) {
            subKeys = new HashMap();
          }
          Key subKey = loadKey(subKeyElement);
          if ((subKey != null) && (name != null)) {
            subKeys.put(name, subKey);
          }
        }
      }
    }
    
    return key;
  }
  










  private static HashMap parseSingleNode(Node node)
  {
    HashMap map = new HashMap();
    
    NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node childNode = childNodes.item(i);
      
      if ((childNode instanceof Element)) {
        Element childElement = (Element)childNode;
        String tagName = childElement.getTagName();
        if (tagName.equals("name")) {
          map.put("name", childElement);
        } else if (tagName.equals("value")) {
          map.put("value", childElement);
        } else if (tagName.equals("list")) {
          map.put("list", childElement);
        } else if (tagName.equals("item")) {
          ArrayList list = (ArrayList)map.get("items");
          if (list == null) {
            list = new ArrayList();
            map.put("items", list);
          }
          list.add(childElement);
        } else if (tagName.equals("key")) {
          ArrayList list = (ArrayList)map.get("keys");
          if (list == null) {
            list = new ArrayList();
            map.put("keys", list);
          }
          list.add(childElement);
        }
      } else if ((childNode instanceof Text)) {
        map.put("text", childNode);
      }
    }
    
    return map;
  }
  
  private static Element getChildElementNamed(String name, Node node) {
    NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node childNode = childNodes.item(i);
      if ((childNode instanceof Element)) {
        Element childElement = (Element)childNode;
        String tagName = childElement.getTagName();
        if (tagName.equals(name)) {
          return childElement;
        }
      }
    }
    return null;
  }
  
  public static void storeConfig() throws IOException {
    if ((configLocation.getParentFile().exists()) && (configLocation.getParentFile().canWrite()))
    {

      storeConfig(configLocation);
    }
  }
  


  private static void storeConfig(File file)
    throws IOException
  {
    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
    storeConfig(bos);
    bos.flush();
    bos.close();
  }
  
  private static void storeConfig(OutputStream os) throws IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.newDocument();
      
      Element rootElement = document.createElement("configuration");
      document.appendChild(rootElement);
      
      if (rootsubKeys != null) {
        for (Iterator iter = rootsubKeys.values().iterator(); iter.hasNext();) {
          Key key = (Key)iter.next();
          rootElement.appendChild(makeKeyElement(document, key));
        }
      }
      
      document.getDocumentElement().normalize();
      
      edu.cmu.cs.stage3.xml.Encoder.write(document, os);




    }
    catch (ParserConfigurationException pce)
    {



      AuthoringTool.showErrorDialog(Messages.getString("Error_parsing_preferences_file_"), pce);
    }
  }
  
  private static Element makeKeyElement(Document document, Key key) {
    Element keyElement = document.createElement("key");
    if ((visibility & 0x1) > 0) {
      keyElement.setAttribute("visibility", "open");
    } else if ((visibility & 0x2) > 0) {
      keyElement.setAttribute("visibility", "advanced");
    } else if ((visibility & 0x4) > 0) {
      keyElement.setAttribute("visibility", "hidden");
    } else {
      keyElement.setAttribute("visibility", "open");
    }
    
    Element nameElement = document.createElement("name");
    nameElement.appendChild(document.createTextNode(name));
    keyElement.appendChild(nameElement);
    
    if (value != null) {
      Element valueElement = document.createElement("value");
      valueElement.appendChild(document.createTextNode(value));
      keyElement.appendChild(valueElement);
    } else if (valueList != null) {
      Element valueElement = document.createElement("value");
      Element listElement = document.createElement("list");
      for (Iterator iter = valueList.iterator(); iter.hasNext();) {
        Element itemElement = document.createElement("item");
        itemElement.appendChild(document.createTextNode((String)iter.next()));
        listElement.appendChild(itemElement);
      }
      valueElement.appendChild(listElement);
      keyElement.appendChild(valueElement);
    }
    
    if (subKeys != null) {
      for (Iterator iter = subKeys.values().iterator(); iter.hasNext();) {
        Key subKey = (Key)iter.next();
        keyElement.appendChild(makeKeyElement(document, subKey));
      }
    }
    
    return keyElement;
  }
  

  protected static HashSet listeners = new HashSet();
  
  public static void addConfigurationListener(ConfigurationListener listener) {
    listeners.add(listener);
  }
  
  public static void removeConfigurationListener(ConfigurationListener listener) {
    listeners.remove(listener);
  }
  



  protected static void fireChanging(String keyName, boolean isList, String oldValue, String newValue, String[] oldValueList, String[] newValueList)
  {
    ConfigurationEvent ev = new ConfigurationEvent(keyName, isList, oldValue, newValue, oldValueList, newValueList);
    Object[] listenerArray = listeners.toArray();
    for (int i = 0; i < listenerArray.length; i++) {
      ((ConfigurationListener)listenerArray[i]).changing(ev);
    }
  }
  



  protected static void fireChanged(String keyName, boolean isList, String oldValue, String newValue, String[] oldValueList, String[] newValueList)
  {
    ConfigurationEvent ev = new ConfigurationEvent(keyName, isList, oldValue, newValue, oldValueList, newValueList);
    Object[] listenerArray = listeners.toArray();
    for (int i = 0; i < listenerArray.length; i++) {
      ((ConfigurationListener)listenerArray[i]).changed(ev);
    }
  }
}
