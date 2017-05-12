package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.lang.Messages;


















public abstract class BinaryBooleanResultingInBooleanQuestion
  extends BooleanQuestion
{
  public BinaryBooleanResultingInBooleanQuestion() {}
  
  protected abstract boolean getValue(boolean paramBoolean1, boolean paramBoolean2);
  
  public final BooleanProperty a = new BooleanProperty(this, "a", Boolean.TRUE);
  public final BooleanProperty b = new BooleanProperty(this, "b", Boolean.TRUE);
  
  protected abstract boolean isShortCircuitable(boolean paramBoolean);
  
  public Object getValue() {
    boolean aValue = a.booleanValue();
    
    if ((this instanceof And)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        Messages.getString("both_") + aValue + " " + Messages.getString("and_") + b.booleanValue() + " " + Messages.getString("is_");
    } else if ((this instanceof Or))
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = 
        Messages.getString("either_") + aValue + " " + Messages.getString("or_") + b.booleanValue() + " " + Messages.getString("is_");
    boolean returnValue;
    boolean returnValue; if (isShortCircuitable(aValue)) {
      returnValue = aValue;
    } else {
      returnValue = getValue(aValue, b.booleanValue());
    }
    if (returnValue) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
