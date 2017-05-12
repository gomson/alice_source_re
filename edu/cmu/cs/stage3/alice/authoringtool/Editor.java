package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;
import edu.cmu.cs.stage3.lang.Messages;
import javax.swing.JComponent;
























public abstract interface Editor
  extends AuthoringToolStateListener
{
  public static final String editorName = Messages.getString("Unnamed_Editor");
  
  public abstract JComponent getJComponent();
  
  public abstract Object getObject();
  
  public abstract void setAuthoringTool(AuthoringTool paramAuthoringTool);
}
