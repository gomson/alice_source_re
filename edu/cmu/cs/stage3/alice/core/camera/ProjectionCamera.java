package edu.cmu.cs.stage3.alice.core.camera;

import edu.cmu.cs.stage3.alice.core.decorator.ProjectionViewVolumeDecorator;

public class ProjectionCamera extends edu.cmu.cs.stage3.alice.core.Camera {
  public final edu.cmu.cs.stage3.alice.core.property.Matrix44Property projection = new edu.cmu.cs.stage3.alice.core.property.Matrix44Property(this, "projection", edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d());
  
  public ProjectionCamera() { super(new edu.cmu.cs.stage3.alice.scenegraph.ProjectionCamera()); }
  
  public edu.cmu.cs.stage3.alice.scenegraph.ProjectionCamera getSceneGraphProjectionCamera() {
    return (edu.cmu.cs.stage3.alice.scenegraph.ProjectionCamera)getSceneGraphCamera();
  }
  
  protected edu.cmu.cs.stage3.alice.core.decorator.ViewVolumeDecorator createViewVolumeDecorator() {
    ProjectionViewVolumeDecorator projectionViewVolumeDecorator = new ProjectionViewVolumeDecorator();
    projectionViewVolumeDecorator.setProjectionCamera(this);
    return projectionViewVolumeDecorator;
  }
  
  protected void propertyChanged(edu.cmu.cs.stage3.alice.core.Property property, Object value) {
    if (property == projection) {
      getSceneGraphProjectionCamera().setProjection((javax.vecmath.Matrix4d)value);
    } else {
      super.propertyChanged(property, value);
    }
  }
}
