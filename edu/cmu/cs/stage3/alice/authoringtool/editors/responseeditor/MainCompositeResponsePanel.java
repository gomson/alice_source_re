package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel;
import java.awt.Color;





























public class MainCompositeResponsePanel
  extends MainCompositeElementPanel
{
  public MainCompositeResponsePanel() {}
  
  protected Color getCustomBackgroundColor()
  {
    return AuthoringToolResources.getColor("userDefinedResponseEditor");
  }
}
