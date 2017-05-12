package edu.cmu.cs.stage3.alice.core.criterion;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion;

















public class ExpressionIsAssignableFromCriterion
  extends InstanceOfCriterion
{
  private Class m_valueClass;
  
  protected ExpressionIsAssignableFromCriterion(Class expressionClass, Class valueClass)
  {
    super(expressionClass);
    m_valueClass = valueClass;
  }
  
  public ExpressionIsAssignableFromCriterion(Class cls) { this(Expression.class, cls); }
  
  public boolean accept(Object o)
  {
    if (super.accept(o)) {
      Class valueClass = ((Expression)o).getValueClass();
      if (valueClass != null) {
        return valueClass.isAssignableFrom(m_valueClass);
      }
      return false;
    }
    
    return false;
  }
}
