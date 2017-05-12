package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget;
import edu.cmu.cs.stage3.awt.AWTUtilities;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public abstract class RenderTargetPickManipulator
  extends ScreenWrappingMouseListener
{
  protected edu.cmu.cs.stage3.alice.core.Transformable ePickedTransformable = null;
  protected edu.cmu.cs.stage3.alice.core.Transformable lastEPickedTransformable = null;
  protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgPickedTransformable = null;
  protected OnscreenRenderTarget renderTarget = null;
  protected HashSet objectsOfInterest = new HashSet();
  protected HashSet listeners = new HashSet();
  protected Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(0, 0), "invisible cursor");
  protected Cursor savedCursor = Cursor.getDefaultCursor();
  protected Point originalMousePoint;
  protected boolean hideCursorOnDrag = true;
  protected boolean popupEnabled = false;
  protected boolean enabled = true;
  protected boolean pickAllForOneObjectOfInterest = true;
  protected boolean ascendTreeEnabled = true;
  protected PickInfo pickInfo;
  
  public RenderTargetPickManipulator(OnscreenRenderTarget renderTarget) {
    setRenderTarget(renderTarget);
  }
  
  public void setEnabled(boolean b) {
    enabled = b;
  }
  
  public boolean isEnabled() {
    return enabled;
  }
  
  public void setPickAllForOneObjectOfInterestEnabled(boolean b) {
    pickAllForOneObjectOfInterest = b;
  }
  
  public boolean isPickAllForOneObjectOfInterestEnabled() {
    return pickAllForOneObjectOfInterest;
  }
  
  public void setRenderTarget(OnscreenRenderTarget renderTarget) {
    if (this.renderTarget != null) {
      this.renderTarget.getAWTComponent().removeMouseListener(this);
      if (popupEnabled) {
        this.renderTarget.getAWTComponent().removeMouseListener(popupMouseListener);
      }
    }
    
    this.renderTarget = renderTarget;
    if (renderTarget != null) {
      renderTarget.getAWTComponent().addMouseListener(this);
      if (popupEnabled) {
        this.renderTarget.getAWTComponent().addMouseListener(popupMouseListener);
      }
    }
  }
  
  public RenderTarget getRenderTarget() {
    return renderTarget;
  }
  
  public edu.cmu.cs.stage3.alice.core.Transformable getCorePickedTransformable() {
    return ePickedTransformable;
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.Transformable getSceneGraphPickedTransformable() {
    return sgPickedTransformable;
  }
  
  public void addRenderTargetPickManipulatorListener(RenderTargetPickManipulatorListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }
  
  public void removeRenderTargetPickManipulatorListener(RenderTargetPickManipulatorListener listener) {
    if (listener != null) {
      listeners.remove(listener);
    }
  }
  





  public boolean addObjectOfInterest(edu.cmu.cs.stage3.alice.core.Transformable trans)
  {
    return objectsOfInterest.add(trans);
  }
  





  public boolean removeObjectOfInterest(edu.cmu.cs.stage3.alice.core.Transformable trans)
  {
    return objectsOfInterest.remove(trans);
  }
  
  public void clearObjectsOfInterestList() {
    objectsOfInterest.clear();
  }
  
  public boolean getHideCursorOnDrag() {
    return hideCursorOnDrag;
  }
  
  public void setHideCursorOnDrag(boolean b) {
    hideCursorOnDrag = b;
  }
  
  public void setAscendTreeEnabled(boolean b) {
    ascendTreeEnabled = b;
  }
  
  public boolean isAscendTreeEnabled() {
    return ascendTreeEnabled;
  }
  
  public boolean isPopupEnabled() {
    return popupEnabled;
  }
  
  public synchronized void setPopupEnabled(boolean b) {
    if (renderTarget != null) {
      if ((b) && (!popupEnabled)) {
        renderTarget.getAWTComponent().addMouseListener(popupMouseListener);
      } else if ((!b) && (popupEnabled)) {
        renderTarget.getAWTComponent().removeMouseListener(popupMouseListener);
      }
    }
    popupEnabled = b;
  }
  
  public void mousePressed(MouseEvent ev) {
    if (enabled) {
      super.mousePressed(ev);
      
      firePrePick();
      if ((objectsOfInterest.size() == 1) && (pickAllForOneObjectOfInterest)) {
        ePickedTransformable = ((edu.cmu.cs.stage3.alice.core.Transformable)objectsOfInterest.iterator().next());
        sgPickedTransformable = ePickedTransformable.getSceneGraphTransformable();
      }
      else {
        if ((AuthoringTool.getHack() != null) && 
          (AuthoringTool.getHack().getUndoRedoStack() != null)) {
          AuthoringTool.getHack().getUndoRedoStack().setIsListening(false);
        }
        

        pickInfo = renderTarget.pick(ev.getX(), ev.getY(), false, true);
        
        if (pickInfo != null)
        {
          Visual[] visuals = pickInfo.getVisuals();
          



          if ((visuals != null) && (visuals.length >= 1))
          {
            ePickedTransformable = ((edu.cmu.cs.stage3.alice.core.Transformable)visuals[0].getBonus());
            if (ePickedTransformable == null) {
              sgPickedTransformable = ((edu.cmu.cs.stage3.alice.scenegraph.Transformable)visuals[0].getParent());
              ePickedTransformable = ((edu.cmu.cs.stage3.alice.core.Transformable)sgPickedTransformable.getBonus());
            } else {
              sgPickedTransformable = ePickedTransformable.getSceneGraphTransformable();
            }
            if (ascendTreeEnabled) {
              while ((ePickedTransformable != null) && ((ePickedTransformable.getParent() instanceof edu.cmu.cs.stage3.alice.core.Transformable)) && (!ePickedTransformable.doEventsStopAscending()) && (!objectsOfInterest.contains(ePickedTransformable)))
              {
                sgPickedTransformable = ((edu.cmu.cs.stage3.alice.core.Transformable)ePickedTransformable.getParent()).getSceneGraphTransformable();
                ePickedTransformable = ((edu.cmu.cs.stage3.alice.core.Transformable)sgPickedTransformable.getBonus());
              }
            }
            
            if ((!objectsOfInterest.isEmpty()) && (!objectsOfInterest.contains(ePickedTransformable))) {
              abortAction();
            }
          }
          else {
            sgPickedTransformable = null;
            ePickedTransformable = null;
          }
        }
        else {
          sgPickedTransformable = null;
          ePickedTransformable = null;
        }
      }
      firePostPick(pickInfo);
      
      originalMousePoint = ev.getPoint();
      if ((!isActionAborted()) && (hideCursorOnDrag) && (doWrap) && (!ev.getComponent().getCursor().equals(invisibleCursor))) {
        savedCursor = ev.getComponent().getCursor();
        ev.getComponent().setCursor(invisibleCursor);
      }
    }
  }
  
  public void mouseReleased(MouseEvent ev) {
    if ((!isActionAborted()) && (hideCursorOnDrag) && (doWrap)) {
      ev.getComponent().setCursor(savedCursor);
      
      Point tempPoint = ev.getPoint();
      SwingUtilities.convertPointToScreen(tempPoint, ev.getComponent());
      SwingUtilities.convertPointToScreen(originalMousePoint, ev.getComponent());
      AWTUtilities.setCursorLocation(x, originalMousePoint.y);
    }
    

    lastEPickedTransformable = ePickedTransformable;
    
    ePickedTransformable = null;
    sgPickedTransformable = null;
    pickInfo = null;
    if ((AuthoringTool.getHack() != null) && 
      (AuthoringTool.getHack().getUndoRedoStack() != null)) {
      AuthoringTool.getHack().getUndoRedoStack().setIsListening(true);
    }
    

    super.mouseReleased(ev);
  }
  
  public void abortAction() {
    component.setCursor(savedCursor);
    super.abortAction();
  }
  
  protected void firePrePick() {
    RenderTargetPickManipulatorEvent ev = new RenderTargetPickManipulatorEvent(renderTarget, null);
    for (Iterator iter = listeners.iterator(); iter.hasNext();) {
      ((RenderTargetPickManipulatorListener)iter.next()).prePick(ev);
    }
  }
  
  protected void firePostPick(PickInfo pickInfo) {
    RenderTargetPickManipulatorEvent ev = new RenderTargetPickManipulatorEvent(renderTarget, pickInfo);
    for (Iterator iter = listeners.iterator(); iter.hasNext();) {
      ((RenderTargetPickManipulatorListener)iter.next()).postPick(ev);
    }
  }
  

  protected final MouseListener popupMouseListener = new CustomMouseAdapter() {
    Runnable emptyRunnable = new Runnable() {
      public void run() {}
    };
    
    protected void popupResponse(MouseEvent e) {
      if (lastEPickedTransformable != null) {
        JPopupMenu popup = createPopup(lastEPickedTransformable);
        popup.show(e.getComponent(), e.getX(), e.getY());
        PopupMenuUtilities.ensurePopupIsOnScreen(popup);
      }
    }
    
    private JPopupMenu createPopup(Element element) {
      Vector popupStructure = ElementPopupUtilities.getDefaultStructure(element);
      return ElementPopupUtilities.makeElementPopupMenu(element, popupStructure);
    }
  };
}
