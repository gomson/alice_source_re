package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;




















public class While
  extends Composite
{
  public While() {}
  
  public final BooleanProperty condition = new BooleanProperty(this, "condition", Boolean.TRUE);
  public final BooleanProperty testBeforeAsOpposedToAfter = new BooleanProperty(this, "testBeforeAsOpposedToAfter", Boolean.TRUE);
  
  public Object[] execute() {
    while (condition.booleanValue()) {
      Object[] value = super.execute();
      if (value != null) {
        return value;
      }
    }
    return null;
  }
}
