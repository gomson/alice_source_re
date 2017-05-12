package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;








































public class SimplePicture
  implements DigitalPicture
{
  private String fileName;
  private String title;
  private BufferedImage bufferedImage;
  private PictureFrame pictureFrame;
  private String extension;
  
  public SimplePicture()
  {
    this(200, 100);
  }
  






  public SimplePicture(String fileName)
  {
    load(fileName);
  }
  








  public SimplePicture(int width, int height)
  {
    bufferedImage = new BufferedImage(width, height, 1);
    title = "None";
    fileName = "None";
    extension = "jpg";
    setAllPixelsToAColor(Color.white);
  }
  








  public SimplePicture(int width, int height, Color theColor)
  {
    this(width, height);
    setAllPixelsToAColor(theColor);
  }
  




  public SimplePicture(SimplePicture copyPicture)
  {
    if (fileName != null)
    {
      fileName = new String(fileName);
      extension = extension;
    }
    if (title != null)
      title = new String(title);
    if (bufferedImage != null)
    {
      bufferedImage = new BufferedImage(copyPicture.getWidth(), 
        copyPicture.getHeight(), 1);
      copyPicture(copyPicture);
    }
  }
  




  public SimplePicture(BufferedImage image)
  {
    bufferedImage = image;
    title = "None";
    fileName = "None";
    extension = "jpg";
  }
  




  public String getExtension()
  {
    return extension;
  }
  





  public void copyPicture(SimplePicture sourcePicture)
  {
    Pixel sourcePixel = null;
    Pixel targetPixel = null;
    

    int sourceX = 0; for (int targetX = 0; 
        (sourceX < sourcePicture.getWidth()) && (
          targetX < getWidth()); 
        targetX++)
    {

      int sourceY = 0; for (int targetY = 0; 
          (sourceY < sourcePicture.getHeight()) && (
            targetY < getHeight()); 
          targetY++)
      {
        sourcePixel = sourcePicture.getPixel(sourceX, sourceY);
        targetPixel = getPixel(targetX, targetY);
        targetPixel.setColor(sourcePixel.getColor());sourceY++;
      }
      sourceX++;
    }
  }
  

















  public void setAllPixelsToAColor(Color color)
  {
    for (int x = 0; x < getWidth(); x++)
    {

      for (int y = 0; y < getHeight(); y++)
      {
        getPixel(x, y).setColor(color);
      }
    }
  }
  




  public BufferedImage getBufferedImage()
  {
    return bufferedImage;
  }
  




  public Graphics getGraphics()
  {
    return bufferedImage.getGraphics();
  }
  




  public Graphics2D createGraphics()
  {
    return bufferedImage.createGraphics();
  }
  


  public String getFileName()
  {
    return fileName;
  }
  



  public void setFileName(String name)
  {
    fileName = name;
  }
  



  public String getTitle()
  {
    return title;
  }
  



  public void setTitle(String title)
  {
    this.title = title;
    if (pictureFrame != null) {
      pictureFrame.setTitle(title);
    }
  }
  

  public int getWidth()
  {
    return bufferedImage.getWidth();
  }
  

  public int getHeight()
  {
    return bufferedImage.getHeight();
  }
  


  public PictureFrame getPictureFrame()
  {
    return pictureFrame;
  }
  




  public void setPictureFrame(PictureFrame pictureFrame)
  {
    this.pictureFrame = pictureFrame;
  }
  




  public Image getImage()
  {
    return bufferedImage;
  }
  






  public int getBasicPixel(int x, int y)
  {
    return bufferedImage.getRGB(x, y);
  }
  






  public void setBasicPixel(int x, int y, int rgb)
  {
    bufferedImage.setRGB(x, y, rgb);
  }
  







  public Pixel getPixel(int x, int y)
  {
    Pixel pixel = new Pixel(this, x, y);
    return pixel;
  }
  





  public Pixel[] getPixels()
  {
    int width = getWidth();
    int height = getHeight();
    Pixel[] pixelArray = new Pixel[width * height];
    

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++)
        pixelArray[(row * width + col)] = new Pixel(this, col, row);
    }
    return pixelArray;
  }
  







  public void load(Image image)
  {
    Graphics2D graphics2d = bufferedImage.createGraphics();
    

    graphics2d.drawImage(image, 0, 0, null);
    

    show();
  }
  




  public void show()
  {
    if (pictureFrame != null) {
      pictureFrame.updateImageAndShowIt();
    }
    else
    {
      pictureFrame = new PictureFrame(this);
    }
  }
  


  public void hide()
  {
    if (pictureFrame != null) {
      pictureFrame.setVisible(false);
    }
  }
  



  public void setVisible(boolean flag)
  {
    if (flag) {
      show();
    } else {
      hide();
    }
  }
  



  public void explore()
  {
    new PictureExplorer(new SimplePicture(this));
  }
  





  public void repaint()
  {
    if (pictureFrame != null) {
      pictureFrame.repaint();
    }
    else
    {
      pictureFrame = new PictureFrame(this);
    }
  }
  



  public void loadOrFail(String fileName)
    throws IOException
  {
    this.fileName = fileName;
    

    int posDot = fileName.lastIndexOf('.');
    if (posDot >= 0) {
      extension = fileName.substring(posDot + 1);
    }
    
    if (title == null) {
      title = fileName;
    }
    File file = new File(this.fileName);
    
    if (!file.canRead())
    {

      file = new File(FileChooser.getMediaPath(this.fileName));
      if (!file.canRead())
      {
        throw new IOException(this.fileName + " " + Messages.getString("could_not_be_opened__Check_that_you_specified_the_path"));
      }
    }
    
    bufferedImage = ImageIO.read(file);
  }
  






  public boolean load(String fileName)
  {
    try
    {
      loadOrFail(fileName);
      return true;
    }
    catch (Exception ex) {
      System.out.println(Messages.getString("There_was_an_error_trying_to_open_") + fileName);
      bufferedImage = new BufferedImage(600, 200, 
        1);
      addMessage(Messages.getString("Couldn_t_load_") + fileName, 5, 100); }
    return false;
  }
  









  public boolean loadImage(String fileName)
  {
    return load(fileName);
  }
  







  public void addMessage(String message, int xPos, int yPos)
  {
    Graphics2D graphics2d = bufferedImage.createGraphics();
    

    graphics2d.setPaint(Color.white);
    

    graphics2d.setFont(new Font("Helvetica", 1, 16));
    

    graphics2d.drawString(message, xPos, yPos);
  }
  







  public void drawString(String text, int xPos, int yPos)
  {
    addMessage(text, xPos, yPos);
  }
  








  public Picture scale(double xFactor, double yFactor)
  {
    AffineTransform scaleTransform = new AffineTransform();
    scaleTransform.scale(xFactor, yFactor);
    

    Picture result = new Picture((int)(getWidth() * xFactor), 
      (int)(getHeight() * yFactor));
    

    Graphics graphics = result.getGraphics();
    Graphics2D g2 = (Graphics2D)graphics;
    

    g2.drawImage(getImage(), scaleTransform, null);
    
    return result;
  }
  








  public Picture getPictureWithWidth(int width)
  {
    double xFactor = width / getWidth();
    Picture result = scale(xFactor, xFactor);
    return result;
  }
  








  public Picture getPictureWithHeight(int height)
  {
    double yFactor = height / getHeight();
    Picture result = scale(yFactor, yFactor);
    return result;
  }
  





  public boolean loadPictureAndShowIt(String fileName)
  {
    boolean result = true;
    

    result = load(fileName);
    

    show();
    
    return result;
  }
  




  public void writeOrFail(String fileName)
    throws IOException
  {
    String extension = this.extension;
    

    File file = new File(fileName);
    File fileLoc = file.getParentFile();
    

    if (!fileLoc.canWrite())
    {
      throw new IOException(fileName + " " + 
        Messages.getString("could_not_be_opened__Check_to_see_if_you_can_write_to_the_directory_"));
    }
    

    int posDot = fileName.lastIndexOf('.');
    if (posDot >= 0) {
      extension = fileName.substring(posDot + 1);
    }
    
    ImageIO.write(bufferedImage, extension, file);
  }
  






  public boolean write(String fileName)
  {
    try
    {
      writeOrFail(fileName);
      return true;
    } catch (Exception ex) {
      System.out.println(Messages.getString("There_was_an_error_trying_to_write_") + fileName); }
    return false;
  }
  





  public static void setMediaPath(String directory)
  {
    FileChooser.setMediaPath(directory);
  }
  





  public static String getMediaPath(String fileName)
  {
    return FileChooser.getMediaPath(fileName);
  }
  





  public Rectangle2D getTransformEnclosingRect(AffineTransform trans)
  {
    int width = getWidth();
    int height = getHeight();
    double maxX = width - 1;
    double maxY = height - 1;
    
    Point2D.Double p1 = new Point2D.Double(0.0D, 0.0D);
    Point2D.Double p2 = new Point2D.Double(maxX, 0.0D);
    Point2D.Double p3 = new Point2D.Double(maxX, maxY);
    Point2D.Double p4 = new Point2D.Double(0.0D, maxY);
    Point2D.Double result = new Point2D.Double(0.0D, 0.0D);
    Rectangle2D.Double rect = null;
    

    trans.deltaTransform(p1, result);
    double minX = result.getX();
    maxX = result.getX();
    double minY = result.getY();
    maxY = result.getY();
    trans.deltaTransform(p2, result);
    minX = Math.min(minX, result.getX());
    maxX = Math.max(maxX, result.getX());
    minY = Math.min(minY, result.getY());
    maxY = Math.max(maxY, result.getY());
    trans.deltaTransform(p3, result);
    minX = Math.min(minX, result.getX());
    maxX = Math.max(maxX, result.getX());
    minY = Math.min(minY, result.getY());
    maxY = Math.max(maxY, result.getY());
    trans.deltaTransform(p4, result);
    minX = Math.min(minX, result.getX());
    maxX = Math.max(maxX, result.getX());
    minY = Math.min(minY, result.getY());
    maxY = Math.max(maxY, result.getY());
    

    rect = new Rectangle2D.Double(minX, minY, maxX - minX + 1.0D, maxY - minY + 1.0D);
    return rect;
  }
  





  public String toString()
  {
    String output = Messages.getString("Simple_Picture__filename_") + fileName + " " + 
      Messages.getString("height_") + getHeight() + " " + Messages.getString("width_") + getWidth();
    return output;
  }
}
