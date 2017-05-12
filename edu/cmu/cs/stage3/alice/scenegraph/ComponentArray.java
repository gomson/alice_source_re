package edu.cmu.cs.stage3.alice.scenegraph;












public class ComponentArray
  extends VertexGeometry
{
  public ComponentArray() {}
  











  public static final Property COMPONENT_PROPERTY = new Property(ComponentArray.class, "COMPONENT");
  private Component m_component = null;
  
  public Component getComponent() { return m_component; }
  
  public void setComponent(Component component) {
    if (m_component != component) {
      m_component = component;
      onPropertyChange(COMPONENT_PROPERTY);
    }
  }
}
