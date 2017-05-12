package edu.cmu.cs.stage3.alice.core.clock;

import edu.cmu.cs.stage3.alice.core.Clock;
import edu.cmu.cs.stage3.alice.core.World;


















public class FixedFrameRateClock
  implements Clock
{
  private World m_world;
  private int m_frameRate;
  private double m_dt;
  private double m_time;
  
  public FixedFrameRateClock()
  {
    this(24);
  }
  
  public FixedFrameRateClock(int frameRate) { setFrameRate(frameRate); }
  
  public World getWorld() {
    return m_world;
  }
  
  public void setWorld(World world) { m_world = world; }
  


  public int getFrameRate() { return m_frameRate; }
  
  public void setFrameRate(int frameRate) {
    m_frameRate = frameRate;
    m_dt = (1.0D / m_frameRate);
  }
  
  public void start() {
    m_time = (-m_dt);
    if (m_world != null)
      m_world.start();
  }
  
  public void stop() {
    if (m_world != null) {
      m_world.stop();
    }
  }
  
  public void pause() {}
  
  public void resume() {}
  
  public double getTime() {
    return m_time;
  }
  
  public double getTimeElapsed() { return getTime(); }
  
  public void schedule() {
    if (m_world != null) {
      m_time += m_dt;
      m_world.schedule();
    }
  }
}
