package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.util.Criterion;







































class VariableCriterion
  implements Criterion
{
  private Criterion m_wrappedCriterion;
  private String m_name;
  
  public VariableCriterion(String name, Criterion wrappedCriterion)
  {
    m_name = name;
    m_wrappedCriterion = wrappedCriterion;
  }
  
  public String getName() { return m_name; }
  

  public Criterion getWrappedCriterion() { return m_wrappedCriterion; }
  
  public boolean accept(Object o) {
    if ((o instanceof Variable)) {
      Variable variable = (Variable)o;
      if (m_name != null) {
        return m_name == name.getStringValue();
      }
      
      return false;
    }
    
    return false;
  }
  
  public String toString()
  {
    return "VariableCriterion[" + m_name + "]";
  }
}
