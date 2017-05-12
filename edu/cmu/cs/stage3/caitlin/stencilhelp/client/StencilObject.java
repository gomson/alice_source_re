package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Rectangle;
import java.util.Vector;

public abstract interface StencilObject
{
  public abstract Vector getShapes();
  
  public abstract Rectangle getRectangle();
  
  public abstract Rectangle getPreviousRectangle();
  
  public abstract boolean isModified();
  
  public abstract boolean intersectsRectangle(Rectangle paramRectangle);
  
  public abstract void addStencilObjectPositionListener(StencilObjectPositionListener paramStencilObjectPositionListener);
  
  public abstract void removeStencilObjectPositionListener(StencilObjectPositionListener paramStencilObjectPositionListener);
  
  public abstract String getComponentID();
}
