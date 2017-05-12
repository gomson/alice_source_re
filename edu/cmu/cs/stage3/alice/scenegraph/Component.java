package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener;
import edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyListener;
import edu.cmu.cs.stage3.lang.Messages;
import java.util.Enumeration;
import java.util.Vector;
import javax.vecmath.Matrix4d;




















public abstract class Component
  extends Element
{
  public Component() {}
  
  public static final Property PARENT_PROPERTY = new Property(Component.class, "PARENT");
  private Container m_parent = null;
  private Vector m_absoluteTransformationListeners = new Vector();
  private Vector m_hierarchyListeners = new Vector();
  
  protected void releasePass1() {
    if (m_parent != null) {
      warnln(Messages.getString("WARNING__released_component_") + this + " " + Messages.getString("still_has_parent_") + m_parent + ".");
      setParent(null);
    }
    super.releasePass1();
  }
  
  protected void releasePass3()
  {
    Enumeration enum0 = m_absoluteTransformationListeners.elements();
    while (enum0.hasMoreElements()) {
      AbsoluteTransformationListener absoluteTransformationListener = (AbsoluteTransformationListener)enum0.nextElement();
      warnln(Messages.getString("WARNING__released_component_") + this + " " + Messages.getString("still_has_absoluteTransformationListener_") + absoluteTransformationListener + ".");
    }
    



    enum0 = m_hierarchyListeners.elements();
    while (enum0.hasMoreElements()) {
      HierarchyListener hierarchyListener = (HierarchyListener)enum0.nextElement();
      warnln(Messages.getString("WARNING__released_component_") + this + " " + Messages.getString("still_has_hierarchyListener_") + hierarchyListener + ".");
    }
  }
  

  public Container getRoot()
  {
    if (m_parent != null) {
      return m_parent.getRoot();
    }
    return null;
  }
  
  public Matrix4d getAbsoluteTransformation() {
    if (m_parent != null) {
      return m_parent.getAbsoluteTransformation();
    }
    Matrix4d m = new Matrix4d();
    m.setIdentity();
    return m;
  }
  
  public Matrix4d getInverseAbsoluteTransformation() {
    if (m_parent != null) {
      return m_parent.getInverseAbsoluteTransformation();
    }
    Matrix4d m = new Matrix4d();
    m.setIdentity();
    return m;
  }
  

  public Container getParent() { return m_parent; }
  
  public void setParent(Container parent) {
    if (m_parent != parent) {
      if (m_parent != null) {
        m_parent.onRemoveChild(this);
      }
      m_parent = parent;
      if (m_parent != null) {
        m_parent.onAddChild(this);
      }
      onPropertyChange(PARENT_PROPERTY);
      onAbsoluteTransformationChange();
      onHierarchyChange();
    }
  }
  
  public boolean isDescendantOf(Container container) {
    if (container == null) {
      return false;
    }
    if (m_parent == container) {
      return true;
    }
    if (m_parent == null) {
      return false;
    }
    return m_parent.isDescendantOf(container);
  }
  

  public void addAbsoluteTransformationListener(AbsoluteTransformationListener absoluteTransformationListener)
  {
    m_absoluteTransformationListeners.addElement(absoluteTransformationListener);
  }
  
  public void removeAbsoluteTransformationListener(AbsoluteTransformationListener absoluteTransformationListener) { m_absoluteTransformationListeners.removeElement(absoluteTransformationListener); }
  
  public AbsoluteTransformationListener[] getAbsoluteTransformationListeners() {
    AbsoluteTransformationListener[] array = new AbsoluteTransformationListener[m_absoluteTransformationListeners.size()];
    m_absoluteTransformationListeners.copyInto(array);
    return array;
  }
  
  private void onAbsoluteTransformationChange(AbsoluteTransformationEvent absoluteTransformationEvent) { Enumeration enum0 = m_absoluteTransformationListeners.elements();
    while (enum0.hasMoreElements())
      ((AbsoluteTransformationListener)enum0.nextElement()).absoluteTransformationChanged(absoluteTransformationEvent);
  }
  
  protected void onAbsoluteTransformationChange() {
    if (isReleased()) {
      warnln(Messages.getString("WARNING__absolute_transformation_change_on_already_released_") + this + ".");
    } else {
      onAbsoluteTransformationChange(new AbsoluteTransformationEvent(this));
    }
  }
  
  public void addHierarchyListener(HierarchyListener hierarchyListener) {
    m_hierarchyListeners.addElement(hierarchyListener);
  }
  
  public void removeHierarchyListener(HierarchyListener hierarchyListener) { m_hierarchyListeners.removeElement(hierarchyListener); }
  
  public HierarchyListener[] getHierarchyListeners()
  {
    HierarchyListener[] array = new HierarchyListener[m_hierarchyListeners.size()];
    m_hierarchyListeners.copyInto(array);
    return array;
  }
  
  private void onHierarchyChange(HierarchyEvent hierarchyEvent) { Enumeration enum0 = m_hierarchyListeners.elements();
    while (enum0.hasMoreElements())
      ((HierarchyListener)enum0.nextElement()).hierarchyChanged(hierarchyEvent);
  }
  
  protected void onHierarchyChange() {
    if (isReleased()) {
      warnln(Messages.getString("WARNING__scenegraph_heirarchy_change_on_already_released_") + this + ".");

    }
    else
    {
      onHierarchyChange(new HierarchyEvent(this));
    }
  }
}
