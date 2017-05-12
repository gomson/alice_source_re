package edu.cmu.cs.stage3.alice.core.geometry;

import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import java.awt.geom.PathIterator;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

































public class PolygonGroup
{
  private Polygon[] polygons = null;
  
  private Vertex3d[] vertices = null;
  private int[] indices = null;
  
  public PolygonGroup() {}
  
  protected void addPolygon(Polygon poly)
  {
    if (polygons == null) {
      polygons = new Polygon[1];
    } else {
      Polygon[] temp = new Polygon[polygons.length + 1];
      System.arraycopy(polygons, 0, temp, 0, polygons.length);
      polygons = temp;
    }
    polygons[(polygons.length - 1)] = poly;
  }
  
  public void parsePathIterator(PathIterator pi, Point2d offset, int curvature) {
    double[] coords = new double[6];
    int type = -1;
    boolean advance = true;
    
    while (!pi.isDone()) {
      type = pi.currentSegment(coords);
      switch (type) {
      case 0: 
        Polygon p = new Polygon();
        advance = p.parsePathIterator(pi, offset, curvature);
        if (!p.isNull()) { addPolygon(p);
        }
        break;
      }
      if ((advance) && (!pi.isDone()))
        pi.next();
    }
  }
  
  public void triangulate(double extz) {
    vertices = null;indices = null;
    if (polygons == null) return;
    for (int i = 0; i < polygons.length; i++) {
      polygons[i].triangulate(extz);
    }
    vertices = new Vertex3d[polygons[0].getVertices().length];
    indices = new int[polygons[0].getIndices().length];
    System.arraycopy(polygons[0].getVertices(), 0, vertices, 0, vertices.length);
    System.arraycopy(polygons[0].getIndices(), 0, indices, 0, indices.length);
    if ((vertices == null) || (indices == null)) { return;
    }
    for (int i = 0; i < polygons.length; i++) {
      Vertex3d[] newVertices = new Vertex3d[vertices.length + polygons[i].getVertices().length];
      System.arraycopy(vertices, 0, newVertices, 0, vertices.length);
      int offset = vertices.length;
      System.arraycopy(polygons[i].getVertices(), 0, newVertices, vertices.length, polygons[i].getVertices().length);
      vertices = newVertices;
      int[] newIndices = new int[indices.length + polygons[i].getIndices().length];
      System.arraycopy(indices, 0, newIndices, 0, indices.length);
      for (int j = 0; j < polygons[i].getIndices().length; j++) {
        newIndices[(indices.length + j)] = (polygons[i].getIndices()[j] + offset);
      }
      indices = newIndices;
    }
  }
  
  protected void shareVertices()
  {
    if ((vertices == null) || (indices == null)) return;
    for (int i = 0; i < vertices.length; i++) {
      for (int j = i + 1; j < vertices.length; j++) {
        if ((vertices[i].position.equals(vertices[j].position)) && (vertices[i].normal.equals(vertices[j].normal))) {
          for (int k = j + 1; k < vertices.length; k++)
            vertices[(k - 1)] = vertices[k];
          Vertex3d[] newVertices = new Vertex3d[vertices.length - 1];
          System.arraycopy(vertices, 0, newVertices, 0, newVertices.length);
          vertices = newVertices;
          for (int k = 0; k < indices.length; k++) {
            if (indices[k] == j) {
              indices[k] = i;
            } else if (indices[k] > j)
              indices[k] -= 1;
          }
        }
      }
    }
  }
  
  public Vertex3d[] getVertices() {
    return vertices;
  }
  
  public int[] getIndices() {
    return indices;
  }
}
