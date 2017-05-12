package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel.DnDGrip;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.image.ImageIO;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Vector3d;

public abstract class GalleryObject
  extends DnDGroupingPanel
  implements GUIElement
{
  protected GalleryViewer.ObjectXmlData data;
  protected JLabel nameLabel;
  protected JLabel classLabel;
  protected JLabel imageLabel;
  protected JLabel sizeLabel;
  protected JLabel locationLabel;
  protected JPanel containingPanel;
  protected ImageIcon image;
  protected GalleryViewer mainViewer;
  protected String displayName;
  protected String location;
  protected boolean hasAttribution = true;
  protected String rootPath;
  protected GalleryMouseAdapter mouseAdapter = new GalleryMouseAdapter();
  
  protected class GalleryMouseAdapter extends CustomMouseAdapter { protected GalleryMouseAdapter() {}
    
    public void mouseExited(MouseEvent m) { galleryMouseExited(); }
    
    public void mouseEntered(MouseEvent m)
    {
      galleryMouseEntered();
    }
    
    protected void singleClickResponse(MouseEvent ev) {
      respondToMouse();
    }
  }
  

  protected static final Color HIGHLITE = new Color(255, 255, 255);
  protected static final Color BACKGROUND = new Color(128, 128, 128);
  
  protected static final Color FASTEST_COLOR = new Color(0, 255, 0);
  protected static final Color MIDDLE_COLOR = new Color(255, 255, 0);
  protected static final Color SLOWEST_COLOR = new Color(255, 0, 0);
  
  protected static final int FASTEST_SIZE = 0;
  
  protected static final int SLOWEST_SIZE = 1000;
  
  protected Color sizeColor;
  protected boolean mouseOver = false;
  protected Transferable transferable;
  
  protected String getToolTipString()
  {
    return "";
  }
  
  public void setToolTipText(String text) {
    super.setToolTipText(text);
    



    containingPanel.setToolTipText(text);
    grip.setToolTipText(text);
  }
  
  public GalleryObject()
  {
    guiInit();
  }
  
  public Vector3d getBoundingBox() {
    return data.dimensions;
  }
  
  public Image getImage() {
    return super.getImage();
  }
  
  public String getUniqueIdentifier() {
    String root = "";
    String path = GalleryViewer.reverseWebReady(data.objectFilename.replace(':', '_'));
    if (data.type == 2) {
      root = GalleryViewer.webGalleryName;
    }
    if (data.type == 3) {
      root = GalleryViewer.cdGalleryName;
    }
    if (data.type == 1) {
      root = GalleryViewer.localGalleryName;
    }
    int index = path.lastIndexOf(".xml");
    if ((index > -1) && (index < path.length())) {
      int dirIndex = path.lastIndexOf(File.separator);
      if ((dirIndex > -1) && (dirIndex < path.length())) {
        path = path.substring(0, dirIndex);
      }
      else {
        path = "";
      }
    }
    if (path != "") {
      path = File.separator + path;
    }
    String toReturn = root + path;
    return toReturn;
  }
  
  public void set(GalleryViewer.ObjectXmlData dataIn) throws IllegalArgumentException {
    if (dataIn != null) {
      clean();
      data = dataIn;
      mainViewer = data.mainViewer;
      if (data.transferable != null) {
        setTransferable(data.transferable);
      }
      if (data.directoryData != null) {
        rootPath = data.directoryData.rootNode.rootPath;
      }
      else if (data.parentDirectory != null) {
        rootPath = data.parentDirectory.rootNode.rootPath;
      }
      

      displayName = GalleryViewer.cleanUpName(name);
      
      updateGUI();
      String t = objectFilename;
      if (t.endsWith(".a2c")) {
        setToolTipText(getToolTipString().replace("</body>", "<p>" + t + "</p></body>"));
      }
      wakeUp();
    }
    else {
      clean();
      data = null;
      mainViewer = null;
    }
  }
  
  public void goToSleep() {
    removeMouseListener(mouseAdapter);
    containingPanel.removeMouseListener(mouseAdapter);
    grip.removeMouseListener(mouseAdapter);
    removeDragSourceComponent(containingPanel);
  }
  
  public void wakeUp() {
    addMouseListener(mouseAdapter);
    containingPanel.addMouseListener(mouseAdapter);
    grip.addMouseListener(mouseAdapter);
    addDragSourceComponent(containingPanel);
  }
  
  public void clean() {
    goToSleep();
    data = null;
    nameLabel.setText(null);
    classLabel.setText(null);
    imageLabel.setIcon(null);
    imageLabel.setText(null);
    sizeLabel.setText(null);
    image = null;
    mainViewer = null;
  }
  
  public void die() {
    clean();
  }
  
  public abstract void loadImage();
  
  public static Image retrieveImage(String root, String filename, long timestamp)
  {
    return null;
  }
  
  protected void setGalleryViewer(GalleryViewer viewer) {
    mainViewer = viewer;
  }
  
  public void setImage(ImageIcon imageIcon) {
    image = imageIcon;
    imageLabel.setText(null);
    imageLabel.setIcon(image);
    revalidate();
    repaint();
  }
  
  protected String getClassName() {
    return "Class";
  }
  
  public static void storeThumbnail(String thumbFilename, Image toStore, long timeStamp) {
    if (toStore != null) {
      String codec = "png";
      File thumbFile = GalleryViewer.createFile(thumbFilename);
      if (thumbFile != null) {
        try {
          FileOutputStream fos = new FileOutputStream(thumbFile);
          BufferedOutputStream bos = new BufferedOutputStream(fos);
          DataOutputStream dos = new DataOutputStream(bos);
          ImageIO.store(codec, dos, toStore);
          dos.flush();
          fos.close();
          thumbFile.setLastModified(timeStamp);
        }
        catch (InterruptedException localInterruptedException) {}catch (IOException localIOException) {}
      }
    }
  }
  
  protected Color interpolateColor(int point, int low, int high, Color lowColor, Color highColor)
  {
    float dLow = (point - low) / (high - low);
    float dHigh = (high - point) / (high - low);
    int newR = (int)(highColor.getRed() * dLow) + (int)(lowColor.getRed() * dHigh);
    if (newR > 255) {
      newR = 255;
    }
    int newG = (int)(highColor.getGreen() * dLow) + (int)(lowColor.getGreen() * dHigh);
    if (newG > 255) {
      newG = 255;
    }
    int newB = (int)(highColor.getBlue() * dLow) + (int)(lowColor.getBlue() * dHigh);
    if (newB > 255) {
      newB = 255;
    }
    return new Color(newR, newG, newB);
  }
  
  protected Color getSizeColor(int toScale) {
    if (toScale > 1000) {
      return SLOWEST_COLOR;
    }
    if (toScale < 0) {
      return FASTEST_COLOR;
    }
    int middle = 500;
    if (toScale <= middle) {
      return interpolateColor(toScale, 0, middle, FASTEST_COLOR, MIDDLE_COLOR);
    }
    
    return interpolateColor(toScale, middle, 1000, MIDDLE_COLOR, SLOWEST_COLOR);
  }
  
  protected void guiInit()
  {
    setBackground(BACKGROUND);
    setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    containingPanel = new JPanel();
    containingPanel.setLayout(new GridBagLayout());
    containingPanel.setBorder(null);
    containingPanel.setOpaque(false);
    nameLabel = new JLabel();
    nameLabel.setForeground(GalleryViewer.textColor);
    nameLabel.setOpaque(false);
    classLabel = new JLabel();
    classLabel.setForeground(GalleryViewer.textColor);
    classLabel.setOpaque(false);
    classLabel.setText(getClassName());
    imageLabel = new JLabel();
    imageLabel.setOpaque(false);
    sizeLabel = new JLabel();
    sizeLabel.setOpaque(false);
    locationLabel = new JLabel();
    locationLabel.setForeground(GalleryViewer.textColor);
    locationLabel.setOpaque(false);
    image = new ImageIcon();
    add(containingPanel, "Center");
  }
  
  public static String getDisplayName(String toDisplay)
  {
    String displayNameToReturn = new String(toDisplay);
    if (Character.isLowerCase(displayNameToReturn.charAt(0))) {
      displayNameToReturn = Character.toUpperCase(displayNameToReturn.charAt(0)) + displayNameToReturn.substring(1);
    }
    return displayNameToReturn;
  }
  
  protected void updateGUI() {
    containingPanel.removeAll();
    containingPanel.add(classLabel, new GridBagConstraints(1, 0, 2, 1, 0.0D, 0.0D, 11, 0, new Insets(0, 0, 0, 0), 0, 0));
    containingPanel.add(nameLabel, new GridBagConstraints(1, 1, 2, 1, 0.0D, 0.0D, 11, 0, new Insets(0, 0, 0, 0), 0, 0));
    containingPanel.add(imageLabel, new GridBagConstraints(1, 2, 2, 1, 0.0D, 0.0D, 11, 0, new Insets(0, 0, 0, 0), 0, 0));
    
    classLabel.setText(getClassName());
    nameLabel.setText(getDisplayName(displayName));
    sizeColor = getSizeColor(data.size);
    sizeLabel.setForeground(sizeColor);
    imageLabel.setIcon(GalleryViewer.loadingImageIcon);
    sizeLabel.setText(String.valueOf(data.size) + " kb");
    locationLabel.setText(" " + Messages.getString("on") + " " + location);
    if ((data.size > 0) && (data.type == 2)) {
      sizeLabel.setText(String.valueOf(data.size) + "kb");
      sizeLabel.setForeground(sizeColor);
      containingPanel.add(sizeLabel, new GridBagConstraints(1, 3, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
    }
    containingPanel.add(locationLabel, new GridBagConstraints(2, 3, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
    containingPanel.revalidate();
    containingPanel.repaint();
  }
  
  public void respondToMouse() {
    if (mainViewer != null) {
      mainViewer.displayModelDialog(data, image);
    }
  }
  
  public void galleryMouseExited() {
    if (mouseOver) {
      mouseOver = false;
      if (hasAttribution) {
        mainViewer.removeAttribution();
      }
      repaint();
    }
  }
  
  public void galleryMouseEntered() {
    if (!mouseOver) {
      mouseOver = true;
      if (hasAttribution) {
        mainViewer.diplayAttribution(data);
      }
      repaint();
    }
  }
  
  public void paintForeground(Graphics g)
  {
    super.paintForeground(g);
    if (mouseOver) {
      Object oldAntialiasing = null;
      if ((g instanceof Graphics2D)) {
        oldAntialiasing = ((Graphics2D)g).getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
      }
      Rectangle bounds = getBounds();
      for (int i = 1; i <= 2; i++) {
        g.setColor(new Color(HIGHLITE.getRed(), HIGHLITE.getGreen(), HIGHLITE.getBlue(), 255 - (i - 1) * 60));
        g.drawRoundRect(i, i, width - 2 * i, height - 2 * i, arcWidth, arcHeight);
      }
      if ((g instanceof Graphics2D)) {
        ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, oldAntialiasing));
      }
    }
  }
}
