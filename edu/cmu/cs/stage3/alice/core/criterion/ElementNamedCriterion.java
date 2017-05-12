package edu.cmu.cs.stage3.alice.core.criterion;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.util.Criterion;


















public class ElementNamedCriterion
  implements Criterion
{
  private String m_name;
  private boolean m_ignoreCase;
  
  public ElementNamedCriterion(String name) { this(name, true); }
  
  public ElementNamedCriterion(String name, boolean ignoreCase) {
    m_name = name;
    m_ignoreCase = ignoreCase;
  }
  
  public String getName() { return m_name; }
  

  public boolean getIgnoreCase() { return m_ignoreCase; }
  
  public boolean accept(Object o) {
    if ((o instanceof Element)) {
      String nameValue = name.getStringValue();
      if (m_name == null) {
        return nameValue == null;
      }
      if (m_ignoreCase) {
        return m_name.equalsIgnoreCase(nameValue);
      }
      return m_name.equals(nameValue);
    }
    

    return false;
  }
}
