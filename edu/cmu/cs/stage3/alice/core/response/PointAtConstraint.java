package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.lang.Messages;





















public class PointAtConstraint
  extends AbstractPointAtConstraint
{
  public final BooleanProperty onlyAffectYaw = new BooleanProperty(this, "onlyAffectYaw", Boolean.FALSE);
  public class RuntimePointAtConstraint extends AbstractPointAtConstraint.RuntimeAbstractPointAtConstraint { public RuntimePointAtConstraint() { super(); }
    
    protected boolean onlyAffectYaw() {
      return onlyAffectYaw.booleanValue();
    }
    
    public void prologue(double t) {
      super.prologue(t);
      if (onlyAffectYaw.getValue() == null) {
        throw new SimulationPropertyException(Messages.getString("only_affect_yaw_value_must_not_be_null_"), null, onlyAffectYaw);
      }
    }
  }
  
  public PointAtConstraint() {}
}
