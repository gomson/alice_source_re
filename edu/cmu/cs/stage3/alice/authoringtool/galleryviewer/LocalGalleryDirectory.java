package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.ImageIcon;


















public class LocalGalleryDirectory
  extends LocalGalleryObject
{
  protected GalleryViewer.DirectoryStructure directoryData;
  
  public LocalGalleryDirectory() {}
  
  protected static Color localDirColor = new Color(189, 184, 139);
  
  protected String getToolTipString() {
    return "<html><body><p>" + Messages.getString("Group_of_Objects") + "</p><p>" + Messages.getString("Click_to_open_this_group_") + "</p></body></html>";
  }
  
  public void set(GalleryViewer.ObjectXmlData dataIn) throws IllegalArgumentException {
    if (dataIn != null) {
      directoryData = directoryData;
      super.set(dataIn);
    }
  }
  
  public void setImage(ImageIcon imageIcon) {
    if (imageIcon == GalleryViewer.noImageIcon) {
      super.setImage(GalleryViewer.noFolderImageIcon);
    }
    else {
      super.setImage(imageIcon);
    }
  }
  
  protected String getClassName() {
    return " ";
  }
  
  protected void guiInit() {
    super.guiInit();
    setCursor(new Cursor(12));
    setBackground(localDirColor);
    setDragEnabled(false);
    remove(grip);
  }
  
  public void respondToMouse() {
    if (mainViewer != null) {
      mainViewer.changeDirectory(directoryData);
    }
  }
  
  public void galleryMouseExited() {}
  
  public void galleryMouseEntered() {}
}
