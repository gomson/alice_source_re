package edu.cmu.cs.stage3.util.criterion;

import edu.cmu.cs.stage3.util.Criterion;



















public class MatchesAllCriterion
  implements Criterion
{
  protected Criterion[] m_criteria;
  
  public MatchesAllCriterion(Criterion[] criteria)
  {
    m_criteria = criteria;
  }
  
  public boolean accept(Object o) {
    if (m_criteria != null) {
      for (int i = 0; i < m_criteria.length; i++) {
        if (!m_criteria[i].accept(o)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public Criterion[] getCriteria()
  {
    return m_criteria;
  }
}
