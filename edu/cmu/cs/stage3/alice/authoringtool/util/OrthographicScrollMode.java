package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import edu.cmu.cs.stage3.math.Matrix44;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;











public class OrthographicScrollMode
  extends RenderTargetManipulatorMode
{
  protected double minScrollFactor = 0.3D;
  protected double maxScrollFactor = 4.0D;
  protected double scrollRampUpDistance = 10.0D;
  
  protected World world;
  protected Camera camera = null;
  protected Transformable identity = new Transformable();
  protected Vector3d tempVec = new Vector3d();
  protected Vector3d zeroVec = new Vector3d(0.0D, 0.0D, 0.0D);
  protected Vector4d tempVec4 = new Vector4d();
  protected Vector3d cameraForward = new Vector3d();
  protected Vector3d cameraUp = new Vector3d();
  protected Matrix44 oldTransformation;
  protected UndoRedoStack undoRedoStack;
  protected Scheduler scheduler;
  protected Point pressPoint = new Point();
  
  public OrthographicScrollMode() {
    this(null, null);
  }
  
  public OrthographicScrollMode(UndoRedoStack undoRedoStack, Scheduler scheduler) {
    this.undoRedoStack = undoRedoStack;
    this.scheduler = scheduler;
  }
  
  public boolean requiresPickedObject() {
    return false;
  }
  
  public boolean hideCursorOnDrag() {
    return false;
  }
  
  public void mousePressed(MouseEvent ev, Transformable pickedTransformable, PickInfo pickInfo) {
    camera = ((Camera)pickInfo.getSource().getBonus());
    if ((camera instanceof OrthographicCamera)) {
      world = camera.getWorld();
      oldTransformation = camera.getLocalTransformation();
      identity.vehicle.set(world);
      pressPoint.setLocation(ev.getPoint());
    }
  }
  
  public void mouseReleased(MouseEvent ev) {
    if (((camera instanceof OrthographicCamera)) && (undoRedoStack != null) && (scheduler != null) && 
      (!ev.isPopupTrigger())) {
      undoRedoStack.push(new PointOfViewUndoableRedoable(camera, oldTransformation, camera.getLocalTransformation(), scheduler));
    }
  }
  
  public void mouseDragged(MouseEvent ev, int dx, int dy)
  {
    if ((camera instanceof OrthographicCamera)) {
      boolean controlDown = ev.isControlDown();
      boolean shiftDown = ev.isShiftDown();
      
      OrthographicCamera orthoCamera = (OrthographicCamera)camera;
      double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight();
      double nearClipHeightInWorld = orthoCamera.getSceneGraphOrthographicCamera().getPlane()[3] - orthoCamera.getSceneGraphOrthographicCamera().getPlane()[1];
      double deltaFactor = nearClipHeightInWorld / nearClipHeightInScreen;
      double offsetDist = Math.min(Math.sqrt(dx * dx + dy * dy), scrollRampUpDistance);
      

      if (controlDown) {
        if (shiftDown) {
          deltaFactor *= 2.0D * maxScrollFactor;
        } else {
          deltaFactor *= maxScrollFactor;
        }
      } else if (shiftDown) {
        deltaFactor *= minScrollFactor;
      }
      
      tempVec.x = (-dx * deltaFactor);
      tempVec.y = (dy * deltaFactor);
      tempVec.z = 0.0D;
      camera.moveRightNow(tempVec);
    }
  }
}
