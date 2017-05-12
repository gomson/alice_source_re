package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.WhileElementPanel;
import edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder;
import java.awt.Color;


























public class LoopIfTrueResponsePanel
  extends WhileElementPanel
{
  public LoopIfTrueResponsePanel() {}
  
  public void set(WhileLoopInOrder r, AuthoringTool authoringToolIn)
  {
    super.set(r, authoringToolIn);
  }
  
  protected Color getCustomBackgroundColor() {
    return AuthoringToolResources.getColor("WhileLoopInOrder");
  }
}
