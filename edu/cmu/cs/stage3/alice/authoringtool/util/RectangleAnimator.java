package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JWindow;



















public class RectangleAnimator
  extends JWindow
  implements Runnable
{
  protected AuthoringTool authoringTool;
  protected Rectangle sourceBounds;
  protected Rectangle targetBounds;
  protected long duration = 300L;
  protected long startTime;
  
  public RectangleAnimator(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
  }
  
  public Color getColor() {
    return getBackground();
  }
  
  public void setColor(Color color) {
    setBackground(color);
  }
  
  public long getDuration() {
    return duration;
  }
  
  public void setDuration(long duration) {
    this.duration = duration;
  }
  
  public Rectangle getSourceBounds() {
    return sourceBounds;
  }
  
  public void setSourceBounds(Rectangle r) {
    sourceBounds = r;
  }
  
  public Rectangle gettargetBounds() {
    return targetBounds;
  }
  
  public void setTargetBounds(Rectangle r) {
    targetBounds = r;
  }
  
  public void animate(Rectangle sourceBounds, Rectangle targetBounds) {
    setSourceBounds(sourceBounds);
    setTargetBounds(targetBounds);
    
    setBounds(sourceBounds);
    setVisible(true);
    
    startTime = System.currentTimeMillis();
    authoringTool.getScheduler().addEachFrameRunnable(this);
  }
  
  public void animate(Rectangle sourceBounds, Rectangle targetBounds, Color color) {
    setColor(color);
    animate(sourceBounds, targetBounds);
  }
  
  public void run() {
    long time = System.currentTimeMillis();
    long dt = time - startTime;
    if (dt <= duration) {
      double portion = dt / duration;
      int x = sourceBounds.x + (int)(portion * (targetBounds.x - sourceBounds.x));
      int y = sourceBounds.y + (int)(portion * (targetBounds.y - sourceBounds.y));
      int w = sourceBounds.width + (int)(portion * (targetBounds.width - sourceBounds.width));
      int h = sourceBounds.height + (int)(portion * (targetBounds.height - sourceBounds.height));
      setBounds(x, y, w, h);
      repaint();
    } else {
      setVisible(false);
      authoringTool.getScheduler().removeEachFrameRunnable(this);
    }
  }
}
