package edu.cmu.cs.stage3.alice.core.criterion;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion;

















public class ExpressionIsAssignableToCriterion
  extends InstanceOfCriterion
{
  private Class m_valueClass;
  
  protected ExpressionIsAssignableToCriterion(Class expressionClass, Class valueClass)
  {
    super(expressionClass);
    m_valueClass = valueClass;
  }
  
  public ExpressionIsAssignableToCriterion(Class cls) { this(Expression.class, cls); }
  
  public boolean accept(Object o)
  {
    if (super.accept(o)) {
      Class valueClass = ((Expression)o).getValueClass();
      if (valueClass != null) {
        return m_valueClass.isAssignableFrom(valueClass);
      }
      return false;
    }
    
    return false;
  }
}
