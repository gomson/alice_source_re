package movieMaker;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract interface VideoCapturer
{
  public abstract BufferedImage captureScreen()
    throws Exception;
  
  public abstract void setRegion(Rectangle paramRectangle);
  
  public abstract void startCapture();
  
  public abstract void startCapture(int paramInt);
  
  public abstract void stopCapture();
  
  public abstract void playMovie();
  
  public abstract FrameSequencer getFrameSequencer();
  
  public abstract Rectangle getRegion();
  
  public abstract int getFramesPerSecond();
  
  public abstract void setFramesPerSecond(int paramInt);
}
