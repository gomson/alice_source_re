package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Component;
import java.awt.Point;
import java.io.File;

public abstract interface StencilClient
{
  public abstract boolean isDropAccessible(Point paramPoint);
  
  public abstract void update();
  
  public abstract void stateChanged();
  
  public abstract Component getStencilComponent();
  
  public abstract void showStencils(boolean paramBoolean);
  
  public abstract boolean getIsShowing();
  
  public abstract void loadStencilTutorial(File paramFile);
}
