package edu.cmu.cs.stage3.image;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

















public class ImageUtilities
{
  private static MediaTracker s_mediaTracker = new MediaTracker(new Panel());
  private static ImageObserver s_imageObserver = new ImageObserver()
  {
    public boolean imageUpdate(Image image, int infoflags, int x, int y, int width, int height) { return true; } };
  
  public ImageUtilities() {}
  
  private static void waitForImage(Image image) throws InterruptedException { s_mediaTracker.addImage(image, 0);
    try {
      s_mediaTracker.waitForID(0);
    } finally {
      s_mediaTracker.removeImage(image);
    }
  }
  
  public static int getWidth(Image image) throws InterruptedException { waitForImage(image);
    return image.getWidth(s_imageObserver);
  }
  
  public static int getHeight(Image image) throws InterruptedException { waitForImage(image);
    return image.getHeight(s_imageObserver);
  }
  
  public static int[] getPixels(Image image, int width, int height) throws InterruptedException { int[] pixels = new int[width * height];
    PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
    pg.grabPixels();
    if ((pg.getStatus() & 0x80) != 0) {
      throw new RuntimeException("image fetch aborted or errored");
    }
    return pixels;
  }
}
