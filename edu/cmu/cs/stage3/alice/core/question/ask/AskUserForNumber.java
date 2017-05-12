package edu.cmu.cs.stage3.alice.core.question.ask;

import edu.cmu.cs.stage3.alice.core.Clock;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.question.NumberQuestion;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;























public class AskUserForNumber
  extends NumberQuestion
{
  public final StringProperty title = new StringProperty(this, "title", "Question");
  public final StringProperty question = new StringProperty(this, "question", "Enter a Number:");
  private Clock m_clock;
  
  public AskUserForNumber() {}
  
  public Object getValue() { if (m_clock != null) {
      m_clock.pause();
    }
    try {
      String string = DialogManager.showInputDialog(question.getStringValue(), title.getStringValue(), 3);
      Double localDouble; if (string != null) {
        if ((string.matches("-?\\d+")) || (string.matches("-?\\d*\\.\\d+"))) {
          return new Double(string);
        }
        Pattern p = Pattern.compile("(-?\\d*\\.\\d+)|(-?\\d+)");
        Matcher m = p.matcher(string);
        if (m.find()) {
          String t = m.group();
          return new Double(t);
        }
      }
      
      return new Double(0.0D);
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
