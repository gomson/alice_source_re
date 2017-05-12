package edu.cmu.cs.stage3.alice.core.criterion;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.util.Criterion;
import java.lang.reflect.Constructor;


















public class ElementKeyedCriterion
  implements Criterion
{
  private String m_key;
  private boolean m_ignoreCase;
  
  public ElementKeyedCriterion(String key) { this(key, true); }
  
  public ElementKeyedCriterion(String key, boolean ignoreCase) {
    m_key = key;
    m_ignoreCase = ignoreCase;
  }
  
  public String getKey() { return m_key; }
  

  public boolean getIgnoreCase() { return m_ignoreCase; }
  
  public boolean accept(Object o) {
    if ((o instanceof Element)) {
      String key = ((Element)o).getKey();
      if (m_key == null) {
        return key == null;
      }
      if (m_ignoreCase) {
        return m_key.equalsIgnoreCase(key);
      }
      return m_key.equals(key);
    }
    

    return false;
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + m_key + "]";
  }
  
  protected static ElementKeyedCriterion valueOf(String s, Class cls) {
    String beginMarker = cls.getName() + "[";
    String endMarker = "]";
    int begin = s.indexOf(beginMarker) + beginMarker.length();
    int end = s.lastIndexOf(endMarker);
    try {
      Class[] types = { String.class };
      Object[] values = { s.substring(begin, end) };
      Constructor constructor = cls.getConstructor(types);
      return (ElementKeyedCriterion)constructor.newInstance(values);
    } catch (Throwable t) {
      throw new RuntimeException();
    }
  }
  
  public static ElementKeyedCriterion valueOf(String s) {
    return valueOf(s, ElementKeyedCriterion.class);
  }
}
