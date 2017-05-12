package edu.cmu.cs.stage3.alice.core.bubble;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;














public class SpeechBubble
  extends Bubble
{
  public SpeechBubble() {}
  
  protected void paintBackground(Graphics g)
  {
    Rectangle2D totalBound = getTotalBound();
    if (totalBound != null) {
      Point origin = getOrigin();
      Point pixelOffset = getPixelOffset();
      
      int x = (int)(totalBound.getX() + x - 10.0D);
      int y = (int)(totalBound.getY() + y - 10.0D);
      int width = (int)totalBound.getWidth() + 10 + 10;
      int height = (int)totalBound.getHeight() + 10 + 10;
      
      g.setColor(getBackgroundColor());
      g.fillRoundRect(x, y, width, height, 20, 20);
      g.setColor(Color.black);
      g.drawRoundRect(x, y, width, height, 20, 20);
      
      Point connect = new Point(x + (int)((totalBound.getWidth() + 10.0D + 10.0D) * 0.333D), y);
      int dy;
      int dy; if (y > y) {
        connect.translate(0, height);
        dy = -1;
      } else {
        dy = 1;
      }
      Polygon polygon = new Polygon();
      polygon.addPoint(x, y);
      polygon.addPoint(x - 8, y + dy);
      polygon.addPoint(x + 8, y + dy);
      
      g.setColor(getBackgroundColor());
      g.fillPolygon(polygon);
      g.setColor(Color.black);
      g.drawLine(x, y, x - 8, y + dy);
      g.drawLine(x, y, x + 8, y + dy);
    }
  }
}
