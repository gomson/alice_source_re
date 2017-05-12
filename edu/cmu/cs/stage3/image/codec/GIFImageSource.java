package edu.cmu.cs.stage3.image.codec;

import java.io.BufferedInputStream;
import java.io.InputStream;
import sun.awt.image.FileImageSource;
import sun.awt.image.GifImageDecoder;
import sun.awt.image.ImageDecoder;




















































class GIFImageSource
  extends FileImageSource
{
  InputStream is;
  
  public GIFImageSource(InputStream is)
  {
    super("junk");
    
    if ((is instanceof BufferedInputStream)) {
      this.is = is;
    } else {
      this.is = new BufferedInputStream(is);
    }
  }
  
  protected ImageDecoder getDecoder()
  {
    return new GifImageDecoder(this, is);
  }
}
