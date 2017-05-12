package edu.cmu.cs.stage3.alice.core.camera;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.decorator.PerspectiveViewVolumeDecorator;
import edu.cmu.cs.stage3.alice.core.decorator.ViewVolumeDecorator;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;


















public class PerspectiveCamera
  extends Camera
{
  public final NumberProperty minimumX = new NumberProperty(this, "minimumX", null);
  public final NumberProperty minimumY = new NumberProperty(this, "minimumY", null);
  public final NumberProperty maximumX = new NumberProperty(this, "maximumX", null);
  public final NumberProperty maximumY = new NumberProperty(this, "maximumY", null);
  
  public PerspectiveCamera() { super(new edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera()); }
  
  public edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera getSceneGraphPerspectiveCamera() {
    return (edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera)getSceneGraphCamera();
  }
  
  protected ViewVolumeDecorator createViewVolumeDecorator() {
    PerspectiveViewVolumeDecorator perspectiveViewVolumeDecorator = new PerspectiveViewVolumeDecorator();
    perspectiveViewVolumeDecorator.setPerspectiveCamera(this);
    return perspectiveViewVolumeDecorator;
  }
  
  private void minimumXValueChanged(Number value) { double[] plane = (double[])getSceneGraphPerspectiveCamera().getPlane().clone();
    plane[0] = NaN.0D;
    if (value != null) {
      plane[0] = value.doubleValue();
    }
    getSceneGraphPerspectiveCamera().setPlane(plane);
  }
  
  private void minimumYValueChanged(Number value) { double[] plane = (double[])getSceneGraphPerspectiveCamera().getPlane().clone();
    plane[1] = NaN.0D;
    if (value != null) {
      plane[1] = value.doubleValue();
    }
    getSceneGraphPerspectiveCamera().setPlane(plane);
  }
  
  private void maximumXValueChanged(Number value) { double[] plane = (double[])getSceneGraphPerspectiveCamera().getPlane().clone();
    plane[2] = NaN.0D;
    if (value != null) {
      plane[2] = value.doubleValue();
    }
    getSceneGraphPerspectiveCamera().setPlane(plane);
  }
  
  private void maximumYValueChanged(Number value) { double[] plane = (double[])getSceneGraphPerspectiveCamera().getPlane().clone();
    plane[3] = NaN.0D;
    if (value != null) {
      plane[3] = value.doubleValue();
    }
    getSceneGraphPerspectiveCamera().setPlane(plane);
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == minimumX) {
      minimumXValueChanged((Number)value);
    } else if (property == minimumY) {
      minimumYValueChanged((Number)value);
    } else if (property == maximumX) {
      maximumXValueChanged((Number)value);
    } else if (property == maximumY) {
      maximumYValueChanged((Number)value);
    } else {
      super.propertyChanged(property, value);
    }
  }
}
