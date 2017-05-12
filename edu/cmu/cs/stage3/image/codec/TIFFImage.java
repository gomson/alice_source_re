package edu.cmu.cs.stage3.image.codec;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
























































public class TIFFImage
  extends SimpleRenderedImage
{
  private static final boolean DEBUG = true;
  SeekableStream stream;
  int tileSize;
  int tilesX;
  int tilesY;
  long[] tileOffsets;
  long[] tileByteCounts;
  char[] colormap;
  char[] bitsPerSample;
  int samplesPerPixel;
  int extraSamples;
  int compression;
  byte[] palette;
  int bands;
  char[] sampleFormat;
  long tiffT4Options;
  long tiffT6Options;
  int fillOrder;
  boolean decodePaletteAsShorts;
  boolean isBigEndian;
  int predictor;
  int image_type;
  int dataType;
  private static final int TYPE_BILEVEL_WHITE_IS_ZERO = 0;
  private static final int TYPE_BILEVEL_BLACK_IS_ZERO = 1;
  private static final int TYPE_GREYSCALE_WHITE_IS_ZERO = 2;
  private static final int TYPE_GREYSCALE_BLACK_IS_ZERO = 3;
  private static final int TYPE_RGB = 4;
  private static final int TYPE_ARGB_PRE = 5;
  private static final int TYPE_ARGB = 6;
  private static final int TYPE_ORGB = 7;
  private static final int TYPE_RGB_EXTRA = 8;
  private static final int TYPE_PALETTE = 9;
  private static final int TYPE_TRANS = 10;
  public static final int COMP_NONE = 1;
  public static final int COMP_FAX_G3_1D = 2;
  public static final int COMP_FAX_G3_2D = 3;
  public static final int COMP_FAX_G4_2D = 4;
  public static final int COMP_LZW = 5;
  public static final int COMP_PACKBITS = 32773;
  private TIFFFaxDecoder decoder = null;
  private TIFFLZWDecoder lzwDecoder = null;
  











  public TIFFImage(SeekableStream stream, TIFFDecodeParam param, int directory)
    throws IOException
  {
    this.stream = stream;
    if (param == null) {
      param = new TIFFDecodeParam();
    }
    
    decodePaletteAsShorts = param.getDecodePaletteAsShorts();
    

    TIFFDirectory dir = new TIFFDirectory(stream, directory);
    

    isBigEndian = dir.isBigEndian();
    

    minX = (this.minY = 0);
    width = ((int)dir.getFieldAsLong(256));
    height = ((int)dir.getFieldAsLong(257));
    
    int photometric_interp = (int)dir.getFieldAsLong(
      262);
    

    TIFFField bitsField = 
      dir.getField(258);
    
    if (bitsField == null)
    {
      bitsPerSample = new char[1];
      bitsPerSample[0] = '\001';
    } else {
      bitsPerSample = bitsField.getAsChars();
    }
    
    for (int i = 1; i < bitsPerSample.length; i++) {
      if (bitsPerSample[i] != bitsPerSample[1]) {
        throw new RuntimeException(
          JaiI18N.getString("All_samples_must_have_the_same_bit_depth_"));
      }
    }
    

    TIFFField sfield = dir.getField(277);
    if (sfield == null) {
      samplesPerPixel = 1;
    } else {
      samplesPerPixel = ((int)sfield.getAsLong(0));
    }
    

    TIFFField efield = dir.getField(338);
    if (efield == null) {
      extraSamples = 0;
    } else {
      extraSamples = ((int)efield.getAsLong(0));
    }
    


    TIFFField sampleFormatField = 
      dir.getField(339);
    
    if (sampleFormatField != null) {
      sampleFormat = sampleFormatField.getAsChars();
      

      for (int l = 1; l < sampleFormat.length; l++) {
        if (sampleFormat[l] != sampleFormat[0]) {
          throw new RuntimeException(
            JaiI18N.getString("All_samples_must_have_the_same_data_format_"));
        }
      }
    }
    else {
      sampleFormat = new char[] { '\001' };
    }
    
    if ((sampleFormat[0] == '\001') || (sampleFormat[0] == '\004'))
    {

      if (bitsPerSample[0] == '\b') {
        dataType = 0;
      } else if (bitsPerSample[0] == '\020') {
        dataType = 1;
      } else if (bitsPerSample[0] == ' ') {
        dataType = 3;
      }
    }
    else if (sampleFormat[0] == '\002')
    {

      if ((bitsPerSample[0] == '\001') || (bitsPerSample[0] == '\004') || 
        (bitsPerSample[0] == '\b'))
      {
        throw 
          new RuntimeException(JaiI18N.getString("Only_16bit_samples_can_be_signed_"));
      }
      if (bitsPerSample[0] == '\020') {
        dataType = 2;
      } else if (bitsPerSample[0] == ' ') {
        dataType = 3;
      }
    }
    else if (sampleFormat[0] == '\003')
    {

      throw new RuntimeException(JaiI18N.getString("Only_integral_image_data_is_supported_"));
    }
    
    if (dir.getField(322) != null)
    {
      tileWidth = 
        ((int)dir.getFieldAsLong(322));
      tileHeight = 
        ((int)dir.getFieldAsLong(323));
      tileOffsets = 
        dir.getField(324).getAsLongs();
      tileByteCounts = 
        dir.getField(325).getAsLongs();

    }
    else
    {
      tileWidth = width;
      TIFFField field = dir.getField(278);
      if (field == null)
      {

        tileHeight = height;
      } else {
        long l = field.getAsLong(0);
        long infinity = 1L;
        infinity = (infinity << 32) - 1L;
        if (l == infinity)
        {
          tileHeight = height;
        } else {
          tileHeight = ((int)l);
        }
      }
      
      TIFFField tileOffsetsField = 
        dir.getField(273);
      if (tileOffsetsField == null) {
        throw 
          new RuntimeException(JaiI18N.getString("TIFF_STRIP_OFFSETS_is_a_required_field_"));
      }
      tileOffsets = tileOffsetsField.getAsLongs();
      

      TIFFField tileByteCountsField = 
        dir.getField(279);
      if (tileByteCountsField == null) {
        throw 
          new RuntimeException(JaiI18N.getString("TIFF_STRIP_BYTE_COUNTS_is_a_required_field_"));
      }
      tileByteCounts = tileByteCountsField.getAsLongs();
    }
    

    TIFFField fillOrderField = 
      dir.getField(266);
    if (fillOrderField != null) {
      fillOrder = fillOrderField.getAsInt(0);
    }
    else {
      fillOrder = 1;
    }
    

    switch (photometric_interp)
    {

    case 0: 
      bands = 1;
      

      if (bitsPerSample[0] == '\001')
      {
        image_type = 0;
        

        sampleModel = 
          new MultiPixelPackedSampleModel(0, 
          tileWidth, 
          tileHeight, 1);
        

        byte[] r = { -1 };
        byte[] g = { -1 };
        byte[] b = { -1 };
        
        colorModel = new IndexColorModel(1, 2, r, g, b);
      }
      else
      {
        image_type = 2;
        
        if (bitsPerSample[0] == '\004') {
          sampleModel = 
            new MultiPixelPackedSampleModel(0, 
            tileWidth, 
            tileHeight, 
            4);
          
          colorModel = 
            ImageCodec.createGrayIndexColorModel(sampleModel, false);
        }
        else if (bitsPerSample[0] == '\b') {
          sampleModel = 
            RasterFactory.createPixelInterleavedSampleModel(
            0, 
            tileWidth, 
            tileHeight, 
            bands);
          
          colorModel = 
            ImageCodec.createGrayIndexColorModel(sampleModel, false);
        }
        else if (bitsPerSample[0] == '\020')
        {
          sampleModel = 
            RasterFactory.createPixelInterleavedSampleModel(
            dataType, 
            tileWidth, 
            tileHeight, 
            bands);
          
          colorModel = 
            ImageCodec.createComponentColorModel(sampleModel);
        }
        else {
          throw new IllegalArgumentException(
            JaiI18N.getString("Only_4__8_and_16_bit_samples_are_supported_for_Greyscale_TIFFs_"));
        }
      }
      


      break;
    case 1: 
      bands = 1;
      

      if (bitsPerSample[0] == '\001')
      {
        image_type = 1;
        

        sampleModel = 
          new MultiPixelPackedSampleModel(0, 
          tileWidth, 
          tileHeight, 1);
        

        byte[] r = { 0, -1 };
        byte[] g = { 0, -1 };
        byte[] b = { 0, -1 };
        

        colorModel = new IndexColorModel(1, 2, r, g, b);
      }
      else
      {
        image_type = 3;
        
        if (bitsPerSample[0] == '\004') {
          sampleModel = 
            new MultiPixelPackedSampleModel(0, 
            tileWidth, 
            tileHeight, 
            4);
          colorModel = 
            ImageCodec.createGrayIndexColorModel(sampleModel, 
            true);
        } else if (bitsPerSample[0] == '\b') {
          sampleModel = 
            RasterFactory.createPixelInterleavedSampleModel(
            0, 
            tileWidth, 
            tileHeight, 
            bands);
          colorModel = 
            ImageCodec.createComponentColorModel(sampleModel);
        }
        else if (bitsPerSample[0] == '\020')
        {
          sampleModel = 
            RasterFactory.createPixelInterleavedSampleModel(
            dataType, 
            tileWidth, 
            tileHeight, 
            bands);
          colorModel = 
            ImageCodec.createComponentColorModel(sampleModel);
        }
        else {
          throw new IllegalArgumentException(
            JaiI18N.getString("Only_4__8_and_16_bit_samples_are_supported_for_Greyscale_TIFFs_"));
        }
      }
      



      break;
    case 2: 
      bands = samplesPerPixel;
      

      if (bitsPerSample[0] == '\b')
      {
        sampleModel = RasterFactory.createPixelInterleavedSampleModel(
          0, 
          tileWidth, 
          tileHeight, 
          bands);
      } else if (bitsPerSample[0] == '\020')
      {
        sampleModel = RasterFactory.createPixelInterleavedSampleModel(
          dataType, 
          tileWidth, 
          tileHeight, 
          bands);
      } else {
        throw 
          new RuntimeException(JaiI18N.getString("Only_8_and_16_bit_samples_are_supported_for_Full_color_images_"));
      }
      
      if (samplesPerPixel < 3) {
        throw 
          new RuntimeException(JaiI18N.getString("Bad_TIFF_Image_File__SamplesPerPixel_tag_must_have_a_value___3_for_RGB_full_color_images"));
      }
      if (samplesPerPixel == 3)
      {
        image_type = 4;
        
        colorModel = ImageCodec.createComponentColorModel(sampleModel);
      }
      else if (samplesPerPixel == 4)
      {
        if (extraSamples == 0)
        {
          image_type = 7;
          



          colorModel = 
            createAlphaComponentColorModel(dataType, true, 
            false, 
            1);
        }
        else if (extraSamples == 1)
        {
          image_type = 5;
          
          colorModel = 
            createAlphaComponentColorModel(dataType, true, true, 
            3);
        }
        else if (extraSamples == 2)
        {
          image_type = 6;
          

          colorModel = createAlphaComponentColorModel(
            dataType, 
            true, 
            false, 
            2);
        }
      }
      else {
        image_type = 8;
        







        int[] bandOffsets = new int[bands];
        for (int i = 0; i < bands; i++) {
          bandOffsets[i] = i;
        }
        
        if (bitsPerSample[0] == '\b')
        {
          sampleModel = 
            new PixelInterleavedSampleModel(0, 
            tileWidth, tileHeight, 
            bands, bands * tileWidth, 
            bandOffsets);
          colorModel = null;
        }
        else if (bitsPerSample[0] == '\020')
        {
          sampleModel = 
            new PixelInterleavedSampleModel(dataType, 
            tileWidth, 
            tileHeight, 
            bands, bands * tileWidth, 
            bandOffsets);
          colorModel = null;
        }
      }
      
      break;
    


    case 3: 
      image_type = 9;
      

      TIFFField cfield = dir.getField(320);
      if (cfield == null) {
        throw 
          new RuntimeException(JaiI18N.getString("Colormap_must_be_present_for_a_Palette_Color_image_"));
      }
      colormap = cfield.getAsChars();
      



      if (decodePaletteAsShorts) {
        bands = 3;
        
        if ((bitsPerSample[0] != '\004') && (bitsPerSample[0] != '\b') && 
          (bitsPerSample[0] != '\020')) {
          throw new RuntimeException(
            JaiI18N.getString("Only_4__8_or_16_bits_per_sample_are_supported_for_TIFF_Palette_color_images_"));
        }
        





        if (dataType == 0) {
          dataType = 1;
        }
        



        sampleModel = 
          RasterFactory.createPixelInterleavedSampleModel(dataType, 
          tileWidth, 
          tileHeight, 
          bands);
        colorModel = ImageCodec.createComponentColorModel(sampleModel);
      }
      else
      {
        bands = 1;
        
        if (bitsPerSample[0] == '\004')
        {

          sampleModel = 
            new MultiPixelPackedSampleModel(0, 
            tileWidth, 
            tileHeight, 
            bitsPerSample[0]);
        } else if (bitsPerSample[0] == '\b') {
          sampleModel = 
            RasterFactory.createPixelInterleavedSampleModel(
            0, 
            tileWidth, 
            tileHeight, 
            bands);
        } else if (bitsPerSample[0] == '\020')
        {



          sampleModel = 
            RasterFactory.createPixelInterleavedSampleModel(
            1, 
            tileWidth, 
            tileHeight, 
            bands);
        } else {
          throw new RuntimeException(
            JaiI18N.getString("Only_4__8_or_16_bits_per_sample_are_supported_for_TIFF_Palette_color_images_"));
        }
        
        int bandLength = colormap.length / 3;
        byte[] r = new byte[bandLength];
        byte[] g = new byte[bandLength];
        byte[] b = new byte[bandLength];
        
        int gIndex = bandLength;
        int bIndex = bandLength * 2;
        
        if (dataType == 2)
        {
          for (int i = 0; i < bandLength; i++) {
            r[i] = param.decodeSigned16BitsTo8Bits(
              (short)colormap[i]);
            g[i] = param.decodeSigned16BitsTo8Bits(
              (short)colormap[(gIndex + i)]);
            b[i] = param.decodeSigned16BitsTo8Bits(
              (short)colormap[(bIndex + i)]);
          }
          
        }
        else {
          for (int i = 0; i < bandLength; i++) {
            r[i] = param.decode16BitsTo8Bits(colormap[i] & 0xFFFF);
            g[i] = param.decode16BitsTo8Bits(colormap[(gIndex + i)] & 
              0xFFFF);
            b[i] = param.decode16BitsTo8Bits(colormap[(bIndex + i)] & 
              0xFFFF);
          }
        }
        

        colorModel = new IndexColorModel(bitsPerSample[0], 
          bandLength, r, g, b);
      }
      
      break;
    
    case 4: 
      image_type = 10;
      

      throw new RuntimeException(JaiI18N.getString("Reading_of_TIFF_files_with_a_value_of_4_for_the_PhotometricInterpretation_tag_is_not_implemented_yet_"));
    

    default: 
      throw new RuntimeException(JaiI18N.getString("Non_baseline_TIFF_not_implemented_yet___unexpected_value_for_PhotometricInterpretation_tag_"));
    }
    
    
    tilesX = ((width + tileWidth - 1) / tileWidth);
    tilesY = ((height + tileHeight - 1) / tileHeight);
    tileSize = (tileWidth * tileHeight * bands);
    

    TIFFField compField = dir.getField(259);
    if (compField != null)
    {
      compression = compField.getAsInt(0);
      

      if (compression == 3) {
        TIFFField t4OptionsField = 
          dir.getField(292);
        if (t4OptionsField != null) {
          tiffT4Options = t4OptionsField.getAsLong(0);
        }
        else {
          tiffT4Options = 0L;
        }
      }
      

      if (compression == 4) {
        TIFFField t6OptionsField = 
          dir.getField(293);
        if (t6OptionsField != null) {
          tiffT6Options = t6OptionsField.getAsLong(0);
        }
        else {
          tiffT6Options = 0L;
        }
      }
      

      if ((compression == 2) || (compression == 3) || (compression == 4)) {
        decoder = new TIFFFaxDecoder(fillOrder, 
          tileWidth, tileHeight);
      }
      

      if (compression == 5) {
        TIFFField predictorField = 
          dir.getField(317);
        
        if (predictorField == null) {
          predictor = 1;
        } else {
          predictor = predictorField.getAsInt(0);
          
          if ((predictor != 1) && (predictor != 2)) {
            throw new RuntimeException(
              JaiI18N.getString("Illegal_value_for_Predictor_in_TIFF_file_"));
          }
          
          if ((predictor == 2) && (bitsPerSample[0] != '\b')) {
            throw new RuntimeException(bitsPerSample[0] + " " + 
              JaiI18N.getString("bit_samples_are_not_supported_for_Horizontal_differencing_Predictor_"));
          }
        }
        
        lzwDecoder = new TIFFLZWDecoder(tileWidth, predictor, 
          samplesPerPixel);
      }
      
      if ((compression != 1) && 
        (compression != 32773) && 
        (compression != 2) && 
        (compression != 3) && 
        (compression != 4) && 
        (compression != 5)) {
        throw 
          new RuntimeException(JaiI18N.getString("Reading_in_compressed_TIFF_images_is_not_implemented_yet_"));
      }
    }
    else {
      compression = 1;
    }
  }
  



  public TIFFDirectory getPrivateIFD(long offset)
    throws IOException
  {
    return new TIFFDirectory(stream, offset);
  }
  
  private WritableRaster tile00 = null;
  


  public synchronized Raster getTile(int tileX, int tileY)
  {
    if ((tileX == 0) && (tileY == 0) && (tile00 != null)) {
      return tile00;
    }
    
    if ((tileX < 0) || (tileX >= tilesX) || 
      (tileY < 0) || (tileY >= tilesY)) {
      throw 
        new IllegalArgumentException(JaiI18N.getString("Illegal_tile_requested_from_a_TIFFImage_"));
    }
    

    byte[] bdata = null;
    short[] sdata = null;
    int[] idata = null;
    DataBuffer buffer = sampleModel.createDataBuffer();
    
    int dataType = sampleModel.getDataType();
    if (dataType == 0) {
      bdata = ((DataBufferByte)buffer).getData();
    } else if (dataType == 1) {
      sdata = ((DataBufferUShort)buffer).getData();
    } else if (dataType == 2) {
      sdata = ((DataBufferShort)buffer).getData();
    }
    
    WritableRaster tile = 
      RasterFactory.createWritableRaster(sampleModel, 
      buffer, 
      new Point(tileXToX(tileX), 
      tileYToY(tileY)));
    





    long save_offset = 0L;
    try {
      save_offset = stream.getFilePointer();
      stream.seek(tileOffsets[(tileY * tilesX + tileX)]);
    } catch (IOException ioe) {
      throw new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
    }
    

    int byteCount = (int)tileByteCounts[(tileY * tilesX + tileX)];
    

    Rectangle tileRect = new Rectangle(tileXToX(tileX), tileYToY(tileY), 
      tileWidth, tileHeight);
    Rectangle newRect = tileRect.intersection(getBounds());
    int unitsInThisTile = width * height * bands;
    
    byte[] data = new byte[byteCount];
    
    switch (image_type) {
    case 0: 
    case 1: 
      try {
        if (compression == 32773) {
          stream.readFully(data, 0, byteCount);
          
          int bytesInThisTile;
          
          int bytesInThisTile;
          if (width % 8 == 0) {
            bytesInThisTile = width / 8 * height;
          } else {
            bytesInThisTile = 
              (width / 8 + 1) * height;
          }
          decodePackbits(data, bytesInThisTile, bdata);
        } else if (compression == 5) {
          stream.readFully(data, 0, byteCount);
          lzwDecoder.decode(data, bdata, height);
        } else if (compression == 2) {
          stream.readFully(data, 0, byteCount);
          decoder.decode1D(bdata, data, x, height);
        } else if (compression == 3) {
          stream.readFully(data, 0, byteCount);
          decoder.decode2D(bdata, data, x, height, 
            tiffT4Options);
        } else if (compression == 4) {
          stream.readFully(data, 0, byteCount);
          decoder.decodeT6(bdata, data, x, height, 
            tiffT6Options);
        } else if (compression == 1) {
          stream.readFully(bdata, 0, byteCount);
        }
        
        stream.seek(save_offset);
      } catch (IOException ioe) {
        throw 
          new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
      }
    



    case 2: 
    case 3: 
      try
      {
        if (bitsPerSample[0] == '\020')
        {
          if (compression == 32773)
          {
            stream.readFully(data, 0, byteCount);
            





            int bytesInThisTile = unitsInThisTile * 2;
            
            byte[] byteArray = new byte[bytesInThisTile];
            decodePackbits(data, bytesInThisTile, byteArray);
            interpretBytesAsShorts(byteArray, sdata, 
              unitsInThisTile);
          }
          else if (compression == 5)
          {
            stream.readFully(data, 0, byteCount);
            





            byte[] byteArray = new byte[unitsInThisTile * 2];
            lzwDecoder.decode(data, byteArray, height);
            interpretBytesAsShorts(byteArray, sdata, 
              unitsInThisTile);
          }
          else if (compression == 1)
          {
            readShorts(byteCount / 2, sdata);
          }
          



          if (image_type == 2)
          {
            if (dataType == 1)
            {
              for (int l = 0; l < sdata.length; l++) {
                sdata[l] = ((short)(65535 - sdata[l]));
              }
              
            } else if (dataType == 2)
            {
              for (int l = 0; l < sdata.length; l++) {
                sdata[l] = ((short)(sdata[l] ^ 0xFFFFFFFF));
              }
            }
          }
        }
        else if (bitsPerSample[0] == '\b')
        {
          if (compression == 32773)
          {
            stream.readFully(data, 0, byteCount);
            decodePackbits(data, unitsInThisTile, bdata);
          }
          else if (compression == 5)
          {
            stream.readFully(data, 0, byteCount);
            lzwDecoder.decode(data, bdata, height);
          }
          else if (compression == 1)
          {
            stream.readFully(bdata, 0, byteCount);
          }
        }
        else if (bitsPerSample[0] == '\004')
        {
          if (compression == 32773)
          {
            stream.readFully(data, 0, byteCount);
            
            int bytesInThisTile;
            
            int bytesInThisTile;
            if (width % 8 == 0) {
              bytesInThisTile = width / 2 * height;
            } else {
              bytesInThisTile = (width / 2 + 1) * 
                height;
            }
            
            decodePackbits(data, bytesInThisTile, bdata);
          }
          else if (compression == 5)
          {
            stream.readFully(data, 0, byteCount);
            lzwDecoder.decode(data, bdata, height);
          }
          else
          {
            stream.readFully(bdata, 0, byteCount);
          }
        }
        
        stream.seek(save_offset);
      }
      catch (IOException ioe) {
        throw 
          new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
      }
    



    case 4: 
      try
      {
        if (bitsPerSample[0] == '\b')
        {
          if (compression == 1)
          {
            stream.readFully(bdata, 0, byteCount);
          }
          else if (compression == 5)
          {
            stream.readFully(data, 0, byteCount);
            lzwDecoder.decode(data, bdata, height);
          }
          else if (compression == 32773)
          {
            stream.readFully(data, 0, byteCount);
            decodePackbits(data, unitsInThisTile, bdata);
          }
          else {
            throw new RuntimeException(
              JaiI18N.getString("Unsupported_compression_"));
          }
          

          for (int i = 0; i < unitsInThisTile; i += 3) {
            byte bswap = bdata[i];
            bdata[i] = bdata[(i + 2)];
            bdata[(i + 2)] = bswap;
          }
        }
        else if (bitsPerSample[0] == '\020')
        {
          if (compression == 1)
          {
            readShorts(byteCount / 2, sdata);
          }
          else if (compression == 5)
          {
            stream.readFully(data, 0, byteCount);
            





            byte[] byteArray = new byte[unitsInThisTile * 2];
            lzwDecoder.decode(data, byteArray, height);
            interpretBytesAsShorts(byteArray, sdata, 
              unitsInThisTile);
          }
          else if (compression == 32773)
          {
            stream.readFully(data, 0, byteCount);
            





            int bytesInThisTile = unitsInThisTile * 2;
            
            byte[] byteArray = new byte[bytesInThisTile];
            decodePackbits(data, bytesInThisTile, byteArray);
            interpretBytesAsShorts(byteArray, sdata, 
              unitsInThisTile);
          } else {
            throw new RuntimeException(
              JaiI18N.getString("Unsupported_compression_"));
          }
          

          for (int i = 0; i < unitsInThisTile; i += 3) {
            short sswap = sdata[i];
            sdata[i] = sdata[(i + 2)];
            sdata[(i + 2)] = sswap;
          }
        }
        
        stream.seek(save_offset);
      }
      catch (IOException ioe) {
        throw 
          new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
      }
    


    case 5: 
    case 6: 
    case 7: 
      try
      {
        if (bitsPerSample[0] == '\b')
        {
          if (compression == 1)
          {
            stream.readFully(bdata, 0, byteCount);
          }
          else if (compression == 5)
          {
            stream.readFully(data, 0, byteCount);
            lzwDecoder.decode(data, bdata, height);
          }
          else if (compression == 32773)
          {
            stream.readFully(data, 0, byteCount);
            decodePackbits(data, unitsInThisTile, bdata);
          }
          else {
            throw new RuntimeException(
              JaiI18N.getString("Unsupported_compression_"));
          }
          

          for (int i = 0; i < unitsInThisTile; i += 4)
          {
            byte bswap = bdata[i];
            bdata[i] = bdata[(i + 3)];
            bdata[(i + 3)] = bswap;
            

            bswap = bdata[(i + 1)];
            bdata[(i + 1)] = bdata[(i + 2)];
            bdata[(i + 2)] = bswap;
          }
        }
        else if (bitsPerSample[0] == '\020')
        {
          if (compression == 1)
          {
            readShorts(byteCount / 2, sdata);
          }
          else if (compression == 5)
          {
            stream.readFully(data, 0, byteCount);
            





            byte[] byteArray = new byte[unitsInThisTile * 2];
            lzwDecoder.decode(data, byteArray, height);
            interpretBytesAsShorts(byteArray, sdata, 
              unitsInThisTile);
          }
          else if (compression == 32773)
          {
            stream.readFully(data, 0, byteCount);
            





            int bytesInThisTile = unitsInThisTile * 2;
            
            byte[] byteArray = new byte[bytesInThisTile];
            decodePackbits(data, bytesInThisTile, byteArray);
            interpretBytesAsShorts(byteArray, sdata, 
              unitsInThisTile);
          } else {
            throw new RuntimeException(
              JaiI18N.getString("Unsupported_compression_"));
          }
          

          for (int i = 0; i < unitsInThisTile; i += 4)
          {
            short sswap = sdata[i];
            sdata[i] = sdata[(i + 3)];
            sdata[(i + 3)] = sswap;
            

            sswap = sdata[(i + 1)];
            sdata[(i + 1)] = sdata[(i + 2)];
            sdata[(i + 2)] = sswap;
          }
        }
        
        stream.seek(save_offset);
      } catch (IOException ioe) {
        throw 
          new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
      }
    


    case 8: 
      try
      {
        if (bitsPerSample[0] == '\b')
        {
          if (compression == 1)
          {
            stream.readFully(bdata, 0, byteCount);
          }
          else if (compression == 5)
          {
            stream.readFully(data, 0, byteCount);
            lzwDecoder.decode(data, bdata, height);
          }
          else if (compression == 32773)
          {
            stream.readFully(data, 0, byteCount);
            decodePackbits(data, unitsInThisTile, bdata);
          }
          else {
            throw new RuntimeException(
              JaiI18N.getString("Unsupported_compression_"));
          }
        }
        else if (bitsPerSample[0] == '\020')
        {
          if (compression == 1)
          {
            readShorts(byteCount / 2, sdata);
          }
          else if (compression == 5)
          {
            stream.readFully(data, 0, byteCount);
            





            byte[] byteArray = new byte[unitsInThisTile * 2];
            lzwDecoder.decode(data, byteArray, height);
            interpretBytesAsShorts(byteArray, sdata, 
              unitsInThisTile);
          }
          else if (compression == 32773)
          {
            stream.readFully(data, 0, byteCount);
            





            int bytesInThisTile = unitsInThisTile * 2;
            
            byte[] byteArray = new byte[bytesInThisTile];
            decodePackbits(data, bytesInThisTile, byteArray);
            interpretBytesAsShorts(byteArray, sdata, 
              unitsInThisTile);
          } else {
            throw new RuntimeException(
              JaiI18N.getString("Unsupported_compression_"));
          }
        }
        
        stream.seek(save_offset);
      }
      catch (IOException ioe) {
        throw 
          new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
      }
    



    case 9: 
      if (bitsPerSample[0] == '\020')
      {
        if (decodePaletteAsShorts)
        {
          short[] tempData = null;
          




          int unitsBeforeLookup = unitsInThisTile / 3;
          




          int entries = unitsBeforeLookup * 2;
          

          try
          {
            if (compression == 32773)
            {
              stream.readFully(data, 0, byteCount);
              
              byte[] byteArray = new byte[entries];
              decodePackbits(data, entries, byteArray);
              tempData = new short[unitsBeforeLookup];
              interpretBytesAsShorts(byteArray, tempData, 
                unitsBeforeLookup);
            }
            else if (compression == 5)
            {

              stream.readFully(data, 0, byteCount);
              
              byte[] byteArray = new byte[entries];
              lzwDecoder.decode(data, byteArray, height);
              tempData = new short[unitsBeforeLookup];
              interpretBytesAsShorts(byteArray, tempData, 
                unitsBeforeLookup);
            }
            else if (compression == 1)
            {




              tempData = new short[byteCount / 2];
              readShorts(byteCount / 2, tempData);
            }
            
            stream.seek(save_offset);
          }
          catch (IOException ioe) {
            throw 
              new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
          }
          
          if (dataType == 1)
          {



            int count = 0;int len = colormap.length / 3;
            int len2 = len * 2;
            for (int i = 0; i < unitsBeforeLookup; i++)
            {
              int lookup = tempData[i] & 0xFFFF;
              
              int cmapValue = colormap[(lookup + len2)];
              sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
              
              cmapValue = colormap[(lookup + len)];
              sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
              
              cmapValue = colormap[lookup];
              sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
            }
          }
          else if (dataType == 2)
          {



            int count = 0;int len = colormap.length / 3;
            int len2 = len * 2;
            for (int i = 0; i < unitsBeforeLookup; i++)
            {
              int lookup = tempData[i] & 0xFFFF;
              
              int cmapValue = colormap[(lookup + len2)];
              sdata[(count++)] = ((short)cmapValue);
              
              cmapValue = colormap[(lookup + len)];
              sdata[(count++)] = ((short)cmapValue);
              
              cmapValue = colormap[lookup];
              sdata[(count++)] = ((short)cmapValue);
            }
            
          }
          

        }
        else
        {
          try
          {
            if (compression == 32773)
            {
              stream.readFully(data, 0, byteCount);
              





              int bytesInThisTile = unitsInThisTile * 2;
              
              byte[] byteArray = new byte[bytesInThisTile];
              decodePackbits(data, bytesInThisTile, byteArray);
              interpretBytesAsShorts(byteArray, sdata, 
                unitsInThisTile);
            }
            else if (compression == 5)
            {
              stream.readFully(data, 0, byteCount);
              





              byte[] byteArray = new byte[unitsInThisTile * 2];
              lzwDecoder.decode(data, byteArray, height);
              interpretBytesAsShorts(byteArray, sdata, 
                unitsInThisTile);
            }
            else if (compression == 1)
            {
              readShorts(byteCount / 2, sdata);
            }
            
            stream.seek(save_offset);
          }
          catch (IOException ioe) {
            throw 
              new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
          }
        }
      }
      else if (bitsPerSample[0] == '\b')
      {
        if (decodePaletteAsShorts)
        {
          byte[] tempData = null;
          




          int unitsBeforeLookup = unitsInThisTile / 3;
          

          try
          {
            if (compression == 32773)
            {
              stream.readFully(data, 0, byteCount);
              tempData = new byte[unitsBeforeLookup];
              decodePackbits(data, unitsBeforeLookup, tempData);
            }
            else if (compression == 5)
            {
              stream.readFully(data, 0, byteCount);
              tempData = new byte[unitsBeforeLookup];
              lzwDecoder.decode(data, tempData, height);
            }
            else if (compression == 1)
            {
              tempData = new byte[byteCount];
              stream.readFully(tempData, 0, byteCount);
            }
            
            stream.seek(save_offset);
          }
          catch (IOException ioe) {
            throw 
              new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
          }
          



          int count = 0;int len = colormap.length / 3;
          int len2 = len * 2;
          for (int i = 0; i < unitsBeforeLookup; i++)
          {
            int lookup = tempData[i] & 0xFF;
            
            int cmapValue = colormap[(lookup + len2)];
            sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
            
            cmapValue = colormap[(lookup + len)];
            sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
            
            cmapValue = colormap[lookup];
            sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
          }
          

        }
        else
        {
          try
          {
            if (compression == 32773)
            {
              stream.readFully(data, 0, byteCount);
              decodePackbits(data, unitsInThisTile, bdata);
            }
            else if (compression == 5)
            {
              stream.readFully(data, 0, byteCount);
              lzwDecoder.decode(data, bdata, height);
            }
            else if (compression == 1)
            {
              stream.readFully(bdata, 0, byteCount);
            }
            
            stream.seek(save_offset);
          }
          catch (IOException ioe) {
            throw 
              new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
          }
        }
      }
      else if (bitsPerSample[0] == '\004')
      {
        int padding = width % 2 == 0 ? 0 : 1;
        int bytesPostDecoding = (width / 2 + padding) * 
          height;
        

        if (decodePaletteAsShorts)
        {
          byte[] tempData = null;
          try
          {
            stream.readFully(data, 0, byteCount);
            stream.seek(save_offset);
          } catch (IOException ioe) {
            throw 
              new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
          }
          

          if (compression == 32773)
          {
            tempData = new byte[bytesPostDecoding];
            decodePackbits(data, bytesPostDecoding, tempData);
          }
          else if (compression == 5)
          {
            tempData = new byte[bytesPostDecoding];
            lzwDecoder.decode(data, tempData, height);
          }
          else if (compression == 1)
          {
            tempData = data;
          }
          
          int bytes = unitsInThisTile / 3;
          

          data = new byte[bytes];
          
          int srcCount = 0;int dstCount = 0;
          for (int j = 0; j < height; j++) {
            for (int i = 0; i < width / 2; i++) {
              data[(dstCount++)] = 
                ((byte)((tempData[srcCount] & 0xF0) >> 4));
              data[(dstCount++)] = 
                ((byte)(tempData[(srcCount++)] & 0xF));
            }
            
            if (padding == 1) {
              data[(dstCount++)] = 
                ((byte)((tempData[(srcCount++)] & 0xF0) >> 4));
            }
          }
          
          int len = colormap.length / 3;
          int len2 = len * 2;
          
          int count = 0;
          for (int i = 0; i < bytes; i++) {
            int lookup = data[i] & 0xFF;
            int cmapValue = colormap[(lookup + len2)];
            sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
            cmapValue = colormap[(lookup + len)];
            sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
            cmapValue = colormap[lookup];
            sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
          }
          
        }
        else
        {
          try
          {
            if (compression == 32773)
            {
              stream.readFully(data, 0, byteCount);
              decodePackbits(data, bytesPostDecoding, bdata);
            }
            else if (compression == 5)
            {
              stream.readFully(data, 0, byteCount);
              lzwDecoder.decode(data, bdata, height);
            }
            else if (compression == 1)
            {
              stream.readFully(bdata, 0, byteCount);
            }
            
            stream.seek(save_offset);
          }
          catch (IOException ioe) {
            throw 
              new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
          }
        }
      } else {
        throw 
          new RuntimeException(JaiI18N.getString("Support_for_TIFF_Palette_images_with_a_value_of_BitsPerSample_tag_other_than_4_or_8_not_implemeneted_yet_"));
      }
      

      break;
    }
    
    
    if ((tileX == 0) && (tileY == 0)) {
      tile00 = tile;
    }
    return tile;
  }
  


  private void readShorts(int shortCount, short[] shortArray)
  {
    int byteCount = 2 * shortCount;
    byte[] byteArray = new byte[byteCount];
    try
    {
      stream.readFully(byteArray, 0, byteCount);
    } catch (IOException ioe) {
      throw new RuntimeException(JaiI18N.getString("IOException_occured_while_reading_TIFF_image_data_"));
    }
    
    interpretBytesAsShorts(byteArray, shortArray, shortCount);
  }
  







  private void interpretBytesAsShorts(byte[] byteArray, short[] shortArray, int shortCount)
  {
    if (isBigEndian)
    {
      for (int i = 0; i < shortCount; i++) {
        int j = 2 * i;
        int firstByte = byteArray[j] & 0xFF;
        int secondByte = byteArray[(j + 1)] & 0xFF;
        shortArray[i] = ((short)((firstByte << 8) + secondByte));
      }
      
    }
    else {
      for (int i = 0; i < shortCount; i++) {
        int j = 2 * i;
        int firstByte = byteArray[j] & 0xFF;
        int secondByte = byteArray[(j + 1)] & 0xFF;
        shortArray[i] = ((short)((secondByte << 8) + firstByte));
      }
    }
  }
  

  private byte[] decodePackbits(byte[] data, int arraySize, byte[] dst)
  {
    if (dst == null) {
      dst = new byte[arraySize];
    }
    
    int srcCount = 0;int dstCount = 0;
    

    try
    {
      while (dstCount < arraySize)
      {
        byte b = data[(srcCount++)];
        
        if ((b >= 0) && (b <= Byte.MAX_VALUE))
        {

          for (int i = 0; i < b + 1; i++) {
            dst[(dstCount++)] = data[(srcCount++)];
          }
        }
        else if ((b <= -1) && (b >= -127))
        {

          byte repeat = data[(srcCount++)];
          for (int i = 0; i < -b + 1; i++) {
            dst[(dstCount++)] = repeat;
          }
        }
        else
        {
          srcCount++;
        }
      }
    } catch (ArrayIndexOutOfBoundsException ae) {
      throw new RuntimeException(JaiI18N.getString("Unable_to_decode_Packbits_compressed_data___not_enough_data_"));
    }
    
    return dst;
  }
  





  private ComponentColorModel createAlphaComponentColorModel(int dataType, boolean hasAlpha, boolean isAlphaPremultiplied, int transparency)
  {
    ComponentColorModel ccm = null;
    int[][] RGBBits = new int[3][];
    
    RGBBits[0] = { 8, 8, 8, 8 };
    RGBBits[1] = { 16, 16, 16, 16 };
    RGBBits[2] = { 16, 16, 16, 16 };
    RGBBits[2] = { 32, 32, 32, 32 };
    
    ccm = new ComponentColorModel(ColorSpace.getInstance(1000), 
      RGBBits[dataType], hasAlpha, 
      isAlphaPremultiplied, 
      transparency, 
      dataType);
    return ccm;
  }
}
