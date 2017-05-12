package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Vector3;
import java.awt.event.MouseEvent;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;








public class DefaultMoveMode
  extends RenderTargetManipulatorMode
{
  protected edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable;
  protected edu.cmu.cs.stage3.alice.core.Transformable helper = new edu.cmu.cs.stage3.alice.core.Transformable();
  protected World world;
  protected edu.cmu.cs.stage3.alice.core.Camera camera = null;
  protected edu.cmu.cs.stage3.alice.core.Transformable identity = new edu.cmu.cs.stage3.alice.core.Transformable();
  protected Vector3d tempVec = new Vector3d();
  protected Vector3d zeroVec = new Vector3d(0.0D, 0.0D, 0.0D);
  protected Vector4d tempVec4 = new Vector4d();
  protected Vector3d cameraForward = new Vector3d();
  protected Vector3d cameraUp = new Vector3d();
  protected Matrix44 oldTransformation;
  protected MainUndoRedoStack undoRedoStack;
  protected Scheduler scheduler;
  
  public DefaultMoveMode() {
    this(null, null);
  }
  
  public DefaultMoveMode(MainUndoRedoStack undoRedoStack, Scheduler scheduler) {
    this.undoRedoStack = undoRedoStack;
    this.scheduler = scheduler;
    init();
  }
  
  private void init() {
    helper.name.set("helper");
  }
  
  public boolean requiresPickedObject() {
    return true;
  }
  
  public boolean hideCursorOnDrag() {
    return true;
  }
  
  public void mousePressed(MouseEvent ev, edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable, PickInfo pickInfo) {
    this.pickedTransformable = pickedTransformable;
    if (pickedTransformable != null) {
      camera = ((edu.cmu.cs.stage3.alice.core.Camera)pickInfo.getSource().getBonus());
      world = ((World)camera.getSceneGraphCamera().getRoot().getBonus());
      oldTransformation = pickedTransformable.getLocalTransformation();
      helper.vehicle.set(world);
      identity.vehicle.set(world);
    }
  }
  
  public void mouseReleased(MouseEvent ev) {
    if ((pickedTransformable != null) && (undoRedoStack != null) && 
      (!ev.isPopupTrigger())) {
      undoRedoStack.push(new PointOfViewUndoableRedoable(pickedTransformable, oldTransformation, pickedTransformable.getLocalTransformation(), scheduler));
    }
  }
  
  public void mouseDragged(MouseEvent ev, int dx, int dy)
  {
    if (pickedTransformable != null) {
      boolean controlDown = ev.isControlDown();
      if (AikMin.isMAC()) {
        controlDown = ev.isAltDown();
      }
      boolean shiftDown = ev.isShiftDown();
      

      if ((camera instanceof OrthographicCamera)) {
        OrthographicCamera orthoCamera = (OrthographicCamera)camera;
        double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();
        double nearClipHeightInWorld = orthoCamera.getSceneGraphOrthographicCamera().getPlane()[3] - orthoCamera.getSceneGraphOrthographicCamera().getPlane()[1];
        double deltaFactor = nearClipHeightInWorld / nearClipHeightInScreen;
        
        if (controlDown) {
          if (shiftDown) {
            helper.setTransformationRightNow(MathUtilities.createIdentityMatrix4d(), camera);
            helper.setPositionRightNow(zeroVec, pickedTransformable);
            pickedTransformable.rotateRightNow(MathUtilities.getXAxis(), -dy * 0.01D, helper);
            pickedTransformable.rotateRightNow(MathUtilities.getYAxis(), -dx * 0.01D, pickedTransformable);
          } else {
            helper.setTransformationRightNow(MathUtilities.createIdentityMatrix4d(), camera);
            helper.setPositionRightNow(zeroVec, pickedTransformable);
            pickedTransformable.rotateRightNow(MathUtilities.getZAxis(), -dx * 0.01D, helper);
          }
        } else if (shiftDown) {
          helper.setTransformationRightNow(MathUtilities.createIdentityMatrix4d(), camera);
          helper.setPositionRightNow(zeroVec, pickedTransformable);
          tempVec.x = 0.0D;
          tempVec.y = (-dy * deltaFactor);
          tempVec.z = 0.0D;
          pickedTransformable.moveRightNow(tempVec, helper);
        } else {
          helper.setTransformationRightNow(MathUtilities.createIdentityMatrix4d(), camera);
          helper.setPositionRightNow(zeroVec, pickedTransformable);
          tempVec.x = (dx * deltaFactor);
          tempVec.y = (-dy * deltaFactor);
          tempVec.z = 0.0D;
          pickedTransformable.moveRightNow(tempVec, helper);
        }
      } else {
        double projectionMatrix11 = renderTarget.getProjectionMatrix(camera.getSceneGraphCamera()).getElement(1, 1);
        double nearClipDist = camera.nearClippingPlaneDistance.doubleValue();
        double nearClipHeightInWorld = 2.0D * (nearClipDist / projectionMatrix11);
        double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();
        double pixelHeight = nearClipHeightInWorld / nearClipHeightInScreen;
        
        double objectDist = pickedTransformable.getPosition(camera).getLength();
        double deltaFactor = objectDist * pixelHeight / nearClipDist;
        
        if (controlDown) {
          if (shiftDown) {
            helper.setTransformationRightNow(MathUtilities.createIdentityMatrix4d(), camera);
            helper.setPositionRightNow(zeroVec, pickedTransformable);
            pickedTransformable.rotateRightNow(MathUtilities.getXAxis(), -dy * 0.01D, helper);
            pickedTransformable.rotateRightNow(MathUtilities.getYAxis(), -dx * 0.01D, pickedTransformable);
          } else {
            helper.setTransformationRightNow(MathUtilities.createIdentityMatrix4d(), world);
            helper.setPositionRightNow(zeroVec, pickedTransformable);
            pickedTransformable.rotateRightNow(MathUtilities.getYAxis(), -dx * 0.01D, helper);
          }
        } else if (shiftDown) {
          helper.setTransformationRightNow(MathUtilities.createIdentityMatrix4d(), world);
          helper.setPositionRightNow(zeroVec, pickedTransformable);
          tempVec.x = 0.0D;
          tempVec.y = (-dy * deltaFactor);
          tempVec.z = 0.0D;
          pickedTransformable.moveRightNow(tempVec, helper);
        } else {
          Matrix4d cameraTransformation = camera.getSceneGraphTransformable().getAbsoluteTransformation();
          cameraUp.x = m10;
          cameraUp.y = m11;
          cameraUp.z = m12;
          cameraForward.x = m20;
          cameraForward.y = m21;
          cameraForward.z = m22;
          
          helper.setPositionRightNow(zeroVec, pickedTransformable);
          if (Math.abs(cameraForward.y) < Math.abs(cameraUp.y)) {
            cameraForward.y = 0.0D;
            helper.setOrientationRightNow(cameraForward, cameraUp, world);
          } else {
            cameraUp.y = 0.0D;
            cameraForward.negate();
            helper.setOrientationRightNow(cameraUp, cameraForward, world);
          }
          
          tempVec.x = (dx * deltaFactor);
          tempVec.y = 0.0D;
          tempVec.z = (-dy * deltaFactor);
          pickedTransformable.moveRightNow(tempVec, helper);
        }
      }
    }
  }
}
