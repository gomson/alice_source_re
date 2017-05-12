package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;













public class MultiColumnPopupLayout
  implements LayoutManager
{
  protected Dimension screenSize;
  
  public MultiColumnPopupLayout() {}
  
  protected void updateInfo()
  {
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    screenSize.height -= 28;
  }
  
  public Dimension preferredLayoutSize(Container parent) {
    updateInfo();
    
    synchronized (parent.getTreeLock()) {
      Insets insets = parent.getInsets();
      int count = parent.getComponentCount();
      
      int numCols = 1;
      int totalWidth = 0;
      int totalHeight = 0;
      int heightSoFar = 0;
      int maxWidth = 0;
      
      for (int i = 0; i < count; i++) {
        Component comp = parent.getComponent(i);
        Dimension d = comp.getPreferredSize();
        


        maxWidth = Math.max(maxWidth, width);
        
        heightSoFar += height;
        if (heightSoFar > screenSize.height) {
          numCols++;
          totalHeight = Math.max(totalHeight, heightSoFar - height);
          heightSoFar = height;
        }
      }
      
      if (totalHeight == 0) {
        totalHeight = heightSoFar;
      }
      
      totalWidth += (maxWidth + 1) * numCols - 1;
      
      Dimension d = new Dimension(left + right + totalWidth, top + bottom + totalHeight);
      
      return d;
    }
  }
  
  public Dimension minimumLayoutSize(Container parent) {
    return preferredLayoutSize(parent);
  }
  
  public void layoutContainer(Container parent) {
    synchronized (parent.getTreeLock()) {
      Insets insets = parent.getInsets();
      Dimension parentSize = parent.getSize();
      
      int count = parent.getComponentCount();
      
      int x = left;
      int y = top;
      int w = 0;
      int h = 0;
      
      int widthThisColumn = 0;
      int heightSoFar = 0;
      
      ArrayList oneColumn = new ArrayList();
      
      for (int i = 0; i < count; i++) {
        Component comp = parent.getComponent(i);
        Dimension d = comp.getPreferredSize();
        
        heightSoFar += height;
        if ((heightSoFar > height) && (!oneColumn.isEmpty())) {
          w = widthThisColumn;
          for (Iterator iter = oneColumn.iterator(); iter.hasNext();) {
            Component c = (Component)iter.next();
            h = getPreferredSizeheight;
            c.setBounds(x, y, w, h);
            y += h;
          }
          
          oneColumn.clear();
          x += widthThisColumn + 1;
          y = top;
          w = 0;
          h = 0;
          
          heightSoFar = height;
          widthThisColumn = 0;
        }
        
        oneColumn.add(comp);
        widthThisColumn = Math.max(widthThisColumn, width);
      }
      
      if (!oneColumn.isEmpty()) {
        w = widthThisColumn;
        for (Iterator iter = oneColumn.iterator(); iter.hasNext();) {
          Component c = (Component)iter.next();
          h = getPreferredSizeheight;
          c.setBounds(x, y, w, h);
          y += h;
        }
      }
    }
  }
  
  public void addLayoutComponent(String name, Component comp) {}
  
  public void removeLayoutComponent(Component comp) {}
}
