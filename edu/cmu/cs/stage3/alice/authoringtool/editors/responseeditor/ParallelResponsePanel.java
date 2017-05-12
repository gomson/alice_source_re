package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.response.DoTogether;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;



















public class ParallelResponsePanel
  extends CompositeResponsePanel
{
  protected static BufferedImage parallelBackgroundImage;
  
  public ParallelResponsePanel()
  {
    headerText = AuthoringToolResources.getReprForValue(DoTogether.class);
  }
  
  public void set(DoTogether r, AuthoringTool authoringToolIn) {
    super.set(r, authoringToolIn);
  }
  




  protected void updateGUI()
  {
    super.updateGUI();
  }
  
  protected Color getCustomBackgroundColor() {
    return AuthoringToolResources.getColor("DoTogether");
  }
  




  protected static Dimension parallelBackgroundImageSize = new Dimension(-1, -1);
  
  protected void createBackgroundImage(int width, int height) {
    parallelBackgroundImageSize.setSize(width, height);
    parallelBackgroundImage = new BufferedImage(width, height, 2);
    Graphics2D g = (Graphics2D)parallelBackgroundImage.getGraphics();
    g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
    g.setColor(backgroundColor);
    g.fillRect(0, 0, width, height);
  }
  




  protected void paintTextureEffect(Graphics g, Rectangle bounds)
  {
    if ((width > parallelBackgroundImageSizewidth) || (height > parallelBackgroundImageSizeheight)) {
      createBackgroundImage(width, height);
    }
    g.setClip(x, y, width, height);
    g.drawImage(parallelBackgroundImage, x, y, this);
  }
}
