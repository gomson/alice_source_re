package edu.cmu.cs.stage3.alice.core.geometry;

import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.util.Triangle;
import edu.cmu.cs.stage3.alice.scenegraph.util.Triangulator;
import java.awt.geom.PathIterator;
import java.util.ListIterator;
import java.util.Vector;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;

































public class Polygon
{
  private Vector segments;
  private Vertex3d[] triVertices = null;
  private int[] indices = null;
  
  public Polygon() {
    segments = new Vector();
  }
  
  public boolean parsePathIterator(PathIterator pi, Point2d offset, int curvature) {
    double[] coords = new double[6];
    int type = -1;
    boolean advance = true;
    
    while (!pi.isDone()) {
      type = pi.currentSegment(coords);
      switch (type) {
      case 0: 
        if ((!segments.isEmpty()) && 
          (!((PolygonSegment)segments.firstElement()).contains(x - coords[0], y - coords[1]))) {
          return false;
        }
        PolygonSegment ps = new PolygonSegment();
        advance = ps.parsePathIterator(pi, offset, curvature);
        if (!ps.isNull()) {
          segments.add(ps);
        }
        break;
      }
      
      if ((advance) && (!pi.isDone())) {
        pi.next();
      }
    }
    return true;
  }
  
  public boolean isNull() {
    return segments.isEmpty();
  }
  
  public void triangulate(double extz) {
    triVertices = null;indices = null;
    if (segments.isEmpty()) { return;
    }
    Triangulator triangulator = new Triangulator();
    
    ListIterator li = segments.listIterator();
    while (li.hasNext()) {
      triangulator.addContour(((PolygonSegment)li.next()).points());
    }
    
    triangulator.triangulate();
    
    indices = new int[triangles.size() * 3 * 2];
    triVertices = new Vertex3d[points.size() * 2];
    
    Vector3d norm1 = new Vector3d(0.0D, 0.0D, -1.0D);
    Vector3d norm2 = new Vector3d(0.0D, 0.0D, 1.0D);
    
    li = points.listIterator();
    for (int i = 0; li.hasNext(); i++) {
      Point2d curPoint = (Point2d)li.next();
      triVertices[i] = new Vertex3d(new Point3d(x, y, -extz / 2.0D), norm1, null, null, new TexCoord2f());
      triVertices[(points.size() + i)] = new Vertex3d(new Point3d(x, y, extz / 2.0D), norm2, null, null, new TexCoord2f());
    }
    
    li = triangles.listIterator();
    for (int i = 0; li.hasNext(); i++) {
      Triangle curTri = (Triangle)li.next();
      indices[(i * 3)] = triangulator.indexOfPoint(vertices[2]);
      indices[(i * 3 + 1)] = triangulator.indexOfPoint(vertices[1]);
      indices[(i * 3 + 2)] = triangulator.indexOfPoint(vertices[0]);
      indices[(triangles.size() * 3 + i * 3)] = (points.size() + indices[(i * 3 + 2)]);
      indices[(triangles.size() * 3 + i * 3 + 1)] = (points.size() + indices[(i * 3 + 1)]);
      indices[(triangles.size() * 3 + i * 3 + 2)] = (points.size() + indices[(i * 3)]);
    }
    

    genSideStrips(extz);
    
    li = segments.listIterator();
    while (li.hasNext()) {
      PolygonSegment seg = (PolygonSegment)li.next();
      Vertex3d[] newVertices = new Vertex3d[triVertices.length + seg.getSideVertices().length];
      System.arraycopy(triVertices, 0, newVertices, 0, triVertices.length);
      int offset = triVertices.length;
      System.arraycopy(seg.getSideVertices(), 0, newVertices, triVertices.length, seg.getSideVertices().length);
      triVertices = newVertices;
      int[] newIndices = new int[indices.length + seg.getIndices().length];
      System.arraycopy(indices, 0, newIndices, 0, indices.length);
      for (int j = 0; j < seg.getIndices().length; j++) {
        newIndices[(indices.length + j)] = (seg.getIndices()[j] + offset);
      }
      indices = newIndices;
    }
  }
  
  protected void genSideStrips(double extz)
  {
    if (segments.isEmpty()) return;
    ((PolygonSegment)segments.firstElement()).genSideStrips(extz);
    ListIterator li = segments.listIterator();
    while (li.hasNext())
      ((PolygonSegment)li.next()).genSideStrips(extz);
  }
  
  public Vertex3d[] getVertices() {
    return triVertices;
  }
  
  public int[] getIndices() {
    return indices;
  }
}
