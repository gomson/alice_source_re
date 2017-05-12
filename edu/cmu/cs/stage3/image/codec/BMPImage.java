package edu.cmu.cs.stage3.image.codec;

import java.awt.Point;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;







































































class BMPImage
  extends SimpleRenderedImage
{
  private BufferedInputStream inputStream;
  private long bitmapFileSize;
  private long bitmapOffset;
  private long compression;
  private long imageSize;
  private byte[] palette;
  private int imageType;
  private int numBands;
  private boolean isBottomUp;
  private int bitsPerPixel;
  private int redMask;
  private int greenMask;
  private int blueMask;
  private static final int VERSION_2_1_BIT = 0;
  private static final int VERSION_2_4_BIT = 1;
  private static final int VERSION_2_8_BIT = 2;
  private static final int VERSION_2_24_BIT = 3;
  private static final int VERSION_3_1_BIT = 4;
  private static final int VERSION_3_4_BIT = 5;
  private static final int VERSION_3_8_BIT = 6;
  private static final int VERSION_3_24_BIT = 7;
  private static final int VERSION_3_NT_16_BIT = 8;
  private static final int VERSION_3_NT_32_BIT = 9;
  private static final int VERSION_4_1_BIT = 10;
  private static final int VERSION_4_4_BIT = 11;
  private static final int VERSION_4_8_BIT = 12;
  private static final int VERSION_4_16_BIT = 13;
  private static final int VERSION_4_24_BIT = 14;
  private static final int VERSION_4_32_BIT = 15;
  private static final int LCS_CALIBRATED_RGB = 0;
  private static final int LCS_sRGB = 1;
  private static final int LCS_CMYK = 2;
  private static final int BI_RGB = 0;
  private static final int BI_RLE8 = 1;
  private static final int BI_RLE4 = 2;
  private static final int BI_BITFIELDS = 3;
  private int blueBits;
  private int greenBits;
  private int redBits;
  private WritableRaster theTile = null;
  




  public BMPImage(InputStream stream)
  {
    if ((stream instanceof BufferedInputStream)) {
      inputStream = ((BufferedInputStream)stream);
    } else {
      inputStream = new BufferedInputStream(stream);
    }
    

    try
    {
      if ((readUnsignedByte(inputStream) != 66) || 
        (readUnsignedByte(inputStream) != 77)) {
        throw 
          new RuntimeException(JaiI18N.getString("Invalid_magic_value_for_BMP_file"));
      }
      

      bitmapFileSize = readDWord(inputStream);
      

      readWord(inputStream);
      readWord(inputStream);
      

      bitmapOffset = readDWord(inputStream);
      



      long size = readDWord(inputStream);
      
      if (size == 12L) {
        width = readWord(inputStream);
        height = readWord(inputStream);
      } else {
        width = readLong(inputStream);
        height = readLong(inputStream);
      }
      
      int planes = readWord(inputStream);
      bitsPerPixel = readWord(inputStream);
      
      properties.put("color_planes", new Integer(planes));
      properties.put("bits_per_pixel", new Integer(bitsPerPixel));
      


      numBands = 3;
      
      if (size == 12L)
      {
        properties.put("bmp_version", "BMP v. 2.x");
        

        if (bitsPerPixel == 1) {
          imageType = 0;
        } else if (bitsPerPixel == 4) {
          imageType = 1;
        } else if (bitsPerPixel == 8) {
          imageType = 2;
        } else if (bitsPerPixel == 24) {
          imageType = 3;
        }
        

        int numberOfEntries = (int)((bitmapOffset - 14L - size) / 3L);
        int sizeOfPalette = numberOfEntries * 3;
        palette = new byte[sizeOfPalette];
        inputStream.read(palette, 0, sizeOfPalette);
        properties.put("palette", palette);
      }
      else {
        compression = readDWord(inputStream);
        imageSize = readDWord(inputStream);
        long xPelsPerMeter = readLong(inputStream);
        long yPelsPerMeter = readLong(inputStream);
        long colorsUsed = readDWord(inputStream);
        long colorsImportant = readDWord(inputStream);
        
        switch ((int)compression) {
        case 0: 
          properties.put("compression", "BI_RGB");
          break;
        
        case 1: 
          properties.put("compression", "BI_RLE8");
          break;
        
        case 2: 
          properties.put("compression", "BI_RLE4");
          break;
        
        case 3: 
          properties.put("compression", "BI_BITFIELDS");
        }
        
        
        properties.put("x_pixels_per_meter", new Long(xPelsPerMeter));
        properties.put("y_pixels_per_meter", new Long(yPelsPerMeter));
        properties.put("colors_used", new Long(colorsUsed));
        properties.put("colors_important", new Long(colorsImportant));
        
        if (size == 40L)
        {
          switch ((int)compression)
          {


          case 0: 
          case 1: 
          case 2: 
            int numberOfEntries = (int)((bitmapOffset - 14L - size) / 4L);
            int sizeOfPalette = numberOfEntries * 4;
            palette = new byte[sizeOfPalette];
            inputStream.read(palette, 0, sizeOfPalette);
            properties.put("palette", palette);
            
            if (bitsPerPixel == 1) {
              imageType = 4;
            } else if (bitsPerPixel == 4) {
              imageType = 5;
            } else if (bitsPerPixel == 8) {
              imageType = 6;
            } else if (bitsPerPixel == 24) {
              imageType = 7;
            } else if (bitsPerPixel == 16) {
              imageType = 8;
              redMask = 31744;
              greenMask = 992;
              blueMask = 31;
              properties.put("red_mask", new Integer(redMask));
              properties.put("green_mask", new Integer(greenMask));
              properties.put("blue_mask", new Integer(blueMask));
              computeShifts();
            } else if (bitsPerPixel == 32) {
              imageType = 9;
              redMask = 16711680;
              greenMask = 65280;
              blueMask = 255;
              properties.put("red_mask", new Integer(redMask));
              properties.put("green_mask", new Integer(greenMask));
              properties.put("blue_mask", new Integer(blueMask));
              computeShifts();
            }
            
            properties.put("bmp_version", "BMP v. 3.x");
            break;
          

          case 3: 
            if (bitsPerPixel == 16) {
              imageType = 8;
            } else if (bitsPerPixel == 32) {
              imageType = 9;
            }
            

            redMask = ((int)readDWord(inputStream));
            greenMask = ((int)readDWord(inputStream));
            blueMask = ((int)readDWord(inputStream));
            
            properties.put("red_mask", new Integer(redMask));
            properties.put("green_mask", new Integer(greenMask));
            properties.put("blue_mask", new Integer(blueMask));
            
            computeShifts();
            
            if (colorsUsed != 0L)
            {
              int sizeOfPalette = (int)colorsUsed * 4;
              palette = new byte[sizeOfPalette];
              inputStream.read(palette, 0, sizeOfPalette);
              properties.put("palette", palette);
            }
            
            properties.put("bmp_version", "BMP v. 3.x NT");
            break;
          
          default: 
            throw 
              new RuntimeException(JaiI18N.getString("Invalid_compression_specified_in_BMP_file_"));
            
            break; } } else if (size == 108L)
        {

          properties.put("bmp_version", "BMP v. 4.x");
          

          redMask = ((int)readDWord(inputStream));
          greenMask = ((int)readDWord(inputStream));
          blueMask = ((int)readDWord(inputStream));
          
          int alphaMask = (int)readDWord(inputStream);
          long csType = readDWord(inputStream);
          int redX = readLong(inputStream);
          int redY = readLong(inputStream);
          int redZ = readLong(inputStream);
          int greenX = readLong(inputStream);
          int greenY = readLong(inputStream);
          int greenZ = readLong(inputStream);
          int blueX = readLong(inputStream);
          int blueY = readLong(inputStream);
          int blueZ = readLong(inputStream);
          long gammaRed = readDWord(inputStream);
          long gammaGreen = readDWord(inputStream);
          long gammaBlue = readDWord(inputStream);
          

          int numberOfEntries = (int)((bitmapOffset - 14L - size) / 4L);
          int sizeOfPalette = numberOfEntries * 4;
          palette = new byte[sizeOfPalette];
          inputStream.read(palette, 0, sizeOfPalette);
          
          if ((palette != null) || (palette.length != 0)) {
            properties.put("palette", palette);
          }
          
          switch ((int)csType)
          {
          case 0: 
            properties.put("color_space", "LCS_CALIBRATED_RGB");
            properties.put("redX", new Integer(redX));
            properties.put("redY", new Integer(redY));
            properties.put("redZ", new Integer(redZ));
            properties.put("greenX", new Integer(greenX));
            properties.put("greenY", new Integer(greenY));
            properties.put("greenZ", new Integer(greenZ));
            properties.put("blueX", new Integer(blueX));
            properties.put("blueY", new Integer(blueY));
            properties.put("blueZ", new Integer(blueZ));
            properties.put("gamma_red", new Long(gammaRed));
            properties.put("gamma_green", new Long(gammaGreen));
            properties.put("gamma_blue", new Long(gammaBlue));
            

            throw 
              new RuntimeException(JaiI18N.getString("Not_implemented_yet_"));
          

          case 1: 
            properties.put("color_space", "LCS_sRGB");
            break;
          
          case 2: 
            properties.put("color_space", "LCS_CMYK");
            
            throw 
              new RuntimeException(JaiI18N.getString("Not_implemented_yet_"));
          }
          
          if (bitsPerPixel == 1) {
            imageType = 10;
          } else if (bitsPerPixel == 4) {
            imageType = 11;
          } else if (bitsPerPixel == 8) {
            imageType = 12;
          } else if (bitsPerPixel == 16) {
            imageType = 13;
            if ((int)compression == 0) {
              redMask = 31744;
              greenMask = 992;
              blueMask = 31;
            }
          } else if (bitsPerPixel == 24) {
            imageType = 14;
          } else if (bitsPerPixel == 32) {
            imageType = 15;
            if ((int)compression == 0) {
              redMask = 16711680;
              greenMask = 65280;
              blueMask = 255;
            }
          }
          
          properties.put("red_mask", new Integer(redMask));
          properties.put("green_mask", new Integer(greenMask));
          properties.put("blue_mask", new Integer(blueMask));
          properties.put("alpha_mask", new Integer(alphaMask));
          
          computeShifts();
        } else {
          properties.put("bmp_version", "BMP v. 5.x");
          throw 
            new RuntimeException(JaiI18N.getString("BMP_version_5_not_implemented_yet_"));
        }
      }
    } catch (IOException ioe) {
      throw new RuntimeException(JaiI18N.getString("IOException_while_reading_the_BMP_file_headers_"));
    }
    
    if (height > 0)
    {
      isBottomUp = true;
    }
    else {
      isBottomUp = false;
      height = Math.abs(height);
    }
    

    tileWidth = width;
    tileHeight = height;
    

    if ((bitsPerPixel == 1) || (bitsPerPixel == 4) || (bitsPerPixel == 8))
    {
      numBands = 1;
      
      if (bitsPerPixel == 8) {
        sampleModel = 
          RasterFactory.createPixelInterleavedSampleModel(
          0, 
          width, height, 
          numBands);
      }
      else {
        sampleModel = 
          new MultiPixelPackedSampleModel(0, 
          width, height, 
          bitsPerPixel);
      }
      int size;
      byte[] r;
      byte[] g;
      byte[] b;
      if ((imageType == 0) || 
        (imageType == 1) || 
        (imageType == 2))
      {
        int size = palette.length / 3;
        
        if (size > 256) {
          size = 256;
        }
        

        byte[] r = new byte[size];
        byte[] g = new byte[size];
        byte[] b = new byte[size];
        for (int i = 0; i < size; i++) {
          int off = 3 * i;
          b[i] = palette[off];
          g[i] = palette[(off + 1)];
          r[i] = palette[(off + 2)];
        }
      } else {
        size = palette.length / 4;
        
        if (size > 256) {
          size = 256;
        }
        

        r = new byte[size];
        g = new byte[size];
        b = new byte[size];
        for (int i = 0; i < size; i++) {
          int off = 4 * i;
          b[i] = palette[off];
          g[i] = palette[(off + 1)];
          r[i] = palette[(off + 2)];
        }
      }
      
      colorModel = new IndexColorModel(bitsPerPixel, size, r, g, b);
    }
    else {
      numBands = 3;
      
      sampleModel = 
        RasterFactory.createPixelInterleavedSampleModel(
        0, width, height, numBands);
      
      colorModel = 
        ImageCodec.createComponentColorModel(sampleModel);
    }
  }
  

  private void read1Bit(byte[] bdata, int paletteEntries)
  {
    int padding = 0;
    int bytesPerScanline = (int)Math.ceil(width / 8.0D);
    
    int remainder = bytesPerScanline % 4;
    if (remainder != 0) {
      padding = 4 - remainder;
    }
    
    int imSize = (bytesPerScanline + padding) * height;
    

    byte[] values = new byte[imSize];
    try {
      int bytesRead = 0;
      while (bytesRead < imSize)
      {
        bytesRead = bytesRead + inputStream.read(values, bytesRead, imSize - bytesRead);
      }
    } catch (IOException ioe) {
      throw 
        new RuntimeException(JaiI18N.getString("Error_while_reading_the_BMP_file_"));
    }
    
    if (isBottomUp)
    {



      for (int i = 0; i < height; i++) {
        System.arraycopy(values, 
          imSize - (i + 1) * (bytesPerScanline + padding), 
          bdata, 
          i * bytesPerScanline, bytesPerScanline);
      }
      
    } else {
      for (int i = 0; i < height; i++) {
        System.arraycopy(values, 
          i * (bytesPerScanline + padding), 
          bdata, 
          i * bytesPerScanline, 
          bytesPerScanline);
      }
    }
  }
  


  private void read4Bit(byte[] bdata, int paletteEntries)
  {
    int padding = 0;
    
    int bytesPerScanline = (int)Math.ceil(width / 2.0D);
    int remainder = bytesPerScanline % 4;
    if (remainder != 0) {
      padding = 4 - remainder;
    }
    
    int imSize = (bytesPerScanline + padding) * height;
    

    byte[] values = new byte[imSize];
    try {
      int bytesRead = 0;
      while (bytesRead < imSize)
      {
        bytesRead = bytesRead + inputStream.read(values, bytesRead, imSize - bytesRead);
      }
    } catch (IOException ioe) {
      throw 
        new RuntimeException(JaiI18N.getString("Error_while_reading_the_BMP_file_"));
    }
    
    if (isBottomUp)
    {


      for (int i = 0; i < height; i++) {
        System.arraycopy(values, 
          imSize - (i + 1) * (bytesPerScanline + padding), 
          bdata, 
          i * bytesPerScanline, 
          bytesPerScanline);
      }
    } else {
      for (int i = 0; i < height; i++) {
        System.arraycopy(values, 
          i * (bytesPerScanline + padding), 
          bdata, 
          i * bytesPerScanline, 
          bytesPerScanline);
      }
    }
  }
  


  private void read8Bit(byte[] bdata, int paletteEntries)
  {
    int padding = 0;
    

    int bitsPerScanline = width * 8;
    if (bitsPerScanline % 32 != 0) {
      padding = (bitsPerScanline / 32 + 1) * 32 - bitsPerScanline;
      padding = (int)Math.ceil(padding / 8.0D);
    }
    
    int imSize = (width + padding) * height;
    

    byte[] values = new byte[imSize];
    try {
      int bytesRead = 0;
      while (bytesRead < imSize)
      {
        bytesRead = bytesRead + inputStream.read(values, bytesRead, imSize - bytesRead);
      }
    } catch (IOException ioe) {
      throw 
        new RuntimeException(JaiI18N.getString("Error_while_reading_the_BMP_file_"));
    }
    
    if (isBottomUp)
    {


      for (int i = 0; i < height; i++) {
        System.arraycopy(values, 
          imSize - (i + 1) * (width + padding), 
          bdata, 
          i * width, 
          width);
      }
    } else {
      for (int i = 0; i < height; i++) {
        System.arraycopy(values, 
          i * (width + padding), 
          bdata, 
          i * width, 
          width);
      }
    }
  }
  

  private void read24Bit(byte[] bdata)
  {
    int padding = 0;
    

    int bitsPerScanline = width * 24;
    if (bitsPerScanline % 32 != 0) {
      padding = (bitsPerScanline / 32 + 1) * 32 - bitsPerScanline;
      padding = (int)Math.ceil(padding / 8.0D);
    }
    
    int imSize = (int)imageSize;
    if (imSize == 0) {
      imSize = (int)(bitmapFileSize - bitmapOffset);
    }
    

    byte[] values = new byte[imSize];
    try {
      int bytesRead = 0;
      while (bytesRead < imSize)
      {
        bytesRead = bytesRead + inputStream.read(values, bytesRead, imSize - bytesRead);
      }
    }
    catch (IOException ioe) {
      throw new RuntimeException(ioe.getMessage());
    }
    
    int l = 0;
    
    if (isBottomUp) {
      int max = width * height * 3 - 1;
      
      int count = -padding;
      for (int i = 0; i < height; i++) {
        l = max - (i + 1) * width * 3 + 1;
        count += padding;
        for (int j = 0; j < width; j++) {
          bdata[(l++)] = values[(count++)];
          bdata[(l++)] = values[(count++)];
          bdata[(l++)] = values[(count++)];
        }
      }
    } else {
      int count = -padding;
      for (int i = 0; i < height; i++) {
        count += padding;
        for (int j = 0; j < width; j++) {
          bdata[(l++)] = values[(count++)];
          bdata[(l++)] = values[(count++)];
          bdata[(l++)] = values[(count++)];
        }
      }
    }
  }
  
  private void read16Bit(byte[] bdata)
  {
    int padding = 0;
    

    int bitsPerScanline = width * 16;
    if (bitsPerScanline % 32 != 0) {
      padding = (bitsPerScanline / 32 + 1) * 32 - bitsPerScanline;
      padding = (int)Math.ceil(padding / 8.0D);
    }
    
    int imSize = (int)imageSize;
    if (imSize == 0) {
      imSize = (int)(bitmapFileSize - bitmapOffset);
    }
    
    int l = 0;
    try
    {
      if (isBottomUp) {
        int max = width * height * 3 - 1;
        
        for (int i = 0; i < height; i++) {
          l = max - (i + 1) * width * 3 + 1;
          for (int j = 0; j < width; j++) {
            int value = readWord(inputStream);
            
            bdata[(l++)] = ((byte)((value & blueMask) >> blueBits));
            bdata[(l++)] = ((byte)((value & greenMask) >> greenBits));
            bdata[(l++)] = ((byte)((value & redMask) >> redBits));
          }
          for (int m = 0; m < padding; m++) {
            inputStream.read();
          }
        }
      } else {
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            int value = readWord(inputStream);
            bdata[(l++)] = ((byte)((value & blueMask) >> blueBits));
            bdata[(l++)] = ((byte)((value & greenMask) >> greenBits));
            bdata[(l++)] = ((byte)((value & redMask) >> redBits));
          }
          for (int m = 0; m < padding; m++) {
            inputStream.read();
          }
        }
      }
    } catch (IOException ioe) {
      throw new RuntimeException(JaiI18N.getString("Error_while_reading_the_BMP_file_"));
    }
  }
  
  private void read32Bit(byte[] bdata) {
    int imSize = (int)imageSize;
    if (imSize == 0) {
      imSize = (int)(bitmapFileSize - bitmapOffset);
    }
    
    int l = 0;
    try
    {
      if (isBottomUp) {
        int max = width * height * 3 - 1;
        
        for (int i = 0; i < height; i++) {
          l = max - (i + 1) * width * 3 + 1;
          for (int j = 0; j < width; j++) {
            int value = (int)readDWord(inputStream);
            
            bdata[(l++)] = ((byte)((value & blueMask) >> blueBits));
            bdata[(l++)] = ((byte)((value & greenMask) >> greenBits));
            bdata[(l++)] = ((byte)((value & redMask) >> redBits));
          }
        }
      } else {
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            int value = (int)readDWord(inputStream);
            bdata[(l++)] = ((byte)((value & blueMask) >> blueBits));
            bdata[(l++)] = ((byte)((value & greenMask) >> greenBits));
            bdata[(l++)] = ((byte)((value & redMask) >> redBits));
          }
        }
      }
    } catch (IOException ioe) {
      throw new RuntimeException(JaiI18N.getString("Error_while_reading_the_BMP_file_"));
    }
  }
  

  private void readRLE8(byte[] bdata)
  {
    int imSize = (int)imageSize;
    if (imSize == 0) {
      imSize = (int)(bitmapFileSize - bitmapOffset);
    }
    
    int padding = 0;
    

    int remainder = width % 4;
    if (remainder != 0) {
      padding = 4 - remainder;
    }
    

    byte[] values = new byte[imSize];
    try {
      int bytesRead = 0;
      while (bytesRead < imSize)
      {
        bytesRead = bytesRead + inputStream.read(values, bytesRead, imSize - bytesRead);
      }
    } catch (IOException ioe) {
      throw new RuntimeException(JaiI18N.getString("Error_while_reading_the_BMP_file_"));
    }
    

    byte[] val = decodeRLE8(imSize, padding, values);
    

    imSize = width * height;
    
    if (isBottomUp)
    {



      int bytesPerScanline = width;
      for (int i = 0; i < height; i++) {
        System.arraycopy(val, 
          imSize - (i + 1) * bytesPerScanline, 
          bdata, 
          i * bytesPerScanline, bytesPerScanline);
      }
    }
    else
    {
      bdata = val;
    }
  }
  
  private byte[] decodeRLE8(int imSize, int padding, byte[] values)
  {
    byte[] val = new byte[width * height];
    int count = 0;int l = 0;
    
    boolean flag = false;
    
    while (count != imSize)
    {
      int value = values[(count++)] & 0xFF;
      
      if (value == 0) {
        switch (values[(count++)] & 0xFF)
        {
        case 0: 
          break;
        


        case 1: 
          flag = true;
          break;
        

        case 2: 
          int xoff = values[(count++)] & 0xFF;
          int yoff = values[count] & 0xFF;
          
          l += xoff + yoff * width;
          break;
        
        default: 
          int end = values[(count - 1)] & 0xFF;
          for (int i = 0; i < end; i++) {
            val[(l++)] = ((byte)(values[(count++)] & 0xFF));
          }
          


          if (isEven(end)) break;
          count++;
          

          break; }
      } else { for (int i = 0; i < value; i++) {
          val[(l++)] = ((byte)(values[count] & 0xFF));
        }
        count++;
      }
      

      if (flag) {
        break;
      }
    }
    
    return val;
  }
  

  private int[] readRLE4()
  {
    int imSize = (int)imageSize;
    if (imSize == 0) {
      imSize = (int)(bitmapFileSize - bitmapOffset);
    }
    
    int padding = 0;
    

    int remainder = width % 4;
    if (remainder != 0) {
      padding = 4 - remainder;
    }
    

    int[] values = new int[imSize];
    try {
      for (int i = 0; i < imSize; i++) {
        values[i] = inputStream.read();
      }
    } catch (IOException ioe) {
      throw new RuntimeException(JaiI18N.getString("Error_while_reading_the_BMP_file_"));
    }
    

    int[] val = decodeRLE4(imSize, padding, values);
    

    if (isBottomUp)
    {
      int[] inverted = val;
      val = new int[width * height];
      int l = 0;
      
      for (int i = height - 1; i >= 0; i--) {
        int index = i * width;
        int lineEnd = l + width;
        while (l != lineEnd) {
          val[(l++)] = inverted[(index++)];
        }
      }
    }
    


    return val;
  }
  
  private int[] decodeRLE4(int imSize, int padding, int[] values)
  {
    int[] val = new int[width * height];
    int count = 0;int l = 0;
    
    boolean flag = false;
    
    while (count != imSize)
    {
      int value = values[(count++)];
      
      if (value == 0)
      {

        switch (values[(count++)])
        {
        case 0: 
          break;
        


        case 1: 
          flag = true;
          break;
        

        case 2: 
          int xoff = values[(count++)];
          int yoff = values[count];
          
          l += xoff + yoff * width;
          break;
        
        default: 
          int end = values[(count - 1)];
          for (int i = 0; i < end; i++) {
            val[(l++)] = (isEven(i) ? (values[count] & 0xF0) >> 4 : 
              values[(count++)] & 0xF);
          }
          


          if (!isEven(end)) {
            count++;
          }
          


          if (isEven((int)Math.ceil(end / 2))) break;
          count++;
          


          break; }
      } else {
        int[] alternate = { (values[count] & 0xF0) >> 4, 
          values[count] & 0xF };
        for (int i = 0; i < value; i++) {
          val[(l++)] = alternate[(i % 2)];
        }
        
        count++;
      }
      

      if (flag) {
        break;
      }
    }
    

    return val;
  }
  
  private boolean isEven(int number) {
    return number % 2 == 0;
  }
  
  private void computeShifts() {
    redBits = computeShift(redMask);
    greenBits = computeShift(greenMask);
    blueBits = computeShift(blueMask);
  }
  
  private int computeShift(int hexNumber) {
    int factor = 1;
    int result = 0;int count = 0;
    
    while (result == 0) {
      result = hexNumber & factor;
      count++;
      factor <<= 1;
    }
    
    return count - 1;
  }
  

  private int readUnsignedByte(InputStream stream)
    throws IOException
  {
    return stream.read() & 0xFF;
  }
  
  private int readUnsignedShort(InputStream stream) throws IOException
  {
    int b1 = readUnsignedByte(stream);
    int b2 = readUnsignedByte(stream);
    return (b2 << 8 | b1) & 0xFFFF;
  }
  
  private int readShort(InputStream stream) throws IOException
  {
    int b1 = readUnsignedByte(stream);
    int b2 = readUnsignedByte(stream);
    return b2 << 8 | b1;
  }
  
  private int readWord(InputStream stream) throws IOException
  {
    return readUnsignedShort(stream);
  }
  
  private long readUnsignedInt(InputStream stream) throws IOException
  {
    int b1 = readUnsignedByte(stream);
    int b2 = readUnsignedByte(stream);
    int b3 = readUnsignedByte(stream);
    int b4 = readUnsignedByte(stream);
    long l = b4 << 24 | b3 << 16 | b2 << 8 | b1;
    return l & 0xFFFFFFFFFFFFFFFF;
  }
  
  private int readInt(InputStream stream) throws IOException
  {
    int b1 = readUnsignedByte(stream);
    int b2 = readUnsignedByte(stream);
    int b3 = readUnsignedByte(stream);
    int b4 = readUnsignedByte(stream);
    return b4 << 24 | b3 << 16 | b2 << 8 | b1;
  }
  
  private long readDWord(InputStream stream) throws IOException
  {
    return readUnsignedInt(stream);
  }
  
  private int readLong(InputStream stream) throws IOException
  {
    return readInt(stream);
  }
  
  private synchronized Raster computeTile(int tileX, int tileY) {
    if (theTile != null) {
      return theTile;
    }
    

    Point org = new Point(tileXToX(tileX), tileYToY(tileY));
    WritableRaster tile = 
      RasterFactory.createWritableRaster(sampleModel, org);
    DataBufferByte buffer = (DataBufferByte)tile.getDataBuffer();
    byte[] bdata = buffer.getData();
    

    switch (imageType)
    {

    case 0: 
      read1Bit(bdata, 3);
      break;
    

    case 1: 
      read4Bit(bdata, 3);
      break;
    

    case 2: 
      read8Bit(bdata, 3);
      break;
    

    case 3: 
      read24Bit(bdata);
      break;
    

    case 4: 
      read1Bit(bdata, 4);
      break;
    
    case 5: 
      switch ((int)compression) {
      case 0: 
        read4Bit(bdata, 4);
        break;
      
      case 2: 
        int[] pixels = readRLE4();
        tile.setPixels(0, 0, width, height, pixels);
        break;
      case 1: 
      default: 
        throw 
          new RuntimeException(JaiI18N.getString("Invalid_compression_specified_for_BMP_file_"));
      }
      
      break;
    case 6: 
      switch ((int)compression) {
      case 0: 
        read8Bit(bdata, 4);
        break;
      
      case 1: 
        readRLE8(bdata);
        break;
      
      default: 
        throw 
          new RuntimeException(JaiI18N.getString("Invalid_compression_specified_for_BMP_file_"));
      }
      
      

      break;
    case 7: 
      read24Bit(bdata);
      break;
    
    case 8: 
      read16Bit(bdata);
      break;
    
    case 9: 
      read32Bit(bdata);
      break;
    
    case 10: 
      read1Bit(bdata, 4);
      break;
    
    case 11: 
      switch ((int)compression)
      {
      case 0: 
        read4Bit(bdata, 4);
        break;
      
      case 2: 
        int[] pixels = readRLE4();
        tile.setPixels(0, 0, width, height, pixels);
        break;
      case 1: 
      default: 
        throw 
          new RuntimeException(JaiI18N.getString("Invalid_compression_specified_for_BMP_file_"));
      }
      
    case 12: 
      switch ((int)compression)
      {
      case 0: 
        read8Bit(bdata, 4);
        break;
      
      case 1: 
        readRLE8(bdata);
        break;
      
      default: 
        throw 
          new RuntimeException(JaiI18N.getString("Invalid_compression_specified_for_BMP_file_"));
      }
      
      break;
    case 13: 
      read16Bit(bdata);
      break;
    
    case 14: 
      read24Bit(bdata);
      break;
    
    case 15: 
      read32Bit(bdata);
    }
    
    
    theTile = tile;
    
    return tile;
  }
  
  public synchronized Raster getTile(int tileX, int tileY) {
    if ((tileX != 0) || (tileY != 0)) {
      throw 
        new IllegalArgumentException(JaiI18N.getString("Illegal_tile_requested_from_a_BMPImage_"));
    }
    return computeTile(tileX, tileY);
  }
}
