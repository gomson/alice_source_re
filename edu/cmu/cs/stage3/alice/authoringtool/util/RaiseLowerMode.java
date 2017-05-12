package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Vector3;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.vecmath.Matrix4d;











public class RaiseLowerMode
  extends DefaultMoveMode
{
  public RaiseLowerMode(MainUndoRedoStack undoRedoStack, Scheduler scheduler)
  {
    super(undoRedoStack, scheduler);
  }
  
  public void mouseDragged(MouseEvent ev, int dx, int dy) {
    if (pickedTransformable != null) { double deltaFactor;
      double deltaFactor;
      if ((camera instanceof OrthographicCamera)) {
        OrthographicCamera orthoCamera = (OrthographicCamera)camera;
        double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();
        double nearClipHeightInWorld = orthoCamera.getSceneGraphOrthographicCamera().getPlane()[3] - orthoCamera.getSceneGraphOrthographicCamera().getPlane()[1];
        deltaFactor = nearClipHeightInWorld / nearClipHeightInScreen;
      } else {
        double projectionMatrix11 = renderTarget.getProjectionMatrix(camera.getSceneGraphCamera()).getElement(1, 1);
        double nearClipDist = camera.nearClippingPlaneDistance.doubleValue();
        double nearClipHeightInWorld = 2.0D * (nearClipDist / projectionMatrix11);
        double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();
        double pixelHeight = nearClipHeightInWorld / nearClipHeightInScreen;
        double objectDist = pickedTransformable.getPosition(camera).getLength();
        deltaFactor = objectDist * pixelHeight / nearClipDist;
      }
      
      helper.setTransformationRightNow(MathUtilities.createIdentityMatrix4d(), world);
      helper.setPositionRightNow(zeroVec, pickedTransformable);
      tempVec.x = 0.0D;
      tempVec.y = (-dy * deltaFactor);
      tempVec.z = 0.0D;
      pickedTransformable.moveRightNow(tempVec, helper);
    }
  }
}
