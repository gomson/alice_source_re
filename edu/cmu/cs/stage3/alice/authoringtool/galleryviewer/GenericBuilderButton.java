package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;























public abstract class GenericBuilderButton
  extends GalleryObject
{
  public GenericBuilderButton() {}
  
  public void set(GalleryViewer.ObjectXmlData dataIn, ImageIcon icon)
    throws IllegalArgumentException
  {
    if (dataIn != null) {
      clean();
      data = dataIn;
      image = icon;
      mainViewer = data.mainViewer;
      if (data.transferable != null)
        setTransferable(data.transferable);
      updateGUI();
      wakeUp();
    } else {
      clean();
      data = null;
      mainViewer = null;
    }
  }
  
  public String getUniqueIdentifier() {
    if (data != null) {
      return data.name;
    }
    return "GenericBuilder";
  }
  
  public void loadImage() {
    imageLabel.setText(null);
    imageLabel.setIcon(image);
  }
  
  protected String getClassName() {
    return " ";
  }
  
  protected void updateGUI() {
    containingPanel.removeAll();
    classLabel.setText(getClassName());
    containingPanel.add(classLabel, new GridBagConstraints(0, 0, 2, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
    containingPanel.add(nameLabel, new GridBagConstraints(0, 1, 2, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
    loadImage();
    containingPanel.add(imageLabel, new GridBagConstraints(0, 2, 2, 1, 0.0D, 0.0D, 11, 0, new Insets(0, 0, 0, 0), 0, 0));
    nameLabel.setText(data.name);
    locationLabel.setText(" ");
    containingPanel.add(locationLabel, new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
    containingPanel.revalidate();
    containingPanel.repaint();
    add(containingPanel, "Center");
  }
  
  protected void guiInit() {
    super.guiInit();
    setCursor(new Cursor(12));
    setDragEnabled(false);
    remove(grip);
    loadImage();
  }
}
