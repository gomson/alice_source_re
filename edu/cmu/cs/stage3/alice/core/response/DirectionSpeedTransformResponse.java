package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.event.ExpressionEvent;
import edu.cmu.cs.stage3.alice.core.event.ExpressionListener;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.lang.Messages;
















public abstract class DirectionSpeedTransformResponse
  extends TransformResponse
{
  public DirectionSpeedTransformResponse() {}
  
  protected abstract Direction getDefaultDirection();
  
  public final DirectionProperty direction = new DirectionProperty(this, "direction", getDefaultDirection());
  public final NumberProperty speed = new NumberProperty(this, "speed", new Double(1.0D));
  
  protected abstract boolean acceptsDirection(Direction paramDirection);
  
  protected void propertyChanging(Property property, Object value) {
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
  
  public class RuntimeDirectionSpeedTransformResponse extends TransformResponse.RuntimeTransformResponse implements ExpressionListener { public RuntimeDirectionSpeedTransformResponse() { super(); }
    private double m_speed = NaN.0D;
    private boolean m_isSpeedDirty = true;
    private Expression m_expression = null;
    
    protected double getSpeed() { if (m_isSpeedDirty) {
        m_speed = speed.doubleValue();
      }
      return m_speed;
    }
    
    public void expressionChanged(ExpressionEvent expressionEvent) { m_isSpeedDirty = true; }
    

    public void prologue(double t)
    {
      super.prologue(t);
      if (direction.getDirectionValue() == null) {
        throw new SimulationPropertyException(Messages.getString("direction_value_must_not_be_null_"), null, direction);
      }
      if (speed.getValue() == null) {
        throw new SimulationPropertyException(Messages.getString("speed_value_must_not_be_null_"), null, speed);
      }
      Object o = speed.get();
      if ((o instanceof Expression)) {
        m_expression = ((Expression)o);
        m_expression.addExpressionListener(this);
      } else {
        m_expression = null;
      }
    }
    
    public void epilogue(double t) {
      super.epilogue(t);
      if (m_expression != null) {
        m_expression.removeExpressionListener(this);
        m_expression = null;
      }
    }
  }
}
