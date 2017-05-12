package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenListener;
import edu.cmu.cs.stage3.lang.Messages;
import java.util.Enumeration;
import java.util.Vector;





















public abstract class Container
  extends Component
{
  public Container() {}
  
  private Vector m_children = new Vector();
  private Component[] m_childArray = null;
  private Vector m_childrenListeners = new Vector();
  private ChildrenListener[] m_childrenListenerArray = null;
  
  public boolean isAncestorOf(Component component) {
    if (component == null) {
      return false;
    }
    return component.isDescendantOf(this);
  }
  

  protected void releasePass1()
  {
    Component[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      warnln(Messages.getString("WARNING__released_container_") + this + " " + Messages.getString("still_has_child_") + children[i] + ".");
      children[i].setParent(null);
    }
    super.releasePass1();
  }
  
  protected void releasePass2() {
    m_children = null;
    m_childArray = null;
    super.releasePass2();
  }
  
  protected void releasePass3() {
    Enumeration enum0 = m_childrenListeners.elements();
    while (enum0.hasMoreElements()) {
      ChildrenListener childrenListener = (ChildrenListener)enum0.nextElement();
      warnln(Messages.getString("WARNING__released_container_") + this + " " + Messages.getString("still_has_childrenListener_") + childrenListener + ".");
    }
    m_childrenListeners = null;
    m_childrenListenerArray = null;
    super.releasePass3();
  }
  
  protected void onAddChild(Component child) { if (isReleased()) {
      warnln(Messages.getString("WARNING__scenegraph_addChild_") + child + " " + Messages.getString("on_already_released_") + this + ".");
    }
    else if (child.isReleased()) {
      warnln(Messages.getString("WARNING__scenegraph_addChild_from_") + this + " " + Messages.getString("on_already_released_child_") + child + ".");
    } else {
      m_children.addElement(child);
      m_childArray = null;
      ChildrenEvent childrenEvent = new ChildrenEvent(this, 1, child);
      ChildrenListener[] childrenListeners = getChildrenListeners();
      for (int i = 0; i < childrenListeners.length; i++) {
        childrenListeners[i].childAdded(childrenEvent);
      }
    }
  }
  
  protected void onRemoveChild(Component child) {
    if (isReleased()) {
      warnln(Messages.getString("WARNING__scenegraph_removeChild_") + child + " " + Messages.getString("on_already_released_") + this + ".");
    }
    else if (child.isReleased()) {
      warnln(Messages.getString("WARNING__scenegraph_removeChild_from_") + this + " " + Messages.getString("on_already_released_child_") + child + ".");
    } else {
      m_children.removeElement(child);
      m_childArray = null;
      ChildrenEvent childrenEvent = new ChildrenEvent(this, 2, child);
      ChildrenListener[] childrenListeners = getChildrenListeners();
      for (int i = 0; i < childrenListeners.length; i++) {
        childrenListeners[i].childRemoved(childrenEvent);
      }
    }
  }
  
  public Component[] getChildren() {
    if (m_childArray == null) {
      m_childArray = new Component[m_children.size()];
      m_children.copyInto(m_childArray);
    }
    return m_childArray;
  }
  
  public int getChildCount() { return m_children.size(); }
  

  public Component getChildAt(int i) { return (Component)m_children.elementAt(i); }
  
  public void addChildrenListener(ChildrenListener childrenListener) {
    m_childrenListeners.addElement(childrenListener);
    m_childrenListenerArray = null;
  }
  
  public void removeChildrenListener(ChildrenListener childrenListener) { m_childrenListeners.removeElement(childrenListener);
    m_childrenListenerArray = null;
  }
  
  public ChildrenListener[] getChildrenListeners() { if (m_childrenListenerArray == null) {
      m_childrenListenerArray = new ChildrenListener[m_childrenListeners.size()];
      m_childrenListeners.copyInto(m_childrenListenerArray);
    }
    return m_childrenListenerArray;
  }
  
  protected void onAbsoluteTransformationChange() {
    super.onAbsoluteTransformationChange();
    Component[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      children[i].onAbsoluteTransformationChange();
    }
  }
  
  protected void onHierarchyChange() {
    super.onHierarchyChange();
    Component[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      children[i].onHierarchyChange();
    }
  }
}
