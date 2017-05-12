package edu.cmu.cs.stage3.image.codec;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.SequenceInputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;











































































































































class PNGImage
  extends SimpleRenderedImage
{
  public static final int PNG_COLOR_GRAY = 0;
  public static final int PNG_COLOR_RGB = 2;
  public static final int PNG_COLOR_PALETTE = 3;
  public static final int PNG_COLOR_GRAY_ALPHA = 4;
  public static final int PNG_COLOR_RGB_ALPHA = 6;
  private static final String[] colorTypeNames = {
    "Grayscale", "Error", "Truecolor", "Index", 
    "Grayscale with alpha", "Error", "Truecolor with alpha" };
  
  public static final int PNG_FILTER_NONE = 0;
  
  public static final int PNG_FILTER_SUB = 1;
  
  public static final int PNG_FILTER_UP = 2;
  
  public static final int PNG_FILTER_AVERAGE = 3;
  public static final int PNG_FILTER_PAETH = 4;
  private static final int RED_OFFSET = 2;
  private static final int GREEN_OFFSET = 1;
  private static final int BLUE_OFFSET = 0;
  private int[][] bandOffsets = {
  
    0, new int[1], 
    { 0, 1 }, 
    { 0, 1, 2 }, 
    { 0, 1, 2, 3 } };
  
  private int bitDepth;
  
  private int colorType;
  
  private int compressionMethod;
  
  private int filterMethod;
  
  private int interlaceMethod;
  
  private int paletteEntries;
  
  private byte[] redPalette;
  
  private byte[] greenPalette;
  private byte[] bluePalette;
  private byte[] alphaPalette;
  private int bkgdRed;
  private int bkgdGreen;
  private int bkgdBlue;
  private int grayTransparentAlpha;
  private int redTransparentAlpha;
  private int greenTransparentAlpha;
  private int blueTransparentAlpha;
  private int maxOpacity;
  private int[] significantBits = null;
  
  private boolean hasBackground = false;
  



  private boolean suppressAlpha = false;
  

  private boolean expandPalette = false;
  

  private boolean output8BitGray = false;
  

  private boolean outputHasAlphaPalette = false;
  

  private boolean performGammaCorrection = false;
  

  private boolean expandGrayAlpha = false;
  

  private boolean generateEncodeParam = false;
  

  private PNGDecodeParam decodeParam = null;
  

  private PNGEncodeParam encodeParam = null;
  
  private boolean emitProperties = true;
  
  private float fileGamma = 0.45455F;
  
  private float userExponent = 1.0F;
  
  private float displayExponent = 2.2F;
  
  private float[] chromaticity = null;
  
  private int sRGBRenderingIntent = -1;
  

  private int postProcess = 0;
  


  private static final int POST_NONE = 0;
  


  private static final int POST_GAMMA = 1;
  


  private static final int POST_GRAY_LUT = 2;
  


  private static final int POST_GRAY_LUT_ADD_TRANS = 3;
  


  private static final int POST_PALETTE_TO_RGB = 4;
  

  private static final int POST_PALETTE_TO_RGBA = 5;
  

  private static final int POST_ADD_GRAY_TRANS = 6;
  

  private static final int POST_ADD_RGB_TRANS = 7;
  

  private static final int POST_REMOVE_GRAY_TRANS = 8;
  

  private static final int POST_REMOVE_RGB_TRANS = 9;
  

  private static final int POST_EXP_MASK = 16;
  

  private static final int POST_GRAY_ALPHA_EXP = 16;
  

  private static final int POST_GAMMA_EXP = 17;
  

  private static final int POST_GRAY_LUT_ADD_TRANS_EXP = 19;
  

  private static final int POST_ADD_GRAY_TRANS_EXP = 22;
  

  private Vector streamVec = new Vector();
  
  private DataInputStream dataStream;
  
  private int bytesPerPixel;
  
  private int inputBands;
  private int outputBands;
  private int chunkIndex = 0;
  
  private Vector textKeys = new Vector();
  private Vector textStrings = new Vector();
  
  private Vector ztextKeys = new Vector();
  private Vector ztextStrings = new Vector();
  
  private WritableRaster theTile;
  
  private int[] gammaLut = null;
  
  private void initGammaLut(int bits) {
    double exp = userExponent / (fileGamma * displayExponent);
    int numSamples = 1 << bits;
    int maxOutSample = bits == 16 ? 65535 : 255;
    
    gammaLut = new int[numSamples];
    for (int i = 0; i < numSamples; i++) {
      double gbright = i / (numSamples - 1);
      double gamma = Math.pow(gbright, exp);
      int igamma = (int)(gamma * maxOutSample + 0.5D);
      if (igamma > maxOutSample) {
        igamma = maxOutSample;
      }
      gammaLut[i] = igamma;
    }
  }
  
  private final byte[][] expandBits = {
  
    0, { 0, -1 }, 
    { 0, 85, -86, -1 }, 
    



    00, 17, 34, 51, 68, 85, 102, 119, -120, -103, -86, -69, -52, -35, -18, -1 } };
  

  private int[] grayLut = null;
  
  private void initGrayLut(int bits) {
    int len = 1 << bits;
    grayLut = new int[len];
    
    if (performGammaCorrection) {
      for (int i = 0; i < len; i++) {
        grayLut[i] = gammaLut[i];
      }
    } else {
      for (int i = 0; i < len; i++) {
        grayLut[i] = expandBits[bits][i];
      }
    }
  }
  
  public PNGImage(InputStream stream, PNGDecodeParam decodeParam)
    throws IOException
  {
    if (!stream.markSupported()) {
      stream = new BufferedInputStream(stream);
    }
    DataInputStream distream = new DataInputStream(stream);
    
    if (decodeParam == null) {
      decodeParam = new PNGDecodeParam();
    }
    this.decodeParam = decodeParam;
    

    suppressAlpha = decodeParam.getSuppressAlpha();
    expandPalette = decodeParam.getExpandPalette();
    output8BitGray = decodeParam.getOutput8BitGray();
    expandGrayAlpha = decodeParam.getExpandGrayAlpha();
    if (decodeParam.getPerformGammaCorrection()) {
      userExponent = decodeParam.getUserExponent();
      displayExponent = decodeParam.getDisplayExponent();
      performGammaCorrection = true;
      output8BitGray = true;
    }
    generateEncodeParam = decodeParam.getGenerateEncodeParam();
    
    if (emitProperties) {
      properties.put("file_type", "PNG v. 1.0");
    }
    try
    {
      long magic = distream.readLong();
      if (magic != -8552249625308161526L) {
        String msg = JaiI18N.getString("PNG_magic_number_not_found_");
        throw new RuntimeException(msg);
      }
    } catch (Exception e) {
      e.printStackTrace();
      String msg = JaiI18N.getString("Error_reading_PNG_header_");
      throw new RuntimeException(msg);
    }
    
    try
    {
      for (;;)
      {
        String chunkType = getChunkType(distream);
        if (chunkType.equals("IHDR")) {
          PNGChunk chunk = readChunk(distream);
          parse_IHDR_chunk(chunk);
        } else if (chunkType.equals("PLTE")) {
          PNGChunk chunk = readChunk(distream);
          parse_PLTE_chunk(chunk);
        } else if (chunkType.equals("IDAT")) {
          PNGChunk chunk = readChunk(distream);
          streamVec.add(new ByteArrayInputStream(chunk.getData()));
        } else { if (chunkType.equals("IEND")) {
            PNGChunk chunk = readChunk(distream);
            parse_IEND_chunk(chunk);
            break; }
          if (chunkType.equals("bKGD")) {
            PNGChunk chunk = readChunk(distream);
            parse_bKGD_chunk(chunk);
          } else if (chunkType.equals("cHRM")) {
            PNGChunk chunk = readChunk(distream);
            parse_cHRM_chunk(chunk);
          } else if (chunkType.equals("gAMA")) {
            PNGChunk chunk = readChunk(distream);
            parse_gAMA_chunk(chunk);
          } else if (chunkType.equals("hIST")) {
            PNGChunk chunk = readChunk(distream);
            parse_hIST_chunk(chunk);
          } else if (chunkType.equals("iCCP")) {
            PNGChunk chunk = readChunk(distream);
            parse_iCCP_chunk(chunk);
          } else if (chunkType.equals("pHYs")) {
            PNGChunk chunk = readChunk(distream);
            parse_pHYs_chunk(chunk);
          } else if (chunkType.equals("sBIT")) {
            PNGChunk chunk = readChunk(distream);
            parse_sBIT_chunk(chunk);
          } else if (chunkType.equals("sRGB")) {
            PNGChunk chunk = readChunk(distream);
            parse_sRGB_chunk(chunk);
          } else if (chunkType.equals("tEXt")) {
            PNGChunk chunk = readChunk(distream);
            parse_tEXt_chunk(chunk);
          } else if (chunkType.equals("tIME")) {
            PNGChunk chunk = readChunk(distream);
            parse_tIME_chunk(chunk);
          } else if (chunkType.equals("tRNS")) {
            PNGChunk chunk = readChunk(distream);
            parse_tRNS_chunk(chunk);
          } else if (chunkType.equals("zTXt")) {
            PNGChunk chunk = readChunk(distream);
            parse_zTXt_chunk(chunk);
          } else {
            PNGChunk chunk = readChunk(distream);
            

            String type = chunk.getTypeString();
            byte[] data = chunk.getData();
            if (encodeParam != null) {
              encodeParam.addPrivateChunk(type, data);
            }
            if (emitProperties) {
              String key = "chunk_" + chunkIndex++ + ":" + type;
              properties.put(key.toLowerCase(), data);
            }
          }
        }
      }
      


      String msg;
      


      if (significantBits != null) {
        return;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      msg = JaiI18N.getString("I_O_error_reading_PNG_file_");
      throw new RuntimeException(msg);
    }
    




    significantBits = new int[inputBands];
    for (int i = 0; i < inputBands; i++) {
      significantBits[i] = bitDepth;
    }
    
    if (emitProperties) {
      properties.put("significant_bits", significantBits);
    }
  }
  
  private static String getChunkType(DataInputStream distream)
  {
    try {
      distream.mark(8);
      int length = distream.readInt();
      int type = distream.readInt();
      distream.reset();
      
      String typeString = new String();
      typeString = typeString + (char)(type >> 24);
      typeString = typeString + (char)(type >> 16 & 0xFF);
      typeString = typeString + (char)(type >> 8 & 0xFF);
      return typeString + (char)(type & 0xFF);
    }
    catch (Exception e) {
      e.printStackTrace(); }
    return null;
  }
  
  private static PNGChunk readChunk(DataInputStream distream)
  {
    try {
      int length = distream.readInt();
      int type = distream.readInt();
      byte[] data = new byte[length];
      distream.readFully(data);
      int crc = distream.readInt();
      
      return new PNGChunk(length, type, data, crc);
    } catch (Exception e) {
      e.printStackTrace(); }
    return null;
  }
  
  private void parse_IHDR_chunk(PNGChunk chunk)
  {
    tileWidth = (this.width = chunk.getInt4(0));
    tileHeight = (this.height = chunk.getInt4(4));
    
    bitDepth = chunk.getInt1(8);
    
    if ((bitDepth != 1) && (bitDepth != 2) && (bitDepth != 4) && 
      (bitDepth != 8) && (bitDepth != 16))
    {
      String msg = JaiI18N.getString("Illegal_bit_depth_for_a_PNG_image_");
      throw new RuntimeException(msg);
    }
    maxOpacity = ((1 << bitDepth) - 1);
    
    colorType = chunk.getInt1(9);
    if ((colorType != 0) && 
      (colorType != 2) && 
      (colorType != 3) && 
      (colorType != 4) && 
      (colorType != 6)) {
      System.out.println(JaiI18N.getString("Bad_color_type_for_a_PNG_image_"));
    }
    
    if ((colorType == 2) && (bitDepth < 8))
    {
      String msg = JaiI18N.getString("An_RGB_PNG_image_can_t_have_a_bit_depth_less_than_8_");
      throw new RuntimeException(msg);
    }
    
    if ((colorType == 3) && (bitDepth == 16))
    {
      String msg = JaiI18N.getString("A_palette_color_PNG_image_can_t_have_a_bit_depth_of_16_");
      throw new RuntimeException(msg);
    }
    
    if ((colorType == 4) && (bitDepth < 8))
    {
      String msg = JaiI18N.getString("A_PNG_Gray_Alpha_image_can_t_have_a_bit_depth_less_than_8_");
      throw new RuntimeException(msg);
    }
    
    if ((colorType == 6) && (bitDepth < 8))
    {
      String msg = JaiI18N.getString("A_PNG_RGB_Alpha_image_can_t_have_a_bit_depth_less_than_8_");
      throw new RuntimeException(msg);
    }
    
    if (emitProperties) {
      properties.put("color_type", colorTypeNames[colorType]);
    }
    
    if (generateEncodeParam) {
      if (colorType == 3) {
        encodeParam = new PNGEncodeParam.Palette();
      } else if ((colorType == 0) || 
        (colorType == 4)) {
        encodeParam = new PNGEncodeParam.Gray();
      } else {
        encodeParam = new PNGEncodeParam.RGB();
      }
      decodeParam.setEncodeParam(encodeParam);
    }
    
    if (encodeParam != null) {
      encodeParam.setBitDepth(bitDepth);
    }
    if (emitProperties) {
      properties.put("bit_depth", new Integer(bitDepth));
    }
    
    if (performGammaCorrection)
    {
      float gamma = 0.45454544F * (displayExponent / userExponent);
      if (encodeParam != null) {
        encodeParam.setGamma(gamma);
      }
      if (emitProperties) {
        properties.put("gamma", new Float(gamma));
      }
    }
    
    compressionMethod = chunk.getInt1(10);
    if (compressionMethod != 0)
    {
      String msg = JaiI18N.getString("Unsupported_PNG_compression_method__not_0__");
      throw new RuntimeException(msg);
    }
    
    filterMethod = chunk.getInt1(11);
    if (filterMethod != 0)
    {
      String msg = JaiI18N.getString("Unsupported_PNG_filter_method__not_0__");
      throw new RuntimeException(msg);
    }
    
    interlaceMethod = chunk.getInt1(12);
    if (interlaceMethod == 0) {
      if (encodeParam != null) {
        encodeParam.setInterlacing(false);
      }
      if (emitProperties) {
        properties.put("interlace_method", "None");
      }
    } else if (interlaceMethod == 1) {
      if (encodeParam != null) {
        encodeParam.setInterlacing(true);
      }
      if (emitProperties) {
        properties.put("interlace_method", "Adam7");
      }
    }
    else {
      String msg = JaiI18N.getString("Unsupported_PNG_interlace_method__not_0_or_1__");
      throw new RuntimeException(msg);
    }
    
    bytesPerPixel = (bitDepth == 16 ? 2 : 1);
    
    switch (colorType) {
    case 0: 
      inputBands = 1;
      outputBands = 1;
      
      if ((output8BitGray) && (bitDepth < 8)) {
        postProcess = 2;
      } else if (performGammaCorrection) {
        postProcess = 1;
      } else {
        postProcess = 0;
      }
      break;
    
    case 2: 
      inputBands = 3;
      bytesPerPixel *= 3;
      outputBands = 3;
      
      if (performGammaCorrection) {
        postProcess = 1;
      } else {
        postProcess = 0;
      }
      break;
    
    case 3: 
      inputBands = 1;
      bytesPerPixel = 1;
      outputBands = (expandPalette ? 3 : 1);
      
      if (expandPalette) {
        postProcess = 4;
      } else {
        postProcess = 0;
      }
      break;
    
    case 4: 
      inputBands = 2;
      bytesPerPixel *= 2;
      
      if (suppressAlpha) {
        outputBands = 1;
        postProcess = 8;
      } else {
        if (performGammaCorrection) {
          postProcess = 1;
        } else {
          postProcess = 0;
        }
        if (expandGrayAlpha) {
          postProcess |= 0x10;
          outputBands = 4;
        } else {
          outputBands = 2;
        }
      }
      break;
    
    case 6: 
      inputBands = 4;
      bytesPerPixel *= 4;
      outputBands = (!suppressAlpha ? 4 : 3);
      
      if (suppressAlpha) {
        postProcess = 9;
      } else if (performGammaCorrection) {
        postProcess = 1;
      } else {
        postProcess = 0;
      }
      break;
    }
  }
  
  private void parse_IEND_chunk(PNGChunk chunk) throws Exception
  {
    int textLen = textKeys.size();
    String[] textArray = new String[2 * textLen];
    for (int i = 0; i < textLen; i++) {
      String key = (String)textKeys.elementAt(i);
      String val = (String)textStrings.elementAt(i);
      textArray[(2 * i)] = key;
      textArray[(2 * i + 1)] = val;
      if (emitProperties) {
        String uniqueKey = "text_" + i + ":" + key;
        properties.put(uniqueKey.toLowerCase(), val);
      }
    }
    if (encodeParam != null) {
      encodeParam.setText(textArray);
    }
    

    int ztextLen = ztextKeys.size();
    String[] ztextArray = new String[2 * ztextLen];
    for (int i = 0; i < ztextLen; i++) {
      String key = (String)ztextKeys.elementAt(i);
      String val = (String)ztextStrings.elementAt(i);
      ztextArray[(2 * i)] = key;
      ztextArray[(2 * i + 1)] = val;
      if (emitProperties) {
        String uniqueKey = "ztext_" + i + ":" + key;
        properties.put(uniqueKey.toLowerCase(), val);
      }
    }
    if (encodeParam != null) {
      encodeParam.setCompressedText(ztextArray);
    }
    

    InputStream seqStream = 
      new SequenceInputStream(streamVec.elements());
    InputStream infStream = 
      new InflaterInputStream(seqStream, new Inflater());
    dataStream = new DataInputStream(infStream);
    

    int depth = bitDepth;
    if ((colorType == 0) && 
      (bitDepth < 8) && (output8BitGray)) {
      depth = 8;
    }
    if ((colorType == 3) && (expandPalette)) {
      depth = 8;
    }
    int bytesPerRow = (outputBands * width * depth + 7) / 8;
    int scanlineStride = 
      depth == 16 ? bytesPerRow / 2 : bytesPerRow;
    
    theTile = createRaster(width, height, outputBands, 
      scanlineStride, 
      depth);
    
    if ((performGammaCorrection) && (gammaLut == null)) {
      initGammaLut(bitDepth);
    }
    if ((postProcess == 2) || 
      (postProcess == 3) || 
      (postProcess == 19)) {
      initGrayLut(bitDepth);
    }
    
    decodeImage(interlaceMethod == 1);
    sampleModel = theTile.getSampleModel();
    
    if ((colorType == 3) && (!expandPalette)) {
      if (outputHasAlphaPalette) {
        colorModel = new IndexColorModel(bitDepth, 
          paletteEntries, 
          redPalette, 
          greenPalette, 
          bluePalette, 
          alphaPalette);
      } else {
        colorModel = new IndexColorModel(bitDepth, 
          paletteEntries, 
          redPalette, 
          greenPalette, 
          bluePalette);
      }
    } else if ((colorType == 0) && 
      (bitDepth < 8) && (!output8BitGray)) {
      byte[] palette = expandBits[bitDepth];
      colorModel = new IndexColorModel(bitDepth, 
        palette.length, 
        palette, 
        palette, 
        palette);
    } else {
      colorModel = 
        ImageCodec.createComponentColorModel(sampleModel);
    }
  }
  
  private void parse_PLTE_chunk(PNGChunk chunk) {
    paletteEntries = (chunk.getLength() / 3);
    redPalette = new byte[paletteEntries];
    greenPalette = new byte[paletteEntries];
    bluePalette = new byte[paletteEntries];
    
    int pltIndex = 0;
    

    if (performGammaCorrection) {
      if (gammaLut == null) {
        initGammaLut(bitDepth == 16 ? 16 : 8);
      }
      
      for (int i = 0; i < paletteEntries; i++) {
        byte r = chunk.getByte(pltIndex++);
        byte g = chunk.getByte(pltIndex++);
        byte b = chunk.getByte(pltIndex++);
        
        redPalette[i] = ((byte)gammaLut[(r & 0xFF)]);
        greenPalette[i] = ((byte)gammaLut[(g & 0xFF)]);
        bluePalette[i] = ((byte)gammaLut[(b & 0xFF)]);
      }
    } else {
      for (int i = 0; i < paletteEntries; i++) {
        redPalette[i] = chunk.getByte(pltIndex++);
        greenPalette[i] = chunk.getByte(pltIndex++);
        bluePalette[i] = chunk.getByte(pltIndex++);
      }
    }
  }
  
  private void parse_bKGD_chunk(PNGChunk chunk) {
    hasBackground = true;
    
    switch (colorType) {
    case 3: 
      int bkgdIndex = chunk.getByte(0) & 0xFF;
      
      bkgdRed = (redPalette[bkgdIndex] & 0xFF);
      bkgdGreen = (greenPalette[bkgdIndex] & 0xFF);
      bkgdBlue = (bluePalette[bkgdIndex] & 0xFF);
      
      if (encodeParam != null)
      {
        ((PNGEncodeParam.Palette)encodeParam).setBackgroundPaletteIndex(bkgdIndex);
      }
      break;
    case 0: case 4: 
      int bkgdGray = chunk.getInt2(0);
      bkgdRed = (this.bkgdGreen = this.bkgdBlue = bkgdGray);
      
      if (encodeParam != null)
      {
        ((PNGEncodeParam.Gray)encodeParam).setBackgroundGray(bkgdGray);
      }
      break;
    case 2: case 6: 
      bkgdRed = chunk.getInt2(0);
      bkgdGreen = chunk.getInt2(2);
      bkgdBlue = chunk.getInt2(4);
      
      int[] bkgdRGB = new int[3];
      bkgdRGB[0] = bkgdRed;
      bkgdRGB[1] = bkgdGreen;
      bkgdRGB[2] = bkgdBlue;
      if (encodeParam != null)
      {
        ((PNGEncodeParam.RGB)encodeParam).setBackgroundRGB(bkgdRGB);
      }
      break;
    }
    
    int r = 0;int g = 0;int b = 0;
    if (bitDepth < 8) {
      r = expandBits[bitDepth][bkgdRed];
      g = expandBits[bitDepth][bkgdGreen];
      b = expandBits[bitDepth][bkgdBlue];
    } else if (bitDepth == 8) {
      r = bkgdRed;
      g = bkgdGreen;
      b = bkgdBlue;
    } else if (bitDepth == 16) {
      r = bkgdRed >> 8;
      g = bkgdGreen >> 8;
      b = bkgdBlue >> 8;
    }
    if (emitProperties) {
      properties.put("background_color", new Color(r, g, b));
    }
  }
  
  private void parse_cHRM_chunk(PNGChunk chunk)
  {
    if (sRGBRenderingIntent != -1) {
      return;
    }
    
    chromaticity = new float[8];
    chromaticity[0] = (chunk.getInt4(0) / 100000.0F);
    chromaticity[1] = (chunk.getInt4(4) / 100000.0F);
    chromaticity[2] = (chunk.getInt4(8) / 100000.0F);
    chromaticity[3] = (chunk.getInt4(12) / 100000.0F);
    chromaticity[4] = (chunk.getInt4(16) / 100000.0F);
    chromaticity[5] = (chunk.getInt4(20) / 100000.0F);
    chromaticity[6] = (chunk.getInt4(24) / 100000.0F);
    chromaticity[7] = (chunk.getInt4(28) / 100000.0F);
    
    if (encodeParam != null) {
      encodeParam.setChromaticity(chromaticity);
    }
    if (emitProperties) {
      properties.put("white_point_x", new Float(chromaticity[0]));
      properties.put("white_point_y", new Float(chromaticity[1]));
      properties.put("red_x", new Float(chromaticity[2]));
      properties.put("red_y", new Float(chromaticity[3]));
      properties.put("green_x", new Float(chromaticity[4]));
      properties.put("green_y", new Float(chromaticity[5]));
      properties.put("blue_x", new Float(chromaticity[6]));
      properties.put("blue_y", new Float(chromaticity[7]));
    }
  }
  
  private void parse_gAMA_chunk(PNGChunk chunk)
  {
    if (sRGBRenderingIntent != -1) {
      return;
    }
    
    fileGamma = (chunk.getInt4(0) / 100000.0F);
    
    float exp = 
      performGammaCorrection ? displayExponent / userExponent : 1.0F;
    if (encodeParam != null) {
      encodeParam.setGamma(fileGamma * exp);
    }
    if (emitProperties) {
      properties.put("gamma", new Float(fileGamma * exp));
    }
  }
  
  private void parse_hIST_chunk(PNGChunk chunk) {
    if (redPalette == null) {
      String msg = JaiI18N.getString("PNG_can_t_have_hIST_chunk_without_a_PLTE_chunk_");
      throw new RuntimeException(msg);
    }
    
    int length = redPalette.length;
    int[] hist = new int[length];
    for (int i = 0; i < length; i++) {
      hist[i] = chunk.getInt2(2 * i);
    }
    
    if (encodeParam != null) {
      encodeParam.setPaletteHistogram(hist);
    }
  }
  
  private void parse_iCCP_chunk(PNGChunk chunk) {
    String name = new String();
    

    int textIndex = 0;
    byte b; while ((b = chunk.getByte(textIndex++)) != 0) { byte b;
      name = name + (char)b;
    }
  }
  
  private void parse_pHYs_chunk(PNGChunk chunk) {
    int xPixelsPerUnit = chunk.getInt4(0);
    int yPixelsPerUnit = chunk.getInt4(4);
    int unitSpecifier = chunk.getInt1(8);
    
    if (encodeParam != null) {
      encodeParam.setPhysicalDimension(xPixelsPerUnit, 
        yPixelsPerUnit, 
        unitSpecifier);
    }
    if (emitProperties) {
      properties.put("x_pixels_per_unit", new Integer(xPixelsPerUnit));
      properties.put("y_pixels_per_unit", new Integer(yPixelsPerUnit));
      properties.put("pixel_aspect_ratio", 
        new Float(xPixelsPerUnit / yPixelsPerUnit));
      if (unitSpecifier == 1) {
        properties.put("pixel_units", "Meters");
      } else if (unitSpecifier != 0)
      {
        String msg = JaiI18N.getString("Unknown_PNG_pHYs_unit_specifier__not_0_or_1__");
        throw new RuntimeException(msg);
      }
    }
  }
  
  private void parse_sBIT_chunk(PNGChunk chunk) {
    if (colorType == 3) {
      significantBits = new int[3];
    } else {
      significantBits = new int[inputBands];
    }
    for (int i = 0; i < significantBits.length; i++) {
      int bits = chunk.getByte(i);
      int depth = colorType == 3 ? 8 : bitDepth;
      if ((bits <= 0) || (bits > depth))
      {

        String msg = JaiI18N.getString("Illegal_PNG_sBit_value____0_or___bit_depth__");
        throw new RuntimeException(msg);
      }
      significantBits[i] = bits;
    }
    
    if (encodeParam != null) {
      encodeParam.setSignificantBits(significantBits);
    }
    if (emitProperties) {
      properties.put("significant_bits", significantBits);
    }
  }
  
  private void parse_sRGB_chunk(PNGChunk chunk) {
    sRGBRenderingIntent = chunk.getByte(0);
    


    fileGamma = 0.45455F;
    
    chromaticity = new float[8];
    chromaticity[0] = 3.127F;
    chromaticity[1] = 3.29F;
    chromaticity[2] = 6.4F;
    chromaticity[3] = 3.3F;
    chromaticity[4] = 3.0F;
    chromaticity[5] = 6.0F;
    chromaticity[6] = 1.5F;
    chromaticity[7] = 0.6F;
    
    if (performGammaCorrection)
    {
      float gamma = fileGamma * (displayExponent / userExponent);
      if (encodeParam != null) {
        encodeParam.setGamma(gamma);
        encodeParam.setChromaticity(chromaticity);
      }
      if (emitProperties) {
        properties.put("gamma", new Float(gamma));
        properties.put("white_point_x", new Float(chromaticity[0]));
        properties.put("white_point_y", new Float(chromaticity[1]));
        properties.put("red_x", new Float(chromaticity[2]));
        properties.put("red_y", new Float(chromaticity[3]));
        properties.put("green_x", new Float(chromaticity[4]));
        properties.put("green_y", new Float(chromaticity[5]));
        properties.put("blue_x", new Float(chromaticity[6]));
        properties.put("blue_y", new Float(chromaticity[7]));
      }
    }
  }
  
  private void parse_tEXt_chunk(PNGChunk chunk) {
    String key = new String();
    String value = new String();
    

    int textIndex = 0;
    byte b; while ((b = chunk.getByte(textIndex++)) != 0) { byte b;
      key = key + (char)b;
    }
    
    for (int i = textIndex; i < chunk.getLength(); i++) {
      value = value + (char)chunk.getByte(i);
    }
    
    textKeys.add(key);
    textStrings.add(value);
  }
  
  private void parse_tIME_chunk(PNGChunk chunk) {
    int year = chunk.getInt2(0);
    int month = chunk.getInt1(2) - 1;
    int day = chunk.getInt1(3);
    int hour = chunk.getInt1(4);
    int minute = chunk.getInt1(5);
    int second = chunk.getInt1(6);
    
    TimeZone gmt = TimeZone.getTimeZone("GMT");
    
    GregorianCalendar cal = new GregorianCalendar(gmt);
    cal.set(year, month, day, 
      hour, minute, second);
    Date date = cal.getTime();
    
    if (encodeParam != null) {
      encodeParam.setModificationTime(date);
    }
    if (emitProperties) {
      properties.put("timestamp", date);
    }
  }
  
  private void parse_tRNS_chunk(PNGChunk chunk) {
    if (colorType == 3) {
      int entries = chunk.getLength();
      if (entries > paletteEntries)
      {
        String msg = JaiI18N.getString("Too_many_PNG_alpha_palette_entries_");
        throw new RuntimeException(msg);
      }
      

      alphaPalette = new byte[paletteEntries];
      for (int i = 0; i < entries; i++) {
        alphaPalette[i] = chunk.getByte(i);
      }
      

      for (int i = entries; i < paletteEntries; i++) {
        alphaPalette[i] = -1;
      }
      
      if (!suppressAlpha) {
        if (expandPalette) {
          postProcess = 5;
          outputBands = 4;
        } else {
          outputHasAlphaPalette = true;
        }
      }
    } else if (colorType == 0) {
      grayTransparentAlpha = chunk.getInt2(0);
      
      if (!suppressAlpha) {
        if (bitDepth < 8) {
          output8BitGray = true;
          maxOpacity = 255;
          postProcess = 3;
        } else {
          postProcess = 6;
        }
        
        if (expandGrayAlpha) {
          outputBands = 4;
          postProcess |= 0x10;
        } else {
          outputBands = 2;
        }
        
        if (encodeParam != null)
        {
          ((PNGEncodeParam.Gray)encodeParam).setTransparentGray(grayTransparentAlpha);
        }
      }
    } else if (colorType == 2) {
      redTransparentAlpha = chunk.getInt2(0);
      greenTransparentAlpha = chunk.getInt2(2);
      blueTransparentAlpha = chunk.getInt2(4);
      
      if (!suppressAlpha) {
        outputBands = 4;
        postProcess = 7;
        
        if (encodeParam != null) {
          int[] rgbTrans = new int[3];
          rgbTrans[0] = redTransparentAlpha;
          rgbTrans[1] = greenTransparentAlpha;
          rgbTrans[2] = blueTransparentAlpha;
          ((PNGEncodeParam.RGB)encodeParam)
            .setTransparentRGB(rgbTrans);
        }
      }
    } else if ((colorType == 4) || 
      (colorType == 6))
    {
      String msg = JaiI18N.getString("PNG_image_already_has_alpha__can_t_have_tRNS_chunk_");
      throw new RuntimeException(msg);
    }
  }
  
  private void parse_zTXt_chunk(PNGChunk chunk) {
    String key = new String();
    String value = new String();
    

    int textIndex = 0;
    byte b; while ((b = chunk.getByte(textIndex++)) != 0) { byte b;
      key = key + (char)b;
    }
    int method = chunk.getByte(textIndex++);
    try
    {
      int length = chunk.getLength() - textIndex;
      byte[] data = chunk.getData();
      InputStream cis = 
        new ByteArrayInputStream(data, textIndex, length);
      InputStream iis = new InflaterInputStream(cis);
      
      int c;
      while ((c = iis.read()) != -1) { int c;
        value = value + (char)c;
      }
      
      ztextKeys.add(key);
      ztextStrings.add(value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  



  private WritableRaster createRaster(int width, int height, int bands, int scanlineStride, int bitDepth)
  {
    WritableRaster ras = null;
    Point origin = new Point(0, 0);
    if ((bitDepth < 8) && (bands == 1)) {
      DataBuffer dataBuffer = new DataBufferByte(height * scanlineStride);
      ras = Raster.createPackedRaster(dataBuffer, 
        width, height, 
        bitDepth, 
        origin);
    } else if (bitDepth <= 8) {
      DataBuffer dataBuffer = new DataBufferByte(height * scanlineStride);
      ras = Raster.createInterleavedRaster(dataBuffer, 
        width, height, 
        scanlineStride, 
        bands, 
        bandOffsets[bands], 
        origin);
    } else {
      DataBuffer dataBuffer = new DataBufferUShort(height * scanlineStride);
      ras = Raster.createInterleavedRaster(dataBuffer, 
        width, height, 
        scanlineStride, 
        bands, 
        bandOffsets[bands], 
        origin);
    }
    
    return ras;
  }
  

  private static void decodeSubFilter(byte[] curr, int count, int bpp)
  {
    for (int i = bpp; i < count; i++)
    {

      int val = curr[i] & 0xFF;
      val += (curr[(i - bpp)] & 0xFF);
      
      curr[i] = ((byte)val);
    }
  }
  
  private static void decodeUpFilter(byte[] curr, byte[] prev, int count)
  {
    for (int i = 0; i < count; i++) {
      int raw = curr[i] & 0xFF;
      int prior = prev[i] & 0xFF;
      
      curr[i] = ((byte)(raw + prior));
    }
  }
  


  private static void decodeAverageFilter(byte[] curr, byte[] prev, int count, int bpp)
  {
    for (int i = 0; i < bpp; i++) {
      int raw = curr[i] & 0xFF;
      int priorRow = prev[i] & 0xFF;
      
      curr[i] = ((byte)(raw + priorRow / 2));
    }
    
    for (int i = bpp; i < count; i++) {
      int raw = curr[i] & 0xFF;
      int priorPixel = curr[(i - bpp)] & 0xFF;
      int priorRow = prev[i] & 0xFF;
      
      curr[i] = ((byte)(raw + (priorPixel + priorRow) / 2));
    }
  }
  
  private static int paethPredictor(int a, int b, int c) {
    int p = a + b - c;
    int pa = Math.abs(p - a);
    int pb = Math.abs(p - b);
    int pc = Math.abs(p - c);
    
    if ((pa <= pb) && (pa <= pc))
      return a;
    if (pb <= pc) {
      return b;
    }
    return c;
  }
  



  private static void decodePaethFilter(byte[] curr, byte[] prev, int count, int bpp)
  {
    for (int i = 0; i < bpp; i++) {
      int raw = curr[i] & 0xFF;
      int priorRow = prev[i] & 0xFF;
      
      curr[i] = ((byte)(raw + priorRow));
    }
    
    for (int i = bpp; i < count; i++) {
      int raw = curr[i] & 0xFF;
      int priorPixel = curr[(i - bpp)] & 0xFF;
      int priorRow = prev[i] & 0xFF;
      int priorRowPixel = prev[(i - bpp)] & 0xFF;
      
      curr[i] = ((byte)(raw + paethPredictor(priorPixel, 
        priorRow, 
        priorRowPixel)));
    }
  }
  




  private void processPixels(int process, Raster src, WritableRaster dst, int xOffset, int step, int y, int width)
  {
    int[] ps = src.getPixel(0, 0, null);
    int[] pd = dst.getPixel(0, 0, null);
    
    int dstX = xOffset;
    switch (process) {
    case 0: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        dst.setPixel(dstX, y, ps);
        dstX += step;
      }
      break;
    
    case 1: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        for (int i = 0; i < inputBands; i++) {
          int x = ps[i];
          ps[i] = gammaLut[x];
        }
        
        dst.setPixel(dstX, y, ps);
        dstX += step;
      }
      break;
    
    case 2: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        pd[0] = grayLut[ps[0]];
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
      break;
    
    case 3: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        int val = ps[0];
        pd[0] = grayLut[val];
        if (val == grayTransparentAlpha) {
          pd[1] = 0;
        } else {
          pd[1] = maxOpacity;
        }
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
      break;
    
    case 4: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        int val = ps[0];
        pd[0] = redPalette[val];
        pd[1] = greenPalette[val];
        pd[2] = bluePalette[val];
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
      break;
    
    case 5: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        int val = ps[0];
        pd[0] = redPalette[val];
        pd[1] = greenPalette[val];
        pd[2] = bluePalette[val];
        pd[3] = alphaPalette[val];
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
      break;
    
    case 6: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        int val = ps[0];
        if (performGammaCorrection) {
          val = gammaLut[val];
        }
        pd[0] = val;
        if (val == grayTransparentAlpha) {
          pd[1] = 0;
        } else {
          pd[1] = maxOpacity;
        }
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
      break;
    
    case 7: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        int r = ps[0];
        int g = ps[1];
        int b = ps[2];
        if (performGammaCorrection) {
          pd[0] = gammaLut[r];
          pd[1] = gammaLut[g];
          pd[2] = gammaLut[b];
        } else {
          pd[0] = r;
          pd[1] = g;
          pd[2] = b;
        }
        if ((r == redTransparentAlpha) && 
          (g == greenTransparentAlpha) && 
          (b == blueTransparentAlpha)) {
          pd[3] = 0;
        } else {
          pd[3] = maxOpacity;
        }
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
      break;
    
    case 8: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        int g = ps[0];
        if (performGammaCorrection) {
          pd[0] = gammaLut[g];
        } else {
          pd[0] = g;
        }
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
      break;
    
    case 9: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        int r = ps[0];
        int g = ps[1];
        int b = ps[2];
        if (performGammaCorrection) {
          pd[0] = gammaLut[r];
          pd[1] = gammaLut[g];
          pd[2] = gammaLut[b];
        } else {
          pd[0] = r;
          pd[1] = g;
          pd[2] = b;
        }
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
      break;
    
    case 17: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        int val = ps[0];
        int alpha = ps[1];
        int gamma = gammaLut[val];
        pd[0] = gamma;
        pd[1] = gamma;
        pd[2] = gamma;
        pd[3] = alpha;
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
      break;
    
    case 16: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        int val = ps[0];
        int alpha = ps[1];
        pd[0] = val;
        pd[1] = val;
        pd[2] = val;
        pd[3] = alpha;
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
      break;
    
    case 22: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        int val = ps[0];
        if (performGammaCorrection) {
          val = gammaLut[val];
        }
        pd[0] = val;
        pd[1] = val;
        pd[2] = val;
        if (val == grayTransparentAlpha) {
          pd[3] = 0;
        } else {
          pd[3] = maxOpacity;
        }
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
      break;
    
    case 19: 
      for (int srcX = 0; srcX < width; srcX++) {
        src.getPixel(srcX, 0, ps);
        
        int val = ps[0];
        int val2 = grayLut[val];
        pd[0] = val2;
        pd[1] = val2;
        pd[2] = val2;
        if (val == grayTransparentAlpha) {
          pd[3] = 0;
        } else {
          pd[3] = maxOpacity;
        }
        
        dst.setPixel(dstX, y, pd);
        dstX += step;
      }
    }
    
  }
  






  private void decodePass(WritableRaster imRas, int xOffset, int yOffset, int xStep, int yStep, int passWidth, int passHeight)
  {
    if ((passWidth == 0) || (passHeight == 0)) {
      return;
    }
    
    int bytesPerRow = (inputBands * passWidth * bitDepth + 7) / 8;
    int eltsPerRow = bitDepth == 16 ? bytesPerRow / 2 : bytesPerRow;
    byte[] curr = new byte[bytesPerRow];
    byte[] prior = new byte[bytesPerRow];
    

    WritableRaster passRow = 
      createRaster(passWidth, 1, inputBands, 
      eltsPerRow, 
      bitDepth);
    DataBuffer dataBuffer = passRow.getDataBuffer();
    int type = dataBuffer.getDataType();
    byte[] byteData = null;
    short[] shortData = null;
    if (type == 0) {
      byteData = ((DataBufferByte)dataBuffer).getData();
    } else {
      shortData = ((DataBufferUShort)dataBuffer).getData();
    }
    


    int srcY = 0; for (int dstY = yOffset; 
          srcY < passHeight; 
        dstY += yStep)
    {
      int filter = 0;
      try {
        filter = dataStream.read();
        dataStream.readFully(curr, 0, bytesPerRow);
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      switch (filter) {
      case 0: 
        break;
      case 1: 
        decodeSubFilter(curr, bytesPerRow, bytesPerPixel);
        break;
      case 2: 
        decodeUpFilter(curr, prior, bytesPerRow);
        break;
      case 3: 
        decodeAverageFilter(curr, prior, bytesPerRow, bytesPerPixel);
        break;
      case 4: 
        decodePaethFilter(curr, prior, bytesPerRow, bytesPerPixel);
        break;
      
      default: 
        String msg = JaiI18N.getString("Unknown_PNG_filter_type__not_0_4__");
        throw new RuntimeException(msg);
      }
      
      
      if (bitDepth < 16) {
        System.arraycopy(curr, 0, byteData, 0, bytesPerRow);
      } else {
        int idx = 0;
        for (int j = 0; j < eltsPerRow; j++) {
          shortData[j] = 
            ((short)(curr[idx] << 8 | curr[(idx + 1)] & 0xFF));
          idx += 2;
        }
      }
      
      processPixels(postProcess, 
        passRow, imRas, xOffset, xStep, dstY, passWidth);
      

      byte[] tmp = prior;
      prior = curr;
      curr = tmp;srcY++;
    }
  }
  





  private void decodeImage(boolean useInterlacing)
  {
    if (!useInterlacing) {
      decodePass(theTile, 0, 0, 1, 1, width, height);
    } else {
      decodePass(theTile, 0, 0, 8, 8, (width + 7) / 8, (height + 7) / 8);
      decodePass(theTile, 4, 0, 8, 8, (width + 3) / 8, (height + 7) / 8);
      decodePass(theTile, 0, 4, 4, 8, (width + 3) / 4, (height + 3) / 8);
      decodePass(theTile, 2, 0, 4, 4, (width + 1) / 4, (height + 3) / 4);
      decodePass(theTile, 0, 2, 2, 4, (width + 1) / 2, (height + 1) / 4);
      decodePass(theTile, 1, 0, 2, 2, width / 2, (height + 1) / 2);
      decodePass(theTile, 0, 1, 1, 2, width, height / 2);
    }
  }
  

  public Raster getTile(int tileX, int tileY)
  {
    if ((tileX != 0) || (tileY != 0))
    {
      String msg = JaiI18N.getString("Illegal_tile_requested_from_a_PNG_image_");
      throw new IllegalArgumentException(msg);
    }
    return theTile;
  }
}
