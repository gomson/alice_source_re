package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.LoopNElementPanel;
import edu.cmu.cs.stage3.alice.core.response.LoopNInOrder;
import java.awt.Color;


























public class CountLoopPanel
  extends LoopNElementPanel
{
  public CountLoopPanel()
  {
    backgroundColor = getCustomBackgroundColor();
  }
  
  public void set(LoopNInOrder r, AuthoringTool authoringToolIn)
  {
    super.set(r, authoringToolIn);
  }
  
  protected Color getCustomBackgroundColor() {
    return AuthoringToolResources.getColor("LoopNInOrder");
  }
}
