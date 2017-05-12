package edu.cmu.cs.stage3.image.codec;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Date;
import java.util.Vector;



















































































public abstract class PNGEncodeParam
  implements ImageEncodeParam
{
  public static final int INTENT_PERCEPTUAL = 0;
  public static final int INTENT_RELATIVE = 1;
  public static final int INTENT_SATURATION = 2;
  public static final int INTENT_ABSOLUTE = 3;
  public static final int PNG_FILTER_NONE = 0;
  public static final int PNG_FILTER_SUB = 1;
  public static final int PNG_FILTER_UP = 2;
  public static final int PNG_FILTER_AVERAGE = 3;
  public static final int PNG_FILTER_PAETH = 4;
  protected int bitDepth;
  
  public PNGEncodeParam() {}
  
  public static PNGEncodeParam getDefaultEncodeParam(RenderedImage im)
  {
    ColorModel colorModel = im.getColorModel();
    if ((colorModel instanceof IndexColorModel)) {
      return new Palette();
    }
    
    SampleModel sampleModel = im.getSampleModel();
    int numBands = sampleModel.getNumBands();
    
    if ((numBands == 1) || (numBands == 2)) {
      return new Gray();
    }
    return new RGB();
  }
  


  public abstract void setBitDepth(int paramInt);
  

  public static class Palette
    extends PNGEncodeParam
  {
    private boolean backgroundSet = false;
    

    public Palette() {}
    
    public void unsetBackground()
    {
      backgroundSet = false;
    }
    



    public boolean isBackgroundSet()
    {
      return backgroundSet;
    }
    





    public void setBitDepth(int bitDepth)
    {
      if ((bitDepth != 1) && (bitDepth != 2) && (bitDepth != 4) && 
        (bitDepth != 8)) {
        throw new IllegalArgumentException(JaiI18N.getString("Bit_depth_not_equal_to_1__2__4__or_8_"));
      }
      this.bitDepth = bitDepth;
      bitDepthSet = true;
    }
    


    private int[] palette = null;
    private boolean paletteSet = false;
    


    private int backgroundPaletteIndex;
    


    private int[] transparency;
    


    public void setPalette(int[] rgb)
    {
      if ((rgb.length < 3) || (rgb.length > 768)) {
        throw 
          new IllegalArgumentException(JaiI18N.getString("Bad_palette_length_"));
      }
      if (rgb.length % 3 != 0) {
        throw 
          new IllegalArgumentException(JaiI18N.getString("Not_divisible_by_3_"));
      }
      
      palette = ((int[])rgb.clone());
      paletteSet = true;
    }
    









    public int[] getPalette()
    {
      if (!paletteSet) {
        throw new IllegalStateException(JaiI18N.getString("RGB_palette_has_not_been_set_"));
      }
      return (int[])palette.clone();
    }
    


    public void unsetPalette()
    {
      palette = null;
      paletteSet = false;
    }
    


    public boolean isPaletteSet()
    {
      return paletteSet;
    }
    








    public void setBackgroundPaletteIndex(int index)
    {
      backgroundPaletteIndex = index;
      backgroundSet = true;
    }
    








    public int getBackgroundPaletteIndex()
    {
      if (!backgroundSet) {
        throw new IllegalStateException(JaiI18N.getString("background_palette_index_has_not_been_set_"));
      }
      return backgroundPaletteIndex;
    }
    










    public void setPaletteTransparency(byte[] alpha)
    {
      transparency = new int[alpha.length];
      for (int i = 0; i < alpha.length; i++) {
        transparency[i] = (alpha[i] & 0xFF);
      }
      transparencySet = true;
    }
    









    public byte[] getPaletteTransparency()
    {
      if (!transparencySet) {
        throw new IllegalStateException(JaiI18N.getString("Palette_transparency_has_not_been_set_"));
      }
      byte[] alpha = new byte[transparency.length];
      for (int i = 0; i < alpha.length; i++) {
        alpha[i] = ((byte)transparency[i]);
      }
      return alpha;
    }
  }
  




  public static class Gray
    extends PNGEncodeParam
  {
    private boolean backgroundSet = false;
    private int backgroundPaletteGray;
    private int[] transparency;
    private int bitShift;
    
    public Gray() {}
    
    public void unsetBackground() { backgroundSet = false; }
    




    public boolean isBackgroundSet()
    {
      return backgroundSet;
    }
    










    public void setBitDepth(int bitDepth)
    {
      if ((bitDepth != 1) && (bitDepth != 2) && (bitDepth != 4) && 
        (bitDepth != 8) && (bitDepth != 16)) {
        throw new IllegalArgumentException();
      }
      this.bitDepth = bitDepth;
      bitDepthSet = true;
    }
    








    public void setBackgroundGray(int gray)
    {
      backgroundPaletteGray = gray;
      backgroundSet = true;
    }
    









    public int getBackgroundGray()
    {
      if (!backgroundSet) {
        throw new IllegalStateException(JaiI18N.getString("Background_gray_level_has_not_been_set_"));
      }
      return backgroundPaletteGray;
    }
    











    public void setTransparentGray(int transparentGray)
    {
      transparency = new int[1];
      transparency[0] = transparentGray;
      transparencySet = true;
    }
    









    public int getTransparentGray()
    {
      if (!transparencySet) {
        throw new IllegalStateException(JaiI18N.getString("Transparent_gray_value_has_not_been_set_"));
      }
      int gray = transparency[0];
      return gray;
    }
    

    private boolean bitShiftSet = false;
    





    public void setBitShift(int bitShift)
    {
      if (bitShift < 0) {
        throw new RuntimeException();
      }
      this.bitShift = bitShift;
      bitShiftSet = true;
    }
    







    public int getBitShift()
    {
      if (!bitShiftSet) {
        throw new IllegalStateException(JaiI18N.getString("Bit_shift_has_not_been_set_"));
      }
      return bitShift;
    }
    



    public void unsetBitShift()
    {
      bitShiftSet = false;
    }
    


    public boolean isBitShiftSet()
    {
      return bitShiftSet;
    }
    


    public boolean isBitDepthSet()
    {
      return bitDepthSet;
    }
  }
  




  public static class RGB
    extends PNGEncodeParam
  {
    private boolean backgroundSet = false;
    private int[] backgroundRGB;
    private int[] transparency;
    
    public RGB() {}
    
    public void unsetBackground() {
      backgroundSet = false;
    }
    



    public boolean isBackgroundSet()
    {
      return backgroundSet;
    }
    




    public void setBitDepth(int bitDepth)
    {
      if ((bitDepth != 8) && (bitDepth != 16)) {
        throw new RuntimeException();
      }
      this.bitDepth = bitDepth;
      bitDepthSet = true;
    }
    









    public void setBackgroundRGB(int[] rgb)
    {
      if (rgb.length != 3) {
        throw new RuntimeException();
      }
      backgroundRGB = rgb;
      backgroundSet = true;
    }
    







    public int[] getBackgroundRGB()
    {
      if (!backgroundSet) {
        throw new IllegalStateException(JaiI18N.getString("RGB_background_color_has_not_been_set_"));
      }
      return backgroundRGB;
    }
    











    public void setTransparentRGB(int[] transparentRGB)
    {
      transparency = ((int[])transparentRGB.clone());
      transparencySet = true;
    }
    








    public int[] getTransparentRGB()
    {
      if (!transparencySet) {
        throw new IllegalStateException(JaiI18N.getString("Transparent_RGB_value_has_not_been_set_"));
      }
      return (int[])transparency.clone();
    }
  }
  

  protected boolean bitDepthSet = false;
  












  public int getBitDepth()
  {
    if (!bitDepthSet) {
      throw new IllegalStateException(JaiI18N.getString("Grayscale_bit_depth_has_not_been_set_"));
    }
    return bitDepth;
  }
  





  public void unsetBitDepth()
  {
    bitDepthSet = false;
  }
  
  private boolean useInterlacing = false;
  


  public void setInterlacing(boolean useInterlacing)
  {
    this.useInterlacing = useInterlacing;
  }
  


  public boolean getInterlacing()
  {
    return useInterlacing;
  }
  


















  public void unsetBackground()
  {
    throw new RuntimeException(JaiI18N.getString("unsetBackground__not_implemented_by_the_superclass__PNGEncodeParam__"));
  }
  





  public boolean isBackgroundSet()
  {
    throw new RuntimeException(JaiI18N.getString("isBackgroundSet__not_implemented_by_the_superclass__PNGEncodeParam__"));
  }
  


  private float[] chromaticity = null;
  private boolean chromaticitySet = false;
  




  private float gamma;
  




  public void setChromaticity(float[] chromaticity)
  {
    if (chromaticity.length != 8) {
      throw new IllegalArgumentException();
    }
    this.chromaticity = ((float[])chromaticity.clone());
    chromaticitySet = true;
  }
  





  public void setChromaticity(float whitePointX, float whitePointY, float redX, float redY, float greenX, float greenY, float blueX, float blueY)
  {
    float[] chroma = new float[8];
    chroma[0] = whitePointX;
    chroma[1] = whitePointY;
    chroma[2] = redX;
    chroma[3] = redY;
    chroma[4] = greenX;
    chroma[5] = greenY;
    chroma[6] = blueX;
    chroma[7] = blueY;
    setChromaticity(chroma);
  }
  











  public float[] getChromaticity()
  {
    if (!chromaticitySet) {
      throw new IllegalStateException(JaiI18N.getString("Chromaticity_has_not_been_set_"));
    }
    return (float[])chromaticity.clone();
  }
  


  public void unsetChromaticity()
  {
    chromaticity = null;
    chromaticitySet = false;
  }
  


  public boolean isChromaticitySet()
  {
    return chromaticitySet;
  }
  



  private boolean gammaSet = false;
  




  public void setGamma(float gamma)
  {
    this.gamma = gamma;
    gammaSet = true;
  }
  







  public float getGamma()
  {
    if (!gammaSet) {
      throw new IllegalStateException(JaiI18N.getString("Gamma_has_not_been_set_"));
    }
    return gamma;
  }
  


  public void unsetGamma()
  {
    gammaSet = false;
  }
  


  public boolean isGammaSet()
  {
    return gammaSet;
  }
  


  private int[] paletteHistogram = null;
  private boolean paletteHistogramSet = false;
  






  public void setPaletteHistogram(int[] paletteHistogram)
  {
    this.paletteHistogram = ((int[])paletteHistogram.clone());
    paletteHistogramSet = true;
  }
  







  public int[] getPaletteHistogram()
  {
    if (!paletteHistogramSet) {
      throw new IllegalStateException(JaiI18N.getString("Palette_histogram_has_not_been_set_"));
    }
    return paletteHistogram;
  }
  


  public void unsetPaletteHistogram()
  {
    paletteHistogram = null;
    paletteHistogramSet = false;
  }
  


  public boolean isPaletteHistogramSet()
  {
    return paletteHistogramSet;
  }
  


  private byte[] ICCProfileData = null;
  private boolean ICCProfileDataSet = false;
  





  public void setICCProfileData(byte[] ICCProfileData)
  {
    this.ICCProfileData = ((byte[])ICCProfileData.clone());
    ICCProfileDataSet = true;
  }
  







  public byte[] getICCProfileData()
  {
    if (!ICCProfileDataSet) {
      throw new IllegalStateException(JaiI18N.getString("ICC_profile_has_not_been_set_"));
    }
    return (byte[])ICCProfileData.clone();
  }
  


  public void unsetICCProfileData()
  {
    ICCProfileData = null;
    ICCProfileDataSet = false;
  }
  


  public boolean isICCProfileDataSet()
  {
    return ICCProfileDataSet;
  }
  


  private int[] physicalDimension = null;
  private boolean physicalDimensionSet = false;
  








  public void setPhysicalDimension(int[] physicalDimension)
  {
    this.physicalDimension = ((int[])physicalDimension.clone());
    physicalDimensionSet = true;
  }
  




  public void setPhysicalDimension(int xPixelsPerUnit, int yPixelsPerUnit, int unitSpecifier)
  {
    int[] pd = new int[3];
    pd[0] = xPixelsPerUnit;
    pd[1] = yPixelsPerUnit;
    pd[2] = unitSpecifier;
    
    setPhysicalDimension(pd);
  }
  










  public int[] getPhysicalDimension()
  {
    if (!physicalDimensionSet) {
      throw new IllegalStateException(JaiI18N.getString("Physical_dimension_information_has_not_been_set_"));
    }
    return (int[])physicalDimension.clone();
  }
  


  public void unsetPhysicalDimension()
  {
    physicalDimension = null;
    physicalDimensionSet = false;
  }
  


  public boolean isPhysicalDimensionSet()
  {
    return physicalDimensionSet;
  }
  


  private PNGSuggestedPaletteEntry[] suggestedPalette = null;
  private boolean suggestedPaletteSet = false;
  






  public void setSuggestedPalette(PNGSuggestedPaletteEntry[] palette)
  {
    suggestedPalette = ((PNGSuggestedPaletteEntry[])palette.clone());
    suggestedPaletteSet = true;
  }
  










  public PNGSuggestedPaletteEntry[] getSuggestedPalette()
  {
    if (!suggestedPaletteSet) {
      throw new IllegalStateException(JaiI18N.getString("Suggested_palette_information_has_not_been_set_"));
    }
    return (PNGSuggestedPaletteEntry[])suggestedPalette.clone();
  }
  


  public void unsetSuggestedPalette()
  {
    suggestedPalette = null;
    suggestedPaletteSet = false;
  }
  


  public boolean isSuggestedPaletteSet()
  {
    return suggestedPaletteSet;
  }
  


  private int[] significantBits = null;
  private boolean significantBitsSet = false;
  



  private int SRGBIntent;
  




  public void setSignificantBits(int[] significantBits)
  {
    this.significantBits = ((int[])significantBits.clone());
    significantBitsSet = true;
  }
  









  public int[] getSignificantBits()
  {
    if (!significantBitsSet) {
      throw new IllegalStateException(JaiI18N.getString("Significant_bits_values_have_not_been_set_"));
    }
    return (int[])significantBits.clone();
  }
  


  public void unsetSignificantBits()
  {
    significantBits = null;
    significantBitsSet = false;
  }
  


  public boolean isSignificantBitsSet()
  {
    return significantBitsSet;
  }
  



  private boolean SRGBIntentSet = false;
  







  public void setSRGBIntent(int SRGBIntent)
  {
    this.SRGBIntent = SRGBIntent;
    SRGBIntentSet = true;
  }
  







  public int getSRGBIntent()
  {
    if (!SRGBIntentSet) {
      throw new IllegalStateException(JaiI18N.getString("sRGB_rendereding_intent_has_not_been_set_"));
    }
    return SRGBIntent;
  }
  


  public void unsetSRGBIntent()
  {
    SRGBIntentSet = false;
  }
  


  public boolean isSRGBIntentSet()
  {
    return SRGBIntentSet;
  }
  


  private String[] text = null;
  private boolean textSet = false;
  


  private Date modificationTime;
  


  public void setText(String[] text)
  {
    this.text = text;
    textSet = true;
  }
  








  public String[] getText()
  {
    if (!textSet) {
      throw new IllegalStateException(JaiI18N.getString("Uncompressed_text_strings_have_not_been_set_"));
    }
    return text;
  }
  


  public void unsetText()
  {
    text = null;
    textSet = false;
  }
  


  public boolean isTextSet()
  {
    return textSet;
  }
  



  private boolean modificationTimeSet = false;
  







  public void setModificationTime(Date modificationTime)
  {
    this.modificationTime = modificationTime;
    modificationTimeSet = true;
  }
  







  public Date getModificationTime()
  {
    if (!modificationTimeSet) {
      throw new IllegalStateException(JaiI18N.getString("Modification_time_has_not_been_set_"));
    }
    return modificationTime;
  }
  


  public void unsetModificationTime()
  {
    modificationTime = null;
    modificationTimeSet = false;
  }
  


  public boolean isModificationTimeSet()
  {
    return modificationTimeSet;
  }
  


  boolean transparencySet = false;
  


  public void unsetTransparency()
  {
    transparencySet = false;
  }
  


  public boolean isTransparencySet()
  {
    return transparencySet;
  }
  


  private String[] zText = null;
  private boolean zTextSet = false;
  






  public void setCompressedText(String[] text)
  {
    zText = text;
    zTextSet = true;
  }
  










  public String[] getCompressedText()
  {
    if (!zTextSet) {
      throw new IllegalStateException(JaiI18N.getString("Compressed_text_strings_have_not_been_set_"));
    }
    return zText;
  }
  


  public void unsetCompressedText()
  {
    zText = null;
    zTextSet = false;
  }
  


  public boolean isCompressedTextSet()
  {
    return zTextSet;
  }
  


  Vector chunkType = new Vector();
  Vector chunkData = new Vector();
  







  public synchronized void addPrivateChunk(String type, byte[] data)
  {
    chunkType.add(type);
    chunkData.add(data.clone());
  }
  



  public synchronized int getNumPrivateChunks()
  {
    return chunkType.size();
  }
  




  public synchronized String getPrivateChunkType(int index)
  {
    return (String)chunkType.elementAt(index);
  }
  





  public synchronized byte[] getPrivateChunkData(int index)
  {
    return (byte[])chunkData.elementAt(index);
  }
  




  public synchronized void removeUnsafeToCopyPrivateChunks()
  {
    Vector newChunkType = new Vector();
    Vector newChunkData = new Vector();
    
    int len = getNumPrivateChunks();
    for (int i = 0; i < len; i++) {
      String type = getPrivateChunkType(i);
      char lastChar = type.charAt(3);
      if ((lastChar >= 'a') && (lastChar <= 'z')) {
        newChunkType.add(type);
        newChunkData.add(getPrivateChunkData(i));
      }
    }
    
    chunkType = newChunkType;
    chunkData = newChunkData;
  }
  


  public synchronized void removeAllPrivateChunks()
  {
    chunkType = new Vector();
    chunkData = new Vector();
  }
  


  private static final int abs(int x)
  {
    return x < 0 ? -x : x;
  }
  




  public static final int paethPredictor(int a, int b, int c)
  {
    int p = a + b - c;
    int pa = abs(p - a);
    int pb = abs(p - b);
    int pc = abs(p - c);
    
    if ((pa <= pb) && (pa <= pc))
      return a;
    if (pb <= pc) {
      return b;
    }
    return c;
  }
  





























































  public int filterRow(byte[] currRow, byte[] prevRow, byte[][] scratchRows, int bytesPerRow, int bytesPerPixel)
  {
    int[] filterBadness = new int[5];
    for (int i = 0; i < 5; i++) {
      filterBadness[i] = Integer.MAX_VALUE;
    }
    

    int badness = 0;
    
    for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
      int curr = currRow[i] & 0xFF;
      badness += curr;
    }
    
    filterBadness[0] = badness;
    


    byte[] subFilteredRow = scratchRows[1];
    int badness = 0;
    
    for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
      int curr = currRow[i] & 0xFF;
      int left = currRow[(i - bytesPerPixel)] & 0xFF;
      int difference = curr - left;
      subFilteredRow[i] = ((byte)difference);
      
      badness += abs(difference);
    }
    
    filterBadness[1] = badness;
    


    byte[] upFilteredRow = scratchRows[2];
    int badness = 0;
    
    for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
      int curr = currRow[i] & 0xFF;
      int up = prevRow[i] & 0xFF;
      int difference = curr - up;
      upFilteredRow[i] = ((byte)difference);
      
      badness += abs(difference);
    }
    
    filterBadness[2] = badness;
    


    byte[] averageFilteredRow = scratchRows[3];
    int badness = 0;
    
    for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
      int curr = currRow[i] & 0xFF;
      int left = currRow[(i - bytesPerPixel)] & 0xFF;
      int up = prevRow[i] & 0xFF;
      int difference = curr - (left + up) / 2;
      averageFilteredRow[i] = ((byte)difference);
      
      badness += abs(difference);
    }
    
    filterBadness[3] = badness;
    


    byte[] paethFilteredRow = scratchRows[4];
    int badness = 0;
    
    for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
      int curr = currRow[i] & 0xFF;
      int left = currRow[(i - bytesPerPixel)] & 0xFF;
      int up = prevRow[i] & 0xFF;
      int upleft = prevRow[(i - bytesPerPixel)] & 0xFF;
      int predictor = paethPredictor(left, up, upleft);
      int difference = curr - predictor;
      paethFilteredRow[i] = ((byte)difference);
      
      badness += abs(difference);
    }
    
    filterBadness[4] = badness;
    

    int filterType = 0;
    int minBadness = filterBadness[0];
    
    for (int i = 1; i < 5; i++) {
      if (filterBadness[i] < minBadness) {
        minBadness = filterBadness[i];
        filterType = i;
      }
    }
    
    if (filterType == 0) {
      System.arraycopy(currRow, bytesPerPixel, 
        scratchRows[0], bytesPerPixel, 
        bytesPerRow);
    }
    
    return filterType;
  }
}
