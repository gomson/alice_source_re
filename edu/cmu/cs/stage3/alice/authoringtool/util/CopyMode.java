package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.response.MoveAnimation;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import java.awt.event.MouseEvent;







public class CopyMode
  extends DefaultMoveMode
{
  protected Class[] classesToShare = new Class[0];
  
  private boolean hasBeenDragged;
  

  public CopyMode() {}
  

  public CopyMode(MainUndoRedoStack undoRedoStack, Scheduler scheduler)
  {
    super(undoRedoStack, scheduler);
  }
  
  private boolean isButton1(MouseEvent ev)
  {
    return (ev.getModifiers() & 0x10) != 0;
  }
  
  public void mousePressed(MouseEvent ev, Transformable pickedTransformable, PickInfo pickInfo) {
    if (isButton1(ev)) {
      undoRedoStack.startCompound();
      if (pickedTransformable != null) {
        String name = AuthoringToolResources.getNameForNewChild(name.getStringValue(), pickedTransformable.getParent());
        int index = pickedTransformable.getParent().getIndexOfChild(pickedTransformable) + 1;
        



        pickedTransformable = (Transformable)pickedTransformable.HACK_createCopy(name, pickedTransformable.getParent(), index, classesToShare, null);
        AuthoringToolResources.addElementToAppropriateProperty(pickedTransformable, pickedTransformable.getParent());
      }
      



      super.mousePressed(ev, pickedTransformable, pickInfo);
      hasBeenDragged = false;
    }
  }
  
  public void mouseReleased(MouseEvent ev) {
    if (isButton1(ev))
    {
      if (!hasBeenDragged)
      {

        MoveAnimation moveAnimation = new MoveAnimation();
        subject.set(pickedTransformable);
        direction.set(Direction.FORWARD);
        amount.set(new Double(1.0D));
        isScaledBySize.set(Boolean.TRUE);
        
        MoveAnimation undoAnimation = new MoveAnimation();
        subject.set(pickedTransformable);
        direction.set(Direction.FORWARD);
        amount.set(new Double(-1.0D));
        isScaledBySize.set(Boolean.TRUE);
        
        Property[] affectedProperties = {
          pickedTransformable.localTransformation };
        

        AuthoringTool.getHack().performOneShot(moveAnimation, undoAnimation, affectedProperties);
      }
      undoRedoStack.stopCompound();
    }
  }
  
  public void mouseDragged(MouseEvent ev, int dx, int dy) {
    if (isButton1(ev)) {
      super.mouseDragged(ev, dx, dy);
      hasBeenDragged = true;
    }
  }
}
