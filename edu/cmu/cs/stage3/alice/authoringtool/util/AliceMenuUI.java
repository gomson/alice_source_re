package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicMenuUI;






















public class AliceMenuUI
  extends BasicMenuUI
{
  public AliceMenuUI() {}
  
  protected MouseInputListener createMouseInputListener(JComponent c) { return new AliceMouseInputHandler(); }
  
  protected class AliceMouseInputHandler implements MouseInputListener {
    protected AliceMouseInputHandler() {}
    
    public void mousePressed(MouseEvent e) { JMenu menu = (JMenu)menuItem;
      
      if (!menu.isEnabled()) {
        return;
      }
      
      MenuSelectionManager manager = MenuSelectionManager.defaultManager();
      
      if (menu.isTopLevelMenu()) {
        if (menu.isSelected()) {
          manager.clearSelectedPath();
        } else {
          Container cnt = menu.getParent();
          
          if ((cnt != null) && ((cnt instanceof JMenuBar))) {
            MenuElement[] me = new MenuElement[2];
            me[0] = ((MenuElement)cnt);
            me[1] = menu;
            manager.setSelectedPath(me);
          }
        }
      }
      
      MenuElement[] selectedPath = manager.getSelectedPath();
      
      if ((selectedPath.length <= 0) || (selectedPath[(selectedPath.length - 1)] != menu.getPopupMenu())) {
        if ((menu.isTopLevelMenu()) || (menu.getDelay() == 0)) {
          MenuElement[] newPath = new MenuElement[selectedPath.length + 1];
          System.arraycopy(selectedPath, 0, newPath, 0, selectedPath.length);
          newPath[selectedPath.length] = menu.getPopupMenu();
          manager.setSelectedPath(newPath);
        } else {
          setupPostTimer(menu);
        }
      }
    }
    
    public void mouseReleased(MouseEvent e) {
      JMenu menu = (JMenu)menuItem;
      
      if (!menu.isEnabled()) {
        return;
      }
      
      MenuSelectionManager manager = MenuSelectionManager.defaultManager();
      manager.processMouseEvent(e);
      
      if (!e.isConsumed()) {
        manager.clearSelectedPath();
      }
    }
    
    public void mouseEntered(MouseEvent e) {
      JMenu menu = (JMenu)menuItem;
      
      if (!menu.isEnabled()) {
        return;
      }
      
      MenuSelectionManager manager = MenuSelectionManager.defaultManager();
      MenuElement[] selectedPath = manager.getSelectedPath();
      
      if (!menu.isTopLevelMenu()) {
        if (menu.getDelay() == 0) {
          MenuElement[] path = getPath();
          MenuElement[] newPath = new MenuElement[getPath().length + 1];
          System.arraycopy(path, 0, newPath, 0, path.length);
          newPath[path.length] = menu.getPopupMenu();
          manager.setSelectedPath(newPath);
        }
        else
        {
          manager.setSelectedPath(getPath());
          setupPostTimer(menu);
        }
      }
      else if ((selectedPath.length > 0) && (selectedPath[0] == menu.getParent())) {
        MenuElement[] newPath = new MenuElement[3];
        


        newPath[0] = ((MenuElement)menu.getParent());
        newPath[1] = menu;
        newPath[2] = menu.getPopupMenu();
        manager.setSelectedPath(newPath);
      }
    }
    
    public void mouseDragged(MouseEvent e)
    {
      JMenu menu = (JMenu)menuItem;
      
      if (!menu.isEnabled()) {
        return;
      }
      
      MenuSelectionManager.defaultManager().processMouseEvent(e);
    }
    
    public void mouseClicked(MouseEvent e) {}
    
    public void mouseExited(MouseEvent e) {}
    
    public void mouseMoved(MouseEvent e) {} }
  
  protected void setupPostTimer(JMenu menu) { Timer timer = new Timer(menu.getDelay(), new AlicePostAction(menu));
    timer.setRepeats(false);
    timer.start();
  }
  
  private static class AlicePostAction extends AbstractAction {
    JMenu menu;
    
    AlicePostAction(JMenu menu) {
      this.menu = menu;
    }
    
    public void actionPerformed(ActionEvent e) {
      MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
      
      MenuElement[] path = ((BasicMenuUI)menu.getUI()).getPath();
      MenuElement[] newPath = new MenuElement[path.length + 1];
      for (int i = 0; i < path.length; i++) {
        newPath[i] = path[i];
      }
      newPath[path.length] = menu.getPopupMenu();
      defaultManager.setSelectedPath(newPath);
    }
    
    public boolean isEnabled() {
      return menu.getModel().isEnabled();
    }
  }
  
  public MenuElement[] getPath() {
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
    
    return (MenuElement[])path.toArray(new MenuElement[0]);
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
