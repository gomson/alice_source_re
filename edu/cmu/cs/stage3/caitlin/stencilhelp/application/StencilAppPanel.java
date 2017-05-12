package edu.cmu.cs.stage3.caitlin.stencilhelp.application;

import edu.cmu.cs.stage3.caitlin.stencilhelp.client.StencilManager;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

































public class StencilAppPanel
  extends JPanel
  implements StencilApplication
{
  Hashtable nameToComp = new Hashtable();
  Hashtable compToName = new Hashtable();
  
  StencilManager stencilManager = null;
  JPanel stencilComponent = null;
  JFrame frame = null;
  
  long lastEventTime = -1L;
  
  public StencilAppPanel(JFrame frame) {
    this.frame = frame;
  }
  
  public void launchControl()
  {
    if (stencilManager == null) {
      stencilManager = new StencilManager(this);
    }
    setGlassPane(stencilManager.getStencilComponent());
  }
  
  public void setGlassPane(Component c)
  {
    stencilComponent = ((JPanel)c);
    stencilComponent.setOpaque(false);
    
    frame.setGlassPane(c);
    
    stencilManager.showStencils(!stencilManager.getIsShowing());
    
    if (!stencilManager.getIsShowing()) {
      frame.removeKeyListener(stencilManager);
    } else {
      frame.addKeyListener(stencilManager);
    }
    
    requestFocus();
  }
  
  public String getIDForPoint(Point p, boolean dropSite) {
    Component c = getComponentAtPoint(p);
    


    if (c != null) {
      Object value = compToName.get(c);
      if (value != null) { return (String)value;
      }
      while (c != null) {
        c = c.getParent();
        if (c != null) value = compToName.get(c);
        if (value != null) { return (String)value;
        }
      }
      return null;
    }
    return null;
  }
  
  public Rectangle getBoxForID(String ID) { Component c = (Component)nameToComp.get(ID);
    if (c != null) {
      Point corner = c.getLocationOnScreen();
      SwingUtilities.convertPointFromScreen(corner, getRootPane());
      Rectangle rect = new Rectangle(corner, c.getSize());
      return rect; }
    return null;
  }
  

  public boolean isIDVisible(String ID) { return true; }
  
  public void makeIDVisible(String ID) {}
  
  public void makeWayPoint() {}
  
  public void goToPreviousWayPoint() {}
  
  public void clearWayPoints() {}
  public boolean doesStateMatch(StateCapsule stateCapsule) { return true; }
  public StateCapsule getCurrentState() { return null; }
  public StateCapsule getStateCapsuleFromString(String capsuleString) { return null; }
  
  public void performTask(String taskString) {}
  
  public void handleMouseEvent(MouseEvent e) {
    Point stencilComponentPoint = e.getPoint();
    if (e.getWhen() != lastEventTime)
    {

      lastEventTime = e.getWhen();
      Point containerPoint = SwingUtilities.convertPoint(
        stencilComponent, 
        stencilComponentPoint, 
        this);
      
      Component component = SwingUtilities.getDeepestComponentAt(
        this, 
        x, 
        y);
      




      Point componentPoint = SwingUtilities.convertPoint(
        stencilComponent, 
        stencilComponentPoint, 
        component);
      


      if (component != null) {
        component.dispatchEvent(new MouseEvent(component, 
          e.getID(), 
          e.getWhen(), 
          e.getModifiers(), 
          x, 
          y, 
          e.getClickCount(), 
          e.isPopupTrigger()));
      }
    }
  }
  
  private Component getComponentAtPoint(Point stencilComponentPoint)
  {
    Point containerPoint = SwingUtilities.convertPoint(
      stencilComponent, 
      stencilComponentPoint, 
      this);
    
    Component component = SwingUtilities.getDeepestComponentAt(
      this, 
      x, 
      y);
    
    return component;
  }
  
  public void addToTable(String name, Component c) {
    nameToComp.put(name, c);
    compToName.put(c, name);
  }
  
  public void replaceTable(String name, Component c) {
    Component old = (Component)nameToComp.remove(name);
    nameToComp.put(name, c);
    
    if (old != null) compToName.remove(old);
    compToName.put(c, name);
  }
  
  public void deFocus() {
    requestFocus();
  }
  
  public Dimension getScreenSize() {
    return getSize();
  }
}
