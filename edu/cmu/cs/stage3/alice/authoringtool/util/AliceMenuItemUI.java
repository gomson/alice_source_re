package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicMenuItemUI;






















public class AliceMenuItemUI
  extends BasicMenuItemUI
{
  public AliceMenuItemUI() {}
  
  protected MouseInputListener createMouseInputListener(JComponent c) { return new MouseInputHandler(); }
  
  protected class MouseInputHandler implements MouseInputListener {
    protected MouseInputHandler() {}
    
    public void mouseReleased(MouseEvent e) { MenuSelectionManager manager = MenuSelectionManager.defaultManager();
      Point p = e.getPoint();
      
      if ((x >= 0) && (x < menuItem.getWidth()) && (y >= 0) && (y < menuItem.getHeight())) {
        MenuElement[] path = getPath();
        manager.clearSelectedPath();
        menuItem.doClick(0);
        

        for (int i = 0; i < path.length; i++) {
          if ((path[i] instanceof AlicePopupMenu)) {
            ((AlicePopupMenu)path[i]).setVisible(false);
          }
          
        }
      }
      else
      {
        manager.processMouseEvent(e);
      }
    }
    
    public void mouseEntered(MouseEvent e) {
      MenuSelectionManager manager = MenuSelectionManager.defaultManager();
      int modifiers = e.getModifiers();
      

      if ((modifiers & 0x1C) != 0) {
        MenuSelectionManager.defaultManager().processMouseEvent(e);
      } else {
        manager.setSelectedPath(getPath());
      }
    }
    

    public void mouseExited(MouseEvent e)
    {
      MenuSelectionManager manager = MenuSelectionManager.defaultManager();
      int modifiers = e.getModifiers();
      

      if ((modifiers & 0x1C) != 0) {
        MenuSelectionManager.defaultManager().processMouseEvent(e);
      } else {
        MenuElement[] path = manager.getSelectedPath();
        
        if (path.length > 1) {
          MenuElement[] newPath = new MenuElement[path.length - 1];
          for (int i = 0; i < path.length - 1; i++) {
            newPath[i] = path[i];
          }
          
          manager.setSelectedPath(newPath);
        }
      }
    }
    

    public void mouseDragged(MouseEvent e) { MenuSelectionManager.defaultManager().processMouseEvent(e); }
    
    public void mouseClicked(MouseEvent e) {}
    
    public void mousePressed(MouseEvent e) {}
    
    public void mouseMoved(MouseEvent e) {}
  }
  
  public MenuElement[] getPath() { MenuSelectionManager m = MenuSelectionManager.defaultManager();
    MenuElement[] oldPath = m.getSelectedPath();
    
    int i = oldPath.length;
    
    if (i == 0) {
      return new MenuElement[0];
    }
    
    Component parent = menuItem.getParent();
    MenuElement[] newPath;
    if (oldPath[(i - 1)].getComponent() == parent) {
      MenuElement[] newPath = new MenuElement[i + 1];
      System.arraycopy(oldPath, 0, newPath, 0, i);
      newPath[i] = menuItem;
    } else {
      Vector path = new Vector();
      MenuElement me = menuItem;
      while ((me instanceof MenuElement)) {
        path.add(0, me);
        if ((me instanceof JPopupMenu)) {
          Object o = ((JPopupMenu)me).getInvoker();
          if (((o instanceof MenuElement)) && (o != me)) {
            me = (MenuElement)o;
          } else {
            me = null;
          }
        } else if ((me instanceof JMenuItem)) {
          Object o = ((JMenuItem)me).getParent();
          if (((o instanceof MenuElement)) && (o != me)) {
            me = (MenuElement)o;
          } else {
            me = null;
          }
        } else {
          me = null;
        }
      }
      
      newPath = (MenuElement[])path.toArray(new MenuElement[0]);
    }
    
    return newPath;
  }
  
  public void printPath(MenuElement[] path) {
    System.out.print("path [");
    for (int i = 0; i < path.length; i++) {
      MenuElement me = path[i];
      if ((me instanceof JMenu)) {
        System.out.print(((JMenu)me).getText() + ", ");
      } else if ((me instanceof JPopupMenu)) {
        Object invoker = ((JPopupMenu)me).getInvoker();
        if ((invoker instanceof JMenu)) {
          System.out.print(((JMenu)invoker).getText() + ".popupMenu, ");
        } else {
          System.out.print("anonymous popupMenu, ");
        }
      } else {
        System.out.print(me.getClass().getName());
      }
    }
    System.out.println("]");
  }
}
