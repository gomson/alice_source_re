package edu.cmu.cs.stage3.image.codec;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;















































public class FPXImage
  extends SimpleRenderedImage
{
  private static final int SUBIMAGE_COLOR_SPACE_COLORLESS = 0;
  private static final int SUBIMAGE_COLOR_SPACE_MONOCHROME = 0;
  private static final int SUBIMAGE_COLOR_SPACE_PHOTOYCC = 0;
  private static final int SUBIMAGE_COLOR_SPACE_NIFRGB = 0;
  private static final String[] COLORSPACE_NAME = {
    "Colorless", "Monochrome", "PhotoYCC", "NIF RGB" };
  
  StructuredStorage storage;
  
  int numResolutions;
  
  int highestResWidth;
  
  int highestResHeight;
  
  float defaultDisplayHeight;
  
  float defaultDisplayWidth;
  
  int displayHeightWidthUnits;
  
  boolean[] subimageValid;
  
  int[] subimageWidth;
  int[] subimageHeight;
  int[][] subimageColor;
  int[] decimationMethod;
  float[] decimationPrefilterWidth;
  int highestResolution = -1;
  

  int maxJPEGTableIndex;
  

  byte[][] JPEGTable;
  

  int numChannels;
  

  int tileHeaderTableOffset;
  

  int tileHeaderEntryLength;
  

  SeekableStream subimageHeaderStream;
  
  SeekableStream subimageDataStream;
  
  int resolution;
  
  int tilesAcross;
  
  int[] bandOffsets = { 0, 1, 2 };
  
  private static final int[] RGBBits8 = { 8, 8, 8 };
  
  private static final ComponentColorModel colorModelRGB8 = new ComponentColorModel(ColorSpace.getInstance(1004), 
    RGBBits8, false, false, 
    1, 
    0);
  
  public FPXImage(SeekableStream stream, FPXDecodeParam param)
    throws IOException
  {
    storage = new StructuredStorage(stream);
    
    readImageContents();
    if (param == null) {
      param = new FPXDecodeParam();
    }
    resolution = param.getResolution();
    readResolution();
    
    bandOffsets = new int[numChannels];
    for (int i = 0; i < numChannels; i++) {
      bandOffsets[i] = i;
    }
    
    minX = 0;
    minY = 0;
    
    sampleModel = 
      RasterFactory.createPixelInterleavedSampleModel(
      0, 
      tileWidth, 
      tileHeight, 
      numChannels, 
      numChannels * tileWidth, 
      bandOffsets);
    colorModel = ImageCodec.createComponentColorModel(sampleModel);
  }
  
  private void readImageContents() throws IOException {
    storage.changeDirectoryToRoot();
    storage.changeDirectory("Data Object Store 000001");
    SeekableStream imageContents = 
      storage.getStream("\005Image Contents");
    
    PropertySet icps = new PropertySet(imageContents);
    numResolutions = ((int)icps.getUI4(16777216));
    highestResWidth = ((int)icps.getUI4(16777218));
    highestResHeight = ((int)icps.getUI4(16777219));
    


    displayHeightWidthUnits = ((int)icps.getUI4(16777222, 0L));
    








    subimageValid = new boolean[numResolutions];
    subimageWidth = new int[numResolutions];
    subimageHeight = new int[numResolutions];
    subimageColor = new int[numResolutions][];
    
    decimationMethod = new int[numResolutions];
    decimationPrefilterWidth = new float[numResolutions];
    

    for (int i = 0; i < numResolutions; i++) {
      int index = i << 16;
      if (!icps.hasProperty(0x2000000 | index)) {
        break;
      }
      
      highestResolution = i;
      subimageValid[i] = true;
      subimageWidth[i] = ((int)icps.getUI4(0x2000000 | index));
      subimageHeight[i] = ((int)icps.getUI4(0x2000001 | index));
      byte[] subimageColorBlob = icps.getBlob(0x2000002 | index);
      decimationMethod[i] = icps.getI4(0x2000004 | index);
      

      int numSubImages = FPXUtils.getIntLE(subimageColorBlob, 0);
      int numChannels = FPXUtils.getIntLE(subimageColorBlob, 4);
      







      subimageColor[i] = new int[numChannels];
      for (int c = 0; c < numChannels; c++) {
        int color = FPXUtils.getIntLE(subimageColorBlob, 8 + 4 * c);
        subimageColor[i][c] = color;
      }
    }
    













    maxJPEGTableIndex = ((int)icps.getUI4(50331650, -1L));
    
    JPEGTable = new byte[maxJPEGTableIndex + 1][];
    for (int i = 0; i <= maxJPEGTableIndex; i++) {
      int index = i << 16;
      if (icps.hasProperty(0x3000001 | index))
      {
        JPEGTable[i] = icps.getBlob(0x3000001 | index);
      }
      else {
        JPEGTable[i] = null;
      }
    }
  }
  
  private void readResolution() throws IOException {
    if (resolution == -1) {
      resolution = highestResolution;
    }
    


    storage.changeDirectoryToRoot();
    storage.changeDirectory("Data Object Store 000001");
    storage.changeDirectory("Resolution 000" + resolution);
    
    subimageHeaderStream = storage.getStream("Subimage 0000 Header");
    subimageHeaderStream.skip(28L);
    int headerLength = subimageHeaderStream.readIntLE();
    width = subimageHeaderStream.readIntLE();
    height = subimageHeaderStream.readIntLE();
    int numTiles = subimageHeaderStream.readIntLE();
    tileWidth = subimageHeaderStream.readIntLE();
    tileHeight = subimageHeaderStream.readIntLE();
    numChannels = subimageHeaderStream.readIntLE();
    tileHeaderTableOffset = (subimageHeaderStream.readIntLE() + 28);
    tileHeaderEntryLength = subimageHeaderStream.readIntLE();
    














    subimageDataStream = storage.getStream("Subimage 0000 Data");
    

    tilesAcross = ((width + tileWidth - 1) / tileWidth);
  }
  
  private int getTileOffset(int tileIndex)
    throws IOException
  {
    subimageHeaderStream.seek(tileHeaderTableOffset + 
      16 * tileIndex);
    return subimageHeaderStream.readIntLE() + 28;
  }
  
  private int getTileSize(int tileIndex)
    throws IOException
  {
    subimageHeaderStream.seek(tileHeaderTableOffset + 
      16 * tileIndex + 4);
    return subimageHeaderStream.readIntLE();
  }
  
  private int getCompressionType(int tileIndex)
    throws IOException
  {
    subimageHeaderStream.seek(tileHeaderTableOffset + 
      16 * tileIndex + 8);
    return subimageHeaderStream.readIntLE();
  }
  
  private int getCompressionSubtype(int tileIndex)
    throws IOException
  {
    subimageHeaderStream.seek(tileHeaderTableOffset + 
      16 * tileIndex + 12);
    return subimageHeaderStream.readIntLE();
  }
  
  private static final byte[] PhotoYCCToRGBLUT = {
    0, 1, 1, 2, 2, 3, 4, 
    5, 6, 7, 8, 9, 10, 11, 
    12, 13, 14, 15, 16, 17, 18, 
    19, 20, 22, 23, 24, 25, 26, 
    28, 29, 30, 31, 
    
    33, 34, 35, 36, 38, 39, 
    40, 41, 43, 44, 45, 47, 48, 
    49, 51, 52, 53, 55, 56, 57, 
    59, 60, 61, 63, 64, 65, 67, 
    68, 70, 71, 72, 74, 
    
    75, 76, 78, 79, 81, 82, 83, 
    85, 86, 88, 89, 91, 92, 93, 
    95, 96, 98, 99, 101, 102, 
    103, 105, 106, 108, 109, 111, 
    112, 113, 115, 116, 118, 119, 
    
    121, 122, 123, 125, 126, Byte.MIN_VALUE, 
    -127, -126, -124, -123, -122, -120, 
    -119, -118, -116, -115, -114, -112, 
    -111, -110, -108, -107, -106, -104, 
    -103, -102, -101, -99, -98, -97, 
    -96, -94, 
    
    -93, -92, -91, -90, -88, -87, 
    -86, -85, -84, -82, -81, -80, 
    -79, -78, -77, -76, -74, -73, 
    -72, -71, -70, -69, -68, -67, 
    -66, -65, -64, -62, -61, -60, 
    -59, -58, 
    
    -57, -56, -55, -54, -53, -52, 
    -52, -51, -50, -49, -48, -47, 
    -46, -45, -44, -43, -43, -42, 
    -41, -40, -39, -39, -38, -37, 
    -36, -35, -35, -34, -33, -33, 
    -32, -31, 
    
    -31, -30, -29, -29, -28, -27, 
    -27, -26, -26, -25, -25, -24, 
    -23, -23, -22, -22, -21, -21, 
    -20, -20, -20, -19, -19, -18, 
    -18, -18, -17, -17, -16, -16, 
    -16, -15, 
    
    -15, -15, -14, -14, -14, -14, 
    -13, -13, -13, -12, -12, -12, 
    -12, -11, -11, -11, -11, -11, 
    -10, -10, -10, -10, -10, -9, 
    -9, -9, -9, -9, -9, -8, 
    -8, -8, 
    
    -8, -8, -8, -7, -7, -7, 
    -7, -7, -7, -7, -7, -7, 
    -6, -6, -6, -6, -6, -6, 
    -6, -6, -6, -6, -5, -5, 
    -5, -5, -5, -5, -5, -5, 
    -5, -5, 
    
    -5, -5, -5, -5, -4, -4, 
    -4, -4, -4, -4, -4, -4, 
    -4, -4, -4, -4, -4, -4, 
    -4, -4, -4, -3, -3, -3, 
    -3, -3, -3, -3, -3, -3, 
    -3, -3, 
    
    -3, -3, -3, -3, -3, -3, 
    -3, -2, -2, -2, -2, -2, 
    -2, -2, -2, -2, -2, -2, 
    -2, -2, -2, -2, -1, -1, 
    -1, -1, -1, -1, -1, -1, 
    -1, -1, 
    
    -1, -1, -1, -1, -1, -1, 
    -1, -1, -1 };
  
  private final byte PhotoYCCToNIFRed(float scaledY, float Cb, float Cr)
  {
    float red = scaledY + 1.8215F * Cr - 249.55F;
    if (red < 0.0F)
      return 0;
    if (red > 360.0F) {
      return -1;
    }
    byte r = PhotoYCCToRGBLUT[((int)red)];
    return r;
  }
  
  private final byte PhotoYCCToNIFGreen(float scaledY, float Cb, float Cr)
  {
    float green = scaledY - 0.43031F * Cb - 0.9271F * Cr + 194.14F;
    if (green < 0.0F)
      return 0;
    if (green > 360.0F) {
      return -1;
    }
    byte g = PhotoYCCToRGBLUT[((int)green)];
    return g;
  }
  
  private final byte PhotoYCCToNIFBlue(float scaledY, float Cb, float Cr)
  {
    float blue = scaledY + 2.2179F * Cb - 345.99F;
    if (blue < 0.0F)
      return 0;
    if (blue > 360.0F) {
      return -1;
    }
    byte b = PhotoYCCToRGBLUT[((int)blue)];
    return b;
  }
  
  private final byte YCCToNIFRed(float Y, float Cb, float Cr)
  {
    float red = Y + 1.402F * Cr - 178.75499F;
    if (red < 0.0F)
      return 0;
    if (red > 255.0F) {
      return -1;
    }
    return (byte)(int)red;
  }
  
  private final byte YCCToNIFGreen(float Y, float Cb, float Cr)
  {
    float green = Y - 0.34414F * Cb - 0.71414F * Cr + 134.9307F;
    if (green < 0.0F)
      return 0;
    if (green > 255.0F) {
      return -1;
    }
    return (byte)(int)green;
  }
  
  private final byte YCCToNIFBlue(float Y, float Cb, float Cr)
  {
    float blue = Y + 1.772F * Cb - 225.93F;
    if (blue < 0.0F)
      return 0;
    if (blue > 255.0F) {
      return -1;
    }
    return (byte)(int)blue;
  }
  
  private Raster getUncompressedTile(int tileX, int tileY)
    throws IOException
  {
    int tx = tileXToX(tileX);
    int ty = tileYToY(tileY);
    Raster ras = 
      RasterFactory.createInterleavedRaster(0, 
      tileWidth, 
      tileHeight, 
      numChannels * tileWidth, 
      numChannels, 
      bandOffsets, 
      new Point(tx, ty));
    

    DataBufferByte dataBuffer = (DataBufferByte)ras.getDataBuffer();
    byte[] data = dataBuffer.getData();
    
    int tileIndex = tileY * tilesAcross + tileX;
    subimageDataStream.seek(getTileOffset(tileIndex));
    subimageDataStream.readFully(data, 0, 
      numChannels * tileWidth * tileHeight);
    

    if (subimageColor[resolution][0] >> 16 == 2) {
      int size = tileWidth * tileHeight;
      for (int i = 0; i < size; i++) {
        float Y = data[(3 * i)] & 0xFF;
        float Cb = data[(3 * i + 1)] & 0xFF;
        float Cr = data[(3 * i + 2)] & 0xFF;
        
        float scaledY = Y * 1.3584F;
        byte red = PhotoYCCToNIFRed(scaledY, Cb, Cr);
        byte green = PhotoYCCToNIFGreen(scaledY, Cb, Cr);
        byte blue = PhotoYCCToNIFBlue(scaledY, Cb, Cr);
        
        data[(3 * i)] = red;
        data[(3 * i + 1)] = green;
        data[(3 * i + 2)] = blue;
      }
    }
    
    return ras;
  }
  

  private Raster getSingleColorCompressedTile(int tileX, int tileY)
    throws IOException
  {
    int tx = tileXToX(tileX);
    int ty = tileYToY(tileY);
    Raster ras = 
      RasterFactory.createInterleavedRaster(0, 
      tileWidth, 
      tileHeight, 
      numChannels * tileWidth, 
      numChannels, 
      bandOffsets, 
      new Point(tx, ty));
    
    int subimageColorType = subimageColor[resolution][0] >> 16;
    
    DataBufferByte dataBuffer = (DataBufferByte)ras.getDataBuffer();
    byte[] data = dataBuffer.getData();
    
    int tileIndex = tileY * tilesAcross + tileX;
    int color = getCompressionSubtype(tileIndex);
    byte c0 = (byte)(color >> 0 & 0xFF);
    byte c1 = (byte)(color >> 8 & 0xFF);
    byte c2 = (byte)(color >> 16 & 0xFF);
    byte alpha = (byte)(color >> 24 & 0xFF);
    byte blue;
    byte red;
    byte green;
    byte blue;
    if (subimageColor[resolution][0] >> 16 == 2) {
      float Y = c0 & 0xFF;
      float Cb = c1 & 0xFF;
      float Cr = c2 & 0xFF;
      
      float scaledY = Y * 1.3584F;
      byte red = PhotoYCCToNIFRed(scaledY, Cb, Cr);
      byte green = PhotoYCCToNIFGreen(scaledY, Cb, Cr);
      blue = PhotoYCCToNIFBlue(scaledY, Cb, Cr);
    } else {
      red = c0;
      green = c1;
      blue = c2;
    }
    
    int index = 0;
    int pixels = tileWidth * tileHeight;
    
    if ((numChannels != 1) && 
      (numChannels != 2)) {
      if (numChannels == 3) {
        for (int i = 0; i < pixels; i++) {
          data[(index + 0)] = red;
          data[(index + 1)] = green;
          data[(index + 2)] = blue;
          
          index += 3;
        }
      } else if (numChannels == 4) {
        for (int i = 0; i < pixels; i++) {
          data[(index + 0)] = red;
          data[(index + 1)] = green;
          data[(index + 2)] = blue;
          data[(index + 3)] = alpha;
          
          index += 4;
        }
      }
    }
    return ras;
  }
  

  private Raster getJPEGCompressedTile(int tileX, int tileY)
    throws IOException
  {
    int tileIndex = tileY * tilesAcross + tileX;
    
    int tx = tileXToX(tileX);
    int ty = tileYToY(tileY);
    
    int subtype = getCompressionSubtype(tileIndex);
    int interleave = subtype >> 0 & 0xFF;
    int chroma = subtype >> 8 & 0xFF;
    int conversion = subtype >> 16 & 0xFF;
    int table = subtype >> 24 & 0xFF;
    

    JPEGDecodeParam param = null;
    
    if (table != 0) {
      InputStream tableStream = 
        new ByteArrayInputStream(JPEGTable[table]);
      JPEGImageDecoder dec = JPEGCodec.createJPEGDecoder(tableStream);
      Raster junk = dec.decodeAsRaster();
      param = dec.getJPEGDecodeParam();
    }
    
    subimageDataStream.seek(getTileOffset(tileIndex));
    JPEGImageDecoder dec; JPEGImageDecoder dec; if (param != null) {
      dec = JPEGCodec.createJPEGDecoder(subimageDataStream, param);
    } else {
      dec = JPEGCodec.createJPEGDecoder(subimageDataStream);
    }
    Raster ras = dec.decodeAsRaster().createTranslatedChild(tx, ty);
    
    DataBufferByte dataBuffer = (DataBufferByte)ras.getDataBuffer();
    byte[] data = dataBuffer.getData();
    
    int subimageColorType = subimageColor[resolution][0] >> 16;
    
    int size = tileWidth * tileHeight;
    if ((conversion == 0) && (subimageColorType == 2))
    {
      int offset = 0;
      for (int i = 0; i < size; i++) {
        float Y = data[offset] & 0xFF;
        float Cb = data[(offset + 1)] & 0xFF;
        float Cr = data[(offset + 2)] & 0xFF;
        
        float scaledY = Y * 1.3584F;
        byte red = PhotoYCCToNIFRed(scaledY, Cb, Cr);
        byte green = PhotoYCCToNIFGreen(scaledY, Cb, Cr);
        byte blue = PhotoYCCToNIFBlue(scaledY, Cb, Cr);
        
        data[offset] = red;
        data[(offset + 1)] = green;
        data[(offset + 2)] = blue;
        
        offset += numChannels;
      }
    } else if ((conversion == 1) && (subimageColorType == 3))
    {
      int offset = 0;
      for (int i = 0; i < size; i++) {
        float Y = data[offset] & 0xFF;
        float Cb = data[(offset + 1)] & 0xFF;
        float Cr = data[(offset + 2)] & 0xFF;
        
        byte red = YCCToNIFRed(Y, Cb, Cr);
        byte green = YCCToNIFGreen(Y, Cb, Cr);
        byte blue = YCCToNIFBlue(Y, Cb, Cr);
        
        data[offset] = red;
        data[(offset + 1)] = green;
        data[(offset + 2)] = blue;
        
        offset += numChannels;
      }
    }
    



    if ((conversion == 1) && 
      (subimageColorType == 3) && (numChannels == 4))
    {

      int offset = 0;
      for (int i = 0; i < size; i++) {
        data[(offset + 0)] = ((byte)(255 - data[(offset + 0)]));
        data[(offset + 1)] = ((byte)(255 - data[(offset + 1)]));
        data[(offset + 2)] = ((byte)(255 - data[(offset + 2)]));
        
        offset += 4;
      }
    }
    
    return ras;
  }
  
  public Raster getTile(int tileX, int tileY) {
    int tileIndex = tileY * tilesAcross + tileX;
    try
    {
      int ctype = getCompressionType(tileIndex);
      if (ctype == 0)
        return getUncompressedTile(tileX, tileY);
      if (ctype == 1)
        return getSingleColorCompressedTile(tileX, tileY);
      if (ctype == 2) {
        return getJPEGCompressedTile(tileX, tileY);
      }
      return null;
    } catch (IOException e) {
      e.printStackTrace(); }
    return null;
  }
  

  Hashtable properties = null;
  
  private void addLPSTRProperty(String name, PropertySet ps, int id) {
    String s = ps.getLPSTR(id);
    if (s != null) {
      properties.put(name.toLowerCase(), s);
    }
  }
  
  private void addLPWSTRProperty(String name, PropertySet ps, int id) {
    String s = ps.getLPWSTR(id);
    if (s != null) {
      properties.put(name.toLowerCase(), s);
    }
  }
  
  private void addUI4Property(String name, PropertySet ps, int id) {
    if (ps.hasProperty(id)) {
      long i = ps.getUI4(id);
      properties.put(name.toLowerCase(), new Integer((int)i));
    }
  }
  
  private void getSummaryInformation() {
    SeekableStream summaryInformation = null;
    PropertySet sips = null;
    try {
      storage.changeDirectoryToRoot();
      summaryInformation = storage.getStream("\005SummaryInformation");
      sips = new PropertySet(summaryInformation);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    
    addLPSTRProperty("title", sips, 2);
    addLPSTRProperty("subject", sips, 3);
    addLPSTRProperty("author", sips, 4);
    addLPSTRProperty("keywords", sips, 5);
    addLPSTRProperty("comments", sips, 6);
    addLPSTRProperty("template", sips, 7);
    addLPSTRProperty("last saved by", sips, 8);
    addLPSTRProperty("revision number", sips, 9);
  }
  
  private void getImageInfo() {
    SeekableStream imageInfo = null;
    PropertySet iips = null;
    try {
      storage.changeDirectoryToRoot();
      imageInfo = storage.getStream("\005Image Info");
      if (imageInfo == null) {
        return;
      }
      iips = new PropertySet(imageInfo);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    
    addUI4Property("file source", iips, 553648128);
    addUI4Property("scene type", iips, 553648129);
    
    addLPWSTRProperty("software name/manufacturer/release", 
      iips, 553648131);
    addLPWSTRProperty("user defined id", 
      iips, 553648132);
    
    addLPWSTRProperty("copyright message", 
      iips, 570425344);
    addLPWSTRProperty("legal broker for the original image", 
      iips, 570425345);
    addLPWSTRProperty("legal broker for the digital image", 
      iips, 570425346);
    addLPWSTRProperty("authorship", iips, 570425347);
    addLPWSTRProperty("intellectual property notes", iips, 570425348);
  }
  
  private synchronized void getProperties() {
    if (properties != null) {
      return;
    }
    properties = new Hashtable();
    
    getSummaryInformation();
    getImageInfo();
    

    properties.put("max_resolution", new Integer(highestResolution));
  }
  
  public String[] getPropertyNames()
  {
    getProperties();
    
    int len = properties.size();
    String[] names = new String[len];
    Enumeration enum0 = properties.keys();
    
    int count = 0;
    while (enum0.hasMoreElements()) {
      names[(count++)] = ((String)enum0.nextElement());
    }
    
    return names;
  }
  
  public Object getProperty(String name)
  {
    getProperties();
    return properties.get(name.toLowerCase());
  }
}
