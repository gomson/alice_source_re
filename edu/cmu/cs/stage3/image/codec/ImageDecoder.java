package edu.cmu.cs.stage3.image.codec;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;

public abstract interface ImageDecoder
{
  public abstract ImageDecodeParam getParam();
  
  public abstract void setParam(ImageDecodeParam paramImageDecodeParam);
  
  public abstract SeekableStream getInputStream();
  
  public abstract int getNumPages()
    throws IOException;
  
  public abstract Raster decodeAsRaster()
    throws IOException;
  
  public abstract Raster decodeAsRaster(int paramInt)
    throws IOException;
  
  public abstract RenderedImage decodeAsRenderedImage()
    throws IOException;
  
  public abstract RenderedImage decodeAsRenderedImage(int paramInt)
    throws IOException;
}
