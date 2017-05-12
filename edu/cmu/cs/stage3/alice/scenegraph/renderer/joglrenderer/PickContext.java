package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import java.util.Hashtable;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Point3d;

















class PickContext
  extends Context
{
  private RenderTarget m_renderTarget;
  private Hashtable m_pickNameMap = new Hashtable();
  private PickParameters m_pickParameters;
  private PickInfo m_pickInfo;
  
  public PickContext(RenderTarget renderTarget) {
    m_renderTarget = renderTarget;
  }
  
  public void display(GLAutoDrawable drawable)
  {
    super.display(drawable);
    if (m_pickParameters != null) {
      m_renderTarget.commitAnyPendingChanges();
      m_pickNameMap.clear();
      try {
        m_pickInfo = m_renderTarget.performPick(this, m_pickParameters);
      } finally {
        m_pickParameters = null;
      }
    }
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(GLAutoDrawable drawable, int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired) { m_pickParameters = new PickParameters(x, y, isSubElementRequired, isOnlyFrontMostRequired);
    







    drawable.display();
    






    return m_pickInfo;
  }
  
  public int getPickNameForVisualProxy(VisualProxy visualProxy) { int name = m_pickNameMap.size();
    m_pickNameMap.put(new Integer(name), visualProxy);
    return name;
  }
  


  public VisualProxy getPickVisualProxyForName(int name)
  {
    return (VisualProxy)m_pickNameMap.get(new Integer(name));
  }
  
  protected void renderPickVertex(Vertex3d vertex) {
    gl.glVertex3d(position.x, position.y, -position.z);
  }
}
