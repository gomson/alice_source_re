package edu.cmu.cs.stage3.image.codec;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;


























































public abstract class ImageEncoderImpl
  implements ImageEncoder
{
  protected OutputStream output;
  protected ImageEncodeParam param;
  
  public ImageEncoderImpl(OutputStream output, ImageEncodeParam param)
  {
    this.output = output;
    this.param = param;
  }
  






  public ImageEncodeParam getParam()
  {
    return param;
  }
  







  public void setParam(ImageEncodeParam param)
  {
    this.param = param;
  }
  
  public OutputStream getOutputStream()
  {
    return output;
  }
  


  public void encode(Raster ras, ColorModel cm)
    throws IOException
  {
    RenderedImage im = new SingleTileRenderedImage(ras, cm);
    encode(im);
  }
  
  public abstract void encode(RenderedImage paramRenderedImage)
    throws IOException;
}
