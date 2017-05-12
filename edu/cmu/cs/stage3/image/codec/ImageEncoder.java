package edu.cmu.cs.stage3.image.codec;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

public abstract interface ImageEncoder
{
  public abstract ImageEncodeParam getParam();
  
  public abstract void setParam(ImageEncodeParam paramImageEncodeParam);
  
  public abstract OutputStream getOutputStream();
  
  public abstract void encode(Raster paramRaster, ColorModel paramColorModel)
    throws IOException;
  
  public abstract void encode(RenderedImage paramRenderedImage)
    throws IOException;
}
