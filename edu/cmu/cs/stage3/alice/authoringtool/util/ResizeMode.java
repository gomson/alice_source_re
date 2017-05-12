package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.event.MouseEvent;
import javax.vecmath.Vector3d;
















public class ResizeMode
  extends RenderTargetManipulatorMode
{
  protected Transformable pickedTransformable;
  protected MainUndoRedoStack undoRedoStack;
  protected Scheduler scheduler;
  protected Vector3d oldSize;
  
  public ResizeMode(MainUndoRedoStack undoRedoStack, Scheduler scheduler)
  {
    this.undoRedoStack = undoRedoStack;
    this.scheduler = scheduler;
  }
  
  public boolean requiresPickedObject() {
    return true;
  }
  
  public boolean hideCursorOnDrag() {
    return true;
  }
  
  public void mousePressed(MouseEvent ev, Transformable pickedTransformable, PickInfo pickInfo) {
    this.pickedTransformable = pickedTransformable;
    if (pickedTransformable != null) {
      oldSize = pickedTransformable.getSize();
    }
  }
  
  public void mouseReleased(MouseEvent ev) {
    if ((pickedTransformable != null) && (undoRedoStack != null)) {
      if (!ev.isPopupTrigger()) {
        undoRedoStack.push(new SizeUndoableRedoable(pickedTransformable, oldSize, pickedTransformable.getSize(), scheduler));
      }
      
      if (pickedTransformable.poses.size() > 0) {
        DialogManager.showMessageDialog(Messages.getString("Warning__resizing_objects_with_poses_may_make_those_poses_unusable_"), Messages.getString("Pose_warning"), 2);
      }
    }
  }
  
  public void mouseDragged(MouseEvent ev, int dx, int dy) {
    if ((pickedTransformable != null) && (dy != 0)) {
      double divisor = ev.isShiftDown() ? 1000.0D : 100.0D;
      double scaleFactor = 1.0D - dy / divisor;
      pickedTransformable.resizeRightNow(scaleFactor);
    }
  }
}
