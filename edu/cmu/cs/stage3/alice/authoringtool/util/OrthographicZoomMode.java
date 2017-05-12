package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;











public class OrthographicZoomMode
  extends RenderTargetManipulatorMode
{
  protected AuthoringTool authoringTool;
  protected MainUndoRedoStack undoRedoStack;
  protected Scheduler scheduler;
  protected Point pressPoint = new Point();
  protected Dimension renderSize = new Dimension();
  protected Camera camera = null;
  
  public OrthographicZoomMode(AuthoringTool authoringTool, MainUndoRedoStack undoRedoStack, Scheduler scheduler) {
    this.authoringTool = authoringTool;
    this.undoRedoStack = undoRedoStack;
    this.scheduler = scheduler;
  }
  
  public boolean requiresPickedObject() {
    return false;
  }
  
  public boolean hideCursorOnDrag() {
    return true;
  }
  
  public void mousePressed(MouseEvent ev, Transformable pickedTransformable, PickInfo pickInfo) {
    camera = ((Camera)pickInfo.getSource().getBonus());
    pressPoint.setLocation(ev.getPoint());
  }
  
  public void mouseReleased(MouseEvent ev) {
    if (ev.getPoint().equals(pressPoint)) {
      DialogManager.showMessageDialog(Messages.getString("Click_and_drag_to_zoom_"), Messages.getString("Zoom_Message"), 1);
    } else if (undoRedoStack != null) {
      ev.isPopupTrigger();
    }
  }
  

  public void mouseDragged(MouseEvent ev, int dx, int dy)
  {
    if (((camera instanceof OrthographicCamera)) && (
      (dx != 0) || (dy != 0))) {
      double divisor = ev.isShiftDown() ? 1000.0D : 50.0D;
      double scaleFactor;
      double scaleFactor; if (Math.abs(dx) > Math.abs(dy)) {
        scaleFactor = 1.0D - dx / divisor;
      } else {
        scaleFactor = 1.0D - dy / divisor;
      }
      
      OrthographicCamera orthoCamera = (OrthographicCamera)camera;
      renderTarget.getAWTComponent().getSize(renderSize);
      
      double oldMinY = minimumY.getNumberValue().doubleValue();
      double oldMaxY = maximumY.getNumberValue().doubleValue();
      double oldPosX = getPositionx;
      double oldPosY = getPositiony;
      double oldHeight = oldMaxY - oldMinY;
      double pixelHeight = oldHeight / renderSize.getHeight();
      

      double pressDX = (pressPoint.getX() - renderSize.getWidth() / 2.0D) * pixelHeight;
      double pressDY = -(pressPoint.getY() - renderSize.getHeight() / 2.0D) * pixelHeight;
      double pressX = oldPosX + pressDX;
      double pressY = oldPosY + pressDY;
      
      double newPosX = pressX - scaleFactor * pressDX;
      double newPosY = pressY - scaleFactor * pressDY;
      
      double newHeight = oldHeight * scaleFactor;
      double newMinY = -newHeight / 2.0D;
      double newMaxY = -newMinY;
      
      orthoCamera.setPositionRightNow(newPosX, newPosY, 0.0D);
      minimumY.set(new Double(newMinY));
      maximumY.set(new Double(newMaxY));
    }
  }
}
