package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
























class CustomCheckBox
  extends JCheckBox
  implements ImageObserver
{
  private int index = 0;
  Image image;
  
  CustomCheckBox() {}
  JComponent gui;
  
  public void setIndex(int index) { this.index = index; }
  
  Object object;
  private int size = 30;
  
  public void paint(Graphics g) { super.paint(g);
    if (image != null) {
      g.drawImage(image, size, 0, Color.white, this);
    }
  }
  
  public Dimension getPreferredSize() {
    if (image == null) {
      return super.getPreferredSize();
    }
    int x = image.getWidth(this);
    int y = image.getHeight(this);
    return new Dimension(x + size, y + 1);
  }
  
  public Dimension getMinimumSize()
  {
    if (image == null) {
      return super.getMinimumSize();
    }
    int x = image.getWidth(this);
    int y = image.getHeight(this);
    return new Dimension(x + size, y);
  }
  
  public Dimension getMaximumSize()
  {
    if (image == null) {
      return super.getMaximumSize();
    }
    int x = image.getWidth(this);
    int y = image.getHeight(this);
    return new Dimension(x + size, y);
  }
  

  public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
  {
    return true;
  }
}
