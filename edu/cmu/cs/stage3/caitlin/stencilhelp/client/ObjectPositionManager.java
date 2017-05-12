package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException;
import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication;
import java.awt.Dimension;
import java.awt.Rectangle;










public class ObjectPositionManager
{
  StencilApplication stencilApp = null;
  
  public ObjectPositionManager(StencilApplication stencilApp) {
    this.stencilApp = stencilApp;
  }
  
  public double getScreenHeight() { return stencilApp.getScreenSize().getHeight(); }
  

  public double getScreenWidth() { return stencilApp.getScreenSize().getWidth(); }
  
  public Rectangle getInitialBox(String ID) {
    try {
      if (stencilApp.isIDVisible(ID)) {
        return stencilApp.getBoxForID(ID);
      }
    }
    catch (IDDoesNotExistException localIDDoesNotExistException) {}
    return null;
  }
  
  public Rectangle getBoxForID(String ID) {
    try {
      if (!stencilApp.isIDVisible(ID)) {
        stencilApp.makeIDVisible(ID);
      }
      
      return stencilApp.getBoxForID(ID);
    }
    catch (IDDoesNotExistException localIDDoesNotExistException) {}
    

    return null;
  }
}
