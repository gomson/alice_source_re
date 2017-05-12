package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.Timer;

















public class AlicePopupMenu
  extends JPopupMenu
{
  protected int millisecondDelay = 300;
  protected Timer setPopupVisibleTrueTimer;
  protected Timer setPopupVisibleFalseTimer;
  AliceMenu invokerMenu;
  
  public AlicePopupMenu()
  {
    setLayout(new MultiColumnPopupLayout());
  }
  
  public void setVisible(boolean b)
  {
    super.setVisible(b);
    Container popup = null;
    if (b) {
      popup = getParent();
      if (popup != null) {
        popup = popup.getParent();
      }
      if (popup != null) {
        popup = popup.getParent();
      }
      if (popup != null) {
        popup = popup.getParent();
      }
      if ((popup instanceof Window)) {
        Window[] windows = ((Window)popup).getOwnedWindows();
        for (int i = 0; i < windows.length; i++) {
          windows[i].setVisible(false);
        }
      }
    }
  }
  
  public void menuSelectionChanged(boolean isIncluded) {
    if (setPopupVisibleTrueTimer == null) {
      setPopupVisibleTrueTimer = new Timer(millisecondDelay, new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          invokerMenu.setPopupMenuVisible(true);
        }
      });
      setPopupVisibleTrueTimer.setRepeats(false);
    }
    if (setPopupVisibleFalseTimer == null) {
      setPopupVisibleFalseTimer = new Timer(millisecondDelay, new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          invokerMenu.setPopupMenuVisible(false);
        }
      });
      setPopupVisibleFalseTimer.setRepeats(false);
    }
    if (setPopupVisibleTrueTimer.isRunning()) {
      setPopupVisibleTrueTimer.stop();
    }
    if (setPopupVisibleFalseTimer.isRunning()) {
      setPopupVisibleFalseTimer.stop();
    }
    
    if ((getInvoker() instanceof AliceMenu)) {
      boolean allGone = MenuSelectionManager.defaultManager().getSelectedPath().length == 0;
      invokerMenu = ((AliceMenu)getInvoker());
      
      if (isIncluded) {
        setPopupVisibleTrueTimer.start();
      }
      else if (allGone) {
        setVisible(false);
      } else {
        setPopupVisibleFalseTimer.start();
      }
    }
    
    if ((isPopupMenu()) && (!isIncluded)) {
      setVisible(false);
    }
  }
  
  private boolean isPopupMenu() {
    return (getInvoker() != null) && (!(getInvoker() instanceof AliceMenu));
  }
  
  public static AlicePopupMenu item = new AlicePopupMenu();
  
  public static void hidePopup() {
    if (item != null) {
      item.setFocusable(false);
    }
  }
  
  public static void showPopup() {
    if (item != null) {
      item.setFocusable(true);
    }
  }
  
  public void show(Component invoker, int x, int y) {
    final AlicePopupMenu menu = this;
    if (item.isFocusable()) {
      super.show(invoker, x, y);
      PopupMenuUtilities.ensurePopupIsOnScreen(menu);
      if (AikMin.isMAC()) {
        item.addPropertyChangeListener("focusable", new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent arg0) {
            AlicePopupMenu.item.removePropertyChangeListener(this);
            if ((!AlicePopupMenu.item.hasFocus()) && (menu.isVisible()))
              menu.setVisible(false);
          }
        });
      }
    }
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
