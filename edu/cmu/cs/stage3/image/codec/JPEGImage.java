package edu.cmu.cs.stage3.image.codec;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.InputStream;





























































class JPEGImage
  extends SimpleRenderedImage
{
  private BufferedImage image = null;
  




  public JPEGImage(InputStream stream)
  {
    JPEGImageDecoder decoder = 
      JPEGCodec.createJPEGDecoder(stream);
    try
    {
      image = decoder.decodeAsBufferedImage();
    } catch (ImageFormatException e) {
      throw new RuntimeException(JaiI18N.getString("Unable_to_process_image_stream__incorrect_format_"));
    } catch (IOException e) {
      throw new RuntimeException(JaiI18N.getString("Unable_to_process_image_stream__I_O_error_"));
    }
    
    minX = 0;
    minY = 0;
    tileWidth = (this.width = image.getWidth());
    tileHeight = (this.height = image.getHeight());
    


    if (!(image.getSampleModel() instanceof ComponentSampleModel)) {
      int type = -1;
      int numBands = image.getSampleModel().getNumBands();
      if (numBands == 1) {
        type = 10;
      } else if (numBands == 3) {
        type = 5;
      } else if (numBands == 4) {
        type = 6;
      } else {
        throw new RuntimeException(JaiI18N.getString("Cannot_decode_a_2_banded_image_with_a_PackedColorModel_"));
      }
      
      BufferedImage bi = new BufferedImage(width, height, type);
      Graphics2D g = bi.createGraphics();
      g.drawRenderedImage(image, new AffineTransform());
      image = bi;
    }
    
    sampleModel = image.getSampleModel();
    colorModel = image.getColorModel();
  }
  
  public synchronized Raster getTile(int tileX, int tileY) {
    if ((tileX != 0) || (tileY != 0)) {
      throw new IllegalArgumentException(JaiI18N.getString("Illegal_tile_requested_from_a_JPEG_image_"));
    }
    
    return image.getTile(0, 0);
  }
}
