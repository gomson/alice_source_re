package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import java.awt.Color;




























public class MainParallelResponsePanel
  extends MainCompositeResponsePanel
{
  public MainParallelResponsePanel() {}
  
  protected Color getCustomBackgroundColor()
  {
    return AuthoringToolResources.getColor("DoTogether");
  }
  
  public void set(CompositeResponse response, AuthoringTool newAuthoringTool) {
    super.set(response, newAuthoringTool);
  }
}
