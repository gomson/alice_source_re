package edu.cmu.cs.stage3.image.codec;

import java.awt.image.RenderedImage;
import java.io.IOException;














































public class FPXImageDecoder
  extends ImageDecoderImpl
{
  public FPXImageDecoder(SeekableStream input, ImageDecodeParam param)
  {
    super(input, param);
  }
  
  public RenderedImage decodeAsRenderedImage(int page) throws IOException
  {
    if (page != 0) {
      throw new IOException(JaiI18N.getString("Illegal_page_requested_from_an_FPX_file_"));
    }
    return new FPXImage(input, (FPXDecodeParam)param);
  }
}
