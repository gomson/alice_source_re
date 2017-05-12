package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.ProjectionCamera;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import javax.vecmath.Matrix4d;















public abstract class ProjectionCameraProxy
  extends CameraProxy
{
  public ProjectionCameraProxy() {}
  
  protected abstract void onProjectionChange(Matrix4d paramMatrix4d);
  
  protected void changed(Property property, Object value)
  {
    if (property == ProjectionCamera.PROJECTION_PROPERTY) {
      onProjectionChange((Matrix4d)value);
    } else {
      super.changed(property, value);
    }
  }
}
