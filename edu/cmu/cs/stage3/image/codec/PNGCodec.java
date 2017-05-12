package edu.cmu.cs.stage3.image.codec;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
















































public final class PNGCodec
  extends ImageCodec
{
  public PNGCodec() {}
  
  public String getFormatName()
  {
    return "png";
  }
  
  public Class getEncodeParamClass()
  {
    return PNGEncodeParam.class;
  }
  
  public Class getDecodeParamClass()
  {
    return PNGDecodeParam.class;
  }
  

  public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param)
  {
    SampleModel sampleModel = im.getSampleModel();
    

    int dataType = sampleModel.getTransferType();
    if ((dataType == 4) || 
      (dataType == 5)) {
      return false;
    }
    
    int[] sampleSize = sampleModel.getSampleSize();
    int bitDepth = sampleSize[0];
    

    for (int i = 1; i < sampleSize.length; i++) {
      if (sampleSize[i] != bitDepth) {
        return false;
      }
    }
    

    if ((bitDepth < 1) || (bitDepth > 16)) {
      return false;
    }
    

    int numBands = sampleModel.getNumBands();
    if ((numBands < 1) || (numBands > 4)) {
      return false;
    }
    

    ColorModel colorModel = im.getColorModel();
    if (((colorModel instanceof IndexColorModel)) && (
      (numBands != 1) || (bitDepth > 8))) {
      return false;
    }
    


    if (param != null) {
      if ((param instanceof PNGEncodeParam)) {
        if ((colorModel instanceof IndexColorModel)) {
          if (!(param instanceof PNGEncodeParam.Palette)) {
            return false;
          }
        } else if (numBands < 3) {
          if (!(param instanceof PNGEncodeParam.Gray)) {
            return false;
          }
        }
        else if (!(param instanceof PNGEncodeParam.RGB)) {
          return false;
        }
      }
      else {
        return false;
      }
    }
    
    return true;
  }
  

  protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param)
  {
    PNGEncodeParam p = null;
    if (param != null) {
      p = (PNGEncodeParam)param;
    }
    return new PNGImageEncoder(dst, p);
  }
  

  protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param)
  {
    PNGDecodeParam p = null;
    if (param != null) {
      p = (PNGDecodeParam)param;
    }
    return new PNGImageDecoder(src, p);
  }
  

  protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param)
    throws IOException
  {
    PNGDecodeParam p = null;
    if (param != null) {
      p = (PNGDecodeParam)param;
    }
    return new PNGImageDecoder(new FileInputStream(src), p);
  }
  

  protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param)
  {
    PNGDecodeParam p = null;
    if (param != null) {
      p = (PNGDecodeParam)param;
    }
    return new PNGImageDecoder(src, p);
  }
  
  public int getNumHeaderBytes()
  {
    return 8;
  }
  
  public boolean isFormatRecognized(byte[] header)
  {
    return (header[0] == -119) && 
      (header[1] == 80) && 
      (header[2] == 78) && 
      (header[3] == 71) && 
      (header[4] == 13) && 
      (header[5] == 10) && 
      (header[6] == 26) && 
      (header[7] == 10);
  }
}
