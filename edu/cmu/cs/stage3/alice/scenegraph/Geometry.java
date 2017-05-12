package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.BoundListener;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Sphere;
import java.util.Enumeration;
import java.util.Vector;
import javax.vecmath.Matrix4d;
















public abstract class Geometry
  extends Element
{
  public Geometry() {}
  
  public abstract void transform(Matrix4d paramMatrix4d);
  
  protected abstract void updateBoundingBox();
  
  protected abstract void updateBoundingSphere();
  
  protected Box m_boundingBox = null;
  protected Sphere m_boundingSphere = null;
  
  public Box getBoundingBox() {
    if (m_boundingBox == null) {
      updateBoundingBox();
    }
    if (m_boundingBox != null) {
      return (Box)m_boundingBox.clone();
    }
    return null;
  }
  
  public Sphere getBoundingSphere() {
    if (m_boundingSphere == null) {
      updateBoundingSphere();
    }
    if (m_boundingSphere != null) {
      return (Sphere)m_boundingSphere.clone();
    }
    return null;
  }
  

  private Vector m_boundListeners = new Vector();
  
  public void addBoundListener(BoundListener boundListener) { m_boundListeners.addElement(boundListener); }
  

  public void removeBoundListener(BoundListener boundListener) { m_boundListeners.removeElement(boundListener); }
  
  public BoundListener[] getBoundListeners() {
    BoundListener[] array = new BoundListener[m_boundListeners.size()];
    m_boundListeners.copyInto(array);
    return array;
  }
  
  private void onBoundsChange(BoundEvent boundEvent) { Enumeration enum0 = m_boundListeners.elements();
    while (enum0.hasMoreElements())
      ((BoundListener)enum0.nextElement()).boundChanged(boundEvent);
  }
  
  protected void onBoundsChange() {
    m_boundingBox = null;
    m_boundingSphere = null;
    onBoundsChange(new BoundEvent(this));
  }
}
