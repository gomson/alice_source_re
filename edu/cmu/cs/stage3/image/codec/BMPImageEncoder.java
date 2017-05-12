package edu.cmu.cs.stage3.image.codec;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.OutputStream;

































































public class BMPImageEncoder
  extends ImageEncoderImpl
{
  private OutputStream output;
  private int version;
  private boolean isCompressed;
  private boolean isTopDown;
  private int w;
  private int h;
  
  public BMPImageEncoder(OutputStream output, ImageEncodeParam param)
  {
    super(output, param);
    
    this.output = output;
    BMPEncodeParam bmpParam;
    BMPEncodeParam bmpParam;
    if (param == null)
    {
      bmpParam = new BMPEncodeParam();
    } else {
      bmpParam = (BMPEncodeParam)param;
    }
    
    version = bmpParam.getVersion();
    isCompressed = bmpParam.isCompressed();
    isTopDown = bmpParam.isTopDown();
  }
  





  public void encode(RenderedImage im)
    throws IOException
  {
    int minX = im.getMinX();
    int minY = im.getMinY();
    w = im.getWidth();
    h = im.getHeight();
    

    int bitsPerPixel = 24;
    boolean isPalette = false;
    int paletteEntries = 0;
    IndexColorModel icm = null;
    
    SampleModel sm = im.getSampleModel();
    int numBands = sm.getNumBands();
    
    ColorModel cm = im.getColorModel();
    
    if ((numBands != 1) && (numBands != 3)) {
      throw 
        new IllegalArgumentException(JaiI18N.getString("Only_images_with_either_1_or_3_bands_can_be_written_out_as_BMP_files_"));
    }
    
    int[] sampleSize = sm.getSampleSize();
    if (sampleSize[0] > 8) {
      throw new RuntimeException(JaiI18N.getString("BMP_file_format_cannot_support_data_with_a_bitdepth"));
    }
    
    for (int i = 1; i < sampleSize.length; i++) {
      if (sampleSize[i] != sampleSize[0]) {
        throw 
          new RuntimeException(JaiI18N.getString("All_samples_must_have_the_same_size_"));
      }
    }
    

    int dataType = sm.getTransferType();
    if ((dataType == 1) || 
      (dataType == 2) || 
      (dataType == 3) || 
      (dataType == 4) || 
      (dataType == 5)) {
      throw new RuntimeException(JaiI18N.getString("Image_to_be_written_has_ushort_short_int_float_double_data_type__unsuitable_for_BMP_file_format_"));
    }
    

    int destScanlineBytes = w * numBands;
    


    int compression = 0;
    
    byte[] r = null;byte[] g = null;byte[] b = null;byte[] a = null;
    
    if ((cm instanceof IndexColorModel))
    {
      isPalette = true;
      icm = (IndexColorModel)cm;
      paletteEntries = icm.getMapSize();
      
      if (paletteEntries <= 2)
      {
        bitsPerPixel = 1;
        destScanlineBytes = (int)Math.ceil(w / 8.0D);
      }
      else if (paletteEntries <= 16)
      {
        bitsPerPixel = 4;
        destScanlineBytes = (int)Math.ceil(w / 2.0D);
      }
      else if (paletteEntries <= 256)
      {
        bitsPerPixel = 8;

      }
      else
      {

        bitsPerPixel = 24;
        isPalette = false;
        paletteEntries = 0;
        destScanlineBytes = w * 3;
      }
      
      if (isPalette)
      {
        r = new byte[paletteEntries];
        g = new byte[paletteEntries];
        b = new byte[paletteEntries];
        a = new byte[paletteEntries];
        
        icm.getAlphas(a);
        icm.getReds(r);
        icm.getGreens(g);
        icm.getBlues(b);

      }
      

    }
    else if (numBands == 1)
    {
      isPalette = true;
      paletteEntries = 256;
      
      bitsPerPixel = sampleSize[0];
      
      destScanlineBytes = (int)Math.ceil(w * bitsPerPixel / 
        8.0D);
      
      r = new byte['Ā'];
      g = new byte['Ā'];
      b = new byte['Ā'];
      a = new byte['Ā'];
      
      for (int i = 0; i < 256; i++) {
        r[i] = ((byte)i);
        g[i] = ((byte)i);
        b[i] = ((byte)i);
        a[i] = ((byte)i);
      }
    }
    


    int fileSize = 0;
    int offset = 0;
    int headerSize = 0;
    int imageSize = 0;
    int xPelsPerMeter = 0;
    int yPelsPerMeter = 0;
    int colorsUsed = 0;
    int colorsImportant = paletteEntries;
    int padding = 0;
    

    int remainder = destScanlineBytes % 4;
    if (remainder != 0) {
      padding = 4 - remainder;
    }
    
    switch (version) {
    case 0: 
      offset = 26 + paletteEntries * 3;
      headerSize = 12;
      imageSize = (destScanlineBytes + padding) * h;
      fileSize = imageSize + offset;
      throw 
        new RuntimeException(JaiI18N.getString("Encoding_of_BMP_files_in_any_format_other_than_Version_3_is_not_implemented_yet_"));
    



    case 1: 
      offset = 54 + paletteEntries * 4;
      imageSize = (destScanlineBytes + padding) * h;
      fileSize = imageSize + offset;
      headerSize = 40;
      break;
    
    case 2: 
      headerSize = 108;
      throw 
        new RuntimeException(JaiI18N.getString("Encoding_of_BMP_files_in_any_format_other_than_Version_3_is_not_implemented_yet_"));
    }
    
    
    writeFileHeader(fileSize, offset);
    
    writeInfoHeader(headerSize, bitsPerPixel);
    

    writeDWord(compression);
    

    writeDWord(imageSize);
    

    writeDWord(xPelsPerMeter);
    

    writeDWord(yPelsPerMeter);
    

    writeDWord(colorsUsed);
    

    writeDWord(colorsImportant);
    

    if (isPalette)
    {

      switch (version)
      {


      case 0: 
        for (int i = 0; i < paletteEntries; i++) {
          output.write(b[i]);
          output.write(g[i]);
          output.write(r[i]);
        }
        break;
      


      default: 
        for (int i = 0; i < paletteEntries; i++) {
          output.write(b[i]);
          output.write(g[i]);
          output.write(r[i]);
          output.write(a[i]);
        }
      }
      
    }
    



    int scanlineBytes = w * numBands;
    

    int[] pixels = new int[8 * scanlineBytes];
    


    byte[] bpixels = new byte[destScanlineBytes];
    


    if (!isTopDown)
    {

      int lastRow = minY + h;
      
      for (int row = lastRow - 1; row >= minY; row -= 8)
      {
        int rows = Math.min(8, row - minY + 1);
        

        Raster src = im.getData(new Rectangle(minX, row - rows + 1, 
          w, rows));
        
        src.getPixels(minX, row - rows + 1, w, rows, pixels);
        
        int l = 0;
        

        int max = scanlineBytes * rows - 1;
        
        for (int i = 0; i < rows; i++)
        {

          l = max - (i + 1) * scanlineBytes + 1;
          
          writePixels(l, scanlineBytes, bitsPerPixel, pixels, 
            bpixels, padding, numBands, icm);
        }
        
      }
    }
    else
    {
      int lastRow = minY + h;
      for (int row = minY; row < lastRow; row += 8) {
        int rows = Math.min(8, lastRow - row);
        

        Raster src = im.getData(new Rectangle(minX, row, 
          w, rows));
        src.getPixels(minX, row, w, rows, pixels);
        
        int l = 0;
        for (int i = 0; i < rows; i++) {
          writePixels(l, scanlineBytes, bitsPerPixel, pixels, 
            bpixels, padding, numBands, icm);
        }
      }
    }
  }
  



  private void writePixels(int l, int scanlineBytes, int bitsPerPixel, int[] pixels, byte[] bpixels, int padding, int numBands, IndexColorModel icm)
    throws IOException
  {
    int pixel = 0;
    
    int k = 0;
    switch (bitsPerPixel)
    {

    case 1: 
      for (int j = 0; j < scanlineBytes / 8; j++) {
        bpixels[(k++)] = 
        





          ((byte)(pixels[(l++)] << 7 | pixels[(l++)] << 6 | pixels[(l++)] << 5 | pixels[(l++)] << 4 | pixels[(l++)] << 3 | pixels[(l++)] << 2 | pixels[(l++)] << 1 | pixels[(l++)]));
      }
      

      if (scanlineBytes % 8 > 0) {
        pixel = 0;
        for (int j = 0; j < scanlineBytes % 8; j++) {
          pixel |= pixels[(l++)] << 7 - j;
        }
        bpixels[(k++)] = ((byte)pixel);
      }
      output.write(bpixels, 0, (scanlineBytes + 7) / 8);
      
      break;
    

    case 4: 
      for (int j = 0; j < scanlineBytes / 2; j++) {
        pixel = pixels[(l++)] << 4 | pixels[(l++)];
        bpixels[(k++)] = ((byte)pixel);
      }
      
      if (scanlineBytes % 2 == 1) {
        pixel = pixels[l] << 4;
        bpixels[(k++)] = ((byte)pixel);
      }
      output.write(bpixels, 0, (scanlineBytes + 1) / 2);
      break;
    
    case 8: 
      for (int j = 0; j < scanlineBytes; j++) {
        bpixels[j] = ((byte)pixels[(l++)]);
      }
      output.write(bpixels, 0, scanlineBytes);
      break;
    
    case 24: 
      if (numBands == 3) {
        for (int j = 0; j < scanlineBytes; j += 3)
        {
          bpixels[(k++)] = ((byte)pixels[(l + 2)]);
          bpixels[(k++)] = ((byte)pixels[(l + 1)]);
          bpixels[(k++)] = ((byte)pixels[l]);
          l += 3;
        }
        output.write(bpixels, 0, scanlineBytes);
      }
      else {
        int entries = icm.getMapSize();
        
        byte[] r = new byte[entries];
        byte[] g = new byte[entries];
        byte[] b = new byte[entries];
        
        icm.getReds(r);
        icm.getGreens(g);
        icm.getBlues(b);
        

        for (int j = 0; j < scanlineBytes; j++) {
          int index = pixels[l];
          bpixels[(k++)] = b[index];
          bpixels[(k++)] = g[index];
          bpixels[(k++)] = b[index];
          l++;
        }
        output.write(bpixels, 0, scanlineBytes * 3);
      }
      
      break;
    }
    
    
    for (k = 0; k < padding; k++) {
      output.write(0);
    }
  }
  
  private void writeFileHeader(int fileSize, int offset)
    throws IOException
  {
    output.write(66);
    output.write(77);
    

    writeDWord(fileSize);
    

    output.write(0);
    output.write(0);
    output.write(0);
    output.write(0);
    

    writeDWord(offset);
  }
  

  private void writeInfoHeader(int headerSize, int bitsPerPixel)
    throws IOException
  {
    writeDWord(headerSize);
    

    writeDWord(w);
    

    writeDWord(h);
    

    writeWord(1);
    

    writeWord(bitsPerPixel);
  }
  
  public void writeWord(int word) throws IOException
  {
    output.write(word & 0xFF);
    output.write((word & 0xFF00) >> 8);
  }
  
  public void writeDWord(int dword) throws IOException {
    output.write(dword & 0xFF);
    output.write((dword & 0xFF00) >> 8);
    output.write((dword & 0xFF0000) >> 16);
    output.write((dword & 0xFF000000) >> 24);
  }
}
