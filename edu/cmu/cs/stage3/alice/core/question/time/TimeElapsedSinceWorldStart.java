package edu.cmu.cs.stage3.alice.core.question.time;

import edu.cmu.cs.stage3.alice.core.Clock;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.question.NumberQuestion;















public class TimeElapsedSinceWorldStart
  extends NumberQuestion
{
  private Clock m_clock;
  
  public TimeElapsedSinceWorldStart() {}
  
  public Object getValue()
  {
    if (m_clock != null) {
      return new Double(m_clock.getTimeElapsed());
    }
    return null;
  }
  
  protected void started(World world, double time)
  {
    super.started(world, time);
    if (world != null) {
      m_clock = world.getClock();
    }
  }
  
  protected void stopped(World world, double time) {
    m_clock = null;
    super.stopped(world, time);
  }
}
