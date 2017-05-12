package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.response.ForEachTogether;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;





























public class ForAllTogetherResponsePanel
  extends ForEachInListSequentialLoopPanel
{
  public ForAllTogetherResponsePanel()
  {
    headerText = Messages.getString("For_all");
    endHeaderText = Messages.getString("together");
    middleHeaderText = Messages.getString("__every");
  }
  
  public void set(ForEachTogether r, AuthoringTool authoringToolIn) {
    super.set(r, authoringToolIn);
  }
  
  protected Color getCustomBackgroundColor() {
    return AuthoringToolResources.getColor("ForAllTogether");
  }
}
