package movieMaker;

import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract interface DigitalPicture
{
  public abstract String getFileName();
  
  public abstract String getTitle();
  
  public abstract void setTitle(String paramString);
  
  public abstract int getWidth();
  
  public abstract int getHeight();
  
  public abstract Image getImage();
  
  public abstract BufferedImage getBufferedImage();
  
  public abstract int getBasicPixel(int paramInt1, int paramInt2);
  
  public abstract void setBasicPixel(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract Pixel getPixel(int paramInt1, int paramInt2);
  
  public abstract void load(Image paramImage);
  
  public abstract boolean load(String paramString);
  
  public abstract void show();
}
