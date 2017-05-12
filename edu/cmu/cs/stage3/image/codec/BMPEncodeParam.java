package edu.cmu.cs.stage3.image.codec;


















public class BMPEncodeParam
  implements ImageEncodeParam
{
  public static final int VERSION_2 = 0;
  
















  public static final int VERSION_3 = 1;
  
















  public static final int VERSION_4 = 2;
  















  private int version = 1;
  private boolean compressed = false;
  private boolean topDown = false;
  


  public BMPEncodeParam() {}
  

  public void setVersion(int versionNumber)
  {
    checkVersion(versionNumber);
    version = versionNumber;
  }
  
  public int getVersion()
  {
    return version;
  }
  
  public void setCompressed(boolean compressed)
  {
    this.compressed = compressed;
  }
  


  public boolean isCompressed()
  {
    return compressed;
  }
  



  public void setTopDown(boolean topDown)
  {
    this.topDown = topDown;
  }
  


  public boolean isTopDown()
  {
    return topDown;
  }
  
  private void checkVersion(int versionNumber)
  {
    if ((versionNumber != 0) && 
      (versionNumber != 1) && 
      (versionNumber != 2)) {
      throw new RuntimeException(JaiI18N.getString("Unsupported_version_number_specified_for_BMP_file_"));
    }
  }
}
