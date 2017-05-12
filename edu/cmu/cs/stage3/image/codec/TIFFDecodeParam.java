package edu.cmu.cs.stage3.image.codec;






























































































public class TIFFDecodeParam
  implements ImageDecodeParam
{
  private boolean decodePaletteAsShorts = false;
  



  public TIFFDecodeParam() {}
  


  public void setDecodePaletteAsShorts(boolean decodePaletteAsShorts)
  {
    this.decodePaletteAsShorts = decodePaletteAsShorts;
  }
  



  public boolean getDecodePaletteAsShorts()
  {
    return decodePaletteAsShorts;
  }
  








  public byte decode16BitsTo8Bits(int s)
  {
    return (byte)(s >> 8 & 0xFFFF);
  }
  




  public byte decodeSigned16BitsTo8Bits(short s)
  {
    return (byte)(s + Short.MIN_VALUE >> 8);
  }
}
