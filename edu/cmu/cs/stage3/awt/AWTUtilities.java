package edu.cmu.cs.stage3.awt;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Label;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;





public class AWTUtilities
{
  private static boolean s_successfullyLoadedLibrary;
  public static int key;
  public static int modifier;
  
  static
  {
    try
    {
      System.loadLibrary("jni_awtutilities");
      s_successfullyLoadedLibrary = true;
    } catch (Throwable t) {
      s_successfullyLoadedLibrary = false; } }
  
  public AWTUtilities() {}
  
  private static native boolean isGetCursorLocationSupportedNative();
  
  public static boolean isGetCursorLocationSupported() { if (s_successfullyLoadedLibrary) {
      return isGetCursorLocationSupportedNative();
    }
    return false;
  }
  
  private static native boolean isSetCursorLocationSupportedNative();
  
  public static boolean isSetCursorLocationSupported() {
    if (s_successfullyLoadedLibrary) {
      return isSetCursorLocationSupportedNative();
    }
    return false;
  }
  
  private static native boolean isIsKeyPressedSupportedNative();
  
  public static boolean isIsKeyPressedSupported() {
    if (s_successfullyLoadedLibrary) {
      return isIsKeyPressedSupportedNative();
    }
    return false;
  }
  
  private static native boolean isGetModifiersSupportedNative();
  
  public static boolean isGetModifiersSupported() {
    if (s_successfullyLoadedLibrary) {
      return isGetModifiersSupportedNative();
    }
    return false;
  }
  
  private static native boolean isPumpMessageQueueSupportedNative();
  
  public static boolean isPumpMessageQueueSupported() {
    if (s_successfullyLoadedLibrary) {
      return isPumpMessageQueueSupportedNative();
    }
    return false;
  }
  
  private static native void pumpMessageQueueNative();
  
  public static void pumpMessageQueue() {
    if (s_successfullyLoadedLibrary) {
      pumpMessageQueueNative();
    }
  }
  
  private static native void getCursorLocationNative(Point paramPoint);
  
  public static Point getCursorLocation()
  {
    if (s_successfullyLoadedLibrary) {
      Point p = new Point();
      getCursorLocationNative(p);
      return p;
    }
    Point p = MouseInfo.getPointerInfo().getLocation().getLocation();
    return p;
  }
  
  private static native void setCursorLocationNative(int paramInt1, int paramInt2);
  
  public static void setCursorLocation(int x, int y) {
    if (s_successfullyLoadedLibrary) {
      setCursorLocationNative(x, y);
    } else {
      moveMouse(new Point(x, y));
    }
  }
  
  private static void moveMouse(Point p) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices();
    

    for (GraphicsDevice device : gs) {
      GraphicsConfiguration[] configurations = 
        device.getConfigurations();
      for (GraphicsConfiguration config : configurations) {
        Rectangle bounds = config.getBounds();
        if (bounds.contains(p))
        {
          Point b = bounds.getLocation();
          Point s = new Point(x - x, y - y);
          try
          {
            Robot r = new Robot(device);
            r.mouseMove(x, y);
          } catch (AWTException e) {
            e.printStackTrace();
          }
          
          return;
        }
      }
    }
  }
  



  public static void setCursorLocation(Point p) { setCursorLocation(x, y); }
  
  private static native boolean isCursorShowingNative();
  
  public static boolean isCursorShowing() {
    if (s_successfullyLoadedLibrary) {
      return isCursorShowingNative();
    }
    return true;
  }
  
  private static native void setIsCursorShowingNative(boolean paramBoolean);
  
  public static void setIsCursorShowing(boolean isCursorShowing) {
    if (s_successfullyLoadedLibrary) {
      setIsCursorShowingNative(isCursorShowing);
    }
  }
  
  private static native boolean isIsCursorShowingSupportedNative();
  
  public static boolean isIsCursorShowingSupported()
  {
    if (s_successfullyLoadedLibrary) {
      return isIsCursorShowingSupportedNative();
    }
    return false;
  }
  
  private static native boolean isSetIsCursorShowingSupportedNative();
  
  public static boolean isSetIsCursorShowingSupported() {
    if (s_successfullyLoadedLibrary) {
      return isSetIsCursorShowingSupportedNative();
    }
    return false;
  }
  
  private static native boolean isKeyPressedNative(int paramInt);
  
  public static boolean isKeyPressed(int keyCode)
  {
    if (s_successfullyLoadedLibrary) {
      return isKeyPressedNative(keyCode);
    }
    if (keyCode == 17)
      return AikMin.control;
    if (keyCode == 16) {
      return AikMin.shift;
    }
    return false;
  }
  
  private static native int isKeyNative(int paramInt);
  
  public static int isKey(int keyCode) {
    if (s_successfullyLoadedLibrary) {
      return isKeyNative(keyCode);
    }
    return 0;
  }
  
  private static native int getModifiersNative();
  
  public static int getModifiers()
  {
    if (s_successfullyLoadedLibrary) {
      return getModifiersNative();
    }
    return modifier;
  }
  
  public static boolean mouseListenersAreSupported()
  {
    return (isGetModifiersSupported()) && (isGetCursorLocationSupported());
  }
  
  public static boolean mouseMotionListenersAreSupported() {
    return (isGetModifiersSupported()) && (isGetCursorLocationSupported());
  }
  
  private static Vector s_mouseListeners = new Vector();
  private static Vector s_mouseMotionListeners = new Vector();
  
  public static void addMouseListener(MouseListener mouseListener) { s_mouseListeners.addElement(mouseListener); }
  
  public static void removeMouseListener(MouseListener mouseListener) {
    s_mouseListeners.removeElement(mouseListener);
  }
  
  public static void addMouseMotionListener(MouseMotionListener mouseMotionListener) { s_mouseMotionListeners.addElement(mouseMotionListener); }
  
  public static void removeMouseMotionListener(MouseMotionListener mouseMotionListener) {
    s_mouseMotionListeners.removeElement(mouseMotionListener);
  }
  
  private static Component s_source = new Label("edu.cmu.cs.stage3.io.toolkit.Toolkit");
  
  private static int s_prevModifiers = 0;
  private static int s_clickCount = 0;
  
  private static boolean isButton1Pressed(int modifiers) { return (modifiers & 0x10) == 16; }
  
  private static boolean isButton2Pressed(int modifiers) {
    return (modifiers & 0x8) == 8;
  }
  
  private static boolean isButton3Pressed(int modifiers) { return (modifiers & 0x4) == 4; }
  

  private static Point s_prevCursorPos = new Point();
  private static Point s_currCursorPos = new Point();
  
  public static void fireMouseAndMouseMotionListenersIfNecessary() {
    if (mouseListenersAreSupported()) {
      int id = 0;
      int currModifiers = getModifiers();
      boolean drag = false;
      if (isButton1Pressed(s_prevModifiers)) {
        if (isButton1Pressed(currModifiers)) {
          drag = true;
        } else {
          id = 502;
        }
      }
      else if (isButton1Pressed(currModifiers)) {
        id = 501;
        drag = true;
      }
      

      if (isButton2Pressed(s_prevModifiers)) {
        if (isButton2Pressed(currModifiers)) {
          drag = true;
        } else {
          id = 502;
        }
      }
      else if (isButton2Pressed(currModifiers)) {
        id = 501;
        drag = true;
      }
      

      if (isButton3Pressed(s_prevModifiers)) {
        if (isButton3Pressed(currModifiers)) {
          drag = true;
        } else {
          id = 502;
        }
      }
      else if (isButton3Pressed(currModifiers)) {
        id = 501;
        drag = true;
      }
      

      long when = System.currentTimeMillis();
      boolean isPopupTrigger = false;
      
      s_currCursorPos = getCursorLocation();
      
      if (id != 0) {
        if (s_mouseListeners.size() > 0) {
          MouseEvent mouseEvent = new MouseEvent(s_source, id, when, currModifiers, s_currCursorPosx, s_currCursorPosy, s_clickCount, isPopupTrigger);
          for (int i = 0; i < s_mouseListeners.size(); i++) {
            MouseListener mouseListener = (MouseListener)s_mouseListeners.elementAt(i);
            switch (id) {
            case 500: 
              mouseListener.mouseClicked(mouseEvent);
              break;
            case 504: 
              mouseListener.mouseEntered(mouseEvent);
              break;
            case 505: 
              mouseListener.mouseExited(mouseEvent);
              break;
            case 501: 
              mouseListener.mousePressed(mouseEvent);
              break;
            case 502: 
              mouseListener.mouseReleased(mouseEvent);
            }
            
          }
        }
      }
      else if ((s_currCursorPosx != s_prevCursorPosx) || (s_currCursorPosy != s_prevCursorPosy))
      {

        if (s_mouseMotionListeners.size() > 0) {
          if (drag) {
            id = 506;
          } else {
            id = 503;
          }
          MouseEvent mouseEvent = new MouseEvent(s_source, id, when, currModifiers, s_currCursorPosx, s_currCursorPosy, s_clickCount, isPopupTrigger);
          for (int i = 0; i < s_mouseMotionListeners.size(); i++) {
            MouseMotionListener mouseMotionListener = (MouseMotionListener)s_mouseMotionListeners.elementAt(i);
            switch (id) {
            case 503: 
              mouseMotionListener.mouseMoved(mouseEvent);
              break;
            case 506: 
              mouseMotionListener.mouseDragged(mouseEvent);
            }
            
          }
        }
      }
      
      s_prevCursorPosx = s_currCursorPosx;
      s_prevCursorPosy = s_currCursorPosy;
      s_prevModifiers = currModifiers;
    }
  }
}
