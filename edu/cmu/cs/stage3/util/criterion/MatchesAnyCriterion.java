package edu.cmu.cs.stage3.util.criterion;

import edu.cmu.cs.stage3.util.Criterion;



















public class MatchesAnyCriterion
  implements Criterion
{
  protected Criterion[] m_criteria;
  
  public MatchesAnyCriterion(Criterion[] criteria)
  {
    m_criteria = criteria;
  }
  
  public boolean accept(Object o) {
    if (m_criteria != null) {
      for (int i = 0; i < m_criteria.length; i++) {
        if (m_criteria[i].accept(o)) {
          return true;
        }
      }
      return false;
    }
    return false;
  }
}
