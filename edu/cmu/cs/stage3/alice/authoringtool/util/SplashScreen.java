package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;














public class SplashScreen
  extends Frame
{
  protected Image image;
  protected Dimension size;
  protected Window splashWindow;
  
  public SplashScreen(Image image)
  {
    this.image = image;
    
    MediaTracker tracker = new MediaTracker(this);
    tracker.addImage(image, 0);
    try {
      tracker.waitForID(0);
    }
    catch (InterruptedException localInterruptedException) {}
    size = new Dimension(image.getWidth(this), image.getHeight(this));
    if ((size.width < 1) || (size.height < 1)) {
      size = new Dimension(256, 256);
    }
    
    splashWindow = new Window(this) {
      public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
        
        g.setColor(Color.white);
        
        g.setFont(new Font("Dialog", 1, 12));
        String versionString = Messages.getString("version__") + JAlice.getVersion();
        int stringWidth = g.getFontMetrics().stringWidth(versionString);
        g.drawString(versionString, size.width - 10 - stringWidth, size.height - 6);
        g.drawString(Messages.getString("Loading___"), 10, size.height - 6);
      }
    };
    splashWindow.setSize(size);
    
    setSize(size);
  }
  
  public void showSplash() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (width - size.width) / 2;
    int y = (height - size.height) / 2;
    splashWindow.setLocation(x, y);
    setLocation(x, y);
    splashWindow.setVisible(true);
  }
  
  public void hideSplash()
  {
    splashWindow.dispose();
  }
}
