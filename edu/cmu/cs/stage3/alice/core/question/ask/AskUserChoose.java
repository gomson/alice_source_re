package edu.cmu.cs.stage3.alice.core.question.ask;

import edu.cmu.cs.stage3.alice.core.Clock;
import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion;






















public class AskUserChoose
  extends ListObjectQuestion
{
  public final StringProperty title = new StringProperty(this, "title", "Question");
  public final StringProperty question = new StringProperty(this, "question", "Pick an Item:");
  private Clock m_clock;
  
  public AskUserChoose() {}
  
  public Object getValue(List listValue) { if (m_clock != null) {
      m_clock.pause();
    }
    





    if (m_clock != null) {
      m_clock.resume();
    }
    return null;
  }
  




  protected void started(World world, double time)
  {
    super.started(world, time);
    if (world != null)
      m_clock = world.getClock();
  }
  
  protected void stopped(World world, double time) {
    m_clock = null;
    super.stopped(world, time);
  }
}
