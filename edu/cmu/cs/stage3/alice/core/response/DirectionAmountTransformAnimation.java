package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.lang.Messages;
















public abstract class DirectionAmountTransformAnimation
  extends TransformAnimation
{
  public DirectionAmountTransformAnimation() {}
  
  protected abstract Direction getDefaultDirection();
  
  public final DirectionProperty direction = new DirectionProperty(this, "direction", getDefaultDirection());
  public final NumberProperty amount = new NumberProperty(this, "amount", new Double(1.0D));
  
  protected abstract boolean acceptsDirection(Direction paramDirection);
  
  protected void propertyChanging(Property property, Object value)
  {
    if (property == direction) {
      if (((value instanceof Direction)) && 
        (!acceptsDirection((Direction)value)))
      {

        throw new RuntimeException(this + " " + Messages.getString("does_not_accept_direction_") + value);
      }
    }
    else
      super.propertyChanging(property, value);
  }
  
  public class RuntimeDirectionAmountTransformAnimation extends TransformAnimation.RuntimeTransformAnimation {
    public RuntimeDirectionAmountTransformAnimation() { super(); }
    
    public void prologue(double t) {
      super.prologue(t);
      if (direction.getDirectionValue() == null) {
        throw new SimulationPropertyException(Messages.getString("direction_value_must_not_be_null_"), null, direction);
      }
    }
  }
}
