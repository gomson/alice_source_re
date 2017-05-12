package movieMaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;







public class ImageDisplay
  extends JPanel
  implements Scrollable
{
  private Image image;
  private Dimension prefSize;
  private int currentX = 0;
  private int currentY = 0;
  






  public ImageDisplay(Image theImage)
  {
    image = theImage;
    prefSize = new Dimension(image.getWidth(this), image.getHeight(this));
    setPreferredSize(prefSize);
    revalidate();
  }
  






  public ImageDisplay(Image theImage, int x, int y)
  {
    this(theImage);
  }
  






  public Image getImage()
  {
    return image;
  }
  

  public int getCurrentX()
  {
    return currentX;
  }
  

  public int getCurrentY()
  {
    return currentY;
  }
  



  public void setCurrentX(int x)
  {
    currentX = x;
    repaint();
  }
  




  public void setCurrentY(int y)
  {
    currentY = y;
    repaint();
  }
  




  public void setImage(Image theImage)
  {
    image = theImage;
    setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
    repaint();
  }
  




  public Dimension getPreferredScrollableViewportSize()
  {
    return prefSize;
  }
  








  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
  {
    return 1;
  }
  








  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
  {
    return 10;
  }
  



  public boolean getScrollableTracksViewportWidth()
  {
    return false;
  }
  


  public boolean getScrollableTracksViewportHeight()
  {
    return false;
  }
  




  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    int num = 3;
    int xStart = currentX - num;
    int xEnd = currentX + num;
    int yStart = currentY - num;
    int yEnd = currentY + num;
    int width = image.getWidth(this);
    int maxX = width - 1;
    int height = image.getHeight(this);
    int maxY = height - 1;
    

    g.drawImage(image, 0, 0, this);
    

    if ((currentX >= 0) && (currentX < width) && 
      (currentY >= 0) && (currentY < height))
    {


      if (xStart < 0)
        xStart = 0;
      if (xEnd > maxX)
        xEnd = maxX;
      if (yStart < 0)
        yStart = 0;
      if (yEnd > maxY) {
        yEnd = maxY;
      }
      
      g.setColor(Color.yellow);
      g.drawLine(xStart, currentY, xEnd, currentY);
      g.drawLine(currentX, yStart, currentX, yEnd);
      g.setColor(Color.black);
      

      int leftX = currentX - 1;
      int rightX = currentX + 1;
      int upY = currentY - 1;
      int downY = currentY + 1;
      if ((xStart <= leftX) && (upY >= 0))
        g.drawLine(xStart, upY, leftX, upY);
      if ((yStart <= upY) && (leftX >= 0))
        g.drawLine(leftX, yStart, leftX, upY);
      if ((yStart <= upY) && (rightX <= maxX))
        g.drawLine(rightX, yStart, rightX, upY);
      if ((upY >= 0) && (rightX <= xEnd))
        g.drawLine(rightX, upY, xEnd, upY);
      if ((downY < height) && (rightX <= xEnd))
        g.drawLine(rightX, downY, xEnd, downY);
      if ((downY <= yEnd) && (rightX < width))
        g.drawLine(rightX, downY, rightX, yEnd);
      if ((xStart <= leftX) && (downY < height))
        g.drawLine(xStart, downY, leftX, downY);
      if ((leftX >= 0) && (downY <= yEnd)) {
        g.drawLine(leftX, downY, leftX, yEnd);
      }
    }
  }
}
