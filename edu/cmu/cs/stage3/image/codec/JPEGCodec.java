package edu.cmu.cs.stage3.image.codec;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;















































public final class JPEGCodec
  extends ImageCodec
{
  public JPEGCodec() {}
  
  public String getFormatName()
  {
    return "jpeg";
  }
  
  public Class getEncodeParamClass()
  {
    return JPEGEncodeParam.class;
  }
  
  public Class getDecodeParamClass()
  {
    return Object.class;
  }
  

  public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param)
  {
    return true;
  }
  

  protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param)
  {
    JPEGEncodeParam p = null;
    if (param != null) {
      p = (JPEGEncodeParam)param;
    }
    
    return new JPEGImageEncoder(dst, p);
  }
  

  protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param)
  {
    return new JPEGImageDecoder(src, null);
  }
  

  protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param)
    throws IOException
  {
    return new JPEGImageDecoder(new FileInputStream(src), null);
  }
  

  protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param)
  {
    return new JPEGImageDecoder(src, null);
  }
  
  public int getNumHeaderBytes()
  {
    return 3;
  }
  
  public boolean isFormatRecognized(byte[] header)
  {
    return (header[0] == -1) && 
      (header[1] == -40) && 
      (header[2] == -1);
  }
}
