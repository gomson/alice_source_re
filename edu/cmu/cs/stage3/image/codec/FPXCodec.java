package edu.cmu.cs.stage3.image.codec;

import java.awt.image.RenderedImage;
import java.io.OutputStream;














































public final class FPXCodec
  extends ImageCodec
{
  public FPXCodec() {}
  
  public String getFormatName()
  {
    return "fpx";
  }
  
  public Class getEncodeParamClass()
  {
    return null;
  }
  
  public Class getDecodeParamClass()
  {
    return FPXDecodeParam.class;
  }
  

  public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param)
  {
    return false;
  }
  

  protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param)
  {
    throw new RuntimeException(JaiI18N.getString("FPX_encoding_not_supported_yet_"));
  }
  

  protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param)
  {
    return new FPXImageDecoder(src, param);
  }
  
  public int getNumHeaderBytes()
  {
    return 8;
  }
  
  public boolean isFormatRecognized(byte[] header)
  {
    return (header[0] == -48) && 
      (header[1] == -49) && 
      (header[2] == 17) && 
      (header[3] == -32) && 
      (header[4] == -95) && 
      (header[5] == -79) && 
      (header[6] == 26) && 
      (header[7] == -31);
  }
}
