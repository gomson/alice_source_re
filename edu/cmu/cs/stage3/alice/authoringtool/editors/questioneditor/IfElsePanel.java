package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.IfElseElementPanel;
import edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse;
import java.awt.Color;



























public class IfElsePanel
  extends IfElseElementPanel
{
  public IfElsePanel() {}
  
  protected Color getCustomBackgroundColor()
  {
    return AuthoringToolResources.getColor("IfElseInOrder");
  }
  
  public void set(IfElse r, AuthoringTool authoringToolIn) {
    super.set(r, authoringToolIn);
  }
}
