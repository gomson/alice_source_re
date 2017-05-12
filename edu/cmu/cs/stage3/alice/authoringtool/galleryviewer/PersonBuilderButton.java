package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import edu.cmu.cs.stage3.caitlin.personbuilder.PersonBuilder;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.progress.ProgressCancelException;
import edu.cmu.cs.stage3.progress.ProgressPane;
import edu.cmu.cs.stage3.swing.DialogManager;
















public class PersonBuilderButton
  extends GenericBuilderButton
{
  public PersonBuilderButton() {}
  
  private PersonBuilder m_personBuilder = null;
  
  protected String getToolTipString() { return Messages.getString("Click_to_create_your_own_custom_character"); }
  
  public void respondToMouse()
  {
    if ((mainViewer != null) && (isEnabled())) {
      if (m_personBuilder == null) {
        ProgressPane progressPane = new ProgressPane(Messages.getString("Progress"), Messages.getString("Loading_Character_Builder___"))
        {
          protected void construct() throws ProgressCancelException {
            m_personBuilder = new PersonBuilder(data.name, this);
          }
        };
        DialogManager.showDialog(progressPane);
      } else {
        m_personBuilder.reset();
      }
      if (m_personBuilder != null) {
        int result = DialogManager.showConfirmDialog(m_personBuilder, Messages.getString("Person_Builder"), 2, -1);
        if (result == 0) {
          mainViewer.addObject(m_personBuilder.getModel());
        }
      }
    }
  }
}
