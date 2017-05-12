package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.util.Criterion;


















public class UnresolvableReferenceException
  extends Exception
{
  private Criterion m_criterion;
  
  public UnresolvableReferenceException(Criterion criterion, String detail)
  {
    super(detail);
    m_criterion = criterion;
  }
  
  public Criterion getCriterion() { return m_criterion; }
}
