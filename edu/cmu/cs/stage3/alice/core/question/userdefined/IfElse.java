package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;




















public class IfElse
  extends Composite
{
  public IfElse() {}
  
  public final BooleanProperty condition = new BooleanProperty(this, "condition", Boolean.TRUE);
  public final ElementArrayProperty elseComponents = new ElementArrayProperty(this, "elseComponents", null, [Ledu.cmu.cs.stage3.alice.core.question.userdefined.Component.class);
  
  public Object[] execute() {
    if (condition.booleanValue()) {
      return execute(components);
    }
    return execute(elseComponents);
  }
}
