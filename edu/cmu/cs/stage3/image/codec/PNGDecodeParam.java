package edu.cmu.cs.stage3.image.codec;






































































































public class PNGDecodeParam
  implements ImageDecodeParam
{
  private boolean suppressAlpha = false;
  

  public PNGDecodeParam() {}
  
  public boolean getSuppressAlpha()
  {
    return suppressAlpha;
  }
  






  public void setSuppressAlpha(boolean suppressAlpha)
  {
    this.suppressAlpha = suppressAlpha;
  }
  
  private boolean expandPalette = false;
  



  public boolean getExpandPalette()
  {
    return expandPalette;
  }
  









  public void setExpandPalette(boolean expandPalette)
  {
    this.expandPalette = expandPalette;
  }
  
  private boolean output8BitGray = false;
  


  public boolean getOutput8BitGray()
  {
    return output8BitGray;
  }
  














  public void setOutput8BitGray(boolean output8BitGray)
  {
    this.output8BitGray = output8BitGray;
  }
  
  private boolean performGammaCorrection = true;
  













  public boolean getPerformGammaCorrection()
  {
    return performGammaCorrection;
  }
  


  public void setPerformGammaCorrection(boolean performGammaCorrection)
  {
    this.performGammaCorrection = performGammaCorrection;
  }
  
  private float userExponent = 1.0F;
  



  public float getUserExponent()
  {
    return userExponent;
  }
  



































  public void setUserExponent(float userExponent)
  {
    if (userExponent <= 0.0F) {
      throw new IllegalArgumentException(JaiI18N.getString("User_exponent_must_not_be_negative_"));
    }
    this.userExponent = userExponent;
  }
  
  private float displayExponent = 2.2F;
  



  public float getDisplayExponent()
  {
    return displayExponent;
  }
  































  public void setDisplayExponent(float displayExponent)
  {
    if (displayExponent <= 0.0F) {
      throw new IllegalArgumentException(JaiI18N.getString("Display_exponent_must_not_be_negative_"));
    }
    this.displayExponent = displayExponent;
  }
  
  private boolean expandGrayAlpha = false;
  


  public boolean getExpandGrayAlpha()
  {
    return expandGrayAlpha;
  }
  










  public void setExpandGrayAlpha(boolean expandGrayAlpha)
  {
    this.expandGrayAlpha = expandGrayAlpha;
  }
  
  private boolean generateEncodeParam = false;
  
  private PNGEncodeParam encodeParam = null;
  




  public boolean getGenerateEncodeParam()
  {
    return generateEncodeParam;
  }
  







  public void setGenerateEncodeParam(boolean generateEncodeParam)
  {
    this.generateEncodeParam = generateEncodeParam;
  }
  





  public PNGEncodeParam getEncodeParam()
  {
    return encodeParam;
  }
  




  public void setEncodeParam(PNGEncodeParam encodeParam)
  {
    this.encodeParam = encodeParam;
  }
}
