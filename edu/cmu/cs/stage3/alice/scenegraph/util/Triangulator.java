package edu.cmu.cs.stage3.alice.scenegraph.util;

import java.io.PrintStream;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Vector;
import javax.vecmath.Point2d;






























public class Triangulator
{
  private Vector contours;
  public Vector points;
  public Vector triangles;
  public static PointComparator pc = new PointComparator();
  
  public Triangulator() {
    triangles = new Vector();
    contours = new Vector();
    points = new Vector();
  }
  


  private Vector linkedListToVector(PointNode head)
  {
    Vector returnVal = new Vector();
    
    PointNode cur = head;
    do
    {
      returnVal.add(cur);
      cur = next;
    } while ((cur != head) && (cur != null));
    return returnVal;
  }
  
  private void reverseContour(PointNode first) {
    PointNode cur = next;
    next = prev;
    prev = cur;
    
    while ((cur != first) && (cur != null)) {
      PointNode temp = next;
      next = prev;
      prev = temp;
      cur = temp;
    }
    contours.setElementAt(next, contours.indexOf(first));
  }
  
  public int indexOfPoint(Point2d tofind)
  {
    ListIterator li = points.listIterator();
    for (int i = 0; li.hasNext(); i++)
      if (pointCompare((Point2d)li.next(), tofind) == 0)
        return i;
    return -1;
  }
  


  public static int pointCompare(Point2d p1, Point2d p2)
  {
    if (x < x) return -1;
    if (x > x) return 1;
    if (y < y) return -1;
    if (y > y) return 1;
    return 0;
  }
  
  private boolean intersectsContour(PointNode head, SegmentBBox seg) {
    PointNode cur = head;
    do
    {
      if (seg.segmentOverlaps(data, next.data))
        return true;
      cur = next;
    } while ((cur != head) && (cur != null));
    return false;
  }
  
  private double polygonArea(PointNode head) {
    PointNode cur = next;
    double area = 0.0D;
    while ((next != head) && (cur != null)) {
      area += Triangle.signedArea(data, data, next.data);
      cur = next;
    }
    return area;
  }
  


  public void addContour(Point2d[] contour)
  {
    if (contour.length < 1) { return;
    }
    int curpoint = points.size();
    
    PointNode first = new PointNode(contour[0]);
    next = first;
    prev = first;
    points.add(contour[0]);
    
    for (int i = 1; i < contour.length; i++) {
      PointNode newPoint = new PointNode(contour[i]);
      prev.insertAfter(newPoint);
      points.add(contour[i]);
    }
    
    contours.add(first);
  }
  
  public void addContour(Vector contour) {
    if (contour.isEmpty()) { return;
    }
    int curpoint = points.size();
    
    PointNode first = new PointNode((Point2d)contour.firstElement());
    next = first;
    prev = first;
    points.add(contour.firstElement());
    
    ListIterator li = contour.listIterator(1);
    while (li.hasNext()) {
      Point2d cur = (Point2d)li.next();
      PointNode newPoint = new PointNode(cur);
      prev.insertAfter(newPoint);
      points.add(cur);
    }
    
    contours.add(first);
  }
  



  public void debug(String str)
  {
    System.out.println("-------------------------------------");
    System.out.println(str);
    System.out.println("-------------------------------------");
    System.out.println("Points");
    for (int i = 0; i < points.size(); i++) {
      System.out.println(String.valueOf(i) + ": (" + String.valueOf(points.elementAt(i)).x) + "," + String.valueOf(points.elementAt(i)).y) + ")");
    }
    System.out.println();
    System.out.println("Contours");
    for (int i = 0; i < contours.size(); i++) {
      System.out.print("Contour " + String.valueOf(i) + ": ");
      PointNode cur = (PointNode)contours.elementAt(i);
      do {
        System.out.print(String.valueOf(indexOfPoint(data)) + ",");
        cur = next;
      } while ((cur != contours.elementAt(i)) && (cur != null));
      System.out.println();
    }
  }
  
  public void debug2(String str) {
    System.out.println("-------------------------------------");
    System.out.println(str);
    System.out.println("-------------------------------------");
    System.out.println("Points");
    for (int i = 0; i < points.size(); i++) {
      System.out.println(String.valueOf(i) + ": (" + String.valueOf(points.elementAt(i)).x) + "," + String.valueOf(points.elementAt(i)).y) + ")");
    }
    System.out.println();
    System.out.println("Contours");
    for (int i = 0; i < contours.size(); i++) {
      System.out.println("Contour " + String.valueOf(i));
      PointNode cur = (PointNode)contours.elementAt(i);
      do {
        System.out.println(String.valueOf(indexOfPoint(data)) + ": " + String.valueOf(isEar(cur)) + "," + String.valueOf(cur.convex()));
        cur = next;
      } while ((cur != contours.elementAt(i)) && (cur != null));
      System.out.println();
    }
  }
  



  public Vector triangulate()
  {
    sortData();
    
    removeDuplicates();
    adjustOrientations();
    
    buildBridges();
    

    triangles.clear();
    
    boolean changed = true;
    ListIterator li; for (; changed; 
        




        li.hasNext())
    {
      changed = false;
      
      Vector ears = determineEars();
      
      li = ears.listIterator();
      continue;
      if (clipEar((PointNode)li.next())) {
        changed = true;
      }
    }
    
    if (linkedListToVector((PointNode)contours.firstElement()).size() == 3) {
      triangles.add(((PointNode)contours.firstElement()).triangle());
    }
    


    return triangles;
  }
  


  private void sortData()
  {
    if ((points.isEmpty()) || (contours.isEmpty())) { return;
    }
    
    Collections.sort(points, pc);
    

    ListIterator li = contours.listIterator();
    for (int i = 0; li.hasNext(); i++)
    {
      PointNode first = (PointNode)li.next();
      PointNode left = first;
      
      PointNode cur = next;
      
      while ((cur != first) && (cur != null)) {
        if (cur.compareTo(left) < 0)
          left = cur;
        cur = next;
      }
      contours.setElementAt(left, i);
    }
    

    Collections.sort(contours);
  }
  
  private void removeDuplicates() {
    Point2d prevPoint = (Point2d)points.firstElement();
    ListIterator li2 = points.listIterator(1);
    while (li2.hasNext()) {
      Point2d curPoint = (Point2d)li2.next();
      boolean makeChange = false;
      while (pointCompare(curPoint, prevPoint) == 0) {
        li2.remove();
        makeChange = true;
        if (!li2.hasNext())
          break;
        curPoint = (Point2d)li2.next();
      }
      if (makeChange) {
        ListIterator li = contours.listIterator();
        while (li.hasNext()) {
          PointNode first = (PointNode)li.next();
          PointNode cur = first;
          do
          {
            if (cur.compareTo(prevPoint) == 0) {
              data = prevPoint;
            }
            cur = next;
          } while ((cur != first) && (cur != null));
        }
      } else {
        prevPoint = curPoint;
      }
    }
  }
  
  private void adjustOrientations() {
    if (polygonArea((PointNode)contours.firstElement()) < 0.0D)
      reverseContour((PointNode)contours.firstElement());
    ListIterator li = contours.listIterator(1);
    while (li.hasNext()) {
      PointNode cur = (PointNode)li.next();
      if (polygonArea(cur) > 0.0D)
        reverseContour(cur);
    }
  }
  
  private void buildBridges() {
    DistanceComparator dc = new DistanceComparator();
    
    ListIterator li = contours.listIterator();
    while (li.hasNext()) {
      PointNode cur = (PointNode)li.next();
      
      Vector outer = linkedListToVector((PointNode)contours.firstElement());
      start = data;
      Collections.sort(outer, dc);
      
      ListIterator li2 = outer.listIterator();
      while (li2.hasNext()) {
        PointNode outerPoint = (PointNode)li2.next();
        if (outerPoint.compareTo(cur) == 0) {
          next.prev = outerPoint;
          next.prev = cur;
          PointNode temp = next;
          next = next;
          next = temp;
          break; }
        if ((outerPoint.inCone(data)) && (!intersectsContour((PointNode)contours.firstElement(), new SegmentBBox(data, data)))) {
          PointNode n1 = new PointNode(data);
          PointNode n2 = new PointNode(data);
          
          next = n2;
          prev = prev;
          next = next;
          prev = n1;
          prev.next = n1;
          next.prev = n2;
          next = cur;
          prev = outerPoint;
          break;
        }
      }
    }
    PointNode first = (PointNode)contours.firstElement();
    contours = new Vector(1);
    contours.add(first);
  }
  

  private boolean isEar(PointNode ear)
  {
    return (ear.convex() > 0) && 
      (!intersectsContour((PointNode)contours.firstElement(), new SegmentBBox(prev.data, next.data))) && 
      (next.inCone(prev.data)) && (prev.inCone(next.data));
  }
  
  private Vector determineEars() {
    Vector ears = new Vector();
    PointNode head = (PointNode)contours.firstElement();
    PointNode cur = head;
    do
    {
      if (isEar(cur)) {
        ears.add(cur);
      }
      
      cur = next;
    } while ((cur != head) && (cur != null));
    
    return ears;
  }
  
  private boolean clipEar(PointNode ear) {
    if (!isEar(ear))
      return false;
    triangles.add(ear.triangle());
    
    prev.next = next;
    next.prev = prev;
    if (contours.firstElement() == ear) {
      contours.setElementAt(next, 0);
    }
    return true;
  }
}
