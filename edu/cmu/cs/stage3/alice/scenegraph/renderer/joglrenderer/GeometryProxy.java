package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;







abstract class GeometryProxy
  extends ElementProxy
{
  private boolean m_isGeometryChanged;
  






  GeometryProxy() {}
  





  protected boolean isGeometryChanged()
  {
    return m_isGeometryChanged;
  }
  
  protected void setIsGeometryChanged(boolean isGeometryChanged) { m_isGeometryChanged = isGeometryChanged; }
  
  public abstract void render(RenderContext paramRenderContext);
  
  public abstract void pick(PickContext paramPickContext, boolean paramBoolean);
}
