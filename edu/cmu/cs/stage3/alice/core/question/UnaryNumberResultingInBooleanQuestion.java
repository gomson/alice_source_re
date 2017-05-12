package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;




















public abstract class UnaryNumberResultingInBooleanQuestion
  extends BooleanQuestion
{
  public UnaryNumberResultingInBooleanQuestion() {}
  
  public final NumberProperty a = new NumberProperty(this, "a", new Double(0.0D));
  
  protected abstract boolean getValue(double paramDouble);
  
  public Object getValue() { if (getValue(a.doubleValue())) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
