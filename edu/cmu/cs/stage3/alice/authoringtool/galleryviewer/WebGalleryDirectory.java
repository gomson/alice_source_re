package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.ImageIcon;



















public class WebGalleryDirectory
  extends WebGalleryObject
{
  protected GalleryViewer.DirectoryStructure directoryData;
  protected boolean isTopLevelDirectory = false;
  
  public WebGalleryDirectory() {}
  
  protected static Color webDirColor = new Color(189, 184, 139);
  
  protected String getToolTipString() {
    return "<html><body><p>" + Messages.getString("Group_of_Objects") + "</p><p>" + Messages.getString("Click_to_open_this_group_") + "</p></body></html>"; }
  
  public void set(GalleryViewer.ObjectXmlData dataIn) throws IllegalArgumentException
  {
    if (dataIn != null) {
      directoryData = directoryData;
      super.set(dataIn);
    }
  }
  
  protected String getClassName() {
    return " ";
  }
  
  protected void guiInit()
  {
    super.guiInit();
    setCursor(new Cursor(12));
    setBackground(webDirColor);
    setDragEnabled(false);
    remove(grip);
  }
  
  protected void updateGUI() {
    super.updateGUI();
  }
  
  public void setImage(ImageIcon imageIcon) {
    if (imageIcon == GalleryViewer.noImageIcon) {
      super.setImage(GalleryViewer.noFolderImageIcon);
    }
    else {
      super.setImage(imageIcon);
    }
  }
  
  public void respondToMouse() {
    if (mainViewer != null) {
      int dialogVal = -1;
      if ((!GalleryViewer.alreadyEnteredWebGallery) && (mainViewer.shouldShowWebWarning())) {
        dialogVal = DialogManager.showConfirmDialog(Messages.getString("You_are_about_to_enter_the_online_gallery__This_is_accessed_through_the_internet_n") + 
          Messages.getString("and_is_potentially_slow_depending_on_your_connection_"), Messages.getString("Web_gallery_may_be_slow"), 2);
        if (dialogVal == 0) {
          GalleryViewer.enteredWebGallery();
          mainViewer.changeDirectory(directoryData);
        }
      }
      else {
        GalleryViewer.enteredWebGallery();
        mainViewer.changeDirectory(directoryData);
      }
    }
  }
  
  public void galleryMouseExited() {}
  
  public void galleryMouseEntered() {}
}
