package edu.cmu.cs.stage3.image.codec;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import sun.awt.image.InputStreamImageSource;






































































public class GIFImageDecoder
  extends ImageDecoderImpl
{
  BufferedImage bufferedImage = null;
  
  public GIFImageDecoder(InputStream input, ImageDecodeParam param)
  {
    super(input, param);
  }
  
  private synchronized RenderedImage decode() throws IOException {
    if (bufferedImage == null) {
      InputStreamImageSource source = new GIFImageSource(input);
      Image image = 
        Toolkit.getDefaultToolkit().createImage(source);
      
      MediaTracker tracker = new MediaTracker(new Canvas());
      tracker.addImage(image, 0);
      try {
        tracker.waitForID(0);
      } catch (InterruptedException e) {
        throw new RuntimeException(JaiI18N.getString("InterruptedException_occured_while_loading_the_image_using_MediaTracker_"));
      }
      if (tracker.isErrorID(0)) {
        throw new RuntimeException(JaiI18N.getString("MediaTracker_is_unable_to_load_the_image_"));
      }
      tracker.removeImage(image);
      

      int width = image.getWidth(null);
      int height = image.getHeight(null);
      
      bufferedImage = 
        new BufferedImage(width, height, 5);
      Graphics g = bufferedImage.getGraphics();
      g.drawImage(image, 0, 0, null);
    }
    
    return bufferedImage;
  }
  
  public RenderedImage decodeAsRenderedImage(int page) throws IOException
  {
    if (page != 0) {
      throw new IOException(JaiI18N.getString("Illegal_page_requested_from_a_GIF_file_"));
    }
    return decode();
  }
}
