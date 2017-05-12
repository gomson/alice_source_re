package edu.cmu.cs.stage3.alice.scenegraph.util;

import javax.vecmath.Point2d;
































public class SegmentBBox
{
  Point2d[] pointBounds = new Point2d[2];
  double[] yBounds = new double[2];
  
  Point2d[] pointBoundsB = new Point2d[2];
  double[] yBoundsB = new double[2];
  
  public SegmentBBox(Point2d a, Point2d b) {
    if (Triangulator.pointCompare(a, b) < 0) {
      pointBounds[0] = a;
      pointBounds[1] = b;
    } else {
      pointBounds[0] = b;
      pointBounds[1] = a;
    }
    if (y < y) {
      yBounds[0] = y;
      yBounds[1] = y;
    } else {
      yBounds[0] = y;
      yBounds[1] = y;
    }
  }
  
  public boolean boxOverlaps(Point2d a, Point2d b)
  {
    if (Triangulator.pointCompare(a, b) < 0) {
      pointBoundsB[0] = a;
      pointBoundsB[1] = b;
    } else {
      pointBoundsB[0] = b;
      pointBoundsB[1] = a;
    }
    if (y < y) {
      yBoundsB[0] = y;
      yBoundsB[1] = y;
    } else {
      yBoundsB[0] = y;
      yBoundsB[1] = y;
    }
    

    if (Triangulator.pointCompare(pointBounds[1], pointBoundsB[0]) < 0)
      return false;
    if (Triangulator.pointCompare(pointBounds[0], pointBoundsB[1]) > 0)
      return false;
    if (yBounds[1] < yBoundsB[0])
      return false;
    if (yBounds[0] > yBoundsB[1]) {
      return false;
    }
    return true;
  }
  
  public boolean segmentOverlaps(Point2d a, Point2d b) {
    if (!boxOverlaps(a, b)) {
      return false;
    }
    if ((Triangulator.pointCompare(pointBounds[0], pointBounds[1]) == 0) || (Triangulator.pointCompare(pointBoundsB[0], pointBoundsB[1]) == 0))
      return false;
    if ((Triangulator.pointCompare(pointBounds[0], pointBoundsB[0]) == 0) && (Triangulator.pointCompare(pointBounds[1], pointBoundsB[1]) == 0)) {
      return true;
    }
    int orient1 = Triangle.orientation(pointBounds[0], pointBounds[1], pointBoundsB[0]);
    int orient2 = Triangle.orientation(pointBounds[0], pointBounds[1], pointBoundsB[1]);
    
    if ((orient1 == orient2) && (orient1 != 0)) {
      return false;
    }
    
    if (orient1 == 0) {
      if ((Triangulator.pointCompare(pointBounds[0], pointBoundsB[0]) < 0) && (Triangulator.pointCompare(pointBoundsB[0], pointBounds[1]) < 0))
        return true;
      if ((orient2 == 0) && (Triangulator.pointCompare(pointBounds[0], pointBoundsB[1]) < 0) && (Triangulator.pointCompare(pointBoundsB[1], pointBounds[1]) < 0))
        return true;
      return false; }
    if (orient2 == 0) {
      if ((Triangulator.pointCompare(pointBounds[0], pointBoundsB[1]) < 0) && (Triangulator.pointCompare(pointBoundsB[1], pointBounds[1]) < 0))
        return true;
      return false;
    }
    
    if (Triangle.orientation(pointBoundsB[0], pointBoundsB[1], pointBounds[0]) == Triangle.orientation(pointBoundsB[0], pointBoundsB[1], pointBounds[1])) {
      return false;
    }
    return true;
  }
}
