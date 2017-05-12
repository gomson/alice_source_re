package edu.cmu.cs.stage3.alice.core.question.ask;

import edu.cmu.cs.stage3.alice.core.Clock;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.question.BooleanQuestion;
import edu.cmu.cs.stage3.swing.DialogManager;






















public class AskUserYesNo
  extends BooleanQuestion
{
  public final StringProperty title = new StringProperty(this, "title", "Question");
  public final StringProperty question = new StringProperty(this, "question", "Yes or No?");
  private Clock m_clock;
  
  public AskUserYesNo() {}
  
  public Object getValue() { if (m_clock != null) {
      m_clock.pause();
    }
    try {
      int result = DialogManager.showConfirmDialog(question.getStringValue(), title.getStringValue(), 0, 3);
      return new Boolean(result == 0);
    } finally {
      if (m_clock != null) {
        m_clock.resume();
      }
    }
  }
  
  protected void started(World world, double time) {
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
