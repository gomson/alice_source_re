package edu.cmu.cs.stage3.alice.core.criterion;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.util.Criterion;
import java.lang.reflect.Constructor;

















public class ElementNameContainsCriterion
  implements Criterion
{
  private String m_contains;
  private boolean m_ignoreCase;
  
  public ElementNameContainsCriterion(String contains) { this(contains, true); }
  
  public ElementNameContainsCriterion(String contains, boolean ignoreCase) {
    m_contains = contains;
    m_ignoreCase = ignoreCase;
  }
  
  public String getContains() { return m_contains; }
  

  public boolean getIgnoreCase() { return m_ignoreCase; }
  
  public boolean accept(Object o) {
    if ((o instanceof Element)) {
      String name = name.getStringValue();
      if (m_contains == null)
        return name == null;
      if (name == null) {
        return false;
      }
      if (m_ignoreCase) {
        return name.toLowerCase().indexOf(m_contains.toLowerCase()) != -1;
      }
      return name.indexOf(m_contains) != -1;
    }
    

    return false;
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + m_contains + "]";
  }
  
  protected static ElementNameContainsCriterion valueOf(String s, Class cls) {
    String beginMarker = cls.getName() + "[";
    String endMarker = "]";
    int begin = s.indexOf(beginMarker) + beginMarker.length();
    int end = s.lastIndexOf(endMarker);
    try {
      Class[] types = { String.class };
      Object[] values = { s.substring(begin, end) };
      Constructor constructor = cls.getConstructor(types);
      return (ElementNameContainsCriterion)constructor.newInstance(values);
    } catch (Throwable t) {
      throw new RuntimeException();
    }
  }
  
  public static ElementNameContainsCriterion valueOf(String s) {
    return valueOf(s, ElementNameContainsCriterion.class);
  }
}
