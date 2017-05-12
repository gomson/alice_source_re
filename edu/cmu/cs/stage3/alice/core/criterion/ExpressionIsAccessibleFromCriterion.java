package edu.cmu.cs.stage3.alice.core.criterion;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion;

















public class ExpressionIsAccessibleFromCriterion
  extends InstanceOfCriterion
{
  private Element m_from;
  
  public ExpressionIsAccessibleFromCriterion(Element from)
  {
    super(Expression.class);
    m_from = from;
  }
  
  public boolean accept(Object o) {
    if (super.accept(o))
    {















      return false;
    }
    return false;
  }
}
