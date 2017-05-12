package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;





















public class LightenDarkenFilter
  extends RGBImageFilter
{
  private int percent;
  
  public static Image createLightenedOrDarkenedImage(Image i, int percent)
  {
    LightenDarkenFilter filter = new LightenDarkenFilter(percent);
    ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
    Image filteredImage = Toolkit.getDefaultToolkit().createImage(prod);
    
    return filteredImage;
  }
  


  public LightenDarkenFilter(int p)
  {
    percent = p;
    
    canFilterIndexColorModel = true;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    int r = rgb >> 16 & 0xFF;
    int g = rgb >> 8 & 0xFF;
    int b = rgb >> 0 & 0xFF;
    
    if (percent > 0) {
      r = 255 - (255 - r) * (100 - percent) / 100;
      g = 255 - (255 - g) * (100 - percent) / 100;
      b = 255 - (255 - b) * (100 - percent) / 100;
    } else {
      r = r * (100 + percent) / 100;
      g = g * (100 + percent) / 100;
      b = b * (100 + percent) / 100;
    }
    
    if (r < 0) r = 0;
    if (g < 0) g = 0;
    if (b < 0) { b = 0;
    }
    if (r > 255) r = 255;
    if (g > 255) g = 255;
    if (b > 255) { b = 255;
    }
    return rgb & 0xFF000000 | r << 16 | g << 8 | b << 0;
  }
}
