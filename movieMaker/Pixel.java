package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;




























public class Pixel
{
  private DigitalPicture picture;
  private int x;
  private int y;
  
  public Pixel(DigitalPicture picture, int x, int y)
  {
    this.picture = picture;
    

    this.x = x;
    

    this.y = y;
  }
  





  public int getX()
  {
    return x;
  }
  

  public int getY()
  {
    return y;
  }
  







  public int getAlpha()
  {
    int value = picture.getBasicPixel(x, y);
    



    int alpha = value >> 24 & 0xFF;
    
    return alpha;
  }
  










  public int getRed()
  {
    int value = picture.getBasicPixel(x, y);
    



    int red = value >> 16 & 0xFF;
    
    return red;
  }
  





  public static int getRed(int value)
  {
    int red = value >> 16 & 0xFF;
    return red;
  }
  









  public int getGreen()
  {
    int value = picture.getBasicPixel(x, y);
    

    int green = value >> 8 & 0xFF;
    
    return green;
  }
  





  public static int getGreen(int value)
  {
    int green = value >> 8 & 0xFF;
    return green;
  }
  









  public int getBlue()
  {
    int value = picture.getBasicPixel(x, y);
    

    int blue = value & 0xFF;
    
    return blue;
  }
  





  public static int getBlue(int value)
  {
    int blue = value & 0xFF;
    return blue;
  }
  







  public Color getColor()
  {
    int value = picture.getBasicPixel(x, y);
    



    int red = value >> 16 & 0xFF;
    

    int green = value >> 8 & 0xFF;
    

    int blue = value & 0xFF;
    
    return new Color(red, green, blue);
  }
  





  public void setColor(Color newColor)
  {
    int red = newColor.getRed();
    int green = newColor.getGreen();
    int blue = newColor.getBlue();
    

    updatePicture(getAlpha(), red, green, blue);
  }
  











  public void updatePicture(int alpha, int red, int green, int blue)
  {
    int value = (alpha << 24) + (red << 16) + (green << 8) + blue;
    

    picture.setBasicPixel(x, y, value);
  }
  





  private static int correctValue(int value)
  {
    if (value < 0)
      value = 0;
    if (value > 255)
      value = 255;
    return value;
  }
  





  public void setRed(int value)
  {
    int red = correctValue(value);
    

    updatePicture(getAlpha(), red, getGreen(), getBlue());
  }
  





  public void setGreen(int value)
  {
    int green = correctValue(value);
    

    updatePicture(getAlpha(), getRed(), green, getBlue());
  }
  





  public void setBlue(int value)
  {
    int blue = correctValue(value);
    

    updatePicture(getAlpha(), getRed(), getGreen(), blue);
  }
  





  public void setAlpha(int value)
  {
    int alpha = correctValue(value);
    

    updatePicture(alpha, getRed(), getGreen(), getBlue());
  }
  





  public double colorDistance(Color testColor)
  {
    double redDistance = getRed() - testColor.getRed();
    double greenDistance = getGreen() - testColor.getGreen();
    double blueDistance = getBlue() - testColor.getBlue();
    double distance = Math.sqrt(redDistance * redDistance + 
      greenDistance * greenDistance + 
      blueDistance * blueDistance);
    return distance;
  }
  






  public static double colorDistance(Color color1, Color color2)
  {
    double redDistance = color1.getRed() - color2.getRed();
    double greenDistance = color1.getGreen() - color2.getGreen();
    double blueDistance = color1.getBlue() - color2.getBlue();
    double distance = Math.sqrt(redDistance * redDistance + 
      greenDistance * greenDistance + 
      blueDistance * blueDistance);
    return distance;
  }
  




  public double getAverage()
  {
    double average = (getRed() + getGreen() + getBlue()) / 3.0D;
    return average;
  }
  





  public String toString()
  {
    return 
      Messages.getString("Pixel_red_") + getRed() + " " + Messages.getString("green_") + getGreen() + " " + Messages.getString("blue_") + getBlue();
  }
}
