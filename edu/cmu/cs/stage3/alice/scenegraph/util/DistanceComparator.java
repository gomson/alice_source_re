package edu.cmu.cs.stage3.alice.scenegraph.util;

import java.util.Comparator;
import javax.vecmath.Point2d;






























public class DistanceComparator
  implements Comparator
{
  public Point2d start = null;
  
  public DistanceComparator() {}
  
  public DistanceComparator(Point2d point)
  {
    start = point;
  }
  
  public int compare(Object o1, Object o2) throws ClassCastException
  {
    Point2d p1;
    if ((o1 instanceof Point2d)) {
      p1 = (Point2d)o1; } else { Point2d p1;
      if ((o1 instanceof PointNode)) {
        p1 = data;
      } else
        throw new ClassCastException(); }
    Point2d p1; Point2d p2; if ((o2 instanceof Point2d)) {
      p2 = (Point2d)o2; } else { Point2d p2;
      if ((o2 instanceof PointNode)) {
        p2 = data;
      } else
        throw new ClassCastException(); }
    Point2d p2;
    double a = start.distanceL1(p1);
    double b = start.distanceL1(p2);
    
    if (a < b) return -1;
    if (a > b) return 1;
    return 0;
  }
}
