package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;


















public class RenderTargetMultiManipulator
  extends RenderTargetPickManipulator
{
  protected RenderTargetManipulatorMode mode;
  
  public RenderTargetMultiManipulator(OnscreenRenderTarget renderTarget)
  {
    super(renderTarget);
    setPopupEnabled(true);
  }
  
  public RenderTargetManipulatorMode getMode() {
    return mode;
  }
  
  public void setMode(RenderTargetManipulatorMode mode) {
    this.mode = mode;
    
    if (mode != null) {
      setHideCursorOnDrag(mode.hideCursorOnDrag());
      if (renderTarget.getAWTComponent() != null) {
        if (mode.getPreferredCursor() != null) {
          renderTarget.getAWTComponent().setCursor(mode.getPreferredCursor());
        } else {
          renderTarget.getAWTComponent().setCursor(Cursor.getDefaultCursor());
        }
      }
    }
  }
  
  public void mousePressed(MouseEvent ev) {
    if (enabled) {
      super.mousePressed(ev);
      
      if (mode != null) {
        if ((mode.requiresPickedObject()) && (ePickedTransformable != null) && (!ePickedTransformable.doEventsStopAscending()) && (ascendTreeEnabled)) {
          abortAction();
        } else if ((mode.requiresPickedObject()) && (ePickedTransformable == null)) {
          abortAction();
        } else {
          mode.setRenderTarget(renderTarget);
          mode.mousePressed(ev, ePickedTransformable, pickInfo);
        }
      } else {
        abortAction();
      }
    }
  }
  
  public void mouseReleased(MouseEvent ev) {
    if ((enabled) && (!isActionAborted())) {
      mode.setRenderTarget(renderTarget);
      mode.mouseReleased(ev);
    }
    super.mouseReleased(ev);
  }
  
  public void mouseDragged(MouseEvent ev) {
    if ((enabled) && (!isActionAborted())) {
      super.mouseDragged(ev);
      if (mouseIsDown) {
        mode.setRenderTarget(renderTarget);
        mode.mouseDragged(ev, dx, dy);
      }
    }
  }
}
