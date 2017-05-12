package edu.cmu.cs.stage3.alice.moviemaker;

import edu.cmu.cs.stage3.alice.core.Clock;
import edu.cmu.cs.stage3.alice.core.clock.FixedFrameRateClock;
import edu.cmu.cs.stage3.alice.player.DefaultPlayer;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener;
import edu.cmu.cs.stage3.image.ImageIO;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;




public class MovieMaker
  extends DefaultPlayer
{
  private String m_directoryPath;
  private String m_fileName;
  private NumberFormat m_numberFormat;
  private String m_extension;
  private int m_index;
  
  public MovieMaker(Class rendererClass, String directoryPath, String fileName, String localizedPattern, String extension)
  {
    super(rendererClass);
    m_directoryPath = directoryPath;
    m_fileName = fileName;
    m_numberFormat = NumberFormat.getInstance();
    if ((m_numberFormat instanceof DecimalFormat)) {
      DecimalFormat df = (DecimalFormat)m_numberFormat;
      df.applyLocalizedPattern(localizedPattern);
    }
    m_extension = extension;
    m_index = 0;
  }
  
  protected Clock newClock() {
    return new FixedFrameRateClock(30);
  }
  
  protected int getDesiredFrameWidth() {
    return 648;
  }
  
  protected int getDesiredFrameHeight() {
    return 514;
  }
  
  private void getPathForIndex(StringBuffer sb, int index) {
    sb.append(m_directoryPath);
    sb.append(File.separatorChar);
    sb.append(m_fileName);
    sb.append(m_numberFormat.format(index));
    sb.append(".");
    sb.append(m_extension);
  }
  
  public String getPathForIndex(int index) { StringBuffer sb = new StringBuffer();
    getPathForIndex(sb, index);
    return sb.toString();
  }
  
  protected void handleRenderTarget(edu.cmu.cs.stage3.alice.core.RenderTarget renderTarget)
  {
    super.handleRenderTarget(renderTarget);
    renderTarget.addRenderTargetListener(new RenderTargetListener() {
      private int m_index = 0;
      
      public void cleared(RenderTargetEvent renderTargetEvent) {}
      
      public void rendered(RenderTargetEvent renderTargetEvent) { Image image = renderTargetEvent.getRenderTarget().getOffscreenImage();
        String path = getPathForIndex(m_index++);
        try {
          ImageObserver observer = new ImageObserver() {
            public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
              return false;
            }
          };
          BufferedImage bi = new BufferedImage(image.getWidth(observer), image.getHeight(observer), 5);
          bi.getGraphics().drawImage(image, 0, 0, observer);
          ImageIO.store("bmp", new FileOutputStream(path), bi);
        } catch (InterruptedException ie) {
          throw new RuntimeException(ie);
        } catch (IOException ioe) {
          throw new RuntimeException(ioe);
        }
        System.err.println(image);
      }
    });
  }
}
