package edu.cmu.cs.stage3.alice.core.geometry;

import edu.cmu.cs.stage3.alice.core.Geometry;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.VertexArrayProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import javax.vecmath.Vector3d;


















public class VertexGeometry
  extends Geometry
{
  public final VertexArrayProperty vertices = new VertexArrayProperty(this, "vertices", null);
  
  public VertexGeometry(edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry sgVertexGeometry) { super(sgVertexGeometry); }
  
  public edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry getSceneGraphVertexGeometry() {
    return (edu.cmu.cs.stage3.alice.scenegraph.VertexGeometry)getSceneGraphGeometry();
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == vertices) {
      getSceneGraphVertexGeometry().setVertices((Vertex3d[])value);
    } else {
      super.propertyChanged(property, value);
    }
  }
  


  public int getVertexCount() { return getSceneGraphVertexGeometry().getVertexCount(); }
  
  public void setNormals(Vector3d[] normals) {
    Vertex3d[] sgVertices = getSceneGraphVertexGeometry().getVertices();
    if (sgVertices != null) {
      for (int i = 0; i < sgVertices.length; i++) {
        normal = normals[i];
      }
    }
    
    vertices.set(null);
    vertices.set(sgVertices);
  }
}
