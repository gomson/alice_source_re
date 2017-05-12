package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Scene;
import java.nio.DoubleBuffer;
















abstract class ComponentProxy
  extends ElementProxy
{
  private double[] m_absolute = new double[16];
  private double[] m_inverseAbsolute = new double[16];
  private DoubleBuffer m_absoluteBuffer = DoubleBuffer.wrap(m_absolute);
  private DoubleBuffer m_inverseAbsoluteBuffer = DoubleBuffer.wrap(m_inverseAbsolute);
  
  ComponentProxy() { handleAbsoluteTransformationChange(); }
  
  protected void changed(Property property, Object value)
  {
    if (property != Component.PARENT_PROPERTY)
    {

      super.changed(property, value); }
  }
  
  public void handleAbsoluteTransformationChange() {
    m_absolute[0] = NaN.0D;
    m_inverseAbsolute[0] = NaN.0D;
  }
  
  private Component getSceneGraphComponent() { return (Component)getSceneGraphElement(); }
  
  public SceneProxy getSceneProxy()
  {
    Component sgComponent = getSceneGraphComponent();
    Container sgRoot = sgComponent.getRoot();
    if ((sgRoot instanceof Scene)) {
      return (SceneProxy)getProxyFor(sgRoot);
    }
    return null;
  }
  
  public double[] getAbsoluteTransformation()
  {
    if (Double.isNaN(m_absolute[0])) {
      copy(m_absolute, getSceneGraphComponent().getAbsoluteTransformation());
    }
    return m_absolute;
  }
  
  public double[] getInverseAbsoluteTransformation() { if (Double.isNaN(m_inverseAbsolute[0])) {
      copy(m_inverseAbsolute, getSceneGraphComponent().getInverseAbsoluteTransformation());
    }
    return m_inverseAbsolute;
  }
  
  protected DoubleBuffer getAbsoluteTransformationAsBuffer() { getAbsoluteTransformation();
    return m_absoluteBuffer;
  }
  
  protected DoubleBuffer getInverseAbsoluteTransformationAsBuffer() { getInverseAbsoluteTransformation();
    return m_inverseAbsoluteBuffer;
  }
  
  public abstract void render(RenderContext paramRenderContext);
  
  public abstract void setup(RenderContext paramRenderContext);
  
  public abstract void pick(PickContext paramPickContext, PickParameters paramPickParameters);
}
