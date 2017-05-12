package edu.cmu.cs.stage3.alice.core.light;

import edu.cmu.cs.stage3.alice.core.Light;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;




















public class PointLight
  extends Light
{
  public final NumberProperty constantAttenuation = new NumberProperty(this, "constantAttenuation", new Double(1.0D));
  public final NumberProperty linearAttenuation = new NumberProperty(this, "linearAttenuation", new Double(0.0D));
  public final NumberProperty quadraticAttenuation = new NumberProperty(this, "quadraticAttenuation", new Double(0.0D));
  
  protected PointLight(edu.cmu.cs.stage3.alice.scenegraph.PointLight sgLight) { super(sgLight);
    constantAttenuation.set(new Double(getSceneGraphPointLight().getConstantAttenuation()));
    linearAttenuation.set(new Double(getSceneGraphPointLight().getLinearAttenuation()));
    quadraticAttenuation.set(new Double(getSceneGraphPointLight().getQuadraticAttenuation()));
  }
  
  public PointLight() { this(new edu.cmu.cs.stage3.alice.scenegraph.PointLight()); }
  

  public edu.cmu.cs.stage3.alice.scenegraph.PointLight getSceneGraphPointLight() { return (edu.cmu.cs.stage3.alice.scenegraph.PointLight)getSceneGraphLight(); }
  
  private void constantAttenuationValueChanged(Number value) {
    double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    getSceneGraphPointLight().setConstantAttenuation(d);
  }
  
  private void linearAttenuationValueChanged(Number value) { double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    getSceneGraphPointLight().setLinearAttenuation(d);
  }
  
  private void quadraticAttenuationValueChanged(Number value) { double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    getSceneGraphPointLight().setQuadraticAttenuation(d);
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == constantAttenuation) {
      constantAttenuationValueChanged((Number)value);
    } else if (property == linearAttenuation) {
      linearAttenuationValueChanged((Number)value);
    } else if (property == quadraticAttenuation) {
      quadraticAttenuationValueChanged((Number)value);
    } else {
      super.propertyChanged(property, value);
    }
  }
}
