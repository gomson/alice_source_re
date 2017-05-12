package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

public class WaitAndUpdateThread extends Thread {
  long millis = 0L;
  StencilManager.Stencil stencil = null;
  LayoutChangeListener obj = null;
  
  public WaitAndUpdateThread(long millis, StencilManager.Stencil stencil, LayoutChangeListener obj) {
    this.millis = millis;
    this.stencil = stencil;
    this.obj = obj;
  }
  
  public void run()
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException localInterruptedException) {}
    
    boolean success = obj.layoutChanged();
    
    if (!success)
    {
      stencil.setErrorStencil(true);
    }
  }
}
