package edu.cmu.cs.stage3.image.codec;










public class TIFFEncodeParam
  implements ImageEncodeParam
{
  public static final int COMPRESSION_NONE = 1;
  








  public static final int COMPRESSION_PACKBITS = 2;
  








  public static final int COMPRESSION_GROUP3_1D = 3;
  








  public static final int COMPRESSION_GROUP3_2D = 4;
  








  public static final int COMPRESSION_GROUP4 = 5;
  








  public static final int COMPRESSION_LZW = 6;
  







  private int compression = 1;
  private boolean writeTiled = false;
  



  public TIFFEncodeParam() {}
  


  public int getCompression()
  {
    return compression;
  }
  








  public void setCompression(int compression)
  {
    if (compression != 1) {
      throw new Error(JaiI18N.getString("This_compression_scheme_is_not_implemented_yet_"));
    }
    
    if ((compression != 1) && 
      (compression != 2) && 
      (compression != 3) && 
      (compression != 4) && 
      (compression != 5) && 
      (compression != 6))
    {
      throw new Error(JaiI18N.getString("Unsupported_compression_scheme_specified_"));
    }
    
    this.compression = compression;
  }
  


  public boolean getWriteTiled()
  {
    return writeTiled;
  }
  








  public void setWriteTiled(boolean writeTiled)
  {
    if (writeTiled) {
      throw new Error(JaiI18N.getString("Writing_out_Tiled_TIFF_images_is_not_implemented_yet_"));
    }
    
    this.writeTiled = writeTiled;
  }
}
