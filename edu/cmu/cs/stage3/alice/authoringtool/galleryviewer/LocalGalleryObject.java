package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import edu.cmu.cs.stage3.image.ImageIO;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Image;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.ImageIcon;



















public class LocalGalleryObject
  extends GalleryObject
{
  public static final String tumbnailFilename = "thumbnail.png";
  
  public LocalGalleryObject() {}
  
  protected String getToolTipString()
  {
    return "<html><body><p>" + Messages.getString("Object") + "</p><p>" + Messages.getString("Click_to_add_this_object_to_the_world") + "</p></body></html>";
  }
  
  protected void guiInit() {
    super.guiInit();
    if (data != null) {
      if (data.type == 3) {
        location = "CD-ROM";
      }
      else {
        location = Messages.getString("your_computer");
      }
    }
  }
  
  public void set(GalleryViewer.ObjectXmlData dataIn) throws IllegalArgumentException {
    super.set(dataIn);
    if (data != null) {
      if (data.type == 3) {
        location = "CD-ROM";
      }
      else {
        location = Messages.getString("your_computer");
      }
    }
  }
  
  public static Image retrieveImage(String root, String filename, long timestamp) {
    String imageFilename = root + filename;
    ImageIcon toReturn = null;
    if (imageFilename != null) {
      try {
        if ((imageFilename.indexOf(".a2c") == imageFilename.length() - 4) || (imageFilename.indexOf(".a2w") == imageFilename.length() - 4)) {
          ZipFile zip = new ZipFile(imageFilename);
          ZipEntry entry = zip.getEntry("thumbnail.png");
          if (entry != null) {
            InputStream stream = zip.getInputStream(entry);
            Image thumbImage = ImageIO.load("png", stream);
            if (thumbImage != null) {
              toReturn = new ImageIcon(thumbImage);
            }
            else {
              return null;
            }
          }
          else {
            return null;
          }
          zip.close();
        }
        else {
          toReturn = new ImageIcon(imageFilename);
        }
      }
      catch (Exception e) {
        return null;
      }
      if ((toReturn.getIconHeight() < 10) || (toReturn.getIconWidth() < 10)) {
        return null;
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
        if (imageFilename != null) {
          try {
            if ((imageFilename.indexOf(".a2c") == imageFilename.length() - 4) || (imageFilename.indexOf(".a2w") == imageFilename.length() - 4)) {
              ZipFile zip = new ZipFile(imageFilename);
              ZipEntry entry = zip.getEntry("thumbnail.png");
              if (entry != null) {
                InputStream stream = zip.getInputStream(entry);
                Image thumbImage = ImageIO.load("png", stream);
                if (thumbImage != null) {
                  image = new ImageIcon(thumbImage);
                }
                else {
                  image = GalleryViewer.noImageIcon;
                }
              }
              else {
                image = GalleryViewer.noImageIcon;
              }
              zip.close();
            }
            else {
              image = new ImageIcon(imageFilename);
            }
          }
          catch (Exception e) {
            image = GalleryViewer.noImageIcon;
          }
          
        } else {
          image = GalleryViewer.noImageIcon;
        }
        if ((image.getIconHeight() < 10) || (image.getIconWidth() < 10)) {
          image = GalleryViewer.noImageIcon;
        }
        setImage(image);
      }
    };
    Thread t = new Thread(doLoad);
    t.start();
  }
}
