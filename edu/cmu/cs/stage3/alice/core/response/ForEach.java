package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ListProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;
import java.util.Vector;
















public abstract class ForEach
  extends DoInOrder
{
  public final VariableProperty each = new VariableProperty(this, "each", null);
  

  public final ListProperty list = new ListProperty(this, "list", null);
  
  public ForEach() {}
  
  protected void internalFindAccessibleExpressions(Class cls, Vector v) { internalAddExpressionIfAssignableTo((Expression)each.get(), cls, v);
    super.internalFindAccessibleExpressions(cls, v); }
  
  public class RuntimeForEach extends DoInOrder.RuntimeDoInOrder { public RuntimeForEach() { super(); }
    protected int m_listSize = -1;
    
    protected void setForkIndex(int index) { Behavior currentBehavior = getCurrentBehavior();
      if ((currentBehavior != null) && 
        (m_listSize > 0)) {
        currentBehavior.setForkIndex(this, index);
      }
    }
    
    public void prologue(double t)
    {
      Behavior currentBehavior = getCurrentBehavior();
      if (currentBehavior != null) {
        List listValue = list.getListValue();
        if (listValue != null) {
          m_listSize = listValue.size();
        } else {
          m_listSize = 0;
        }
        if (m_listSize > 0) {
          currentBehavior.openFork(this, m_listSize);
          for (int i = 0; i < m_listSize; i++) {
            currentBehavior.setForkIndex(this, i);
            Variable eachVariable = each.getVariableValue();
            Variable eachRuntimeVariable = new Variable();
            valueClass.set(valueClass.get());
            value.set(list.getListValue().itemAtIndex(i));
            currentBehavior.pushEach(eachVariable, eachRuntimeVariable);
          }
        }
      }
      super.prologue(t);
    }
    
    public void epilogue(double t) {
      super.epilogue(t);
      Behavior currentBehavior = getCurrentBehavior();
      if ((currentBehavior != null) && 
        (m_listSize > 0)) {
        for (int i = 0; i < m_listSize; i++) {
          currentBehavior.setForkIndex(this, i);
          currentBehavior.popStack();
        }
        currentBehavior.setForkIndex(this, 0);
        currentBehavior.closeFork(this);
      }
    }
  }
}
