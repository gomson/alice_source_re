package edu.cmu.cs.stage3.alice.scenegraph.util;

import java.util.Comparator;
import javax.vecmath.Point2d;































public class PointComparator
  implements Comparator
{
  public PointComparator() {}
  
  public int compare(Object o1, Object o2)
  {
    Point2d p1 = (Point2d)o1;
    Point2d p2 = (Point2d)o2;
    
    if (x < x) return -1;
    if (x > x) return 1;
    if (y < y) return -1;
    if (y > y) return 1;
    return 0;
  }
}
