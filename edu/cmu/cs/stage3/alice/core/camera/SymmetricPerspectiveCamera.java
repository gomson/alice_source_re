package edu.cmu.cs.stage3.alice.core.camera;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.decorator.SymmetricPerspectiveViewVolumeDecorator;
import edu.cmu.cs.stage3.alice.core.decorator.ViewVolumeDecorator;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;


















public class SymmetricPerspectiveCamera
  extends Camera
{
  public final NumberProperty verticalViewingAngle = new NumberProperty(this, "verticalViewingAngle", null);
  public final NumberProperty horizontalViewingAngle = new NumberProperty(this, "horizontalViewingAngle", null);
  
  public SymmetricPerspectiveCamera() { super(new edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera()); }
  
  public edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera getSceneGraphSymmetricPerspectiveCamera() {
    return (edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera)getSceneGraphCamera();
  }
  
  protected ViewVolumeDecorator createViewVolumeDecorator() {
    SymmetricPerspectiveViewVolumeDecorator symmetricPerspectiveViewVolumeDecorator = new SymmetricPerspectiveViewVolumeDecorator();
    symmetricPerspectiveViewVolumeDecorator.setSymmetricPerspectiveCamera(this);
    return symmetricPerspectiveViewVolumeDecorator;
  }
  
  protected void verticalViewingAngleValueChanged(Number value) { double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    getSceneGraphSymmetricPerspectiveCamera().setVerticalViewingAngle(d);
  }
  
  protected void horizontalViewingAngleValueChanged(Number value) { double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    getSceneGraphSymmetricPerspectiveCamera().setHorizontalViewingAngle(d);
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == verticalViewingAngle) {
      verticalViewingAngleValueChanged((Number)value);
    } else if (property == horizontalViewingAngle) {
      horizontalViewingAngleValueChanged((Number)value);
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  protected double getViewingAngleForGetAGoodLookAt()
  {
    double vva = verticalViewingAngle.doubleValue();
    double hva = horizontalViewingAngle.doubleValue();
    if (Double.isNaN(vva)) {
      if (Double.isNaN(hva)) {
        return super.getViewingAngleForGetAGoodLookAt();
      }
      return vva;
    }
    
    if (Double.isNaN(hva)) {
      return vva;
    }
    return Math.min(vva, hva);
  }
}
