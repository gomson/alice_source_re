package edu.cmu.cs.stage3.awt;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;























public class DynamicFlowLayout
  extends FlowLayout
{
  private Dimension lastPreferredSize;
  private Component anchorComponent;
  private Component ownerComponent;
  private int anchorConstant = 0;
  private Class anchorClass;
  
  public DynamicFlowLayout(int align, Component anchor, Class anchorClass) {
    this(align, anchor, anchorClass, 0);
  }
  
  public DynamicFlowLayout(int align, Component anchor, Class anchorClass, int anchorConstant) {
    super(align);
    anchorComponent = anchor;
    this.anchorClass = anchorClass;
    this.anchorConstant = anchorConstant;
  }
  
  public void layoutContainer(Container target)
  {
    synchronized (target.getTreeLock()) {
      Insets insets = target.getInsets();
      int hgap = getHgap();
      int vgap = getVgap();
      if (lastPreferredSize == null) {
        lastPreferredSize = preferredLayoutSize(target);
      }
      int maxwidth = lastPreferredSize.width;
      int nmembers = target.getComponentCount();
      int x = 0;int y = top + vgap;
      int rowh = 0;int start = 0;
      
      boolean ltr = target.getComponentOrientation().isLeftToRight();
      
      for (int i = 0; i < nmembers; i++) {
        Component m = target.getComponent(i);
        if (m.isVisible()) {
          Dimension d = m.getPreferredSize();
          m.setSize(width, height);
          if ((x == 0) || (x + width <= maxwidth)) {
            if (x > 0) {
              x += hgap;
            }
            x += width;
            rowh = Math.max(rowh, height);
          } else {
            moveComponents(target, left + hgap, y, maxwidth - x, rowh, start, i, ltr);
            x = width;
            y += vgap + rowh;
            rowh = height;
            start = i;
          }
        }
      }
      moveComponents(target, left + hgap, y, maxwidth - x, rowh, start, nmembers, ltr);
    }
  }
  
  private void moveComponents(Container target, int x, int y, int width, int height, int rowStart, int rowEnd, boolean ltr)
  {
    synchronized (target.getTreeLock()) {
      switch (getAlignment()) {
      case 0: 
        x += (ltr ? 0 : width);
        break;
      case 1: 
        x += width / 2;
        break;
      case 2: 
        x += (ltr ? width : 0);
        break;
      case 3: 
        break;
      case 4: 
        x += width;
      }
      
      for (int i = rowStart; i < rowEnd; i++) {
        Component m = target.getComponent(i);
        if (target.isVisible()) {
          if (ltr) {
            m.setLocation(x, y + (height - m.getHeight()) / 2);
          } else {
            m.setLocation(target.getWidth() - x - m.getWidth(), y + (height - m.getHeight()) / 2);
          }
          x += m.getWidth() + getHgap();
        }
      }
    }
  }
  
  public Dimension preferredLayoutSize(Container target)
  {
    Insets insets = target.getInsets();
    int hgap = getHgap();
    int vgap = getVgap();
    if (anchorComponent == null) {
      anchorComponent = getAnchor(target);
    }
    int maxwidth = 0;
    if (anchorComponent == null) {
      maxwidth = target.getWidth() - (left + right + hgap * 2);
    }
    else {
      maxwidth = anchorComponent.getWidth() - (left + right + hgap * 2) - anchorConstant;
    }
    



    int nmembers = target.getComponentCount();
    int x = 0;int y = top + vgap;
    int rowh = 0;
    
    if (maxwidth < 0) {
      maxwidth = 0;
      for (int i = 0; i < nmembers; i++) {
        Component m = target.getComponent(i);
        if (m.isVisible()) {
          Dimension d = m.getPreferredSize();
          y = Math.max(y, height);
          if ((width > 0) && (height > 0)) {
            if (maxwidth > 0) {
              maxwidth += hgap;
            }
            maxwidth += width;
          }
        }
      }
    }
    else {
      boolean ltr = target.getComponentOrientation().isLeftToRight();
      for (int i = 0; i < nmembers; i++) {
        Component m = target.getComponent(i);
        if (m.isVisible()) {
          Dimension d = m.getPreferredSize();
          




          if ((x == 0) || (x + width <= maxwidth))
          {
            if (x > 0) {
              x += hgap;
            }
            x += width;
            rowh = Math.max(rowh, height);
          }
          else
          {
            x = width;
            y += vgap + rowh;
            rowh = height;
          }
        }
      }
    }
    




    lastPreferredSize = new Dimension(maxwidth, y + rowh + vgap);
    return lastPreferredSize;
  }
  
  public Dimension minimumLayoutSize(Container target)
  {
    return preferredLayoutSize(target);
  }
  
  private Component getAnchor(Component current) {
    if ((current == null) || (anchorClass.isAssignableFrom(current.getClass()))) {
      return current;
    }
    
    return getAnchor(current.getParent());
  }
}
