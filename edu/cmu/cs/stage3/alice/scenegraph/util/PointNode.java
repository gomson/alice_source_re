package edu.cmu.cs.stage3.alice.scenegraph.util;

import javax.vecmath.Point2d;































public class PointNode
  implements Comparable
{
  public Point2d data = null;
  public PointNode next = null;
  public PointNode prev = null;
  
  public PointNode(Point2d p) {
    data = p;
  }
  
  public void insertAfter(PointNode toAdd) {
    next = next;
    prev = this;
    if (next != null)
      next.prev = toAdd;
    next = toAdd;
  }
  
  public Triangle triangle() {
    return new Triangle(prev.data, data, next.data);
  }
  
  public int convex() {
    return Triangle.convex(prev.data, data, next.data);
  }
  
  public boolean inCone(Point2d check) {
    return Triangle.inCone(prev.data, data, next.data, check);
  }
  
  public int compareTo(Object o) throws ClassCastException {
    if ((o instanceof PointNode))
      return Triangulator.pointCompare(data, data);
    if ((o instanceof Point2d)) {
      return Triangulator.pointCompare(data, (Point2d)o);
    }
    throw new ClassCastException();
  }
}
