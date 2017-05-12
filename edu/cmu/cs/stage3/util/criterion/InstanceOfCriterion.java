package edu.cmu.cs.stage3.util.criterion;

import edu.cmu.cs.stage3.util.Criterion;


















public class InstanceOfCriterion
  implements Criterion
{
  private Class m_class;
  
  public InstanceOfCriterion(Class _class)
  {
    m_class = _class;
  }
  
  public boolean accept(Object o) { return m_class.isInstance(o); }
}
