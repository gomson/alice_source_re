package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.IfElseElementPanel;
import edu.cmu.cs.stage3.alice.core.response.IfElseInOrder;
import java.awt.Color;


























public class ConditionalResponsePanel
  extends IfElseElementPanel
{
  public ConditionalResponsePanel()
  {
    backgroundColor = getCustomBackgroundColor();
  }
  
  public void set(IfElseInOrder r, AuthoringTool authoringToolIn) {
    super.set(r, authoringToolIn);
  }
  
  protected Color getCustomBackgroundColor() {
    return AuthoringToolResources.getColor("IfElseInOrder");
  }
}
