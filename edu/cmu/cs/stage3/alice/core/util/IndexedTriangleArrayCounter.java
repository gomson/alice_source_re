package edu.cmu.cs.stage3.alice.core.util;

import edu.cmu.cs.stage3.alice.core.Geometry;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.GeometryProperty;
import edu.cmu.cs.stage3.alice.core.property.IntArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.VertexArrayProperty;
import edu.cmu.cs.stage3.util.VisitListener;











public class IndexedTriangleArrayCounter
  implements VisitListener
{
  public IndexedTriangleArrayCounter() {}
  
  private int m_indexedTriangleArrayCount = 0;
  private int m_shownIndexedTriangleArrayCount = 0;
  private int m_vertexCount = 0;
  private int m_shownVertexCount = 0;
  private int m_indexCount = 0;
  private int m_shownIndexCount = 0;
  
  public void visited(Object o) { if ((o instanceof IndexedTriangleArray)) {
      IndexedTriangleArray ita = (IndexedTriangleArray)o;
      m_indexedTriangleArrayCount += 1;
      m_vertexCount += vertices.size();
      m_indexCount += indices.size();
    } else if ((o instanceof Model)) {
      Model model = (Model)o;
      if (isShowing.booleanValue()) {
        Geometry geometry = geometry.getGeometryValue();
        if ((geometry instanceof IndexedTriangleArray)) {
          IndexedTriangleArray ita = (IndexedTriangleArray)geometry;
          m_shownIndexedTriangleArrayCount += 1;
          m_shownVertexCount += vertices.size();
          m_shownIndexCount += indices.size();
        }
      }
    }
  }
  
  public int getIndexedTriangleArrayCount() { return m_indexedTriangleArrayCount; }
  
  public int getShownIndexedTriangleArrayCount() {
    return m_shownIndexedTriangleArrayCount;
  }
  
  public int getVertexCount() { return m_vertexCount; }
  
  public int getShownVertexCount() {
    return m_shownVertexCount;
  }
  
  public int getIndexCount() { return m_indexCount; }
  
  public int getShownIndexCount() {
    return m_shownIndexCount;
  }
}
