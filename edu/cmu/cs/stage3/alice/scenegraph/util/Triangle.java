package edu.cmu.cs.stage3.alice.scenegraph.util;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;


































public class Triangle
{
  public Point2d[] vertices = new Point2d[3];
  
  public Triangle(Point2d a, Point2d b, Point2d c) {
    vertices[0] = a;
    vertices[1] = b;
    vertices[2] = c;
  }
  

  private static Point2d[] sorted = new Point2d[3];
  

  public static double signedArea(Point2d vertex0, Point2d vertex1, Point2d vertex2)
  {
    sorted[0] = vertex0;
    sorted[1] = vertex1;
    sorted[2] = vertex2;
    
    int sign = 1;
    
    if (Triangulator.pointCompare(sorted[0], sorted[1]) > 0) {
      sign = -sign;
      Point2d temp = sorted[0];
      sorted[0] = sorted[1];
      sorted[1] = temp;
    }
    if (Triangulator.pointCompare(sorted[1], sorted[2]) > 0) {
      sign = -sign;
      Point2d temp = sorted[1];
      sorted[1] = sorted[2];
      sorted[2] = temp;
    }
    if (Triangulator.pointCompare(sorted[0], sorted[1]) > 0) {
      sign = -sign;
      Point2d temp = sorted[0];
      sorted[0] = sorted[1];
      sorted[1] = temp;
    }
    
    return sign * ((sorted0x - sorted1x) * (sorted1y - sorted2y) + (sorted1y - sorted0y) * (sorted1x - sorted2x));
  }
  

  public static int orientation(Point2d vertex0, Point2d vertex1, Point2d vertex2)
  {
    double sa = signedArea(vertex0, vertex1, vertex2);
    
    if (sa < 0.0D) return -1;
    if (sa > 0.0D) return 1;
    return 0;
  }
  
  private static Vector2d[] sides = new Vector2d[2];
  
  public static int convex(Point2d vertex0, Point2d vertex1, Point2d vertex2) {
    if (Triangulator.pointCompare(vertex0, vertex1) == 0)
      return 1;
    if (Triangulator.pointCompare(vertex1, vertex2) == 0) {
      return -1;
    }
    int o = orientation(vertex0, vertex1, vertex2);
    if (o != 0) {
      return o;
    }
    sides[0] = new Vector2d(vertex0);
    sides[0].sub(vertex1);
    sides[1] = new Vector2d(vertex2);
    sides[1].sub(vertex1);
    
    if (sides[0].dot(sides[1]) < 0.0D) {
      return 0;
    }
    return -2;
  }
  



  public static boolean inCone(Point2d vertex0, Point2d vertex1, Point2d vertex2, Point2d check)
  {
    if (convex(vertex0, vertex1, vertex2) > 0) {
      if ((Triangulator.pointCompare(vertex0, check) != 0) && (Triangulator.pointCompare(vertex1, check) != 0)) {
        int tri = orientation(vertex0, vertex1, check);
        if (tri < 0)
          return false;
        if (tri == 0) {
          if (Triangulator.pointCompare(vertex0, vertex1) < 0) {
            if ((Triangulator.pointCompare(check, vertex0) < 0) || (Triangulator.pointCompare(check, vertex1) > 0)) {
              return false;
            }
          } else if ((Triangulator.pointCompare(check, vertex1) < 0) || (Triangulator.pointCompare(check, vertex0) > 0)) {
            return false;
          }
        }
      }
      if ((Triangulator.pointCompare(vertex2, check) != 0) && (Triangulator.pointCompare(vertex1, vertex2) != 0)) {
        int tri = orientation(vertex1, vertex2, check);
        if (tri < 0)
          return false;
        if (tri == 0) {
          if (Triangulator.pointCompare(vertex1, vertex2) < 0) {
            if ((Triangulator.pointCompare(check, vertex1) < 0) || (Triangulator.pointCompare(check, vertex2) > 0)) {
              return false;
            }
          } else if ((Triangulator.pointCompare(check, vertex2) < 0) || (Triangulator.pointCompare(check, vertex1) > 0)) {
            return false;
          }
        }
      }
    }
    else if ((orientation(vertex0, vertex1, check) <= 0) && (orientation(vertex1, vertex2, check) < 0)) {
      return false;
    }
    return true;
  }
}
