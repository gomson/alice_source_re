package edu.cmu.cs.stage3.alice.core.geometry;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.IntArrayProperty;





















public class IndexedTriangleArray
  extends VertexGeometry
{
  public final IntArrayProperty indices = new IntArrayProperty(this, "indices", null);
  
  public IndexedTriangleArray() { super(new edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray()); }
  
  public edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray getSceneGraphIndexedTriangleArray() {
    return (edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray)getSceneGraphGeometry();
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == indices) {
      getSceneGraphIndexedTriangleArray().setIndices((int[])value);
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  public int getIndexCount() {
    return getSceneGraphIndexedTriangleArray().getIndexCount();
  }
}
