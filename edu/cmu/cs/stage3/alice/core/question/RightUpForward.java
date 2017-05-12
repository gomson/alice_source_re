package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.math.Vector3;


















public class RightUpForward
  extends Question
{
  public RightUpForward() {}
  
  public final NumberProperty right = new NumberProperty(this, "right", new Double(0.0D));
  public final NumberProperty up = new NumberProperty(this, "up", new Double(0.0D));
  public final NumberProperty forward = new NumberProperty(this, "forward", new Double(0.0D));
  
  public Class getValueClass() {
    return Vector3.class;
  }
  
  public Object getValue() {
    return new Vector3(right.doubleValue(), up.doubleValue(), forward.doubleValue());
  }
}
