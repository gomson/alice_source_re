package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;






















public class WebGalleryObject
  extends GalleryObject
{
  public WebGalleryObject() {}
  
  protected boolean needToWriteThumbnail = false;
  
  protected String getToolTipString() {
    return "<html><body><p>" + Messages.getString("Object") + "</p><p>" + Messages.getString("Click_to_add_this_object_to_the_world") + "</p></body></html>";
  }
  
  protected void guiInit() {
    super.guiInit();
    location = Messages.getString("the_Web");
  }
  
  public static Image retrieveImage(String root, String filename, long timestamp) {
    String imageFilename = root + filename;
    boolean needToLoad = false;
    
    ImageIcon toReturn = null;
    if (imageFilename != null) {
      String cacheFilename = GalleryViewer.cacheDir + GalleryViewer.reverseWebReady(filename);
      File cachedImageFile = new File(cacheFilename);
      if ((cachedImageFile.exists()) && (cachedImageFile.canRead())) {
        if (cachedImageFile.lastModified() == timestamp) {
          toReturn = new ImageIcon(cacheFilename);
        }
        else {
          needToLoad = true;
        }
      }
      else {
        needToLoad = true;
      }
      if (needToLoad) {
        try {
          URL imageURL = new URL(imageFilename);
          toReturn = new ImageIcon(imageURL);
        }
        catch (Exception e) {
          toReturn = null;
        }
      }
      if ((toReturn.getIconHeight() < 10) || (toReturn.getIconWidth() < 10)) {
        toReturn = null;
      }
    }
    return toReturn.getImage();
  }
  
  public void loadImage() {
    String tempFilename = null;
    if ((data != null) && (data.imageFilename != null)) {
      tempFilename = rootPath + data.imageFilename;
    }
    final String imageFilename = tempFilename;
    Runnable doLoad = new Runnable() {
      public void run() {
        String cacheFilename = null;
        if (imageFilename != null) {
          cacheFilename = GalleryViewer.cacheDir + GalleryViewer.reverseWebReady(data.imageFilename);
          File cachedImageFile = new File(cacheFilename);
          if ((cachedImageFile.exists()) && (cachedImageFile.canRead())) {
            if (cachedImageFile.lastModified() == data.timeStamp) {
              image = new ImageIcon(cacheFilename);
            }
            else {
              needToWriteThumbnail = true;
            }
          }
          else {
            needToWriteThumbnail = true;
          }
          if (needToWriteThumbnail) {
            try {
              URL imageURL = new URL(imageFilename);
              image = new ImageIcon(imageURL);
            }
            catch (Exception e) {
              image = GalleryViewer.noImageIcon;
            }
          }
        }
        else {
          image = GalleryViewer.noImageIcon;
          needToWriteThumbnail = false;
        }
        if ((image.getIconHeight() < 10) || (image.getIconWidth() < 10)) {
          image = GalleryViewer.noImageIcon;
          needToWriteThumbnail = false;
        }
        setImage(image);
        if ((needToWriteThumbnail) && (cacheFilename != null)) {
          WebGalleryObject.storeThumbnail(cacheFilename, image.getImage(), data.timeStamp);
        }
      }
    };
    Thread t = new Thread(doLoad);
    t.start();
  }
}
