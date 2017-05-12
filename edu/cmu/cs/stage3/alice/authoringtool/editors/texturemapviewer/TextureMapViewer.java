package edu.cmu.cs.stage3.alice.authoringtool.editors.texturemapviewer;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.Editor;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.property.ImageProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;














public class TextureMapViewer
  extends JPanel
  implements Editor
{
  public String editorName = Messages.getString("TextureMap_Viewer");
  
  protected TextureMap textureMap;
  protected ImagePanel texturePanel = new ImagePanel();
  
  public TextureMapViewer() {
    jbInit();
  }
  
  public JComponent getJComponent() {
    return this;
  }
  
  public Object getObject() {
    return textureMap;
  }
  
  public void setAuthoringTool(AuthoringTool authoringTool) {}
  
  public void setObject(TextureMap textureMap)
  {
    this.textureMap = textureMap;
    if (textureMap != null) {
      texturePanel.setImage(image.getImageValue());
    } else {
      texturePanel.setImage(null);
    }
    texturePanel.revalidate();
    texturePanel.repaint(); }
  
  public void stateChanging(AuthoringToolStateChangedEvent ev) {}
  
  protected class ImagePanel extends JPanel { Image image;
    int buffer = 5;
    int imageWidth = 0;
    int imageHeight = 0;
    
    public ImagePanel() {
      setBackground(Color.black);
    }
    
    public void setImage(Image image) {
      this.image = image;
      if (image != null) {
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(image, 0);
        try {
          tracker.waitForAll(1000L);
          imageWidth = image.getWidth(this);
          imageHeight = image.getHeight(this);
          Dimension size = new Dimension(imageWidth + buffer * 2, imageHeight + buffer * 2);
          setMinimumSize(size);
          setPreferredSize(size);
        } catch (InterruptedException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Interrupted_while_loading_image_"), e);
        }
      } else {
        Dimension size = new Dimension(buffer * 2, buffer * 2);
        setMinimumSize(size);
        setPreferredSize(size);
      }
    }
    
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (image != null) {
        g.setColor(Color.white);
        g.drawRect(buffer, buffer, imageWidth + 1, imageHeight + 1);
        g.drawImage(image, buffer + 1, buffer + 1, this); } } }
  
  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  
  public void stateChanged(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarted(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopped(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPaused(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaved(AuthoringToolStateChangedEvent ev) {}
  
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane textureScrollPane = new JScrollPane();
  
  private void jbInit() {
    setLayout(borderLayout1);
    setBackground(Color.black);
    textureScrollPane.getViewport().setBackground(Color.black);
    textureScrollPane.setBorder(null);
    add(textureScrollPane, "Center");
    textureScrollPane.getViewport().add(texturePanel, null);
  }
}
