package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import java.awt.Color;




























public class MainSequentialResponsePanel
  extends MainCompositeResponsePanel
{
  public MainSequentialResponsePanel() {}
  
  protected Color getCustomBackgroundColor()
  {
    return AuthoringToolResources.getColor("DoInOrder");
  }
  
  public void set(CompositeResponse response, AuthoringTool newAuthoringTool) {
    super.set(response, newAuthoringTool);
  }
}
