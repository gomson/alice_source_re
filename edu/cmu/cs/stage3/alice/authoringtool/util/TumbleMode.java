package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.math.MathUtilities;
import java.awt.event.MouseEvent;


















public class TumbleMode
  extends DefaultMoveMode
{
  public TumbleMode(MainUndoRedoStack undoRedoStack, Scheduler scheduler)
  {
    super(undoRedoStack, scheduler);
  }
  
  public void mouseDragged(MouseEvent ev, int dx, int dy) {
    if (pickedTransformable != null) {
      helper.setTransformationRightNow(MathUtilities.createIdentityMatrix4d(), camera);
      helper.setPositionRightNow(zeroVec, pickedTransformable);
      pickedTransformable.rotateRightNow(MathUtilities.getXAxis(), -dy * 0.01D, helper);
      pickedTransformable.rotateRightNow(MathUtilities.getYAxis(), -dx * 0.01D, pickedTransformable);
    }
  }
}
