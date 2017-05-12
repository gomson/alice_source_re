package edu.cmu.cs.stage3.image.codec;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;




























































































public class FloatDoubleColorModel
  extends ComponentColorModel
{
  ColorSpace colorSpace;
  int colorSpaceType;
  int numColorComponents;
  int numComponents;
  int transparency;
  boolean hasAlpha;
  boolean isAlphaPremultiplied;
  
  private static int[] bitsHelper(int transferType, ColorSpace colorSpace, boolean hasAlpha)
  {
    int numBits = transferType == 4 ? 32 : 64;
    int numComponents = colorSpace.getNumComponents();
    if (hasAlpha) {
      numComponents++;
    }
    int[] bits = new int[numComponents];
    for (int i = 0; i < numComponents; i++) {
      bits[i] = numBits;
    }
    
    return bits;
  }
  


































  public FloatDoubleColorModel(ColorSpace colorSpace, boolean hasAlpha, boolean isAlphaPremultiplied, int transparency, int transferType)
  {
    super(colorSpace, bitsHelper(transferType, colorSpace, hasAlpha), hasAlpha, isAlphaPremultiplied, transparency, transferType);
    
    if ((transferType != 4) && 
      (transferType != 5)) {
      throw new IllegalArgumentException(JaiI18N.getString("transferType_must_be_DataBuffer_TYPE_FLOAT_or_DataBuffer_TYPE_DOUBLE_"));
    }
    
    this.colorSpace = colorSpace;
    colorSpaceType = colorSpace.getType();
    numComponents = 
      (this.numColorComponents = colorSpace.getNumComponents());
    if (hasAlpha) {
      numComponents += 1;
    }
    this.transparency = transparency;
    this.hasAlpha = hasAlpha;
    this.isAlphaPremultiplied = isAlphaPremultiplied;
  }
  





  public int getRed(int pixel)
  {
    throw new IllegalArgumentException(JaiI18N.getString("getRed_int__not_supported_by_this_ColorModel_"));
  }
  





  public int getGreen(int pixel)
  {
    throw new IllegalArgumentException(JaiI18N.getString("getGreen_int__not_supported_by_this_ColorModel_"));
  }
  





  public int getBlue(int pixel)
  {
    throw new IllegalArgumentException(JaiI18N.getString("getBlue_int__not_supported_by_this_ColorModel_"));
  }
  





  public int getAlpha(int pixel)
  {
    throw new IllegalArgumentException(JaiI18N.getString("getAlpha_int__not_supported_by_this_ColorModel_"));
  }
  





  public int getRGB(int pixel)
  {
    throw new IllegalArgumentException(JaiI18N.getString("getRGB_int__not_supported_by_this_ColorModel_"));
  }
  
  private int clamp(float value)
  {
    return value >= 0.0F ? (int)value : value > 255.0F ? 255 : 0;
  }
  
  private int clamp(double value)
  {
    return value >= 0.0D ? (int)value : value > 255.0D ? 255 : 0;
  }
  
  private int getSample(Object inData, int sample) {
    boolean needAlpha = (hasAlpha) && (isAlphaPremultiplied);
    int type = colorSpaceType;
    
    boolean is_sRGB = colorSpace.isCS_sRGB();
    
    if (type == 6) {
      sample = 0;
      is_sRGB = true;
    }
    
    if (is_sRGB) {
      if (transferType == 4) {
        float[] fdata = (float[])inData;
        float fsample = fdata[sample] * 255.0F;
        if (needAlpha) {
          float falp = fdata[numColorComponents];
          return clamp(fsample / falp);
        }
        return clamp(fsample);
      }
      
      double[] ddata = (double[])inData;
      double dsample = ddata[sample] * 255.0D;
      if (needAlpha) {
        double dalp = ddata[numColorComponents];
        return clamp(dsample / dalp);
      }
      return clamp(dsample);
    }
    





    if (transferType == 4) {
      float[] fdata = (float[])inData;
      if (needAlpha) {
        float falp = fdata[numColorComponents];
        float[] norm = new float[numColorComponents];
        for (int i = 0; i < numColorComponents; i++) {
          fdata[i] /= falp;
        }
        float[] rgb = colorSpace.toRGB(norm);
        return (int)(rgb[sample] * falp * 255.0F);
      }
      float[] rgb = colorSpace.toRGB(fdata);
      return (int)(rgb[sample] * 255.0F);
    }
    
    double[] ddata = (double[])inData;
    float[] norm = new float[numColorComponents];
    if (needAlpha) {
      double dalp = ddata[numColorComponents];
      for (int i = 0; i < numColorComponents; i++) {
        norm[i] = ((float)(ddata[i] / dalp));
      }
      float[] rgb = colorSpace.toRGB(norm);
      return (int)(rgb[sample] * dalp * 255.0D);
    }
    for (int i = 0; i < numColorComponents; i++) {
      norm[i] = ((float)ddata[i]);
    }
    float[] rgb = colorSpace.toRGB(norm);
    return (int)(rgb[sample] * 255.0F);
  }
  

























  public int getRed(Object inData)
  {
    return getSample(inData, 0);
  }
  























  public int getGreen(Object inData)
  {
    return getSample(inData, 1);
  }
  























  public int getBlue(Object inData)
  {
    return getSample(inData, 2);
  }
  























  public int getAlpha(Object inData)
  {
    if (!hasAlpha) {
      return 255;
    }
    
    if (transferType == 4) {
      float[] fdata = (float[])inData;
      return (int)(fdata[numColorComponents] * 255.0F);
    }
    double[] ddata = (double[])inData;
    return (int)(ddata[numColorComponents] * 255.0D);
  }
  























  public int getRGB(Object inData)
  {
    boolean needAlpha = (hasAlpha) && (isAlphaPremultiplied);
    int alpha = 255;
    int blue;
    int red;
    int green; int blue; if (colorSpace.isCS_sRGB()) { int blue;
      if (transferType == 4) {
        float[] fdata = (float[])inData;
        float fred = fdata[0];
        float fgreen = fdata[1];
        float fblue = fdata[2];
        float fscale = 255.0F;
        if (needAlpha) {
          float falpha = fdata[3];
          fscale /= falpha;
          alpha = clamp(255.0F * falpha);
        }
        
        int red = clamp(fred * fscale);
        int green = clamp(fgreen * fscale);
        blue = clamp(fblue * fscale);
      } else {
        double[] ddata = (double[])inData;
        double dred = ddata[0];
        double dgreen = ddata[1];
        double dblue = ddata[2];
        double dscale = 255.0D;
        if (needAlpha) {
          double dalpha = ddata[3];
          dscale /= dalpha;
          alpha = clamp(255.0D * dalpha);
        }
        
        int red = clamp(dred * dscale);
        int green = clamp(dgreen * dscale);
        blue = clamp(dblue * dscale);
      } } else { int red;
      if (colorSpaceType == 6) { int red;
        if (transferType == 4) {
          float[] fdata = (float[])inData;
          float fgray = fdata[0];
          if (needAlpha) {
            float falp = fdata[1];
            int blue; int green; int red = green = blue = clamp(fgray * 255.0F / falp);
            alpha = clamp(255.0F * falp); } else { int blue;
            int green;
            red = green = blue = clamp(fgray * 255.0F);
          }
        } else {
          double[] ddata = (double[])inData;
          double dgray = ddata[0];
          if (needAlpha) {
            double dalp = ddata[1];
            int blue; int green; int red = green = blue = clamp(dgray * 255.0D / dalp);
            alpha = clamp(255.0D * dalp); } else { int blue;
            int green;
            red = green = blue = clamp(dgray * 255.0D);
          }
        }
      }
      else {
        float[] norm;
        float[] norm;
        if (transferType == 4) {
          float[] fdata = (float[])inData;
          if (needAlpha) {
            float falp = fdata[numColorComponents];
            float invfalp = 1.0F / falp;
            float[] norm = new float[numColorComponents];
            for (int i = 0; i < numColorComponents; i++) {
              fdata[i] *= invfalp;
            }
            alpha = clamp(255.0F * falp);
          } else {
            norm = fdata;
          }
        } else {
          double[] ddata = (double[])inData;
          norm = new float[numColorComponents];
          if (needAlpha) {
            double dalp = ddata[numColorComponents];
            double invdalp = 1.0D / dalp;
            for (int i = 0; i < numColorComponents; i++) {
              norm[i] = ((float)(ddata[i] * invdalp));
            }
            alpha = clamp(255.0D * dalp);
          } else {
            for (int i = 0; i < numColorComponents; i++) {
              norm[i] = ((float)ddata[i]);
            }
          }
        }
        

        float[] rgb = colorSpace.toRGB(norm);
        
        red = clamp(rgb[0] * 255.0F);
        green = clamp(rgb[1] * 255.0F);
        blue = clamp(rgb[2] * 255.0F);
      }
    }
    return alpha << 24 | red << 16 | green << 8 | blue;
  }
  



















  public Object getDataElements(int rgb, Object pixel)
  {
    if (transferType == 4) {
      float[] floatPixel;
      float[] floatPixel;
      if (pixel == null) {
        floatPixel = new float[numComponents];
      } else {
        if (!(pixel instanceof float[])) {
          throw new ClassCastException(JaiI18N.getString("Type_of_pixel_does_not_match_transfer_type_"));
        }
        floatPixel = (float[])pixel;
        if (floatPixel.length < numComponents) {
          throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("pixel_array_is_not_large_enough_to_hold_all_color_alpha_components_"));
        }
      }
      
      float inv255 = 0.003921569F;
      if (colorSpace.isCS_sRGB()) {
        int alp = rgb >> 24 & 0xFF;
        int red = rgb >> 16 & 0xFF;
        int grn = rgb >> 8 & 0xFF;
        int blu = rgb & 0xFF;
        float norm = inv255;
        if (isAlphaPremultiplied) {
          norm *= alp;
        }
        floatPixel[0] = (red * norm);
        floatPixel[1] = (grn * norm);
        floatPixel[2] = (blu * norm);
        if (hasAlpha) {
          floatPixel[3] = (alp * inv255);
        }
      } else if (colorSpaceType == 6) {
        float gray = (rgb >> 16 & 0xFF) * (0.299F * inv255) + 
          (rgb >> 8 & 0xFF) * (0.587F * inv255) + 
          (rgb & 0xFF) * (0.114F * inv255);
        
        floatPixel[0] = gray;
        
        if (hasAlpha) {
          int alpha = rgb >> 24 & 0xFF;
          floatPixel[1] = (alpha * inv255);
        }
      }
      else {
        float[] norm = new float[3];
        norm[0] = ((rgb >> 16 & 0xFF) * inv255);
        norm[1] = ((rgb >> 8 & 0xFF) * inv255);
        norm[2] = ((rgb & 0xFF) * inv255);
        
        norm = colorSpace.fromRGB(norm);
        for (int i = 0; i < numColorComponents; i++) {
          floatPixel[i] = norm[i];
        }
        if (hasAlpha) {
          int alpha = rgb >> 24 & 0xFF;
          floatPixel[numColorComponents] = (alpha * inv255);
        }
      }
      
      return floatPixel;
    }
    double[] doublePixel;
    double[] doublePixel;
    if (pixel == null) {
      doublePixel = new double[numComponents];
    } else {
      if (!(pixel instanceof double[])) {
        throw new ClassCastException(JaiI18N.getString("Type_of_pixel_does_not_match_transfer_type_"));
      }
      doublePixel = (double[])pixel;
      if (doublePixel.length < numComponents) {
        throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("pixel_array_is_not_large_enough_to_hold_all_color_alpha_components_"));
      }
    }
    
    double inv255 = 0.00392156862745098D;
    if (colorSpace.isCS_sRGB()) {
      int alp = rgb >> 24 & 0xFF;
      int red = rgb >> 16 & 0xFF;
      int grn = rgb >> 8 & 0xFF;
      int blu = rgb & 0xFF;
      double norm = inv255;
      if (isAlphaPremultiplied) {
        norm *= alp;
      }
      doublePixel[0] = (red * norm);
      doublePixel[1] = (grn * norm);
      doublePixel[2] = (blu * norm);
      if (hasAlpha) {
        doublePixel[3] = (alp * inv255);
      }
    } else if (colorSpaceType == 6) {
      double gray = (rgb >> 16 & 0xFF) * (0.299D * inv255) + 
        (rgb >> 8 & 0xFF) * (0.587D * inv255) + 
        (rgb & 0xFF) * (0.114D * inv255);
      
      doublePixel[0] = gray;
      
      if (hasAlpha) {
        int alpha = rgb >> 24 & 0xFF;
        doublePixel[1] = (alpha * inv255);
      }
    } else {
      float inv255F = 0.003921569F;
      

      float[] norm = new float[3];
      norm[0] = ((rgb >> 16 & 0xFF) * inv255F);
      norm[1] = ((rgb >> 8 & 0xFF) * inv255F);
      norm[2] = ((rgb & 0xFF) * inv255F);
      
      norm = colorSpace.fromRGB(norm);
      for (int i = 0; i < numColorComponents; i++) {
        doublePixel[i] = norm[i];
      }
      if (hasAlpha) {
        int alpha = rgb >> 24 & 0xFF;
        doublePixel[numColorComponents] = (alpha * inv255);
      }
    }
    
    return doublePixel;
  }
  






  public int[] getComponents(int pixel, int[] components, int offset)
  {
    throw 
      new IllegalArgumentException(JaiI18N.getString("Pixel_values_for_FloatDoubleColorModel_cannot_be_represented_as_a_single_integer_"));
  }
  




  public int[] getComponents(Object pixel, int[] components, int offset)
  {
    throw 
      new IllegalArgumentException(JaiI18N.getString("Pixel_values_for_FloatDoubleColorModel_cannot_be_represented_as_a_single_integer_"));
  }
  





  public int getDataElement(int[] components, int offset)
  {
    throw 
      new IllegalArgumentException(JaiI18N.getString("Pixel_values_for_FloatDoubleColorModel_cannot_be_represented_as_a_single_integer_"));
  }
  
































  public Object getDataElements(int[] components, int offset, Object obj)
  {
    if (components.length - offset < numComponents) {
      throw new IllegalArgumentException(numComponents + " " + 
        JaiI18N.getString("elements_required_in_the_components_array_"));
    }
    if (transferType == 4) { float[] pixel;
      float[] pixel;
      if (obj == null) {
        pixel = new float[components.length];
      } else {
        pixel = (float[])obj;
      }
      for (int i = 0; i < numComponents; i++) {
        pixel[i] = components[(offset + i)];
      }
      
      return pixel; }
    double[] pixel;
    double[] pixel;
    if (obj == null) {
      pixel = new double[components.length];
    } else {
      pixel = (double[])obj;
    }
    for (int i = 0; i < numComponents; i++) {
      pixel[i] = components[(offset + i)];
    }
    
    return pixel;
  }
  
















  public ColorModel coerceData(WritableRaster raster, boolean isAlphaPremultiplied)
  {
    if ((!hasAlpha) || 
      (this.isAlphaPremultiplied == isAlphaPremultiplied))
    {

      return this;
    }
    
    int w = raster.getWidth();
    int h = raster.getHeight();
    int aIdx = raster.getNumBands() - 1;
    int rminX = raster.getMinX();
    int rY = raster.getMinY();
    

    if (raster.getTransferType() != transferType) {
      throw new IllegalArgumentException(
        JaiI18N.getString("raster_transfer_type_must_match_that_of_this_ColorModel_"));
    }
    
    if (isAlphaPremultiplied) {
      switch (transferType) {
      case 4: 
        float[] pixel = null;
        for (int y = 0; y < h; rY++) {
          int rX = rminX;
          for (int x = 0; x < w; rX++) {
            pixel = (float[])raster.getDataElements(rX, rY, 
              pixel);
            float fAlpha = pixel[aIdx];
            if (fAlpha != 0.0F) {
              for (int c = 0; c < aIdx; c++) {
                pixel[c] *= fAlpha;
              }
              raster.setDataElements(rX, rY, pixel);
            }
            x++;
          }
          y++;
        }
        












        break;
      case 5: 
        double[] pixel = null;
        for (int y = 0; y < h; rY++) {
          int rX = rminX;
          for (int x = 0; x < w; rX++) {
            pixel = (double[])raster.getDataElements(rX, rY, 
              pixel);
            double dAlpha = pixel[aIdx];
            if (dAlpha != 0.0D) {
              for (int c = 0; c < aIdx; c++) {
                pixel[c] *= dAlpha;
              }
              raster.setDataElements(rX, rY, pixel);
            }
            x++;
          }
          y++;
        }
      














      }
      
    } else {
      switch (transferType) {
      case 4: 
        for (int y = 0; y < h; rY++) {
          int rX = rminX;
          for (int x = 0; x < w; rX++) {
            float[] pixel = null;
            pixel = (float[])raster.getDataElements(rX, rY, 
              pixel);
            float fAlpha = pixel[aIdx];
            if (fAlpha != 0.0F) {
              float invFAlpha = 1.0F / fAlpha;
              for (int c = 0; c < aIdx; c++) {
                pixel[c] *= invFAlpha;
              }
            }
            raster.setDataElements(rX, rY, pixel);x++;
          }
          y++;
        }
        














        break;
      case 5: 
        for (int y = 0; y < h; rY++) {
          int rX = rminX;
          for (int x = 0; x < w; rX++) {
            double[] pixel = null;
            pixel = (double[])raster.getDataElements(rX, rY, 
              pixel);
            double dAlpha = pixel[aIdx];
            if (dAlpha != 0.0D) {
              double invDAlpha = 1.0D / dAlpha;
              for (int c = 0; c < aIdx; c++) {
                pixel[c] *= invDAlpha;
              }
            }
            raster.setDataElements(rX, rY, pixel);x++;
          }
          y++;
        }
      }
      
    }
    
















    return new FloatDoubleColorModel(colorSpace, hasAlpha, 
      isAlphaPremultiplied, transparency, 
      transferType);
  }
  







  public boolean isCompatibleRaster(Raster raster)
  {
    SampleModel sm = raster.getSampleModel();
    return isCompatibleSampleModel(sm);
  }
  


















  public WritableRaster createCompatibleWritableRaster(int w, int h)
  {
    SampleModel sm = createCompatibleSampleModel(w, h);
    return RasterFactory.createWritableRaster(sm, new Point(0, 0));
  }
  















  public SampleModel createCompatibleSampleModel(int w, int h)
  {
    int[] bandOffsets = new int[numComponents];
    for (int i = 0; i < numComponents; i++) {
      bandOffsets[i] = i;
    }
    return new ComponentSampleModelJAI(transferType, 
      w, h, 
      numComponents, 
      w * numComponents, 
      bandOffsets);
  }
  



















  public boolean isCompatibleSampleModel(SampleModel sm)
  {
    if ((sm instanceof ComponentSampleModel)) {
      if (sm.getNumBands() != getNumComponents()) {
        return false;
      }
      if (sm.getDataType() != transferType) {
        return false;
      }
      return true;
    }
    return false;
  }
  


  public String toString()
  {
    return "FloatDoubleColorModel: " + super.toString();
  }
}
