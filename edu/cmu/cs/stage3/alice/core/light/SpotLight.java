package edu.cmu.cs.stage3.alice.core.light;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;





















public class SpotLight
  extends PointLight
{
  public final NumberProperty innerBeamAngle = new NumberProperty(this, "innerBeamAngle", new Double(0.4D));
  public final NumberProperty outerBeamAngle = new NumberProperty(this, "outerBeamAngle", new Double(0.5D));
  
  public SpotLight() { super(new edu.cmu.cs.stage3.alice.scenegraph.SpotLight());
    innerBeamAngle.set(new Double(getSceneGraphSpotLight().getInnerBeamAngle()));
    outerBeamAngle.set(new Double(getSceneGraphSpotLight().getOuterBeamAngle()));
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.SpotLight getSceneGraphSpotLight() { return (edu.cmu.cs.stage3.alice.scenegraph.SpotLight)getSceneGraphPointLight(); }
  
  private void innerBeamAngleValueChanged(Number value)
  {
    double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    getSceneGraphSpotLight().setInnerBeamAngle(d);
  }
  
  private void outerBeamAngleValueChanged(Number value) { double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    getSceneGraphSpotLight().setOuterBeamAngle(d);
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == innerBeamAngle) {
      innerBeamAngleValueChanged((Number)value);
    } else if (property == outerBeamAngle) {
      outerBeamAngleValueChanged((Number)value);
    } else {
      super.propertyChanged(property, value);
    }
  }
}
