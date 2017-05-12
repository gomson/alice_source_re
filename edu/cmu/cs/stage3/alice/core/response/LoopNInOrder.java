package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;
import java.util.Vector;













public class LoopNInOrder
  extends DoInOrder
{
  /**
   * @deprecated
   */
  public final NumberProperty count = new NumberProperty(this, "count", null);
  
  public final VariableProperty index = new VariableProperty(this, "index", null);
  
  public final NumberProperty start = new NumberProperty(this, "start", new Double(0.0D));
  public final NumberProperty end = new NumberProperty(this, "end", new Double(Double.POSITIVE_INFINITY));
  public final NumberProperty increment = new NumberProperty(this, "increment", new Double(1.0D));
  
  private static Class[] s_supportedCoercionClasses = new Class[0];
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  

  public LoopNInOrder() {}
  

  protected void loadCompleted()
  {
    super.loadCompleted();
    if (index.get() == null) {
      if (count.get() != null) {
        end.set(count.get());
      }
      Variable indexVariable = new Variable();
      valueClass.set(Number.class);
      name.set("index");
      indexVariable.setParent(this);
      index.set(indexVariable);
    }
  }
  
  protected void internalFindAccessibleExpressions(Class cls, Vector v) {
    internalAddExpressionIfAssignableTo((Expression)index.get(), cls, v);
    super.internalFindAccessibleExpressions(cls, v); }
  
  public class RuntimeLoopNInOrder extends DoInOrder.RuntimeDoInOrder { public RuntimeLoopNInOrder() { super(); }
    
    private int m_endTest;
    private double getIndexValue() { Variable indexVariable = index.getVariableValue();
      Number number = (Number)value.getValue();
      return number.doubleValue();
    }
    
    private void setIndexValue(double value) { Variable indexVariable = index.getVariableValue();
      value.set(new Double(value));
    }
    
    protected boolean preLoopTest(double t) {
      return getIndexValue() < m_endTest;
    }
    
    protected boolean postLoopTest(double t) {
      setIndexValue(getIndexValue() + increment.doubleValue(1.0D));
      return true;
    }
    
    protected boolean isCullable() {
      return false;
    }
    
    public void prologue(double t) {
      Behavior currentBehavior = getCurrentBehavior();
      if (currentBehavior != null) {
        Variable indexVariable = index.getVariableValue();
        Variable indexRuntimeVariable = new Variable();
        valueClass.set(valueClass.get());
        value.set(start.getNumberValue());
        currentBehavior.pushEach(indexVariable, indexRuntimeVariable);
      }
      m_endTest = ((int)end.doubleValue(Double.POSITIVE_INFINITY));
      super.prologue(t);
    }
    
    public void epilogue(double t) {
      super.epilogue(t);
      Behavior currentBehavior = getCurrentBehavior();
      if (currentBehavior != null) {
        currentBehavior.popStack();
      }
    }
  }
}
