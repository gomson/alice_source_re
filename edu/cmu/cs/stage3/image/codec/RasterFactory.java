package edu.cmu.cs.stage3.image.codec;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;



































































































public class RasterFactory
{
  public RasterFactory() {}
  
  public static WritableRaster createInterleavedRaster(int dataType, int width, int height, int numBands, Point location)
  {
    if (numBands < 1) {
      throw new IllegalArgumentException(JaiI18N.getString("Number_of_bands_must_be_greater_than_0_"));
    }
    int[] bandOffsets = new int[numBands];
    for (int i = 0; i < numBands; i++) {
      bandOffsets[i] = (numBands - 1 - i);
    }
    return createInterleavedRaster(dataType, width, height, 
      width * numBands, numBands, 
      bandOffsets, location);
  }
  






































  public static WritableRaster createInterleavedRaster(int dataType, int width, int height, int scanlineStride, int pixelStride, int[] bandOffsets, Point location)
  {
    if (bandOffsets == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Band_offsets_array_is_null_"));
    }
    

    int bands = bandOffsets.length;
    
    int maxBandOff = bandOffsets[0];
    for (int i = 1; i < bands; i++) {
      if (bandOffsets[i] > maxBandOff) {
        maxBandOff = bandOffsets[i];
      }
    }
    
    long lsize = maxBandOff + 
      scanlineStride * (height - 1) + pixelStride * (width - 1) + 
      1L;
    if (lsize > 2147483647L) {
      throw new IllegalArgumentException(JaiI18N.getString("Size_of_array_must_be_smaller_than_Integer_MAX_VALUE_"));
    }
    int size = (int)lsize;
    DataBuffer d;
    DataBuffer d; DataBuffer d; DataBuffer d; DataBuffer d; DataBuffer d; switch (dataType) {
    case 0: 
      d = new DataBufferByte(size);
      break;
    
    case 1: 
      d = new DataBufferUShort(size);
      break;
    
    case 2: 
      d = new DataBufferShort(size);
      break;
    
    case 3: 
      d = new DataBufferInt(size);
      break;
    
    case 4: 
      d = new DataBufferFloat(size);
      break;
    
    case 5: 
      d = new DataBufferDouble(size);
      break;
    
    default: 
      throw new IllegalArgumentException(JaiI18N.getString("Unsupported_data_type_"));
    }
    DataBuffer d;
    return createInterleavedRaster(d, width, height, scanlineStride, 
      pixelStride, bandOffsets, location);
  }
  






























  public static WritableRaster createBandedRaster(int dataType, int width, int height, int bands, Point location)
  {
    if (bands < 1) {
      throw new IllegalArgumentException(JaiI18N.getString("Number_of_bands_must_be_greater_than_0_"));
    }
    int[] bankIndices = new int[bands];
    int[] bandOffsets = new int[bands];
    for (int i = 0; i < bands; i++) {
      bankIndices[i] = i;
      bandOffsets[i] = 0;
    }
    
    return createBandedRaster(dataType, width, height, width, 
      bankIndices, bandOffsets, 
      location);
  }
  











































  public static WritableRaster createBandedRaster(int dataType, int width, int height, int scanlineStride, int[] bankIndices, int[] bandOffsets, Point location)
  {
    int bands = bandOffsets.length;
    
    if (bankIndices == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Bank_indices_array_is_null_"));
    }
    if (bandOffsets == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Band_offsets_array_is_null_"));
    }
    
    if (bandOffsets.length != bankIndices.length) {
      throw new IllegalArgumentException(JaiI18N.getString("bankIndices_length____bandOffsets_length"));
    }
    

    int maxBank = bankIndices[0];
    int maxBandOff = bandOffsets[0];
    for (int i = 1; i < bands; i++) {
      if (bankIndices[i] > maxBank) {
        maxBank = bankIndices[i];
      }
      if (bandOffsets[i] > maxBandOff) {
        maxBandOff = bandOffsets[i];
      }
    }
    
    int banks = maxBank + 1;
    long lsize = maxBandOff + scanlineStride * (height - 1) + (
      width - 1) + 1L;
    if (lsize > 2147483647L) {
      throw new IllegalArgumentException(JaiI18N.getString("Size_of_array_must_be_smaller_than_Integer_MAX_VALUE_"));
    }
    int size = (int)lsize;
    DataBuffer d;
    DataBuffer d; DataBuffer d; DataBuffer d; DataBuffer d; DataBuffer d; switch (dataType) {
    case 0: 
      d = new DataBufferByte(size, banks);
      break;
    
    case 1: 
      d = new DataBufferUShort(size, banks);
      break;
    
    case 2: 
      d = new DataBufferShort(size, banks);
      break;
    
    case 3: 
      d = new DataBufferInt(size, banks);
      break;
    
    case 4: 
      d = new DataBufferFloat(size, banks);
      break;
    
    case 5: 
      d = new DataBufferDouble(size, banks);
      break;
    
    default: 
      throw new IllegalArgumentException(JaiI18N.getString("Unsupported_data_type_"));
    }
    DataBuffer d;
    return createBandedRaster(d, width, height, scanlineStride, 
      bankIndices, bandOffsets, location);
  }
  


























  public static WritableRaster createPackedRaster(int dataType, int width, int height, int[] bandMasks, Point location)
  {
    return Raster.createPackedRaster(dataType, 
      width, height, bandMasks, location);
  }
  








































  public static WritableRaster createPackedRaster(int dataType, int width, int height, int numBands, int bitsPerBand, Point location)
  {
    if (bitsPerBand <= 0) {
      throw new IllegalArgumentException(JaiI18N.getString("bitsPerBands_must_be_greater_than_0_"));
    }
    
    return Raster.createPackedRaster(dataType, width, height, numBands, 
      bitsPerBand, location);
  }
  































  public static WritableRaster createInterleavedRaster(DataBuffer dataBuffer, int width, int height, int scanlineStride, int pixelStride, int[] bandOffsets, Point location)
  {
    if (bandOffsets == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Band_offsets_array_is_null_"));
    }
    if (location == null) {
      location = new Point(0, 0);
    }
    int dataType = dataBuffer.getDataType();
    
    switch (dataType) {
    case 0: 
    case 1: 
      PixelInterleavedSampleModel csm = 
        new PixelInterleavedSampleModel(dataType, width, height, 
        pixelStride, 
        scanlineStride, 
        bandOffsets);
      return Raster.createWritableRaster(csm, dataBuffer, location);
    
    case 2: 
    case 3: 
    case 4: 
    case 5: 
      int minBandOff = bandOffsets[0];
      int maxBandOff = bandOffsets[0];
      for (int i = 1; i < bandOffsets.length; i++) {
        minBandOff = Math.min(minBandOff, bandOffsets[i]);
        maxBandOff = Math.max(maxBandOff, bandOffsets[i]);
      }
      maxBandOff -= minBandOff;
      if (maxBandOff > scanlineStride) {
        throw new IllegalArgumentException(
          JaiI18N.getString("Offsets_between_bands_must_be_less_than_the_scanline_stride_"));
      }
      
      if (pixelStride * width > scanlineStride) {
        throw new IllegalArgumentException(
          JaiI18N.getString("Pixel_stride_times_width_must_be_less_than_the_scanline_stride_"));
      }
      if (pixelStride < maxBandOff) {
        throw new IllegalArgumentException(
          JaiI18N.getString("Pixel_stride_must_be_greater_than_or_equal_to_the_offset_between_bands_"));
      }
      
      SampleModel sm = 
        new ComponentSampleModelJAI(dataType, width, height, 
        pixelStride, 
        scanlineStride, 
        bandOffsets);
      return Raster.createWritableRaster(sm, dataBuffer, location);
    }
    
    throw new IllegalArgumentException(JaiI18N.getString("Unsupported_data_type_"));
  }
  








































  public static WritableRaster createBandedRaster(DataBuffer dataBuffer, int width, int height, int scanlineStride, int[] bankIndices, int[] bandOffsets, Point location)
  {
    if (location == null) {
      location = new Point(0, 0);
    }
    int dataType = dataBuffer.getDataType();
    
    if (bankIndices == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Bank_indices_array_is_null_"));
    }
    if (bandOffsets == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Band_offsets_array_is_null_"));
    }
    
    int bands = bankIndices.length;
    if (bandOffsets.length != bands) {
      throw new IllegalArgumentException(JaiI18N.getString("bankIndices_length____bandOffsets_length"));
    }
    
    SampleModel bsm = 
      new ComponentSampleModelJAI(dataType, width, height, 
      1, scanlineStride, 
      bankIndices, bandOffsets);
    
    switch (dataType) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
      return Raster.createWritableRaster(bsm, dataBuffer, location);
    }
    
    throw new IllegalArgumentException(JaiI18N.getString("Unsupported_data_type_"));
  }
  




























  public static WritableRaster createPackedRaster(DataBuffer dataBuffer, int width, int height, int scanlineStride, int[] bandMasks, Point location)
  {
    return Raster.createPackedRaster(dataBuffer, width, height, 
      scanlineStride, 
      bandMasks, 
      location);
  }
  























  public static WritableRaster createPackedRaster(DataBuffer dataBuffer, int width, int height, int bitsPerPixel, Point location)
  {
    return Raster.createPackedRaster(dataBuffer, width, height, 
      bitsPerPixel, location);
  }
  














  public static Raster createRaster(SampleModel sampleModel, DataBuffer dataBuffer, Point location)
  {
    return Raster.createRaster(sampleModel, dataBuffer, location);
  }
  











  public static WritableRaster createWritableRaster(SampleModel sampleModel, Point location)
  {
    if (location == null) {
      location = new Point(0, 0);
    }
    
    return createWritableRaster(sampleModel, 
      sampleModel.createDataBuffer(), 
      location);
  }
  














  public static WritableRaster createWritableRaster(SampleModel sampleModel, DataBuffer dataBuffer, Point location)
  {
    return Raster.createWritableRaster(sampleModel, dataBuffer, location);
  }
  





























































  public static WritableRaster createWritableChild(WritableRaster raster, int parentX, int parentY, int width, int height, int childMinX, int childMinY, int[] bandList)
  {
    if (parentX < raster.getMinX()) {
      throw new RasterFormatException(JaiI18N.getString("parentX_lies_outside_raster_"));
    }
    if (parentY < raster.getMinY()) {
      throw 
        new RasterFormatException(JaiI18N.getString("parentY_lies_outside_raster_"));
    }
    if (parentX + width > raster.getWidth() + raster.getMinX()) {
      throw 
        new RasterFormatException(JaiI18N.getString("parentX___width__is_outside_raster_"));
    }
    if (parentY + height > raster.getHeight() + raster.getMinY()) {
      throw 
        new RasterFormatException(JaiI18N.getString("parentY___height__is_outside_raster_"));
    }
    
    SampleModel sampleModel = raster.getSampleModel();
    DataBuffer dataBuffer = raster.getDataBuffer();
    int sampleModelTranslateX = raster.getSampleModelTranslateX();
    int sampleModelTranslateY = raster.getSampleModelTranslateY();
    
    SampleModel sm;
    
    if (bandList != null) {
      SampleModel sm = sampleModel.createCompatibleSampleModel(
        sampleModel.getWidth(), 
        sampleModel.getHeight());
      sm = sm.createSubsetSampleModel(bandList);
    }
    else {
      sm = sampleModel;
    }
    
    int deltaX = childMinX - parentX;
    int deltaY = childMinY - parentY;
    
    return new WritableRasterJAI(sm, 
      dataBuffer, 
      new Rectangle(childMinX, childMinY, 
      width, height), 
      new Point(sampleModelTranslateX + deltaX, 
      sampleModelTranslateY + deltaY), 
      raster);
  }
  




































  public static SampleModel createBandedSampleModel(int dataType, int width, int height, int numBands, int[] bankIndices, int[] bandOffsets)
  {
    if (numBands < 1) {
      throw new IllegalArgumentException(JaiI18N.getString("Number_of_bands_must_be_greater_than_0_"));
    }
    if (bankIndices == null) {
      bankIndices = new int[numBands];
      for (int i = 0; i < numBands; i++) {
        bankIndices[i] = i;
      }
    }
    if (bandOffsets == null) {
      bandOffsets = new int[numBands];
      for (int i = 0; i < numBands; i++) {
        bandOffsets[i] = 0;
      }
    }
    if (bandOffsets.length != bankIndices.length) {
      throw new IllegalArgumentException(JaiI18N.getString("bankIndices_length____bandOffsets_length"));
    }
    return new ComponentSampleModelJAI(dataType, 
      width, height, 1, width, 
      bankIndices, 
      bandOffsets);
  }
  

























  public static SampleModel createBandedSampleModel(int dataType, int width, int height, int numBands)
  {
    return createBandedSampleModel(dataType, 
      width, height, numBands, null, null);
  }
  





























  public static SampleModel createPixelInterleavedSampleModel(int dataType, int width, int height, int pixelStride, int scanlineStride, int[] bandOffsets)
  {
    if (bandOffsets == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Band_offsets_array_is_null_"));
    }
    int minBandOff = bandOffsets[0];
    int maxBandOff = bandOffsets[0];
    for (int i = 1; i < bandOffsets.length; i++) {
      minBandOff = Math.min(minBandOff, bandOffsets[i]);
      maxBandOff = Math.max(maxBandOff, bandOffsets[i]);
    }
    maxBandOff -= minBandOff;
    if (maxBandOff > scanlineStride) {
      throw new IllegalArgumentException(
        JaiI18N.getString("Offsets_between_bands_must_be_less_than_the_scanline_stride_"));
    }
    
    if (pixelStride * width > scanlineStride) {
      throw new IllegalArgumentException(
        JaiI18N.getString("Pixel_stride_times_width_must_be_less_than_the_scanline_stride_"));
    }
    if (pixelStride < maxBandOff) {
      throw new IllegalArgumentException(
        JaiI18N.getString("Pixel_stride_must_be_greater_than_or_equal_to_the_offset_between_bands_"));
    }
    
    switch (dataType) {
    case 0: 
    case 1: 
      return new PixelInterleavedSampleModel(dataType, 
        width, height, 
        pixelStride, 
        scanlineStride, 
        bandOffsets);
    case 2: 
    case 3: 
    case 4: 
    case 5: 
      return new ComponentSampleModelJAI(dataType, 
        width, height, 
        pixelStride, 
        scanlineStride, 
        bandOffsets);
    }
    throw new IllegalArgumentException(JaiI18N.getString("Unsupported_data_type_"));
  }
  
























  public static SampleModel createPixelInterleavedSampleModel(int dataType, int width, int height, int numBands)
  {
    if (numBands < 1) {
      throw new IllegalArgumentException(JaiI18N.getString("Number_of_bands_must_be_greater_than_0_"));
    }
    int[] bandOffsets = new int[numBands];
    for (int i = 0; i < numBands; i++) {
      bandOffsets[i] = (numBands - 1 - i);
    }
    
    return createPixelInterleavedSampleModel(dataType, width, height, 
      numBands, numBands * width, bandOffsets);
  }
  




















  public static SampleModel createComponentSampleModel(SampleModel sm, int dataType, int width, int height, int numBands)
  {
    if ((sm instanceof BandedSampleModel)) {
      return createBandedSampleModel(dataType, width, height, numBands);
    }
    return createPixelInterleavedSampleModel(
      dataType, width, height, numBands);
  }
  





































  public static ComponentColorModel createComponentColorModel(int dataType, ColorSpace colorSpace, boolean useAlpha, boolean premultiplied, int transparency)
  {
    if ((transparency != 1) && 
      (transparency != 2) && 
      (transparency != 3))
    {
      throw 
        new IllegalArgumentException(JaiI18N.getString("Illegal_value_for_transparency_"));
    }
    
    if ((useAlpha) && (transparency == 1)) {
      throw 
        new IllegalArgumentException(JaiI18N.getString("Transparency_cannot_be_opaque_when_useAlpha_is_true_"));
    }
    
    if (!useAlpha) {
      premultiplied = false;
      transparency = 1;
    }
    
    int bands = colorSpace.getNumComponents();
    if (useAlpha) {
      bands++;
    }
    
    int dataTypeSize = DataBuffer.getDataTypeSize(dataType);
    int[] bits = new int[bands];
    for (int i = 0; i < bands; i++) {
      bits[i] = dataTypeSize;
    }
    
    switch (dataType) {
    case 0: 
      return new ComponentColorModel(colorSpace, 
        bits, 
        useAlpha, 
        premultiplied, 
        transparency, 
        dataType);
    case 1: 
      return new ComponentColorModel(colorSpace, 
        bits, 
        useAlpha, 
        premultiplied, 
        transparency, 
        dataType);
    
    case 3: 
      return new ComponentColorModel(colorSpace, 
        bits, 
        useAlpha, 
        premultiplied, 
        transparency, 
        dataType);
    case 4: 
      return new FloatDoubleColorModel(colorSpace, 
        useAlpha, 
        premultiplied, 
        transparency, 
        dataType);
    case 5: 
      return new FloatDoubleColorModel(colorSpace, 
        useAlpha, 
        premultiplied, 
        transparency, 
        dataType);
    }
    throw new IllegalArgumentException(
      JaiI18N.getString("This_method_does_not_support_the_input_data_type_"));
  }
}
