package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.ForEachElementPanel;
import edu.cmu.cs.stage3.alice.core.response.ForEachInOrder;
import java.awt.Color;



























public class ForEachInListSequentialLoopPanel
  extends ForEachElementPanel
{
  public ForEachInListSequentialLoopPanel() {}
  
  public void set(ForEachInOrder r, AuthoringTool authoringToolIn)
  {
    super.set(r, authoringToolIn);
  }
  
  protected Color getCustomBackgroundColor() {
    return AuthoringToolResources.getColor("ForEachInOrder");
  }
}
