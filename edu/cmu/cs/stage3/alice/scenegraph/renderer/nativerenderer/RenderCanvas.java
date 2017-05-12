package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import java.awt.Graphics;

public abstract class RenderCanvas extends java.awt.Canvas { private int m_nativeInstance;
  private int m_nativeDrawingSurfaceInfo;
  private OnscreenRenderTarget m_onscreenRenderTarget;
  
  protected abstract void createNativeInstance(RenderTargetAdapter paramRenderTargetAdapter);
  
  protected abstract void releaseNativeInstance();
  protected abstract boolean acquireDrawingSurface();
  protected abstract void releaseDrawingSurface();
  private boolean m_giveUp = false;
  private int count = 0;
  
  protected abstract void swapBuffers();
  protected RenderCanvas(OnscreenRenderTarget onscreenRenderTarget) { m_nativeDrawingSurfaceInfo = 0;
    m_onscreenRenderTarget = onscreenRenderTarget;
    createNativeInstance(m_onscreenRenderTarget.getAdapter());
    

    addKeyListener(new java.awt.event.KeyAdapter() {});
  }
  
  protected void finalize() throws Throwable
  {
    releaseNativeInstance();
    super.finalize();
  }
  


  protected void processKeyEvent(java.awt.event.KeyEvent e)
  {
    super.processKeyEvent(e);
    java.awt.Container parent = getParent();
    if (parent != null) {
      parent.dispatchEvent(e);
    }
  }
  
  public boolean isFocusTraversable()
  {
    return true;
  }
  
  public void update(Graphics graphics) {
    paint(graphics);
  }
  
  public void paint(Graphics graphics) {
    if (!m_giveUp)
    {

      int width = getWidth();
      int height = getHeight();
      if ((width != 0) && (height != 0)) {
        try {
          m_onscreenRenderTarget.getAdapter().setDesiredSize(width, height);
          m_onscreenRenderTarget.clearAndRenderOffscreen();
          m_onscreenRenderTarget.update();
        } catch (RuntimeException re) {
          if (count != 0) return; }
        count += 1;
        Object[] buttonLabels = { "Retry", "Give up" };
        if (edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog("A error has occured in attempting to draw the scene.  Simply retrying might fix the problem.", "Potential Problem", 0, 2, null, buttonLabels, buttonLabels[0]) == 0) {
          m_onscreenRenderTarget.getAdapter().reset();
          repaint();
          count = 0;
        } else {
          m_giveUp = true;
          throw re;
        }
      }
      else
      {
        repaint();
      }
    }
  }
  
  public java.awt.Dimension getMinimumSize() {
    return new java.awt.Dimension(0, 0);
  }
}
