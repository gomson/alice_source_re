package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import javax.media.opengl.GL;

















class IndexedTriangleArrayProxy
  extends VertexGeometryProxy
{
  private int[] m_indices;
  
  IndexedTriangleArrayProxy() {}
  
  public void render(RenderContext context)
  {
    Integer id = context.getDisplayListID(this);
    if (id == null) {
      id = context.generateDisplayListID(this);
      setIsGeometryChanged(true);
    }
    if (isGeometryChanged()) {
      gl.glNewList(id.intValue(), 4865);
      gl.glBegin(4);
      if (m_indices != null)
        for (int i = 0; i < m_indices.length; i += 3) {
          context.renderVertex(getVertexAt(m_indices[(i + 2)]));
          context.renderVertex(getVertexAt(m_indices[(i + 1)]));
          context.renderVertex(getVertexAt(m_indices[(i + 0)]));
        }
      gl.glEnd();
      gl.glEndList();
      setIsGeometryChanged(false);
    } else {
      gl.glCallList(id.intValue());
    }
  }
  
  public void pick(PickContext context, boolean isSubElementRequired) {
    gl.glPushName(-1);
    if (isSubElementRequired) {
      gl.glBegin(4);
      for (int i = 0; i < m_indices.length; i += 3) {
        context.renderPickVertex(getVertexAt(m_indices[(i + 2)]));
        context.renderPickVertex(getVertexAt(m_indices[(i + 1)]));
        context.renderPickVertex(getVertexAt(m_indices[(i + 0)]));
      }
      gl.glEnd();
    } else {
      int id = 0;
      if (m_indices != null)
        for (int i = 0; i < m_indices.length; i += 3) {
          gl.glLoadName(id++);
          gl.glBegin(4);
          context.renderPickVertex(getVertexAt(m_indices[(i + 2)]));
          context.renderPickVertex(getVertexAt(m_indices[(i + 1)]));
          context.renderPickVertex(getVertexAt(m_indices[(i + 0)]));
          gl.glEnd();
        }
    }
    gl.glPopName();
  }
  
  protected void changed(Property property, Object value) {
    if (property == IndexedTriangleArray.INDICES_PROPERTY) {
      m_indices = ((int[])value);
      setIsGeometryChanged(true);
    } else if (property != IndexedTriangleArray.INDEX_LOWER_BOUND_PROPERTY)
    {
      if (property != IndexedTriangleArray.INDEX_UPPER_BOUND_PROPERTY)
      {

        super.changed(property, value);
      }
    }
  }
}
