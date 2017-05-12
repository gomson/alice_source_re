package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.scenegraph.Property;














public abstract class IndexedTriangleArrayProxy
  extends VertexGeometryProxy
{
  public IndexedTriangleArrayProxy() {}
  
  protected abstract void onIndicesChange(int[] paramArrayOfInt);
  
  protected abstract void onIndexLowerBoundChange(int paramInt);
  
  protected abstract void onIndexUpperBoundChange(int paramInt);
  
  protected void changed(Property property, Object value)
  {
    if (property == IndexedTriangleArray.INDICES_PROPERTY) {
      onIndicesChange((int[])value);
    } else if (property == IndexedTriangleArray.INDEX_LOWER_BOUND_PROPERTY) {
      onIndexLowerBoundChange(((Integer)value).intValue());
    } else if (property == IndexedTriangleArray.INDEX_UPPER_BOUND_PROPERTY) {
      onIndexUpperBoundChange(((Integer)value).intValue());
    } else {
      super.changed(property, value);
    }
  }
}
