package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

















public abstract class RenderTargetManipulatorMode
{
  protected OnscreenRenderTarget renderTarget;
  protected Cursor preferredCursor;
  
  public RenderTargetManipulatorMode() {}
  
  public OnscreenRenderTarget getRenderTarget()
  {
    return renderTarget;
  }
  
  public void setRenderTarget(OnscreenRenderTarget renderTarget) {
    this.renderTarget = renderTarget;
  }
  
  public Cursor getPreferredCursor() {
    return preferredCursor;
  }
  
  public void setPreferredCursor(Cursor preferredCursor) {
    this.preferredCursor = preferredCursor;
  }
  
  public abstract boolean requiresPickedObject();
  
  public abstract boolean hideCursorOnDrag();
  
  public abstract void mousePressed(MouseEvent paramMouseEvent, Transformable paramTransformable, PickInfo paramPickInfo);
  
  public abstract void mouseReleased(MouseEvent paramMouseEvent);
  
  public abstract void mouseDragged(MouseEvent paramMouseEvent, int paramInt1, int paramInt2);
}
