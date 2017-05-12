package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.WhileElementPanel;
import edu.cmu.cs.stage3.alice.core.question.userdefined.While;
import java.awt.Color;



























public class WhilePanel
  extends WhileElementPanel
{
  public WhilePanel() {}
  
  protected Color getCustomBackgroundColor()
  {
    return AuthoringToolResources.getColor("WhileLoopInOrder");
  }
  
  public void set(While r, AuthoringTool authoringToolIn) {
    super.set(r, authoringToolIn);
  }
}
