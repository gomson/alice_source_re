package edu.cmu.cs.stage3.util.criterion;

import edu.cmu.cs.stage3.util.Criterion;



















public class NotCriterion
  implements Criterion
{
  protected Criterion m_criterion;
  
  public NotCriterion(Criterion criterion)
  {
    m_criterion = criterion;
  }
  
  public boolean accept(Object o) {
    if (m_criterion != null) {
      return !m_criterion.accept(o);
    }
    return false;
  }
}
