package edu.cmu.cs.stage3.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Vector;















public abstract class Enumerable
  implements Serializable
{
  public Enumerable() {}
  
  private String m_repr = null;
  
  public static Enumerable[] getItems(Class cls) { Vector v = new Vector();
    Field[] fields = cls.getFields();
    for (int i = 0; i < fields.length; i++) {
      int modifiers = fields[i].getModifiers();
      if ((Modifier.isPublic(modifiers)) && (Modifier.isFinal(modifiers)) && (Modifier.isStatic(modifiers))) {
        try {
          v.addElement(fields[i].get(null));
        } catch (IllegalAccessException iae) {
          iae.printStackTrace();
        }
      }
    }
    Enumerable[] array = new Enumerable[v.size()];
    v.copyInto(array);
    return array;
  }
  
  public String getRepr() { if (m_repr == null) {
      Field[] fields = getClass().getFields();
      for (int i = 0; i < fields.length; i++) {
        int modifiers = fields[i].getModifiers();
        if ((Modifier.isPublic(modifiers)) && (Modifier.isFinal(modifiers)) && (Modifier.isStatic(modifiers))) {
          try {
            if (equals(fields[i].get(null))) {
              m_repr = fields[i].getName();
              return m_repr;
            }
          } catch (IllegalAccessException iae) {
            iae.printStackTrace();
          }
        }
      }
      return "unknown";
    }
    return m_repr;
  }
  


  public String toString() { return getClass().getName() + "[" + getRepr() + "]"; }
  
  protected static Enumerable valueOf(String s, Class cls) {
    String[] markers = { cls.getName() + "[", "]" };
    int begin = s.indexOf(markers[0]) + markers[0].length();
    int end = s.indexOf(markers[1]);
    String fieldName = s.substring(begin, end);
    Field[] fields = cls.getFields();
    for (int i = 0; i < fields.length; i++) {
      int modifiers = fields[i].getModifiers();
      if ((Modifier.isPublic(modifiers)) && (Modifier.isFinal(modifiers)) && (Modifier.isStatic(modifiers)) && 
        (fieldName.equals(fields[i].getName()))) {
        try {
          return (Enumerable)fields[i].get(null);
        } catch (IllegalAccessException iae) {
          iae.printStackTrace();
        }
      }
    }
    
    return null;
  }
}
