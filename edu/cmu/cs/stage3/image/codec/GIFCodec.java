package edu.cmu.cs.stage3.image.codec;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;















































public final class GIFCodec
  extends ImageCodec
{
  public GIFCodec() {}
  
  public String getFormatName()
  {
    return "gif";
  }
  
  public Class getEncodeParamClass()
  {
    return Object.class;
  }
  
  public Class getDecodeParamClass()
  {
    return Object.class;
  }
  

  public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param)
  {
    return false;
  }
  










  protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param)
  {
    return null;
  }
  

  protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param)
  {
    return new GIFImageDecoder(src, param);
  }
  

  protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param)
    throws IOException
  {
    return new GIFImageDecoder(new FileInputStream(src), null);
  }
  

  protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param)
  {
    return new GIFImageDecoder(src, param);
  }
  

  public int getNumHeaderBytes()
  {
    return 4;
  }
  
  public boolean isFormatRecognized(byte[] header)
  {
    return (header[0] == 71) && 
      (header[1] == 73) && 
      (header[2] == 70) && 
      (header[3] == 56);
  }
}
