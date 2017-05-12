package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;


















public class StencilStateCapsule
  implements StateCapsule
{
  protected ArrayList existantElements = new ArrayList();
  protected ArrayList nonExistantElements = new ArrayList();
  protected HashMap propertyValues = new HashMap();
  
  protected HashMap elementPositions = new HashMap();
  
  public StencilStateCapsule() {}
  
  public void addExistantElement(String elementKey)
  {
    existantElements.add(elementKey);
  }
  


  public void removeExistantElement(String elementKey)
  {
    existantElements.remove(elementKey);
  }
  
  public String[] getExistantElements() {
    return (String[])existantElements.toArray(new String[0]);
  }
  


  public void addNonExistantElement(String elementKey)
  {
    nonExistantElements.add(elementKey);
  }
  


  public void removeNonExistantElement(String elementKey)
  {
    nonExistantElements.remove(elementKey);
  }
  
  public String[] getNonExistantElements() {
    return (String[])nonExistantElements.toArray(new String[0]);
  }
  






  public void putPropertyValue(String propertyKey, String valueRepr)
  {
    propertyValues.put(propertyKey, valueRepr);
  }
  



  public void removePropertyValue(String propertyKey)
  {
    propertyValues.remove(propertyKey);
  }
  




  public void putElementPosition(String elementKey, int position)
  {
    elementPositions.put(elementKey, new Integer(position));
  }
  


  public void removeElementPosition(String elementKey)
  {
    elementPositions.remove(elementKey);
  }
  
  public int getElementPosition(String elementKey) {
    Integer value = (Integer)elementPositions.get(elementKey);
    if (value != null) {
      return value.intValue();
    }
    return -1;
  }
  




  public String getPropertyValue(String propertyKey)
  {
    return (String)propertyValues.get(propertyKey);
  }
  
  public Set getPropertyValueKeySet() {
    return propertyValues.keySet();
  }
  
  public Set getElementPositionKeySet() {
    return elementPositions.keySet();
  }
  
  public void parse(String storableRepr) {
    StringTokenizer st = new StringTokenizer(storableRepr, "|", false);
    String token = st.nextToken();
    if (!"existantElements".equals(token)) {
      throw new IllegalArgumentException("expected \"existantElements\"; got " + token);
    }
    
    StringTokenizer st2 = new StringTokenizer(st.nextToken(), "?", false);
    while (st2.hasMoreTokens()) {
      addExistantElement(st2.nextToken());
    }
    
    token = st.nextToken();
    if (!"nonExistantElements".equals(token)) {
      throw new IllegalArgumentException("expected \"nonExistantElements\"; got " + token);
    }
    st2 = new StringTokenizer(st.nextToken(), "?", false);
    while (st2.hasMoreTokens()) {
      addNonExistantElement(st2.nextToken());
    }
    
    token = st.nextToken();
    if (!"propertyValues".equals(token)) {
      throw new IllegalArgumentException("expected \"propertyValues\"; got " + token);
    }
    st2 = new StringTokenizer(st.nextToken(), "?", false);
    while (st2.hasMoreTokens()) {
      token = st2.nextToken();
      int colonIndex = token.indexOf(":");
      String propertyKey = token.substring(0, colonIndex);
      String valueRepr = token.substring(colonIndex + 1);
      putPropertyValue(propertyKey, valueRepr);
    }
    
    if (st.hasMoreTokens()) {
      token = st.nextToken();
      if (!"elementPositions".equals(token)) {
        throw new IllegalArgumentException("expected \"elementPositions\"; got " + token);
      }
      st2 = new StringTokenizer(st.nextToken(), "?", false);
      while (st2.hasMoreTokens()) {
        token = st2.nextToken();
        int colonIndex = token.indexOf(":");
        String elementKey = token.substring(0, colonIndex);
        String valueRepr = token.substring(colonIndex + 1);
        putElementPosition(elementKey, Integer.parseInt(valueRepr));
      }
    }
  }
  

  public String getStorableRepr()
  {
    String storableRepr = "";
    
    storableRepr = storableRepr + "existantElements|?";
    
    for (Iterator iter = existantElements.iterator(); iter.hasNext();) {
      storableRepr = storableRepr + (String)iter.next() + "?";
    }
    storableRepr = storableRepr + "|";
    
    storableRepr = storableRepr + "nonExistantElements|?";
    for (Iterator iter = nonExistantElements.iterator(); iter.hasNext();) {
      storableRepr = storableRepr + (String)iter.next() + "?";
    }
    storableRepr = storableRepr + "|";
    
    storableRepr = storableRepr + "propertyValues|?";
    for (Iterator iter = propertyValues.keySet().iterator(); iter.hasNext();) {
      String propertyKey = (String)iter.next();
      String valueRepr = (String)propertyValues.get(propertyKey);
      storableRepr = storableRepr + propertyKey + ":" + valueRepr + "?";
    }
    storableRepr = storableRepr + "|";
    
    storableRepr = storableRepr + "elementPositions|?";
    for (Iterator iter = elementPositions.keySet().iterator(); iter.hasNext();) {
      String elementKey = (String)iter.next();
      Integer valueRepr = (Integer)elementPositions.get(elementKey);
      storableRepr = storableRepr + elementKey + ":" + valueRepr + "?";
    }
    storableRepr = storableRepr + "|";
    
    return storableRepr;
  }
}
