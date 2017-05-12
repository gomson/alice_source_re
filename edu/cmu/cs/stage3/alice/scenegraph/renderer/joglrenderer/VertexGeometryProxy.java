package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry;














abstract class VertexGeometryProxy
  extends GeometryProxy
{
  private Vertex3d[] m_vertices;
  
  VertexGeometryProxy() {}
  
  protected Vertex3d getVertexAt(int index)
  {
    return m_vertices[index];
  }
  
  protected int getNumVertices()
  {
    return m_vertices.length;
  }
  
  protected void changed(Property property, Object value)
  {
    if (property == VertexGeometry.VERTICES_PROPERTY) {
      m_vertices = ((Vertex3d[])value);
      setIsGeometryChanged(true);
    } else if (property != VertexGeometry.VERTEX_LOWER_BOUND_PROPERTY)
    {
      if (property != VertexGeometry.VERTEX_UPPER_BOUND_PROPERTY)
      {

        super.changed(property, value);
      }
    }
  }
}
