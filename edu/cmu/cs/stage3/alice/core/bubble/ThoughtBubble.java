package edu.cmu.cs.stage3.alice.core.bubble;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;















public class ThoughtBubble
  extends Bubble
{
  public ThoughtBubble() {}
  
  private void paintOval(Graphics g, int centerX, int centerY, int width, int height)
  {
    int x = centerX - width / 2;
    int y = centerY - width / 2;
    g.setColor(getBackgroundColor());
    g.fillOval(x, y, width, height);
    g.setColor(Color.black);
    g.drawOval(x, y, width, height);
  }
  
  protected void paintBackground(Graphics g) {
    Rectangle2D totalBound = getTotalBound();
    Point origin = getOrigin();
    Point pixelOffset = getPixelOffset();
    
    if (totalBound != null)
    {
      int x = (int)(totalBound.getX() + x - 10.0D);
      int y = (int)(totalBound.getY() + y - 10.0D);
      int width = (int)totalBound.getWidth() + 10 + 10;
      int height = (int)totalBound.getHeight() + 10 + 10;
      
      g.setColor(getBackgroundColor());
      g.fillRoundRect(x, y, width, height, 20, 20);
      g.setColor(Color.black);
      g.drawRoundRect(x, y, width, height, 20, 20);
      
      Point connect = new Point(x + (int)((totalBound.getWidth() + 10.0D + 10.0D) * 0.33D), y);
      
      if (y > y) {
        connect.translate(0, height + 6);
      }
      
      width = 32;
      height = 24;
      
      paintOval(g, x, y, width, height);
      
      width -= 6;
      height -= 6;
      
      paintOval(g, (x + x) / 2, (y + y) / 2, width, height);
      
      width -= 6;
      height -= 6;
      
      paintOval(g, x, y, width, height);
    }
  }
}
