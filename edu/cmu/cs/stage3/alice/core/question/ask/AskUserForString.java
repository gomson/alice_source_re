package edu.cmu.cs.stage3.alice.core.question.ask;

import edu.cmu.cs.stage3.alice.core.Clock;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.swing.DialogManager;






















public class AskUserForString
  extends Question
{
  public final StringProperty title = new StringProperty(this, "title", "Question");
  public final StringProperty question = new StringProperty(this, "question", "Enter a String:");
  private Clock m_clock;
  
  public AskUserForString() {}
  
  public Class getValueClass() { return String.class; }
  
  public Object getValue()
  {
    if (m_clock != null) {
      m_clock.pause();
    }
    try {
      return DialogManager.showInputDialog(question.getStringValue(), title.getStringValue(), 3);
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
