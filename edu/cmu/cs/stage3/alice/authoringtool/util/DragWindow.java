package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.image.ImageUtilities;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;


















public class DragWindow
  extends Window
{
  protected Image image;
  protected int width;
  protected int height;
  
  public DragWindow(Frame owner)
  {
    super(owner);
    setLayout(new BorderLayout(0, 0));
  }
  
  public void setImage(Image image) {
    this.image = image;
    if (image != null) {
      try {
        width = ImageUtilities.getWidth(image);
        height = ImageUtilities.getHeight(image);
        setSize(width, height);
      } catch (InterruptedException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Interrupted_while_waiting_for_drag_image_to_load_"), e);
      }
    }
    repaint();
  }
  
  public void paint(Graphics g) {
    g.drawImage(image, 0, 0, this);
  }
}
