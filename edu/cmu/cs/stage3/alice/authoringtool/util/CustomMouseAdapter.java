package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;




















public class CustomMouseAdapter
  extends MouseAdapter
{
  public CustomMouseAdapter() {}
  
  protected double clickDistMargin = 5.0D;
  protected long clickTimeMargin = 300L;
  protected long multipleClickTimeMargin = 600L;
  
  protected Point pressPoint = new Point();
  protected long pressTime = 0L;
  protected int clickCount = 0;
  
  public double getClickDistanceMargin() {
    return clickDistMargin;
  }
  
  public void setClickDistanceMargin(double dist) {
    clickDistMargin = dist;
  }
  
  public long getClickTimeMargin() {
    return clickTimeMargin;
  }
  
  public void setClickTimeMargin(long time) {
    clickTimeMargin = time;
  }
  
  public long getMultipleClickTimeMargin() {
    return multipleClickTimeMargin;
  }
  
  public void setMultipleClickTimeMargin(long time) {
    multipleClickTimeMargin = time;
  }
  
  public void mousePressed(MouseEvent ev) {
    long dt = System.currentTimeMillis() - pressTime;
    if (dt > multipleClickTimeMargin) {
      clickCount = 0;
    }
    
    pressPoint.setLocation(ev.getPoint());
    pressTime = System.currentTimeMillis();
    if ((ev.isPopupTrigger()) || (ev.getButton() == 3)) {
      popupResponse(ev);
    } else {
      mouseDownResponse(ev);
    }
  }
  
  public void mouseReleased(MouseEvent ev) {
    if ((ev.isPopupTrigger()) || (ev.getButton() == 3)) {
      popupResponse(ev);
    } else {
      mouseUpResponse(ev);
    }
    
    double dist = pressPoint.distance(ev.getPoint());
    long dt = System.currentTimeMillis() - pressTime;
    if ((dist < clickDistMargin) && (dt < clickTimeMargin)) {
      clickCount += 1;
      if (clickCount == 1) {
        singleClickResponse(ev);
      } else if (clickCount == 2) {
        doubleClickResponse(ev);
      } else if (clickCount == 3) {
        tripleClickResponse(ev);
      }
    }
  }
  
  protected void singleClickResponse(MouseEvent ev) {}
  
  protected void doubleClickResponse(MouseEvent ev) {}
  
  protected void tripleClickResponse(MouseEvent ev) {}
  
  protected void mouseUpResponse(MouseEvent ev) {}
  
  protected void mouseDownResponse(MouseEvent ev) {}
  
  protected void popupResponse(MouseEvent ev) {}
}
