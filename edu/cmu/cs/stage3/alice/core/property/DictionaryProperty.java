package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.lang.Messages;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;









public class DictionaryProperty
  extends ObjectProperty
{
  public DictionaryProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, Dictionary defaultValue)
  {
    super(owner, name, defaultValue, Dictionary.class);
  }
  
  public Dictionary getDictionaryValue() { return (Dictionary)getValue(); }
  
  private static Object valueOf(Class cls, String text) {
    Class[] parameterTypes = { String.class };
    try {
      Method valueOfMethod = cls.getMethod("valueOf", parameterTypes);
      int modifiers = valueOfMethod.getModifiers();
      if ((Modifier.isPublic(modifiers)) && (Modifier.isStatic(modifiers))) {
        Object[] parameters = { text };
        return valueOfMethod.invoke(null, parameters);
      }
      throw new RuntimeException(Messages.getString("valueOf_method_not_public_static_"));
    }
    catch (NoSuchMethodException nsme) {
      nsme.printStackTrace();
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    } catch (InvocationTargetException ite) {
      ite.printStackTrace();
    }
    return null;
  }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException {
    Dictionary dict = new Hashtable();
    NodeList entryNodeList = node.getElementsByTagName("entry");
    for (int i = 0; i < entryNodeList.getLength(); i++) {
      org.w3c.dom.Element entryNode = (org.w3c.dom.Element)entryNodeList.item(i);
      org.w3c.dom.Element keyNode = (org.w3c.dom.Element)entryNode.getElementsByTagName("key").item(0);
      String keyTypeName = keyNode.getAttribute("class");
      try
      {
        Class keyType = Class.forName(keyTypeName);
        Object key; if (keyType == String.class) {
          key = getNodeText(keyNode);
        } else
          key = getValueOf(keyType, getNodeText(keyNode));
      } catch (ClassNotFoundException cnfe) {
        Object key;
        throw new RuntimeException(keyTypeName);
      }
      Object key;
      org.w3c.dom.Element valueNode = (org.w3c.dom.Element)entryNode.getElementsByTagName("value").item(0);
      String valueTypeName = valueNode.getAttribute("class");
      try
      {
        Class valueType = Class.forName(valueTypeName);
        Object value; if (valueType == String.class) {
          value = getNodeText(valueNode);
        } else
          value = getValueOf(valueType, getNodeText(valueNode));
      } catch (ClassNotFoundException cnfe) {
        Object value;
        throw new RuntimeException(valueTypeName); }
      Object value;
      dict.put(key, value);
    }
    set(dict);
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    Dictionary dict = getDictionaryValue();
    if (dict != null) {
      Enumeration enum0 = dict.keys();
      while (enum0.hasMoreElements()) {
        Object key = enum0.nextElement();
        Object value = dict.get(key);
        
        org.w3c.dom.Element entryNode = document.createElement("entry");
        
        org.w3c.dom.Element keyNode = document.createElement("key");
        keyNode.setAttribute("class", key.getClass().getName());
        keyNode.appendChild(createNodeForString(document, key.toString()));
        
        org.w3c.dom.Element valueNode = document.createElement("value");
        valueNode.setAttribute("class", value.getClass().getName());
        valueNode.appendChild(createNodeForString(document, value.toString()));
        
        entryNode.appendChild(keyNode);
        entryNode.appendChild(valueNode);
        node.appendChild(entryNode);
      }
    }
  }
  
  public Enumeration elements() { Dictionary dict = getDictionaryValue();
    if (dict != null) {
      return dict.elements();
    }
    return null;
  }
  
  public Object get(Object key) {
    Dictionary dict = getDictionaryValue();
    if (dict != null) {
      return dict.get(key);
    }
    return null;
  }
  
  public boolean isEmpty() {
    Dictionary dict = getDictionaryValue();
    if (dict != null) {
      return dict.isEmpty();
    }
    return true;
  }
  
  public Enumeration keys() {
    Dictionary dict = getDictionaryValue();
    if (dict != null) {
      return dict.keys();
    }
    return null;
  }
  
  public Object put(Object key, Object value) {
    Dictionary dict = getDictionaryValue();
    Dictionary newDict = new Hashtable();
    if (dict != null)
    {
      Enumeration enum0 = dict.keys();
      while (enum0.hasMoreElements()) {
        Object k = enum0.nextElement();
        Object v = dict.get(k);
        newDict.put(k, v);
      }
    }
    Object o = newDict.put(key, value);
    set(newDict);
    return o;
  }
  
  public Object remove(Object key) { Dictionary dict = getDictionaryValue();
    Dictionary newDict = new Hashtable();
    Object value = null;
    if (dict != null)
    {
      Enumeration enum0 = dict.keys();
      while (enum0.hasMoreElements()) {
        Object k = enum0.nextElement();
        Object v = dict.get(k);
        if (k.equals(key)) {
          value = v;
        } else {
          newDict.put(k, v);
        }
      }
    }
    if (value != null) {
      set(newDict);
    }
    return value;
  }
  
  public int size() { Dictionary dict = getDictionaryValue();
    if (dict != null) {
      return dict.size();
    }
    return 0;
  }
}
