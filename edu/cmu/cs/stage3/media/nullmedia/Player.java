package edu.cmu.cs.stage3.media.nullmedia;
import edu.cmu.cs.stage3.media.AbstractPlayer;

public class Player extends AbstractPlayer { private double m_currentTime = 0.0D;
  private int m_state = 300;
  
  public Player(DataSource dataSource) { super(dataSource); }
  
  public double waitForTimeRemaining(long timeout) {
    return m_currentTime - getActualEndTime();
  }
  
  public int getState() { return m_state; }
  
  private void setState(int state) {
    if (m_state != state) {
      m_state = state;
      fireStateChanged();
    }
  }
  
  public double getCurrentTime() {
    return m_currentTime;
  }
  
  public void setCurrentTime(double currentTime) { m_currentTime = currentTime; }
  
  public double getDuration()
  {
    return getDataSource().getDuration(true);
  }
  
  private double getActualEndTime() {
    double endTime = getEndTime();
    if (Double.isNaN(endTime)) {
      endTime = getDuration();
    }
    return endTime;
  }
  
  private void setCurrentTimeToEnd() { setCurrentTime(getActualEndTime());
    setState(300);
    fireEndReached();
  }
  
  public void setVolumeLevel(float volumeLevel) {}
  
  public void setRate(float rate) {}
  
  public void setPan(float pan) {}
  
  public void prefetch() {}
  
  public void realize() {}
  
  public void start() {
    setState(300);
    setState(600);
    final double timeRemaining = waitForTimeRemaining(0L);
    if (Double.isNaN(timeRemaining)) {
      setCurrentTimeToEnd();





    }
    else
    {




      new Thread()
      {
        public void run()
        {
          try
          {
            Thread.sleep((timeRemaining * 1000.0D));
          }
          catch (InterruptedException localInterruptedException) {}finally
          {
            Player.this.setCurrentTimeToEnd();
          }
        }
      }.start(); }
  }
  
  public void stop() {
    setState(300);
  }
}
