package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.lang.Messages;




















public abstract class BinaryNumberResultingInBooleanQuestion
  extends BooleanQuestion
{
  public BinaryNumberResultingInBooleanQuestion() {}
  
  public final NumberProperty a = new NumberProperty(this, "a", new Double(0.0D));
  public final NumberProperty b = new NumberProperty(this, "b", new Double(0.0D));
  
  protected abstract boolean getValue(double paramDouble1, double paramDouble2);
  
  public Object getValue() { double aValue = a.doubleValue();
    double bValue = b.doubleValue();
    if ((this instanceof NumberIsEqualTo)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        aValue + " == " + bValue + " " + Messages.getString("is_");
    } else if ((this instanceof NumberIsNotEqualTo)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        aValue + " != " + bValue + " " + Messages.getString("is_");
    } else if ((this instanceof NumberIsGreaterThan)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        aValue + " > " + bValue + " " + Messages.getString("is_");
    } else if ((this instanceof NumberIsGreaterThanOrEqualTo)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        aValue + " >= " + bValue + " " + Messages.getString("is_");
    } else if ((this instanceof NumberIsLessThan)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        aValue + " <= " + bValue + " " + Messages.getString("is_");
    } else if ((this instanceof NumberIsLessThanOrEqualTo)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        aValue + " <= " + bValue + " " + Messages.getString("is_");
    }
    if (getValue(aValue, bValue)) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
