package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Geometry;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener;
import edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.BoundListener;
import edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyListener;
import edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.SwingUtilities;

public abstract class AbstractRenderer implements Renderer, AbsoluteTransformationListener, BoundListener, edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenListener, HierarchyListener, edu.cmu.cs.stage3.alice.scenegraph.event.PropertyListener, ReleaseListener
{
  private boolean m_isSoftwareEmulationForced;
  
  public AbstractRenderer() {}
  
  protected abstract void dispatchAbsoluteTransformationChange(AbsoluteTransformationEvent paramAbsoluteTransformationEvent);
  
  protected abstract void dispatchBoundChange(BoundEvent paramBoundEvent);
  
  protected abstract void dispatchChildAdd(ChildrenEvent paramChildrenEvent);
  
  protected abstract void dispatchChildRemove(ChildrenEvent paramChildrenEvent);
  
  private Vector m_onscreenRenderTargets = new Vector();
  private Vector m_offscreenRenderTargets = new Vector();
  private OnscreenRenderTarget[] m_onscreenRenderTargetArray = null;
  private OffscreenRenderTarget[] m_offscreenRenderTargetArray = null;
  
  private Vector m_rendererListeners = new Vector();
  
  private Vector m_pendingAbsoluteTransformationChanges = new Vector();
  private Vector m_pendingBoundChanges = new Vector();
  private Vector m_pendingChildChanges = new Vector();
  private Vector m_pendingHeirarchyChanges = new Vector();
  private Vector m_pendingPropertyChanges = new Vector();
  private Vector m_pendingReleases = new Vector();
  

  protected abstract void dispatchHierarchyChange(HierarchyEvent paramHierarchyEvent);
  
  protected abstract void dispatchPropertyChange(PropertyEvent paramPropertyEvent);
  
  protected abstract void dispatchRelease(ReleaseEvent paramReleaseEvent);
  
  protected abstract boolean requiresHierarchyAndAbsoluteTransformationListening();
  
  protected abstract boolean requiresBoundListening();
  
  private int m_ignoreCount = 0;
  
  public void enterIgnore() { m_ignoreCount += 1; }
  
  public void leaveIgnore() {
    m_ignoreCount -= 1;
  }
  
  private boolean ignore() { return m_ignoreCount > 0; }
  
  public void markAllRenderTargetsDirty() {
    if (m_ignoreCount <= 0)
    {


      RenderTarget[] renderTargets = getOffscreenRenderTargets();
      for (int i = 0; i < renderTargets.length; i++) {
        renderTargets[i].markDirty();
      }
      renderTargets = getOnscreenRenderTargets();
      for (int i = 0; i < renderTargets.length; i++) {
        renderTargets[i].markDirty();
      }
    }
  }
  
  public void addListenersToSGElement(Element sgElement) {
    if ((sgElement instanceof Geometry)) {
      if (requiresBoundListening()) {
        ((Geometry)sgElement).addBoundListener(this);
      }
    } else if ((sgElement instanceof Component)) {
      if ((sgElement instanceof Container)) {
        ((Container)sgElement).addChildrenListener(this);
      }
      if (requiresHierarchyAndAbsoluteTransformationListening()) {
        ((Component)sgElement).addAbsoluteTransformationListener(this);
        ((Component)sgElement).addHierarchyListener(this);
      }
    }
    
    sgElement.addPropertyListener(this);
    sgElement.addReleaseListener(this);
  }
  
  public void removeListenersFromSGElement(Element sgElement) { if ((sgElement instanceof Geometry)) {
      if (requiresBoundListening()) {
        ((Geometry)sgElement).removeBoundListener(this);
      }
    } else if ((sgElement instanceof Component)) {
      if ((sgElement instanceof Container)) {
        ((Container)sgElement).removeChildrenListener(this);
      }
      if (requiresHierarchyAndAbsoluteTransformationListening()) {
        ((Component)sgElement).removeAbsoluteTransformationListener(this);
        ((Component)sgElement).removeHierarchyListener(this);
      }
    }
    sgElement.removePropertyListener(this);
    sgElement.removeReleaseListener(this);
  }
  
  public void commitAnyPendingChanges() {
    if (m_pendingAbsoluteTransformationChanges.size() > 0) {
      synchronized (m_pendingAbsoluteTransformationChanges) {
        Enumeration enum0 = m_pendingAbsoluteTransformationChanges.elements();
        while (enum0.hasMoreElements()) {
          dispatchAbsoluteTransformationChange((AbsoluteTransformationEvent)enum0.nextElement());
        }
        m_pendingAbsoluteTransformationChanges.clear();
      }
    }
    if (m_pendingBoundChanges.size() > 0) {
      synchronized (m_pendingBoundChanges) {
        Enumeration enum0 = m_pendingBoundChanges.elements();
        while (enum0.hasMoreElements()) {
          dispatchBoundChange((BoundEvent)enum0.nextElement());
        }
        m_pendingBoundChanges.clear();
      }
    }
    if (m_pendingChildChanges.size() > 0) {
      synchronized (m_pendingChildChanges) {
        Enumeration enum0 = m_pendingChildChanges.elements();
        while (enum0.hasMoreElements()) {
          ChildrenEvent e = (ChildrenEvent)enum0.nextElement();
          if (e.getID() == 1) {
            dispatchChildAdd(e);
          } else {
            dispatchChildRemove(e);
          }
        }
        m_pendingChildChanges.clear();
      }
    }
    if (m_pendingHeirarchyChanges.size() > 0) {
      synchronized (m_pendingHeirarchyChanges) {
        Enumeration enum0 = m_pendingHeirarchyChanges.elements();
        while (enum0.hasMoreElements()) {
          dispatchHierarchyChange((HierarchyEvent)enum0.nextElement());
        }
        m_pendingHeirarchyChanges.clear();
      }
    }
    if (m_pendingPropertyChanges.size() > 0) {
      synchronized (m_pendingPropertyChanges) {
        Enumeration enum0 = m_pendingPropertyChanges.elements();
        while (enum0.hasMoreElements()) {
          dispatchPropertyChange((PropertyEvent)enum0.nextElement());
        }
        m_pendingPropertyChanges.clear();
      }
    }
  }
  

  private boolean isThreadOK() { return SwingUtilities.isEventDispatchThread(); }
  
  public void absoluteTransformationChanged(AbsoluteTransformationEvent absoluteTransformationEvent) {
    if (isThreadOK()) {
      dispatchAbsoluteTransformationChange(absoluteTransformationEvent);
      markAllRenderTargetsDirty();
    } else {
      m_pendingAbsoluteTransformationChanges.addElement(absoluteTransformationEvent);
    }
  }
  
  public void boundChanged(BoundEvent boundEvent) { if (isThreadOK()) {
      dispatchBoundChange(boundEvent);
      markAllRenderTargetsDirty();
    } else {
      m_pendingBoundChanges.addElement(boundEvent);
    }
  }
  
  public void childAdded(ChildrenEvent childrenEvent) { if (isThreadOK()) {
      dispatchChildAdd(childrenEvent);
      markAllRenderTargetsDirty();
    } else {
      m_pendingChildChanges.addElement(childrenEvent);
    }
  }
  
  public void childRemoved(ChildrenEvent childrenEvent) { if (isThreadOK()) {
      dispatchChildRemove(childrenEvent);
      markAllRenderTargetsDirty();
    } else {
      m_pendingChildChanges.addElement(childrenEvent);
    }
  }
  
  public void hierarchyChanged(HierarchyEvent hierarchyEvent) { if (isThreadOK()) {
      dispatchHierarchyChange(hierarchyEvent);
      markAllRenderTargetsDirty();
    } else {
      m_pendingHeirarchyChanges.addElement(hierarchyEvent);
    }
  }
  
  public synchronized void changed(PropertyEvent propertyEvent) { if (isThreadOK()) {
      dispatchPropertyChange(propertyEvent);
      markAllRenderTargetsDirty();
    } else {
      m_pendingPropertyChanges.addElement(propertyEvent);
    }
  }
  
  public synchronized void releasing(ReleaseEvent releaseEvent) {}
  
  public synchronized void released(ReleaseEvent releaseEvent) { if (isThreadOK()) {
      dispatchRelease(releaseEvent);
      markAllRenderTargetsDirty();
    } else {
      m_pendingReleases.addElement(releaseEvent);
    }
  }
  
  protected void finalize() throws Throwable {
    release();
    super.finalize();
  }
  
  public boolean addRenderTarget(RenderTarget renderTarget) { if ((renderTarget instanceof OnscreenRenderTarget)) {
      m_onscreenRenderTargetArray = null;
      m_onscreenRenderTargets.addElement(renderTarget);
    } else if ((renderTarget instanceof OffscreenRenderTarget)) {
      m_offscreenRenderTargetArray = null;
      m_offscreenRenderTargets.addElement(renderTarget);
    }
    

    return true;
  }
  
  public boolean removeRenderTarget(RenderTarget renderTarget) { if ((renderTarget instanceof OnscreenRenderTarget)) {
      m_onscreenRenderTargetArray = null;
      return m_onscreenRenderTargets.removeElement(renderTarget); }
    if ((renderTarget instanceof OffscreenRenderTarget)) {
      m_offscreenRenderTargetArray = null;
      return m_offscreenRenderTargets.removeElement(renderTarget);
    }
    return true;
  }
  
  public OnscreenRenderTarget[] getOnscreenRenderTargets()
  {
    if (m_onscreenRenderTargetArray == null) {
      m_onscreenRenderTargetArray = new OnscreenRenderTarget[m_onscreenRenderTargets.size()];
      m_onscreenRenderTargets.copyInto(m_onscreenRenderTargetArray);
    }
    return m_onscreenRenderTargetArray;
  }
  
  public OffscreenRenderTarget[] getOffscreenRenderTargets() { if (m_offscreenRenderTargetArray == null) {
      m_offscreenRenderTargetArray = new OffscreenRenderTarget[m_offscreenRenderTargets.size()];
      m_offscreenRenderTargets.copyInto(m_offscreenRenderTargetArray);
    }
    return m_offscreenRenderTargetArray;
  }
  
  public synchronized void release() {}
  
  public boolean isSoftwareEmulationForced()
  {
    return m_isSoftwareEmulationForced;
  }
  
  public void setIsSoftwareEmulationForced(boolean isSoftwareEmulationForced) { m_isSoftwareEmulationForced = isSoftwareEmulationForced; }
}
