package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.LoopNElementPanel;
import edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN;
import java.awt.Color;



























public class LoopNPanel
  extends LoopNElementPanel
{
  public LoopNPanel() {}
  
  protected Color getCustomBackgroundColor()
  {
    return AuthoringToolResources.getColor("LoopNInOrder");
  }
  
  public void set(LoopN r, AuthoringTool authoringToolIn) {
    super.set(r, authoringToolIn);
  }
}
