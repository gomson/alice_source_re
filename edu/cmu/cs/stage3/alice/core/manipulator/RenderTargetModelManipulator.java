package edu.cmu.cs.stage3.alice.core.manipulator;

import edu.cmu.cs.stage3.alice.core.property.Matrix44Property;
import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera;
import edu.cmu.cs.stage3.alice.scenegraph.Scene;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Vector3;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;








public class RenderTargetModelManipulator
  extends RenderTargetPickManipulator
{
  public static final int GROUND_PLANE_MODE = 1;
  public static final int CAMERA_PLANE_MODE = 2;
  public static final int DEFAULT_MODE = 1;
  protected edu.cmu.cs.stage3.alice.scenegraph.Transformable helper = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
  protected Camera sgCamera = null;
  protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgCameraTransformable = null;
  protected Scene sgScene = null;
  protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgIdentity = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
  protected Vector3d tempVec = new Vector3d();
  protected Vector3d zeroVec = new Vector3d(0.0D, 0.0D, 0.0D);
  protected Vector4d tempVec4 = new Vector4d();
  protected Vector3d cameraForward = new Vector3d();
  protected Vector3d cameraUp = new Vector3d();
  protected Matrix44 oldTransformation;
  protected int mode = 1;
  protected boolean popupEnabled = false;
  
  public RenderTargetModelManipulator(OnscreenRenderTarget renderTarget) {
    super(renderTarget);
    setMode(1);
    helper.setName("helper");
  }
  

  public void setMode(int mode)
  {
    this.mode = mode;
    if (mode == 1) {
      setHideCursorOnDrag(true);
    } else if (mode == 2) {
      setHideCursorOnDrag(false);
    }
  }
  
  public void mousePressed(MouseEvent ev) {
    if (enabled) {
      super.mousePressed(ev);
      
      if ((ePickedTransformable != null) && (!ePickedTransformable.doEventsStopAscending())) {
        abortAction();
      }
      else if (sgPickedTransformable != null) {
        sgCamera = renderTarget.getCameras()[0];
        sgCameraTransformable = ((edu.cmu.cs.stage3.alice.scenegraph.Transformable)sgCamera.getParent());
        sgScene = ((Scene)sgCamera.getRoot());
        
        oldTransformation = new Matrix44(sgPickedTransformable.getLocalTransformation());
        
        helper.setParent(sgScene);
        sgIdentity.setParent(sgScene);
      }
    }
  }
  















  public void mouseDragged(MouseEvent ev)
  {
    if (enabled) {
      super.mouseDragged(ev);
      
      if ((mouseIsDown) && 
        (sgPickedTransformable != null)) {
        double deltaFactor = 0.0D;
        if (renderTarget != null) {
          if ((sgCamera instanceof OrthographicCamera)) {
            OrthographicCamera orthoCamera = (OrthographicCamera)sgCamera;
            double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();
            double nearClipHeightInWorld = orthoCamera.getPlane()[3] - orthoCamera.getPlane()[1];
            deltaFactor = nearClipHeightInWorld / nearClipHeightInScreen;
          } else {
            double projectionMatrix11 = renderTarget.getProjectionMatrix(sgCamera).getElement(1, 1);
            double nearClipDist = sgCamera.getNearClippingPlaneDistance();
            double nearClipHeightInWorld = 2.0D * (nearClipDist / projectionMatrix11);
            double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();
            double pixelHeight = nearClipHeightInWorld / nearClipHeightInScreen;
            double objectDist = sgPickedTransformable.getPosition(sgCameraTransformable).getLength();
            deltaFactor = objectDist * pixelHeight / nearClipDist;
          }
        }
        boolean controlDown = ev.isControlDown();
        boolean shiftDown = ev.isShiftDown();
        
        if (mode == 1) {
          if (controlDown) {
            if (shiftDown) {
              helper.setTransformation(MathUtilities.createIdentityMatrix4d(), sgCameraTransformable);
              helper.setPosition(zeroVec, sgPickedTransformable);
              sgPickedTransformable.rotate(MathUtilities.getXAxis(), -dy * 0.01D, helper);
              sgPickedTransformable.rotate(MathUtilities.getYAxis(), -dx * 0.01D, sgPickedTransformable);
            } else {
              helper.setTransformation(MathUtilities.createIdentityMatrix4d(), sgScene);
              helper.setPosition(zeroVec, sgPickedTransformable);
              sgPickedTransformable.rotate(MathUtilities.getYAxis(), -dx * 0.01D, helper);
            }
          } else if (shiftDown) {
            helper.setTransformation(MathUtilities.createIdentityMatrix4d(), sgScene);
            helper.setPosition(zeroVec, sgPickedTransformable);
            tempVec.x = 0.0D;
            tempVec.y = (-dy * deltaFactor);
            tempVec.z = 0.0D;
            sgPickedTransformable.translate(tempVec, helper);
          } else {
            Matrix4d cameraTransformation = sgCameraTransformable.getAbsoluteTransformation();
            cameraUp.x = m10;
            cameraUp.y = m11;
            cameraUp.z = m12;
            cameraForward.x = m20;
            cameraForward.y = m21;
            cameraForward.z = m22;
            
            helper.setPosition(zeroVec, sgPickedTransformable);
            if (Math.abs(cameraForward.y) < Math.abs(cameraUp.y)) {
              cameraForward.y = 0.0D;
              helper.setOrientation(cameraForward, cameraUp, sgScene);
            } else {
              cameraUp.y = 0.0D;
              cameraForward.negate();
              helper.setOrientation(cameraUp, cameraForward, sgScene);
            }
            
            tempVec.x = (dx * deltaFactor);
            tempVec.y = 0.0D;
            tempVec.z = (-dy * deltaFactor);
            sgPickedTransformable.translate(tempVec, helper);
          }
        } else if (mode == 2) {
          if (controlDown) {
            if (!shiftDown)
            {

              helper.setTransformation(MathUtilities.createIdentityMatrix4d(), sgCameraTransformable);
              helper.setPosition(zeroVec, sgPickedTransformable);
              sgPickedTransformable.rotate(MathUtilities.getZAxis(), -dx * 0.01D, helper);
            }
          } else if (shiftDown) {
            Point p = ev.getPoint();
            int bigdx = x - originalMousePoint.x;
            int bigdy = y - originalMousePoint.y;
            sgPickedTransformable.setLocalTransformation(oldTransformation);
            if (Math.abs(bigdx) > Math.abs(bigdy)) {
              tempVec.x = (bigdx * deltaFactor);
              tempVec.y = 0.0D;
            } else {
              tempVec.x = 0.0D;
              tempVec.y = (-bigdy * deltaFactor);
            }
            tempVec.z = 0.0D;
            sgPickedTransformable.translate(tempVec, sgCameraTransformable);
          } else {
            tempVec.x = (dx * deltaFactor);
            tempVec.y = (-dy * deltaFactor);
            tempVec.z = 0.0D;
            sgPickedTransformable.translate(tempVec, sgCameraTransformable);
          }
        }
        
        if (ePickedTransformable != null) {
          ePickedTransformable.localTransformation.set(sgPickedTransformable.getLocalTransformation());
        }
      }
    }
  }
}
