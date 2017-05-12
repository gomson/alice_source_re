package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.math.Matrix44;





















public class VehicleProperty
  extends ReferenceFrameProperty
{
  public VehicleProperty(Element owner, String name, ReferenceFrame defaultValue) { super(owner, name, defaultValue); }
  
  public void set(Object o, boolean preserveAbsoluteTransformation) {
    if (preserveAbsoluteTransformation) {
      Transformable transformable = (Transformable)getOwner();
      Matrix44 m = transformable.getTransformation(ReferenceFrame.ABSOLUTE);
      set(o);
      transformable.setAbsoluteTransformationRightNow(m);
    } else {
      set(o);
    }
  }
}
