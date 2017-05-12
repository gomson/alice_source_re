package edu.cmu.cs.stage3.alice.core.clock;

import edu.cmu.cs.stage3.alice.core.Clock;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;


















public class DefaultClock
  implements Clock
{
  private World m_world;
  private double m_speed = 1.0D;
  private double m_time;
  
  public DefaultClock() {}
  
  public World getWorld() {
    return m_world;
  }
  
  public void setWorld(World world) { m_world = world; }
  
  private long m_whenPrev;
  private int m_pauseCount;
  public double getSpeed() { return m_speed; }
  

  public void setSpeed(double speed) { m_speed = speed; }
  
  public void start()
  {
    m_pauseCount = 0;
    m_whenPrev = System.currentTimeMillis();
    if (m_world != null)
      m_world.start();
  }
  
  public void stop() {
    m_pauseCount = 0;
    m_whenPrev = -1L;
    if (m_world != null) {
      m_world.stop();
    }
  }
  
  public void pause() { m_pauseCount += 1; }
  
  public void resume() {
    m_pauseCount -= 1;
    if (m_pauseCount == 0)
      m_whenPrev = System.currentTimeMillis();
  }
  
  private void updateTime() {
    long whenCurr = System.currentTimeMillis();
    long whenDelta = whenCurr - m_whenPrev;
    if (whenDelta > 0L) {
      double dt = whenDelta * 0.001D;
      dt *= m_speed;
      if (m_world != null) {
        dt *= m_world.speedMultiplier.doubleValue();
      }
      m_time += dt;
    }
    m_whenPrev = whenCurr;
  }
  
  public double getTime() {
    return m_time;
  }
  
  public double getTimeElapsed() { return getTime(); }
  
  public boolean isPaused()
  {
    return m_pauseCount == 0;
  }
  
  public void schedule() {
    if ((m_pauseCount == 0) && 
      (m_world != null)) {
      updateTime();
      m_world.schedule();
    }
  }
}
