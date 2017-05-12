package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.ForEachElementPanel;
import edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach;
import java.awt.Color;



























public class ForEachPanel
  extends ForEachElementPanel
{
  public ForEachPanel() {}
  
  protected Color getCustomBackgroundColor()
  {
    return AuthoringToolResources.getColor("ForEachInOrder");
  }
  
  public void set(ForEach r, AuthoringTool authoringToolIn)
  {
    super.set(r, authoringToolIn);
  }
}
