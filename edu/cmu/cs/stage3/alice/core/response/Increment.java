package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;



















public class Increment
  extends Animation
{
  public Increment() {}
  
  public final VariableProperty variable = new VariableProperty(this, "variable", null);
  

  protected Number getDefaultDuration() { return new Double(0.0D); }
  
  public class RuntimeIncrement extends Animation.RuntimeAnimation { public RuntimeIncrement() { super(); }
    private double m_amountAlreadyIncremented = 0.0D;
    
    private void incrementValue(double amount) { Variable variableValue = variable.getVariableValue();
      Number number = (Number)value.getValue();
      value.set(new Double(number.doubleValue() + amount));
      m_amountAlreadyIncremented += amount;
    }
    
    public void prologue(double t) {
      super.prologue(t);
      m_amountAlreadyIncremented = 0.0D;
    }
    
    public void update(double t) {
      super.update(t);
      incrementValue(getPortion(t) - m_amountAlreadyIncremented);
    }
  }
}
