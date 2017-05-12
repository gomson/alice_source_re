package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.lang.Messages;




















public abstract class UnaryBooleanResultingInBooleanQuestion
  extends BooleanQuestion
{
  public UnaryBooleanResultingInBooleanQuestion() {}
  
  public final BooleanProperty a = new BooleanProperty(this, "a", Boolean.TRUE);
  
  protected abstract boolean getValue(boolean paramBoolean);
  
  public Object getValue() { boolean aValue = getValue(a.booleanValue());
    if ((this instanceof Not)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("not_") + (!aValue) + " " + Messages.getString("is_");
    }
    if (aValue) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
