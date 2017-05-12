package edu.cmu.cs.stage3.image.codec;

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;


















































public class TIFFImageEncoder
  extends ImageEncoderImpl
{
  long firstIFDOffset = 0L;
  boolean skipByte = false;
  
  private static final int TIFF_BILEVEL_WHITE_IS_ZERO = 0;
  
  private static final int TIFF_BILEVEL_BLACK_IS_ZERO = 1;
  
  private static final int TIFF_PALETTE = 2;
  private static final int TIFF_FULLCOLOR = 3;
  private static final int TIFF_GREYSCALE = 4;
  private static final int COMP_NONE = 1;
  
  public TIFFImageEncoder(OutputStream output, ImageEncodeParam param)
  {
    super(output, param);
    if (this.param == null) {
      this.param = new TIFFEncodeParam();
    }
  }
  






  public void encode(RenderedImage im)
    throws IOException
  {
    int compression = 1;
    





    int minX = im.getMinX();
    int minY = im.getMinY();
    int width = im.getWidth();
    int height = im.getHeight();
    
    SampleModel sampleModel = im.getSampleModel();
    int numBands = sampleModel.getNumBands();
    int[] sampleSize = sampleModel.getSampleSize();
    
    int dataType = sampleModel.getDataType();
    if ((dataType != 0) && 
      (dataType != 2) && 
      (dataType != 1))
    {
      throw new Error(JaiI18N.getString("TIFF_encoder_supports_byte_and__unsigned__short_data_only_"));
    }
    
    boolean dataTypeIsShort = 
      (dataType == 2) || 
      (dataType == 1);
    
    ColorModel colorModel = im.getColorModel();
    if ((colorModel != null) && 
      ((colorModel instanceof IndexColorModel)) && 
      (dataTypeIsShort))
    {
      throw new Error(JaiI18N.getString("TIFF_encoder_does_not_support__unsigned__short_palette_images_"));
    }
    IndexColorModel icm = null;
    int sizeOfColormap = 0;
    int[] colormap = null;
    














    int numFields = 12;
    

    if (dataTypeIsShort) {
      numFields += 3;
    }
    
    int photometricInterpretation = 2;
    int imageType = 3;
    























    if ((colorModel instanceof IndexColorModel))
    {
      icm = (IndexColorModel)colorModel;
      int mapSize = icm.getMapSize();
      
      if (sampleSize[0] == 1)
      {

        if (mapSize != 2) {
          throw new IllegalArgumentException(
            JaiI18N.getString("Invalid_image___An_image_with_sampleSize_of_1_bit_must_have_IndexColorModel_with_mapsize_of_2_"));
        }
        
        byte[] r = new byte[mapSize];
        icm.getReds(r);
        byte[] g = new byte[mapSize];
        icm.getGreens(g);
        byte[] b = new byte[mapSize];
        icm.getBlues(b);
        
        if (((r[0] & 0xFF) == 0) && 
          ((r[1] & 0xFF) == 255) && 
          ((g[0] & 0xFF) == 0) && 
          ((g[1] & 0xFF) == 255) && 
          ((b[0] & 0xFF) == 0) && 
          ((b[1] & 0xFF) == 255))
        {
          imageType = 1;
        }
        else if (((r[0] & 0xFF) == 255) && 
          ((r[1] & 0xFF) == 0) && 
          ((g[0] & 0xFF) == 255) && 
          ((g[1] & 0xFF) == 0) && 
          ((b[0] & 0xFF) == 255) && 
          ((b[1] & 0xFF) == 0))
        {
          imageType = 0;
        }
        else {
          imageType = 2;
        }
      }
      else
      {
        imageType = 2;

      }
      


    }
    else if (((colorModel == null) || 
      (colorModel.getColorSpace().getType() == 6)) && 
      (numBands == 1))
    {
      imageType = 4;
    }
    else {
      imageType = 3;
    }
    

    switch (imageType)
    {
    case 0: 
      photometricInterpretation = 0;
      break;
    
    case 1: 
      photometricInterpretation = 1;
      break;
    

    case 4: 
      photometricInterpretation = 1;
      break;
    
    case 2: 
      photometricInterpretation = 3;
      
      icm = (IndexColorModel)colorModel;
      sizeOfColormap = icm.getMapSize();
      
      byte[] r = new byte[sizeOfColormap];
      icm.getReds(r);
      byte[] g = new byte[sizeOfColormap];
      icm.getGreens(g);
      byte[] b = new byte[sizeOfColormap];
      icm.getBlues(b);
      
      int redIndex = 0;int greenIndex = sizeOfColormap;
      int blueIndex = 2 * sizeOfColormap;
      colormap = new int[sizeOfColormap * 3];
      for (int i = 0; i < sizeOfColormap; i++) {
        colormap[(redIndex++)] = (r[i] << 8 & 0xFFFF);
        colormap[(greenIndex++)] = (g[i] << 8 & 0xFFFF);
        colormap[(blueIndex++)] = (b[i] << 8 & 0xFFFF);
      }
      
      sizeOfColormap *= 3;
      

      numFields++;
      break;
    
    case 3: 
      photometricInterpretation = 2;
    }
    
    


    long rowsPerStrip = 8L;
    int strips = (int)Math.ceil(height / 8.0D);
    long[] stripByteCounts = new long[strips];
    
    long bytesPerRow = Math.ceil(sampleSize[0] / 8.0D * 
      width * numBands);
    
    long bytesPerStrip = bytesPerRow * rowsPerStrip;
    
    for (int i = 0; i < strips; i++) {
      stripByteCounts[i] = bytesPerStrip;
    }
    

    long lastStripRows = height - rowsPerStrip * (strips - 1);
    stripByteCounts[(strips - 1)] = (lastStripRows * bytesPerRow);
    
    long totalBytesOfData = bytesPerStrip * (strips - 1) + 
      stripByteCounts[(strips - 1)];
    


    long[] stripOffsets = new long[strips];
    stripOffsets[0] = 8L;
    for (int i = 1; i < strips; i++) {
      stripOffsets[i] = (stripOffsets[(i - 1)] + stripByteCounts[(i - 1)]);
    }
    

    TIFFField[] fields = new TIFFField[numFields];
    

    int fld = 0;
    

    fields[(fld++)] = new TIFFField(256, 
      4, 1, 
      new long[] { width });
    

    fields[(fld++)] = new TIFFField(257, 
      4, 1, 
      new long[] { height });
    
    fields[(fld++)] = new TIFFField(258, 
      3, numBands, 
      sampleSize);
    
    fields[(fld++)] = new TIFFField(259, 
      3, 1, 
      new int[] { compression });
    
    fields[(fld++)] = 
      new TIFFField(262, 
      3, 1, 
      new int[] { photometricInterpretation });
    
    fields[(fld++)] = new TIFFField(273, 
      4, strips, 
      stripOffsets);
    
    fields[(fld++)] = new TIFFField(277, 
      3, 1, 
      new int[] { numBands });
    
    fields[(fld++)] = new TIFFField(278, 
      4, 1, 
      new long[] { rowsPerStrip });
    
    fields[(fld++)] = new TIFFField(279, 
      4, strips, 
      stripByteCounts);
    
    fields[(fld++)] = new TIFFField(282, 
      5, 1, 
      new long[][] { { 72L, 1L } });
    
    fields[(fld++)] = new TIFFField(283, 
      5, 1, 
      new long[][] { { 72L, 1L } });
    
    fields[(fld++)] = new TIFFField(296, 
      3, 1, 
      new int[] { 2 });
    
    if (colormap != null) {
      fields[(fld++)] = new TIFFField(320, 
        3, sizeOfColormap, 
        colormap);
    }
    

    if (dataTypeIsShort)
    {
      int[] sampleFormat = new int[numBands];
      sampleFormat[0] = (dataType == 1 ? 1 : 2);
      for (int b = 1; b < numBands; b++) {
        sampleFormat[b] = sampleFormat[0];
      }
      fields[(fld++)] = new TIFFField(339, 
        3, numBands, 
        sampleFormat);
      

      int[] minValue = new int[numBands];
      minValue[0] = (dataType == 1 ? 
        0 : 32768);
      for (int b = 1; b < numBands; b++) {
        minValue[b] = minValue[0];
      }
      fields[(fld++)] = 
        new TIFFField(340, 
        3, numBands, minValue);
      

      int[] maxValue = new int[numBands];
      maxValue[0] = (dataType == 1 ? 
        65535 : 32767);
      for (int b = 1; b < numBands; b++) {
        maxValue[b] = maxValue[0];
      }
      fields[(fld++)] = 
        new TIFFField(341, 
        3, numBands, maxValue);
    }
    


    firstIFDOffset = (8L + totalBytesOfData);
    
    if (firstIFDOffset % 2L != 0L) {
      skipByte = true;
      firstIFDOffset += 1L;
    }
    

    writeFileHeader(firstIFDOffset);
    

    int[] pixels = new int[8 * width * numBands];
    


    byte[] bpixels = null;
    if (dataType == 0) {
      bpixels = new byte[8 * width * numBands];
    } else if (dataTypeIsShort) {
      bpixels = new byte[16 * width * numBands];
    }
    

    int lastRow = minY + height;
    for (int row = minY; row < lastRow; row += 8) {
      int rows = Math.min(8, lastRow - row);
      int size = rows * width * numBands;
      

      Raster src = im.getData(new Rectangle(minX, 
        row, 
        width, 
        rows));
      src.getPixels(minX, 
        row, 
        width, 
        rows, 
        pixels);
      


      int pixel = 0;
      int k = 0;
      switch (sampleSize[0])
      {

      case 1: 
        int index = 0;
        

        for (int i = 0; i < rows; i++)
        {

          for (int j = 0; j < width / 8; j++)
          {
            pixel = pixels[(index++)] << 7 | 
              pixels[(index++)] << 6 | 
              pixels[(index++)] << 5 | 
              pixels[(index++)] << 4 | 
              pixels[(index++)] << 3 | 
              pixels[(index++)] << 2 | 
              pixels[(index++)] << 1 | 
              pixels[(index++)];
            bpixels[(k++)] = ((byte)pixel);
          }
          


          if (width % 8 > 0) {
            pixel = 0;
            for (int j = 0; j < width % 8; j++) {
              pixel |= pixels[(index++)] << 7 - j;
            }
            bpixels[(k++)] = ((byte)pixel);
          }
        }
        
        output.write(bpixels, 0, rows * ((width + 7) / 8));
        
        break;
      

      case 4: 
        int index = 0;
        

        for (int i = 0; i < rows; i++)
        {


          for (int j = 0; j < width / 2; j++) {
            pixel = pixels[(index++)] << 4 | pixels[(index++)];
            bpixels[(k++)] = ((byte)pixel);
          }
          

          if (width % 2 == 1) {
            pixel = pixels[(index++)] << 4;
            bpixels[(k++)] = ((byte)pixel);
          }
        }
        output.write(bpixels, 0, rows * ((width + 1) / 2));
        break;
      

      case 8: 
        for (int i = 0; i < size; i++) {
          bpixels[i] = ((byte)pixels[i]);
        }
        output.write(bpixels, 0, size);
        break;
      

      case 16: 
        int l = 0;
        for (int i = 0; i < size; i++) {
          short value = (short)pixels[i];
          bpixels[(l++)] = ((byte)((value & 0xFF00) >> 8));
          bpixels[(l++)] = ((byte)(value & 0xFF));
        }
        output.write(bpixels, 0, size * 2);
      }
      
    }
    


    writeDirectory(fields, 0);
  }
  

  private void writeFileHeader(long firstIFDOffset)
    throws IOException
  {
    output.write(77);
    output.write(77);
    

    output.write(0);
    output.write(42);
    

    writeLong(firstIFDOffset);
  }
  
  private void writeDirectory(TIFFField[] fields, int nextIFDOffset)
    throws IOException
  {
    if (skipByte) {
      output.write(0);
    }
    

    int numEntries = fields.length;
    
    long offsetBeyondIFD = firstIFDOffset + 12 * numEntries + 4L + 2L;
    Vector tooBig = new Vector();
    






    writeUnsignedShort(numEntries);
    
    for (int i = 0; i < numEntries; i++)
    {
      TIFFField field = fields[i];
      



      int tag = field.getTag();
      writeUnsignedShort(tag);
      

      int type = field.getType();
      writeUnsignedShort(type);
      

      int count = field.getCount();
      writeLong(count);
      

      if (count * sizeOfType[type] > 4)
      {

        writeLong(offsetBeyondIFD);
        offsetBeyondIFD += count * sizeOfType[type];
        tooBig.add(new Integer(i));
      }
      else
      {
        writeValuesAsFourBytes(field);
      }
    }
    


    writeLong(nextIFDOffset);
    


    for (int i = 0; i < tooBig.size(); i++) {
      int index = ((Integer)tooBig.elementAt(i)).intValue();
      writeValues(fields[index]);
    }
  }
  
  private static final int[] sizeOfType = {
  
    0, 1, 
    1, 
    2, 
    4, 
    8, 
    1, 
    1, 
    2, 
    4, 
    8, 
    4, 
    8 };
  
  private void writeValuesAsFourBytes(TIFFField field)
    throws IOException
  {
    int dataType = field.getType();
    int count = field.getCount();
    
    switch (dataType)
    {

    case 1: 
      byte[] bytes = field.getAsBytes();
      
      for (int i = 0; i < count; i++) {
        output.write(bytes[i]);
      }
      
      for (int i = 0; i < 4 - count; i++) {
        output.write(0);
      }
      
      break;
    

    case 3: 
      int[] shorts = field.getAsInts();
      
      for (int i = 0; i < count; i++) {
        writeUnsignedShort(shorts[i]);
      }
      
      for (int i = 0; i < 2 - count; i++) {
        writeUnsignedShort(0);
      }
      
      break;
    

    case 4: 
      long[] longs = field.getAsLongs();
      
      for (int i = 0; i < count; i++) {
        writeLong(longs[i]);
      }
    }
    
  }
  
  private void writeValues(TIFFField field)
    throws IOException
  {
    int dataType = field.getType();
    int count = field.getCount();
    
    switch (dataType)
    {

    case 1: 
      byte[] bytes = field.getAsBytes();
      for (int i = 0; i < count; i++) {
        output.write(bytes[i]);
      }
      break;
    

    case 3: 
      int[] shorts = field.getAsInts();
      for (int i = 0; i < count; i++) {
        writeUnsignedShort(shorts[i]);
      }
      break;
    

    case 4: 
      long[] longs = field.getAsLongs();
      for (int i = 0; i < count; i++) {
        writeLong(longs[i]);
      }
      break;
    
    case 5: 
      long[][] rationals = field.getAsRationals();
      for (int i = 0; i < count; i++) {
        writeLong(rationals[i][0]);
        writeLong(rationals[i][1]);
      }
    }
    
  }
  


  private void writeUnsignedShort(int s)
    throws IOException
  {
    output.write((s & 0xFF00) >>> 8);
    output.write(s & 0xFF);
  }
  
  private void writeLong(long l) throws IOException {
    output.write((int)((l & 0xFFFFFFFFFF000000) >>> 24));
    output.write((int)((l & 0xFF0000) >>> 16));
    output.write((int)((l & 0xFF00) >>> 8));
    output.write((int)l & 0xFF);
  }
}
