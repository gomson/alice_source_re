package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.image.BufferedImage;
























public class Picture
  extends SimplePicture
{
  public Picture() {}
  
  public Picture(String fileName)
  {
    super(fileName);
  }
  






  public Picture(int width, int height)
  {
    super(width, height);
  }
  





  public Picture(Picture copyPicture)
  {
    super(copyPicture);
  }
  




  public Picture(BufferedImage image)
  {
    super(image);
  }
  








  public String toString()
  {
    String output = Messages.getString("Picture__filename_") + getFileName() + " " + 
      Messages.getString("height_") + getHeight() + " " + 
      Messages.getString("width_") + getWidth();
    return output;
  }
}
