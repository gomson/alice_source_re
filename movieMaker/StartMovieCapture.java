package movieMaker;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.CaptureContentPane;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.ArrayList;
import javax.swing.JPanel;

public class StartMovieCapture
  implements Runnable
{
  private FrameSequencer frameSequencer = null;
  

  private int framesPerSecond = 16;
  

  private Rectangle region = null;
  

  private Thread active = null;
  
  private Rectangle reg = null;
  
  private CaptureContentPane renderContent = null;
  
  private AuthoringTool author = null;
  













  public StartMovieCapture(AuthoringTool a, CaptureContentPane pane, FrameSequencer sequencer, int framesPerSec)
  {
    frameSequencer = sequencer;
    framesPerSecond = framesPerSec;
    author = a;
    renderContent = pane;
  }
  





  public void upDateRectangle()
  {
    reg = new Rectangle(1, 1, 1, 1);
    if (renderContent.getRenderPanel() != null) {
      reg.setSize(renderContent.getRenderPanel().getSize());
    }
    

    Rectangle r = new Rectangle(1, 1, 1, 1);
    r = renderContent.getRenderPanelLocation();
    reg.y = (y + 3);
    int buttonPanelWidth = renderContent.getButtonPanel().getWidth();
    reg.x = (x + (buttonPanelWidth - reg.width) / 2);
    reg.height -= 2;
  }
  
  public void captureMovie() {
    long startTime = 0L;
    long endTime = 0L;
    int timeToSleep = 1000 / framesPerSecond;
    
    Thread current = Thread.currentThread();
    
    upDateRectangle();
    while ((current == active) && (renderContent.getEnd()))
    {
      if ((author.getWorld().isRunning()) && (renderContent.getRunning())) {
        startTime = System.currentTimeMillis();
        try {
          upDateRectangle();
          BufferedImage bi = new Robot().createScreenCapture(reg);
          author.getSoundStorage().frameList.add(new Long(startTime));
          frameSequencer.addFrame(new Picture(bi));
          endTime = System.currentTimeMillis();
          if (endTime - startTime < timeToSleep)
            Thread.sleep(timeToSleep - (endTime - startTime));
        } catch (Exception ex) {
          System.err.println(Messages.getString("Caught_exception_in_StartMovieCapture"));
        }
      }
    }
  }
  



  public void run()
  {
    active = Thread.currentThread();
    captureMovie();
  }
  


  public void stop()
  {
    active = null;
  }
}
