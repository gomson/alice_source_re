package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;






public class AnimationPanel
  extends JComponent
{
  private List imageList = new ArrayList();
  

  private List nameList = new ArrayList();
  

  private int currIndex = 0;
  

  private int framesPerSec = 16;
  





  public AnimationPanel()
  {
    setSize(new Dimension(100, 100));
  }
  




  public AnimationPanel(List pictList)
  {
    Image image = null;
    Picture picture = null;
    for (int i = 0; i < pictList.size(); i++)
    {
      picture = (Picture)pictList.get(i);
      nameList.add(picture.getFileName());
      image = picture.getImage();
      imageList.add(image);
    }
    
    BufferedImage bi = (BufferedImage)image;
    int width = bi.getWidth();
    int height = bi.getHeight();
    setSize(new Dimension(width, height));
    setMinimumSize(new Dimension(width, height));
    setPreferredSize(new Dimension(width, height));
  }
  






  public AnimationPanel(String directory)
  {
    File dirObj = new File(directory);
    String[] fileArray = dirObj.list();
    ImageIcon imageIcon = null;
    Image image = null;
    

    for (int i = 0; i < fileArray.length; i++)
    {
      if (fileArray[i].indexOf(".jpg") >= 0)
      {

        imageIcon = new ImageIcon(directory + fileArray[i]);
        nameList.add(directory + fileArray[i]);
        imageList.add(imageIcon.getImage());
      }
    }
    

    if (imageIcon != null)
    {
      image = (Image)imageList.get(0);
      int width = image.getWidth(null);
      int height = image.getHeight(null);
      setSize(new Dimension(width, 
        height));
      setMinimumSize(new Dimension(width, 
        height));
      setPreferredSize(new Dimension(width, 
        height));
    }
  }
  









  public AnimationPanel(String directory, int theFramesPerSec)
  {
    this(directory);
    framesPerSec = theFramesPerSec;
  }
  




  public int getCurrIndex()
  {
    return currIndex;
  }
  



  public void setFramesPerSec(int numFramesPerSec)
  {
    framesPerSec = numFramesPerSec;
  }
  



  public int getFramesPerSec()
  {
    return framesPerSec;
  }
  



  public void add(Picture picture)
  {
    Image image = picture.getImage();
    imageList.add(image);
    nameList.add(picture.getFileName());
  }
  



  public void showNext()
  {
    currIndex += 1;
    if (currIndex == imageList.size())
      currIndex = 0;
    draw(getGraphics());
  }
  



  public void showPrev()
  {
    currIndex -= 1;
    if (currIndex < 0)
      currIndex = (imageList.size() - 1);
    draw(getGraphics());
  }
  



  public void showAll()
  {
    Graphics g = null;
    long startTime = 0L;
    long endTime = 0L;
    int timeToSleep = 1000 / framesPerSec;
    for (int i = 0; i < imageList.size(); i++)
    {
      startTime = System.currentTimeMillis();
      currIndex = i;
      g = getGraphics();
      draw(g);
      g.dispose();
      endTime = System.currentTimeMillis();
      
      try
      {
        if (endTime - startTime < timeToSleep) {
          Thread.sleep(timeToSleep - (endTime - startTime));
        }
      }
      catch (InterruptedException localInterruptedException) {}
      currIndex = (imageList.size() - 1);
    }
  }
  



  public void showAllFromCurrent()
  {
    Graphics g = null;
    int timeToSleep = 1000 / framesPerSec;
    for (; currIndex < imageList.size(); currIndex += 1)
    {

      g = getGraphics();
      draw(g);
      g.dispose();
      
      try
      {
        Thread.sleep(timeToSleep);
      }
      catch (InterruptedException localInterruptedException) {}
    }
    
    currIndex = (imageList.size() - 1);
  }
  




  public void removeAllBefore()
  {
    File f = null;
    boolean result = false;
    for (int i = 0; i <= currIndex; i++)
    {
      f = new File((String)nameList.get(i));
      result = f.delete();
      if (!result)
        System.out.println(Messages.getString("trouble_deleting_") + 
          nameList.get(i));
      imageList.remove(0);
    }
  }
  



  public void removeAllAfter()
  {
    int i = currIndex + 1;
    int index = i;
    File f = null;
    boolean result = false;
    while (i < imageList.size())
    {
      f = new File((String)nameList.get(index++));
      result = f.delete();
      if (!result)
        System.out.println(Messages.getString("trouble_deleting_") + 
          nameList.get(index - 1));
      imageList.remove(i);
    }
  }
  




  public void draw(Graphics g)
  {
    Image image = (Image)imageList.get(currIndex);
    g.drawImage(image, 0, 0, this);
  }
  




  public void paintComponent(Graphics g)
  {
    if (imageList.size() == 0) {
      g.drawString(Messages.getString("No_images_yet__"), 20, 20);
    } else {
      draw(g);
    }
  }
  


  public static void main(String[] args)
  {
    JFrame frame = new JFrame();
    AnimationPanel panel = new AnimationPanel("c:/intro-prog-java/mediasources/fish/");
    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);
    panel.showAll();
  }
}
