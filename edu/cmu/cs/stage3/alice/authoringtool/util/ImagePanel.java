package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import javax.swing.JPanel;


















public class ImagePanel
  extends JPanel
{
  protected Image image;
  protected MediaTracker tracker;
  protected Dimension size;
  
  public ImagePanel()
  {
    setOpaque(false);
    tracker = new MediaTracker(this);
    size = new Dimension();
  }
  
  public void setImage(Image image) {
    if (this.image != image) {
      if (this.image != null) {
        tracker.removeImage(this.image);
      }
      this.image = image;
      if (image != null) {
        tracker.addImage(this.image, 0);
      }
      try {
        tracker.waitForAll();
      } catch (InterruptedException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Interrupted_while_waiting_for_image_to_load_"), e);
      }
      revalidate();
      repaint();
    }
  }
  
  protected void paintComponent(Graphics g) {
    if (image != null) {
      g.drawImage(image, 0, 0, null);
    }
  }
  
  protected Dimension getImageSize() {
    if (image != null) {
      size.setSize(image.getWidth(null), image.getHeight(null));
    } else {
      size.setSize(0, 0);
    }
    return size;
  }
  
  public Dimension getMinimumSize() {
    return getImageSize();
  }
  
  public Dimension getMaximumSize() {
    return getImageSize();
  }
  
  public Dimension getPreferredSize() {
    return getImageSize();
  }
}
