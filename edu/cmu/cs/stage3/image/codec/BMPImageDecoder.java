package edu.cmu.cs.stage3.image.codec;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;





















































public class BMPImageDecoder
  extends ImageDecoderImpl
{
  public BMPImageDecoder(InputStream input, ImageDecodeParam param)
  {
    super(input, param);
  }
  
  public RenderedImage decodeAsRenderedImage(int page) throws IOException
  {
    if (page != 0) {
      throw new IOException(JaiI18N.getString("Illegal_page_requested_from_a_BMP_file_"));
    }
    return new BMPImage(input);
  }
}
