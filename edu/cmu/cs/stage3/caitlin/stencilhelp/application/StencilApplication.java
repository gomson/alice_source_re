package edu.cmu.cs.stage3.caitlin.stencilhelp.application;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public abstract interface StencilApplication
{
  public abstract void setGlassPane(Component paramComponent);
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract String getIDForPoint(Point paramPoint, boolean paramBoolean);
  
  public abstract Rectangle getBoxForID(String paramString)
    throws IDDoesNotExistException;
  
  public abstract boolean isIDVisible(String paramString)
    throws IDDoesNotExistException;
  
  public abstract void makeIDVisible(String paramString)
    throws IDDoesNotExistException;
  
  public abstract void makeWayPoint();
  
  public abstract void goToPreviousWayPoint();
  
  public abstract void clearWayPoints();
  
  public abstract StateCapsule getCurrentState();
  
  public abstract StateCapsule getStateCapsuleFromString(String paramString);
  
  public abstract boolean doesStateMatch(StateCapsule paramStateCapsule);
  
  public abstract void performTask(String paramString);
  
  public abstract void handleMouseEvent(MouseEvent paramMouseEvent);
  
  public abstract void deFocus();
  
  public abstract Dimension getScreenSize();
}
