package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer;
import java.util.Enumeration;
import java.util.Vector;
import javax.vecmath.Matrix4d;










public abstract class ComponentProxy
  extends ElementProxy
{
  public ComponentProxy() {}
  
  protected abstract void onAbsoluteTransformationChange(Matrix4d paramMatrix4d);
  
  protected abstract void addToScene(SceneProxy paramSceneProxy);
  
  protected abstract void removeFromScene(SceneProxy paramSceneProxy);
  
  private SceneProxy m_sceneProxy = null;
  
  private static Vector s_changed = new Vector();
  
  protected boolean listenToHierarchyAndAbsoluteTransformationChanges() { return ((Renderer)getRenderer()).requiresHierarchyAndAbsoluteTransformationListening(); }
  
  static void updateAbsoluteTransformationChanges() {
    synchronized (s_changed) {
      if (s_changed.size() > 0) {
        Enumeration enum0 = s_changed.elements();
        while (enum0.hasMoreElements()) {
          ComponentProxy componentProxy = (ComponentProxy)enum0.nextElement();
          Component sgComponent = componentProxy.getSceneGraphComponent();
          if (sgComponent != null) {
            Matrix4d m = sgComponent.getAbsoluteTransformation();
            componentProxy.onAbsoluteTransformationChange(m);
          }
        }
        s_changed.removeAllElements();
      }
    }
  }
  
  private Component getSceneGraphComponent() {
    return (Component)getSceneGraphElement();
  }
  
  public void initialize(Element sgElement, AbstractProxyRenderer renderer) {
    super.initialize(sgElement, renderer);
    if (listenToHierarchyAndAbsoluteTransformationChanges()) {
      onAbsoluteTransformationChange();
      onHierarchyChange();
    }
  }
  
  public void onAbsoluteTransformationChange() { synchronized (s_changed) {
      if (!s_changed.contains(this))
      {

        s_changed.addElement(this); }
    }
  }
  
  public void onHierarchyChange() {
    ContainerProxy rootProxy = (ContainerProxy)getProxyFor(getSceneGraphComponent().getRoot());
    if (rootProxy != m_sceneProxy) {
      if (m_sceneProxy != null) {
        removeFromScene(m_sceneProxy);
      }
      if ((rootProxy instanceof SceneProxy)) {
        m_sceneProxy = ((SceneProxy)rootProxy);
      } else {
        m_sceneProxy = null;
      }
      if (m_sceneProxy != null) {
        addToScene(m_sceneProxy);
      }
    }
  }
  
  protected void changed(Property property, Object value) {
    if (property != Component.PARENT_PROPERTY)
    {

      super.changed(property, value);
    }
  }
}
