package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import edu.cmu.cs.stage3.alice.authoringtool.Actions;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.lang.Messages;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;




























public class TextBuilderButton
  extends GenericBuilderButton
{
  protected AuthoringTool authoringTool;
  
  public TextBuilderButton() {}
  
  protected String getToolTipString()
  {
    return Messages.getString("Click_to_create_3D_text");
  }
  
  public void set(GalleryViewer.ObjectXmlData dataIn, ImageIcon icon, AuthoringTool authoringTool)
    throws IllegalArgumentException
  {
    super.set(dataIn, icon);
    this.authoringTool = authoringTool;
  }
  
  public void respondToMouse()
  {
    if (authoringTool != null) {
      authoringTool.getActions().add3DTextAction.actionPerformed(null);
    }
  }
}
