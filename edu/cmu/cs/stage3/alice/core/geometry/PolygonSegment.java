package edu.cmu.cs.stage3.alice.core.geometry;

import edu.cmu.cs.stage3.alice.core.util.Polynomial;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.math.MathUtilities;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Vector;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;






























public class PolygonSegment
{
  private Vector points;
  private Vector normals;
  private Vertex3d[] sideVertices = null;
  private int[] indices = null;
  
  public PolygonSegment() {
    points = new Vector();
    normals = new Vector();
  }
  
  protected Shape getShape() {
    if (points.isEmpty()) return null;
    GeneralPath gp = new GeneralPath();
    gp.moveTo((float)points.firstElement()).x, (float)points.firstElement()).y);
    ListIterator li = points.listIterator(1);
    while (li.hasNext()) {
      Point2d cur = (Point2d)li.next();
      gp.lineTo((float)x, (float)y);
    }
    gp.closePath();
    return gp;
  }
  
  public boolean contains(double x, double y) {
    return getShape().contains(x, y);
  }
  
  protected void addPoint(Point2d point) {
    points.add(point);
    normals.setSize(normals.size() + 2);
    if (points.size() > 1) {
      double dx = points.lastElement()).x - points.elementAt(points.size() - 2)).x;
      double dy = points.lastElement()).y - points.elementAt(points.size() - 2)).y;
      double len = Math.sqrt(dx * dx + dy * dy);
      Vector3d a = new Vector3d(dx / len, 0.0D, dy / len);
      Vector3d b = new Vector3d(0.0D, 1.0D, 0.0D);
      Vector3d c = MathUtilities.crossProduct(a, b);
      normals.setElementAt(new Vector3f(-(float)x, -(float)z, 0.0F), (points.size() - 2) * 2);
      normals.setElementAt(new Vector3f(-(float)x, -(float)z, 0.0F), (points.size() - 1) * 2 + 1);
    }
  }
  
  protected void addQuadraticSpline(Point2d cp1, Point2d cp2, Point2d offset, int numSegs) {
    if (points.isEmpty()) { return;
    }
    normals.setSize(normals.size() + 2 * numSegs);
    
    Point2d cp0 = new Point2d(-points.lastElement()).x + x, -points.lastElement()).y + y);
    
    Point3d[] newPositions = new Point3d[numSegs + 1];
    Vector3d[] newNormals = new Vector3d[numSegs + 1];
    
    Polynomial.evaluateBezierQuadratic(cp0, cp1, cp2, 0.0D, newPositions, newNormals);
    
    normals.setElementAt(new Vector3f(newNormals[0]), (points.size() - 1) * 2);
    for (int i = 1; i <= numSegs; i++) {
      points.add(new Point2d(x - x, y - y));
      normals.setElementAt(new Vector3f(newNormals[i]), (points.size() - 1) * 2);
      normals.setElementAt(new Vector3f(newNormals[i]), (points.size() - 1) * 2 + 1);
    }
  }
  
  protected void close() {
    if (points.isEmpty()) { return;
    }
    if ((points.size() > 1) && (((Point2d)points.lastElement()).equals((Point2d)points.firstElement()))) {
      points.setSize(points.size() - 1);
      normals.setSize(normals.size() - 2);
    }
    if (points.size() >= 3)
    {
      double dx = points.firstElement()).x - points.lastElement()).x;
      double dy = points.firstElement()).y - points.lastElement()).y;
      double len = Math.sqrt(dx * dx + dy * dy);
      Vector3d a = new Vector3d(dx / len, 0.0D, dy / len);
      Vector3d b = new Vector3d(0.0D, 1.0D, 0.0D);
      Vector3d c = MathUtilities.crossProduct(a, b);
      normals.setElementAt(new Vector3f(-(float)x, -(float)z, 0.0F), 1);
      normals.setElementAt(new Vector3f(-(float)x, -(float)z, 0.0F), (points.size() - 1) * 2);
    } else {
      points.clear();
      normals.clear();
    }
  }
  
  public boolean parsePathIterator(PathIterator pi, Point2d offset, int curvature) {
    double[] coords = new double[6];
    int type = -1;
    
    while (!pi.isDone()) {
      type = pi.currentSegment(coords);
      switch (type) {
      case 0: 
        if (!points.isEmpty()) {
          close();
          return false;
        }
        addPoint(new Point2d(x - coords[0], y - coords[1]));
        break;
      case 1: 
        addPoint(new Point2d(x - coords[0], y - coords[1]));
        break;
      case 2: 
        addQuadraticSpline(new Point2d(coords[0], coords[1]), new Point2d(coords[2], coords[3]), offset, curvature);
        break;
      case 3: 
        addPoint(new Point2d(x - coords[0], y - coords[1]));
        addPoint(new Point2d(x - coords[2], y - coords[3]));
        addPoint(new Point2d(x - coords[4], y - coords[5]));
        break;
      case 4: 
        close();
        return true;
      }
      pi.next();
    }
    close();
    return true;
  }
  
  public boolean isNull() {
    return points.isEmpty();
  }
  
  public Vector points() {
    return points;
  }
  
  public void reverse() {
    Collections.reverse(points);
    Collections.reverse(normals);
  }
  
  public void genSideStrips(double extz) {
    sideVertices = null;indices = null;
    if (points.isEmpty()) { return;
    }
    sideVertices = new Vertex3d[points.size() * 4];
    indices = new int[points.size() * 6];
    
    ListIterator li = points.listIterator();
    for (int i = 0; li.hasNext(); i++) {
      Point2d point = (Point2d)li.next();
      
      Point3d pos = new Point3d(x, y, -extz / 2.0D);
      sideVertices[(i * 2)] = new Vertex3d(pos, new Vector3d((Vector3f)normals.elementAt(i * 2)), null, null, new TexCoord2f());
      sideVertices[(i * 2 + 1)] = new Vertex3d(pos, new Vector3d((Vector3f)normals.elementAt(i * 2 + 1)), null, null, new TexCoord2f());
      pos = new Point3d(x, y, extz / 2.0D);
      sideVertices[(points.size() * 2 + i * 2)] = new Vertex3d(pos, new Vector3d((Vector3f)normals.elementAt(i * 2)), null, null, new TexCoord2f());
      sideVertices[(points.size() * 2 + i * 2 + 1)] = new Vertex3d(pos, new Vector3d((Vector3f)normals.elementAt(i * 2 + 1)), null, null, new TexCoord2f());
    }
    
    for (int i = 0; i < points.size() - 1; i++)
    {
      indices[(i * 6)] = (2 * i);
      indices[(i * 6 + 1)] = (2 * i + 3);
      indices[(i * 6 + 2)] = (2 * i + 3 + points.size() * 2);
      indices[(i * 6 + 3)] = (2 * i);
      indices[(i * 6 + 4)] = (2 * i + 3 + points.size() * 2);
      indices[(i * 6 + 5)] = (2 * i + points.size() * 2);
    }
    








    indices[((points.size() - 1) * 6)] = (2 * (points.size() - 1));
    indices[((points.size() - 1) * 6 + 1)] = 1;
    indices[((points.size() - 1) * 6 + 2)] = (1 + points.size() * 2);
    indices[((points.size() - 1) * 6 + 3)] = (2 * (points.size() - 1));
    indices[((points.size() - 1) * 6 + 4)] = (1 + points.size() * 2);
    indices[((points.size() - 1) * 6 + 5)] = (2 * (points.size() - 1) + points.size() * 2);
  }
  










  public Vertex3d[] getSideVertices()
  {
    return sideVertices;
  }
  
  public int[] getIndices() {
    return indices;
  }
}
