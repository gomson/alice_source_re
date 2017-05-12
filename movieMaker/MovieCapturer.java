package movieMaker;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;







public class MovieCapturer
  implements VideoCapturer
{
  private FrameSequencer frameSequencer = null;
  

  private int framesPerSec = 16;
  

  private Rectangle region = null;
  






  public MovieCapturer(String directory)
  {
    frameSequencer = new FrameSequencer(directory);
  }
  






  public MovieCapturer(String directory, String baseName)
  {
    frameSequencer = new FrameSequencer(directory, baseName);
  }
  





  public FrameSequencer getFrameSequencer()
  {
    return frameSequencer;
  }
  

  public int getFramesPerSecond()
  {
    return framesPerSec;
  }
  



  public void setFramesPerSecond(int frameRate)
  {
    framesPerSec = frameRate;
  }
  



  public Rectangle getRegion()
  {
    return region;
  }
  




  public BufferedImage captureScreen()
    throws Exception
  {
    BufferedImage image = new Robot().createScreenCapture(
      new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    
    return image;
  }
  











  public BufferedImage captureRegion(int x1, int y1, int width, int height)
    throws AWTException
  {
    BufferedImage screen = new Robot().createScreenCapture(
      new Rectangle(x1, y1, width, height));
    
    return screen;
  }
  



  public BufferedImage captureRegion()
    throws Exception
  {
    if (region != null)
    {
      BufferedImage image = 
        new Robot().createScreenCapture(region);
      return image;
    }
    return captureScreen();
  }
  




  public void captureMovie()
  {
    boolean done = false;
    BufferedImage image = null;
    long startTime = 0L;
    long endTime = 0L;
    int timeToSleep = 1000 / framesPerSec;
    while (!done) {
      try
      {
        startTime = System.currentTimeMillis();
        image = captureRegion();
        frameSequencer.addFrame(new Picture(image));
        endTime = System.currentTimeMillis();
        Thread.sleep(timeToSleep - (endTime - startTime));
      }
      catch (Exception localException) {}
    }
  }
  



  public void run()
  {
    captureMovie();
  }
  





  public void captureMovie(int numSeconds)
  {
    BufferedImage image = null;
    int timeToSleep = 1000 / framesPerSec;
    long startTime = 0L;
    long endTime = 0L;
    for (int i = 0; i < framesPerSec * numSeconds; i++) {
      try
      {
        startTime = System.currentTimeMillis();
        image = captureRegion();
        frameSequencer.addFrame(new Picture(image));
        endTime = System.currentTimeMillis();
        Thread.sleep(timeToSleep - (endTime - startTime));
      }
      catch (Exception localException) {}
    }
  }
  









  public void captureMovie(int x1, int y1, int width, int height)
  {
    boolean done = false;
    long startTime = 0L;
    long endTime = 0L;
    BufferedImage image = null;
    int timeToSleep = 1000 / framesPerSec;
    while (!done) {
      try
      {
        startTime = System.currentTimeMillis();
        image = captureRegion(x1, y1, width, height);
        frameSequencer.addFrame(new Picture(image));
        endTime = System.currentTimeMillis();
        Thread.sleep(timeToSleep - (endTime - startTime));
      }
      catch (Exception localException) {}
    }
  }
  











  public void captureMovie(int x1, int y1, int width, int height, int numSeconds)
  {
    long startTime = 0L;
    long endTime = 0L;
    BufferedImage image = null;
    int timeToSleep = 1000 / framesPerSec;
    for (int i = 0; i < numSeconds * framesPerSec; i++) {
      try
      {
        startTime = System.currentTimeMillis();
        image = captureRegion(x1, y1, width, height);
        frameSequencer.addFrame(new Picture(image));
        endTime = System.currentTimeMillis();
        Thread.sleep(timeToSleep - (endTime - startTime));
      }
      catch (Exception localException) {}
    }
  }
  



  public void playMovie()
  {
    frameSequencer.play(framesPerSec);
  }
  






  public void setRegion(Rectangle theRegion)
  {
    if ((theRegion.getWidth() % 4.0D != 0.0D) || 
      (theRegion.getHeight() % 4.0D != 0.0D))
    {
      int width = (int)theRegion.getWidth() / 4 * 4;
      int height = (int)theRegion.getHeight() / 4 * 4;
      region = new Rectangle((int)theRegion.getX(), 
        (int)theRegion.getY(), 
        width, height);
    }
    else
    {
      region = theRegion;
    }
  }
  




  public void startCapture()
  {
    captureMovie();
  }
  





  public void startCapture(int numSeconds)
  {
    captureMovie(numSeconds);
  }
  
  public void stopCapture() {}
}
