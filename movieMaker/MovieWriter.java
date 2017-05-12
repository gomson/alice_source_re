package movieMaker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;









public class MovieWriter
{
  private String framesDir = null;
  
  private int frameRate = 16;
  
  private String movieName = null;
  
  private String movieDir = null;
  
  private String outputURL = null;
  





  public MovieWriter()
  {
    framesDir = FileChooser.pickADirectory();
    movieDir = framesDir;
    movieName = getMovieName();
    outputURL = getOutputURL();
  }
  






  public MovieWriter(String dirPath)
  {
    framesDir = dirPath;
    movieName = getMovieName();
    movieDir = framesDir;
    outputURL = getOutputURL();
  }
  





  public MovieWriter(int theFrameRate)
  {
    framesDir = FileChooser.pickADirectory();
    frameRate = theFrameRate;
    movieDir = framesDir;
    movieName = getMovieName();
    outputURL = getOutputURL();
  }
  








  public MovieWriter(int theFrameRate, String theFramesDir)
  {
    framesDir = theFramesDir;
    frameRate = theFrameRate;
    movieDir = framesDir;
    movieName = getMovieName();
    outputURL = getOutputURL();
  }
  








  public MovieWriter(int theFrameRate, String theFramesDir, String theMovieName)
  {
    framesDir = theFramesDir;
    frameRate = theFrameRate;
    movieDir = framesDir;
    movieName = theMovieName;
    outputURL = getOutputURL();
  }
  









  public MovieWriter(int theFrameRate, String theFramesDir, String theMovieName, String theMovieDir)
  {
    framesDir = theFramesDir;
    frameRate = theFrameRate;
    movieDir = theMovieDir;
    movieName = theMovieName;
    outputURL = getOutputURL();
  }
  















  public MovieWriter(String theFramesDir, int theFrameRate, String theOutputURL)
  {
    framesDir = theFramesDir;
    frameRate = theFrameRate;
    outputURL = theOutputURL;
    movieDir = theFramesDir;
  }
  








  private String getMovieName()
  {
    File dir = new File(framesDir);
    return dir.getName();
  }
  





  private String getOutputURL()
  {
    File dir = null;
    URL myURL = null;
    if (framesDir != null) {
      try
      {
        dir = new File(movieDir + movieName);
        myURL = dir.toURL();
      }
      catch (Exception localException) {}
    }
    return myURL.toString();
  }
  





  public List getFrameNames()
  {
    File dir = new File(framesDir);
    String[] filesArray = dir.list();
    List files = new ArrayList();
    long lenFirst = 0L;
    for (int x = 0; x < filesArray.length; x++)
    {
      String fileName = filesArray[x];
      
      if (fileName.indexOf(".jpg") >= 0)
      {
        File f = new File(framesDir + fileName);
        
        if ((lenFirst == 0L) || 
          (f.length() > lenFirst / 2L))
        {
          try
          {
            BufferedImage i = ImageIO.read(f);
            files.add(framesDir + fileName);
          }
          catch (Exception localException) {}
        }
        
        if (lenFirst == 0L)
          lenFirst = f.length();
      }
    }
    return files;
  }
  



  public boolean writeAVI()
  {
    JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
    List frameNames = getFrameNames();
    Picture p = new Picture((String)frameNames.get(0));
    return imageToMovie.doItAVI(p.getWidth(), p.getHeight(), 
      frameRate, frameNames, outputURL + ".avi");
  }
  



  public boolean writeQuicktime()
  {
    JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
    List frameNames = getFrameNames();
    Picture p = new Picture((String)frameNames.get(0));
    return imageToMovie.doItQuicktime(p.getWidth(), p.getHeight(), 
      frameRate, frameNames, outputURL + ".mov");
  }
}
