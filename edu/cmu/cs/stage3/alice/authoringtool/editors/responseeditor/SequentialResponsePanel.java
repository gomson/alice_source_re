package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.response.DoInOrder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
























public class SequentialResponsePanel
  extends CompositeResponsePanel
{
  protected static BufferedImage sequentialBackgroundImage;
  
  public SequentialResponsePanel()
  {
    headerText = AuthoringToolResources.getReprForValue(DoInOrder.class);
  }
  
  public void set(DoInOrder r, AuthoringTool authoringToolIn) {
    super.set(r, authoringToolIn);
  }
  
  protected Color getCustomBackgroundColor() {
    return AuthoringToolResources.getColor("DoInOrder");
  }
  
  protected void updateGUI() {
    super.updateGUI();
  }
  



  protected static Dimension sequentialBackgroundImageSize = new Dimension(-1, -1);
  
  protected void createBackgroundImage(int width, int height) {
    sequentialBackgroundImageSize.setSize(width, height);
    sequentialBackgroundImage = new BufferedImage(width, height, 2);
    Graphics2D g = (Graphics2D)sequentialBackgroundImage.getGraphics();
    g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
    g.setColor(backgroundColor);
    g.fillRect(0, 0, width, height);
  }
  




  protected void paintTextureEffect(Graphics g, Rectangle bounds)
  {
    if ((width > sequentialBackgroundImageSizewidth) || (height > sequentialBackgroundImageSizeheight)) {
      createBackgroundImage(width, height);
    }
    g.setClip(x, y, width, height);
    g.drawImage(sequentialBackgroundImage, x, y, this);
  }
}
