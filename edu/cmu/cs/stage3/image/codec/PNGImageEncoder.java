package edu.cmu.cs.stage3.image.codec;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;


































































































































































































































































public class PNGImageEncoder
  extends ImageEncoderImpl
{
  private static final int PNG_COLOR_GRAY = 0;
  private static final int PNG_COLOR_RGB = 2;
  private static final int PNG_COLOR_PALETTE = 3;
  private static final int PNG_COLOR_GRAY_ALPHA = 4;
  private static final int PNG_COLOR_RGB_ALPHA = 6;
  private static final byte[] magic = {
    -119, 80, 78, 71, 
    13, 10, 26, 10 };
  
  private PNGEncodeParam param;
  
  private RenderedImage image;
  
  private int width;
  
  private int height;
  
  private int bitDepth;
  private int bitShift;
  private int numBands;
  private int colorType;
  private int bpp;
  private boolean skipAlpha = false;
  private boolean compressGray = false;
  
  private boolean interlace;
  
  private byte[] redPalette = null;
  private byte[] greenPalette = null;
  private byte[] bluePalette = null;
  private byte[] alphaPalette = null;
  
  private DataOutputStream dataOutput;
  
  public PNGImageEncoder(OutputStream output, PNGEncodeParam param)
  {
    super(output, param);
    
    if (param != null) {
      this.param = param;
    }
    dataOutput = new DataOutputStream(output);
  }
  
  private void writeMagic() throws IOException {
    dataOutput.write(magic);
  }
  
  private void writeIHDR() throws IOException {
    ChunkStream cs = new ChunkStream("IHDR");
    cs.writeInt(width);
    cs.writeInt(height);
    cs.writeByte((byte)bitDepth);
    cs.writeByte((byte)colorType);
    cs.writeByte(0);
    cs.writeByte(0);
    cs.writeByte(interlace ? 1 : 0);
    
    cs.writeToStream(dataOutput);
  }
  
  private byte[] prevRow = null;
  private byte[] currRow = null;
  
  private byte[][] filteredRows = null;
  
  private static int clamp(int val, int maxValue) {
    return val > maxValue ? maxValue : val;
  }
  

  private void encodePass(OutputStream os, Raster ras, int xOffset, int yOffset, int xSkip, int ySkip)
    throws IOException
  {
    int minX = ras.getMinX();
    int minY = ras.getMinY();
    int width = ras.getWidth();
    int height = ras.getHeight();
    
    xOffset *= numBands;
    xSkip *= numBands;
    
    int samplesPerByte = 8 / bitDepth;
    
    int numSamples = width * numBands;
    int[] samples = new int[numSamples];
    
    int pixels = (numSamples - xOffset + xSkip - 1) / xSkip;
    int bytesPerRow = pixels * numBands;
    if (bitDepth < 8) {
      bytesPerRow = (bytesPerRow + samplesPerByte - 1) / samplesPerByte;
    } else if (bitDepth == 16) {
      bytesPerRow *= 2;
    }
    
    if (bytesPerRow == 0) {
      return;
    }
    
    currRow = new byte[bytesPerRow + bpp];
    prevRow = new byte[bytesPerRow + bpp];
    
    filteredRows = new byte[5][bytesPerRow + bpp];
    
    int maxValue = (1 << bitDepth) - 1;
    
    for (int row = minY + yOffset; row < minY + height; row += ySkip) {
      ras.getPixels(minX, row, width, 1, samples);
      
      if (compressGray) {
        int shift = 8 - bitDepth;
        for (int i = 0; i < width; i++) {
          samples[i] >>= shift;
        }
      }
      
      int count = bpp;
      int pos = 0;
      int tmp = 0;
      
      switch (bitDepth) {
      case 1: 
      case 2: 
      case 4: 
        int mask = samplesPerByte - 1;
        for (int s = xOffset; s < numSamples; s += xSkip) {
          int val = clamp(samples[s] >> bitShift, maxValue);
          tmp = tmp << bitDepth | val;
          
          if ((pos++ & mask) == mask) {
            currRow[(count++)] = ((byte)tmp);
            tmp = 0;
          }
        }
        

        if ((pos & mask) != 0) {
          tmp <<= (8 / bitDepth - pos) * bitDepth;
          currRow[(count++)] = ((byte)tmp);
        }
        break;
      
      case 8: 
        for (int s = xOffset; s < numSamples; s += xSkip) {
          for (int b = 0; b < numBands; b++) {
            currRow[(count++)] = 
              ((byte)clamp(samples[(s + b)] >> bitShift, maxValue));
          }
        }
        break;
      
      case 16: 
        for (int s = xOffset; s < numSamples; s += xSkip) {
          for (int b = 0; b < numBands; b++) {
            int val = clamp(samples[(s + b)] >> bitShift, maxValue);
            currRow[(count++)] = ((byte)(val >> 8));
            currRow[(count++)] = ((byte)(val & 0xFF));
          }
        }
      }
      
      

      int filterType = param.filterRow(currRow, prevRow, 
        filteredRows, 
        bytesPerRow, bpp);
      
      os.write(filterType);
      os.write(filteredRows[filterType], bpp, bytesPerRow);
      

      byte[] swap = currRow;
      currRow = prevRow;
      prevRow = swap;
    }
  }
  
  private void writeIDAT() throws IOException {
    IDATOutputStream ios = new IDATOutputStream(dataOutput, 8192);
    DeflaterOutputStream dos = 
      new DeflaterOutputStream(ios, new Deflater(9));
    

    Raster ras = image.getData();
    
    if (skipAlpha) {
      int numBands = ras.getNumBands() - 1;
      int[] bandList = new int[numBands];
      for (int i = 0; i < numBands; i++) {
        bandList[i] = i;
      }
      ras = ras.createChild(0, 0, 
        ras.getWidth(), ras.getHeight(), 
        0, 0, 
        bandList);
    }
    
    if (interlace)
    {
      encodePass(dos, ras, 0, 0, 8, 8);
      
      encodePass(dos, ras, 4, 0, 8, 8);
      
      encodePass(dos, ras, 0, 4, 4, 8);
      
      encodePass(dos, ras, 2, 0, 4, 4);
      
      encodePass(dos, ras, 0, 2, 2, 4);
      
      encodePass(dos, ras, 1, 0, 2, 2);
      
      encodePass(dos, ras, 0, 1, 1, 2);
    } else {
      encodePass(dos, ras, 0, 0, 1, 1);
    }
    
    dos.finish();
    ios.flush();
  }
  
  private void writeIEND() throws IOException {
    ChunkStream cs = new ChunkStream("IEND");
    cs.writeToStream(dataOutput);
  }
  
  private static final float[] srgbChroma = {
    0.3127F, 0.329F, 0.64F, 0.33F, 0.3F, 0.6F, 0.15F, 0.06F };
  
  private void writeCHRM() throws IOException
  {
    if ((param.isChromaticitySet()) || (param.isSRGBIntentSet())) {
      ChunkStream cs = new ChunkStream("cHRM");
      float[] chroma;
      float[] chroma;
      if (!param.isSRGBIntentSet()) {
        chroma = param.getChromaticity();
      } else {
        chroma = srgbChroma;
      }
      
      for (int i = 0; i < 8; i++) {
        cs.writeInt((int)(chroma[i] * 100000.0F));
      }
      cs.writeToStream(dataOutput);
    }
  }
  
  private void writeGAMA() throws IOException {
    if ((param.isGammaSet()) || (param.isSRGBIntentSet())) {
      ChunkStream cs = new ChunkStream("gAMA");
      float gamma;
      float gamma;
      if (!param.isSRGBIntentSet()) {
        gamma = param.getGamma();
      } else {
        gamma = 0.45454544F;
      }
      
      cs.writeInt((int)(gamma * 100000.0F));
      cs.writeToStream(dataOutput);
    }
  }
  
  private void writeICCP() throws IOException {
    if (param.isICCProfileDataSet()) {
      ChunkStream cs = new ChunkStream("iCCP");
      byte[] ICCProfileData = param.getICCProfileData();
      cs.write(ICCProfileData);
      cs.writeToStream(dataOutput);
    }
  }
  
  private void writeSBIT() throws IOException {
    if (param.isSignificantBitsSet()) {
      ChunkStream cs = new ChunkStream("sBIT");
      int[] significantBits = param.getSignificantBits();
      int len = significantBits.length;
      for (int i = 0; i < len; i++) {
        cs.writeByte(significantBits[i]);
      }
      cs.writeToStream(dataOutput);
    }
  }
  
  private void writeSRGB() throws IOException {
    if (param.isSRGBIntentSet()) {
      ChunkStream cs = new ChunkStream("sRGB");
      
      int intent = param.getSRGBIntent();
      cs.write(intent);
      cs.writeToStream(dataOutput);
    }
  }
  
  private void writePLTE() throws IOException {
    if (redPalette == null) {
      return;
    }
    
    ChunkStream cs = new ChunkStream("PLTE");
    for (int i = 0; i < redPalette.length; i++) {
      cs.writeByte(redPalette[i]);
      cs.writeByte(greenPalette[i]);
      cs.writeByte(bluePalette[i]);
    }
    
    cs.writeToStream(dataOutput);
  }
  
  private void writeBKGD() throws IOException {
    if (param.isBackgroundSet()) {
      ChunkStream cs = new ChunkStream("bKGD");
      
      switch (colorType) {
      case 0: 
      case 4: 
        int gray = ((PNGEncodeParam.Gray)param).getBackgroundGray();
        cs.writeShort(gray);
        break;
      
      case 3: 
        int index = 
          ((PNGEncodeParam.Palette)param).getBackgroundPaletteIndex();
        cs.writeByte(index);
        break;
      
      case 2: 
      case 6: 
        int[] rgb = ((PNGEncodeParam.RGB)param).getBackgroundRGB();
        cs.writeShort(rgb[0]);
        cs.writeShort(rgb[1]);
        cs.writeShort(rgb[2]);
      }
      
      
      cs.writeToStream(dataOutput);
    }
  }
  
  private void writeHIST() throws IOException {
    if (param.isPaletteHistogramSet()) {
      ChunkStream cs = new ChunkStream("hIST");
      
      int[] hist = param.getPaletteHistogram();
      for (int i = 0; i < hist.length; i++) {
        cs.writeShort(hist[i]);
      }
      
      cs.writeToStream(dataOutput);
    }
  }
  
  private void writeTRNS() throws IOException {
    if ((param.isTransparencySet()) && 
      (colorType != 4) && 
      (colorType != 6)) {
      ChunkStream cs = new ChunkStream("tRNS");
      
      if ((param instanceof PNGEncodeParam.Palette)) {
        byte[] t = 
          ((PNGEncodeParam.Palette)param).getPaletteTransparency();
        for (int i = 0; i < t.length; i++) {
          cs.writeByte(t[i]);
        }
      } else if ((param instanceof PNGEncodeParam.Gray)) {
        int t = ((PNGEncodeParam.Gray)param).getTransparentGray();
        cs.writeShort(t);
      } else if ((param instanceof PNGEncodeParam.RGB)) {
        int[] t = ((PNGEncodeParam.RGB)param).getTransparentRGB();
        cs.writeShort(t[0]);
        cs.writeShort(t[1]);
        cs.writeShort(t[2]);
      }
      
      cs.writeToStream(dataOutput);
    } else if (colorType == 3) {
      int lastEntry = Math.min(255, alphaPalette.length - 1);
      
      for (int nonOpaque = lastEntry; nonOpaque >= 0; nonOpaque--) {
        if (alphaPalette[nonOpaque] != -1) {
          break;
        }
      }
      
      if (nonOpaque >= 0) {
        ChunkStream cs = new ChunkStream("tRNS");
        for (int i = 0; i <= nonOpaque; i++) {
          cs.writeByte(alphaPalette[i]);
        }
        cs.writeToStream(dataOutput);
      }
    }
  }
  
  private void writePHYS() throws IOException {
    if (param.isPhysicalDimensionSet()) {
      ChunkStream cs = new ChunkStream("pHYs");
      
      int[] dims = param.getPhysicalDimension();
      cs.writeInt(dims[0]);
      cs.writeInt(dims[1]);
      cs.writeByte((byte)dims[2]);
      
      cs.writeToStream(dataOutput);
    }
  }
  
  private void writeSPLT() throws IOException {
    if (param.isSuggestedPaletteSet()) {
      ChunkStream cs = new ChunkStream("sPLT");
      
      System.out.println("sPLT not supported yet.");
      
      cs.writeToStream(dataOutput);
    }
  }
  
  private void writeTIME() throws IOException {
    if (param.isModificationTimeSet()) {
      ChunkStream cs = new ChunkStream("tIME");
      
      Date date = param.getModificationTime();
      TimeZone gmt = TimeZone.getTimeZone("GMT");
      
      GregorianCalendar cal = new GregorianCalendar(gmt);
      cal.setTime(date);
      
      int year = cal.get(1);
      int month = cal.get(2);
      int day = cal.get(5);
      int hour = cal.get(11);
      int minute = cal.get(12);
      int second = cal.get(13);
      
      cs.writeShort(year);
      cs.writeByte(month + 1);
      cs.writeByte(day);
      cs.writeByte(hour);
      cs.writeByte(minute);
      cs.writeByte(second);
      
      cs.writeToStream(dataOutput);
    }
  }
  
  private void writeTEXT() throws IOException {
    if (param.isTextSet()) {
      String[] text = param.getText();
      
      for (int i = 0; i < text.length / 2; i++) {
        byte[] keyword = text[(2 * i)].getBytes();
        byte[] value = text[(2 * i + 1)].getBytes();
        
        ChunkStream cs = new ChunkStream("tEXt");
        
        cs.write(keyword, 0, Math.min(keyword.length, 79));
        cs.write(0);
        cs.write(value);
        
        cs.writeToStream(dataOutput);
      }
    }
  }
  
  private void writeZTXT() throws IOException {
    if (param.isCompressedTextSet()) {
      String[] text = param.getCompressedText();
      
      for (int i = 0; i < text.length / 2; i++) {
        byte[] keyword = text[(2 * i)].getBytes();
        byte[] value = text[(2 * i + 1)].getBytes();
        
        ChunkStream cs = new ChunkStream("zTXt");
        
        cs.write(keyword, 0, Math.min(keyword.length, 79));
        cs.write(0);
        cs.write(0);
        
        DeflaterOutputStream dos = new DeflaterOutputStream(cs);
        dos.write(value);
        dos.finish();
        
        cs.writeToStream(dataOutput);
      }
    }
  }
  
  private void writePrivateChunks() throws IOException {
    int numChunks = param.getNumPrivateChunks();
    for (int i = 0; i < numChunks; i++) {
      String type = param.getPrivateChunkType(i);
      char char3 = type.charAt(3);
      
      byte[] data = param.getPrivateChunkData(i);
      
      ChunkStream cs = new ChunkStream(type);
      cs.write(data);
      cs.writeToStream(dataOutput);
    }
  }
  
  private final byte[][] expandBits = {
  
    0, { 0, -1 }, 
    { 0, 85, -86, -1 }, 
    



    00, 17, 34, 51, 68, 85, 102, 119, -120, -103, -86, -69, -52, -35, -18, -1 } };
  











  private PNGEncodeParam.Gray createGrayParam(byte[] redPalette, byte[] greenPalette, byte[] bluePalette, byte[] alphaPalette)
  {
    PNGEncodeParam.Gray param = new PNGEncodeParam.Gray();
    int numTransparent = 0;
    
    int entries = 1 << bitDepth;
    for (int i = 0; i < entries; i++) {
      byte red = redPalette[i];
      if ((red != greenPalette[i]) || 
        (red != bluePalette[i]) || 
        (red != expandBits[bitDepth][i])) {
        return null;
      }
      

      byte alpha = alphaPalette[i];
      if (alpha == 0) {
        param.setTransparentGray(i);
        
        numTransparent++;
        if (numTransparent > 1) {
          return null;
        }
      } else if (alpha != -1) {
        return null;
      }
    }
    
    return param;
  }
  
  public void encode(RenderedImage im) throws IOException
  {
    image = im;
    width = image.getWidth();
    height = image.getHeight();
    
    SampleModel sampleModel = image.getSampleModel();
    
    int[] sampleSize = sampleModel.getSampleSize();
    

    bitDepth = -1;
    bitShift = 0;
    

    if ((param instanceof PNGEncodeParam.Gray)) {
      PNGEncodeParam.Gray paramg = (PNGEncodeParam.Gray)param;
      if (paramg.isBitDepthSet()) {
        bitDepth = paramg.getBitDepth();
      }
      
      if (paramg.isBitShiftSet()) {
        bitShift = paramg.getBitShift();
      }
    }
    

    if (bitDepth == -1)
    {

      bitDepth = sampleSize[0];
      
      for (int i = 1; i < sampleSize.length; i++) {
        if (sampleSize[i] != bitDepth) {
          throw new RuntimeException();
        }
      }
      

      if ((bitDepth > 2) && (bitDepth < 4)) {
        bitDepth = 4;
      } else if ((bitDepth > 4) && (bitDepth < 8)) {
        bitDepth = 8;
      } else if ((bitDepth > 8) && (bitDepth < 16)) {
        bitDepth = 16;
      } else if (bitDepth > 16) {
        throw new RuntimeException();
      }
    }
    
    numBands = sampleModel.getNumBands();
    bpp = (numBands * (bitDepth == 16 ? 2 : 1));
    
    ColorModel colorModel = image.getColorModel();
    if ((colorModel instanceof IndexColorModel)) {
      if ((bitDepth < 1) || (bitDepth > 8)) {
        throw new RuntimeException();
      }
      if (sampleModel.getNumBands() != 1) {
        throw new RuntimeException();
      }
      
      IndexColorModel icm = (IndexColorModel)colorModel;
      int size = icm.getMapSize();
      
      redPalette = new byte[size];
      greenPalette = new byte[size];
      bluePalette = new byte[size];
      alphaPalette = new byte[size];
      
      icm.getReds(redPalette);
      icm.getGreens(greenPalette);
      icm.getBlues(bluePalette);
      icm.getAlphas(alphaPalette);
      
      bpp = 1;
      
      if (param == null) {
        param = createGrayParam(redPalette, 
          greenPalette, 
          bluePalette, 
          alphaPalette);
      }
      

      if (param == null) {
        param = new PNGEncodeParam.Palette();
      }
      
      if ((param instanceof PNGEncodeParam.Palette))
      {
        PNGEncodeParam.Palette parami = (PNGEncodeParam.Palette)param;
        if (parami.isPaletteSet()) {
          int[] palette = parami.getPalette();
          size = palette.length / 3;
          
          int index = 0;
          for (int i = 0; i < size; i++) {
            redPalette[i] = ((byte)palette[(index++)]);
            greenPalette[i] = ((byte)palette[(index++)]);
            bluePalette[i] = ((byte)palette[(index++)]);
            alphaPalette[i] = -1;
          }
        }
        colorType = 3;
      } else if ((param instanceof PNGEncodeParam.Gray)) {
        redPalette = (this.greenPalette = this.bluePalette = this.alphaPalette = null);
        colorType = 0;
      } else {
        throw new RuntimeException();
      }
    } else if (numBands == 1) {
      if (param == null) {
        param = new PNGEncodeParam.Gray();
      }
      colorType = 0;
    } else if (numBands == 2) {
      if (param == null) {
        param = new PNGEncodeParam.Gray();
      }
      
      if (param.isTransparencySet()) {
        skipAlpha = true;
        numBands = 1;
        if ((sampleSize[0] == 8) && (bitDepth < 8)) {
          compressGray = true;
        }
        bpp = (bitDepth == 16 ? 2 : 1);
        colorType = 0;
      } else {
        if (bitDepth < 8) {
          bitDepth = 8;
        }
        colorType = 4;
      }
    } else if (numBands == 3) {
      if (param == null) {
        param = new PNGEncodeParam.RGB();
      }
      colorType = 2;
    } else if (numBands == 4) {
      if (param == null) {
        param = new PNGEncodeParam.RGB();
      }
      if (param.isTransparencySet()) {
        skipAlpha = true;
        numBands = 3;
        bpp = (bitDepth == 16 ? 6 : 3);
        colorType = 2;
      } else {
        colorType = 6;
      }
    }
    
    interlace = param.getInterlacing();
    
    writeMagic();
    
    writeIHDR();
    
    writeCHRM();
    writeGAMA();
    writeICCP();
    writeSBIT();
    writeSRGB();
    
    writePLTE();
    
    writeHIST();
    writeTRNS();
    writeBKGD();
    
    writePHYS();
    writeSPLT();
    writeTIME();
    writeTEXT();
    writeZTXT();
    
    writePrivateChunks();
    
    writeIDAT();
    
    writeIEND();
    
    dataOutput.flush();
  }
}
