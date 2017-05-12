package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;



















public class Decrement
  extends Animation
{
  public Decrement() {}
  
  public final VariableProperty variable = new VariableProperty(this, "variable", null);
  

  protected Number getDefaultDuration() { return new Double(0.0D); }
  
  public class RuntimeDecrement extends Animation.RuntimeAnimation { public RuntimeDecrement() { super(); }
    private double m_amountAlreadyDecremented = 0.0D;
    
    private void decrementValue(double amount) { Variable variableValue = variable.getVariableValue();
      Number number = (Number)value.getValue();
      value.set(new Double(number.doubleValue() - amount));
      m_amountAlreadyDecremented += amount;
    }
    
    public void prologue(double t) {
      super.prologue(t);
      m_amountAlreadyDecremented = 0.0D;
    }
    
    public void update(double t) {
      super.update(t);
      decrementValue(getPortion(t) - m_amountAlreadyDecremented);
    }
  }
}
