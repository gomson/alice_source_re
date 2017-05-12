package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;




























public class ImagePreview
  extends JComponent
  implements PropertyChangeListener
{
  ImageIcon thumbnail = null;
  File file = null;
  int width = 0; int height = 0;
  boolean needUpdating = false;
  
  public ImagePreview(JFileChooser fc) {
    setPreferredSize(new Dimension(300, 300));
    setFont(new Font("SansSerif", 1, 12));
    fc.addPropertyChangeListener(this);
    needUpdating = true;
  }
  
  public ImagePreview(JFileChooser fc, Image Icon) {
    setPreferredSize(new Dimension(300, 300));
    setFont(new Font("SansSerif", 1, 12));
    fc.addPropertyChangeListener(this);
    needUpdating = false;
    ImageIcon tmpIcon = new ImageIcon(Icon);
    height = tmpIcon.getIconHeight();
    width = tmpIcon.getIconWidth();
    if (tmpIcon != null) {
      if (tmpIcon.getIconWidth() > 200) {
        thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(
          200, -1, 1));
      } else {
        thumbnail = tmpIcon;
      }
    }
    repaint();
  }
  
  public void loadImage() {
    if (file == null) {
      thumbnail = null;
      return;
    }
    
    ImageIcon tmpIcon = new ImageIcon(file.getPath());
    height = tmpIcon.getIconHeight();
    width = tmpIcon.getIconWidth();
    if (tmpIcon != null) {
      if (tmpIcon.getIconWidth() > 200) {
        thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(
          200, -1, 1));
      } else {
        thumbnail = tmpIcon;
      }
    }
  }
  
  public void propertyChange(PropertyChangeEvent e) {
    boolean update = false;
    String prop = e.getPropertyName();
    

    if ("directoryChanged".equals(prop)) {
      file = null;
      update = true;

    }
    else if ("SelectedFileChangedProperty".equals(prop)) {
      file = ((File)e.getNewValue());
      update = true;
    }
    

    if ((update) && (needUpdating)) {
      thumbnail = null;
      loadImage();
      repaint();
    }
  }
  
  protected void paintComponent(Graphics g) {
    if (thumbnail == null) {
      loadImage();
    }
    
    if (thumbnail != null) {
      int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
      int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;
      
      if (y < 60) {
        y = 60;
      }
      
      thumbnail.paintIcon(this, g, x, y);
      g.drawString(Messages.getString("Dimensions___") + width + " x " + 
        height, 50, 30);
    }
  }
}
