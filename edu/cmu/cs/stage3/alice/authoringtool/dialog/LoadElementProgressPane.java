package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.progress.ProgressCancelException;
import edu.cmu.cs.stage3.progress.ProgressPane;
import edu.cmu.cs.stage3.swing.DialogManager;













public class LoadElementProgressPane
  extends ProgressPane
{
  private DirectoryTreeLoader m_loader;
  private Element m_externalRoot;
  private Element m_loadedElement;
  
  public LoadElementProgressPane(String title, String preDescription)
  {
    super(title, preDescription);
  }
  
  protected void construct() throws ProgressCancelException
  {
    m_loadedElement = null;
    try {
      m_loadedElement = Element.load(
        m_loader, m_externalRoot, this);
    } catch (ProgressCancelException pce) {
      throw pce;


    }
    catch (UnresolvablePropertyReferencesException upre)
    {


      StringBuffer sb = new StringBuffer();
      sb.append(
        Messages.getString("WARNING__unable_to_resolve_references___n"));
      PropertyReference[] propertyReferences = upre
        .getPropertyReferences();
      for (int i = 0; i < propertyReferences.length; i++) {
        Property property = propertyReferences[i]
          .getProperty();
        sb.append("    ");
        sb.append(property.getOwner().toString());
        sb.append('[');
        sb.append(property.getName());
        sb.append("] -> ");
        sb.append(propertyReferences[i].getCriterion());
        sb.append('\n');
      }
      sb.append('\n');
      sb.append(
        Messages.getString("Would_you_like_to_continue__setting_all_values_to_None_"));
      if (DialogManager.showConfirmDialog(
        sb.toString(), Messages.getString("Unable_to_load_world"), 
        0) == 0) {
        m_loadedElement = upre.getElement();
      }
    }
    catch (Throwable t) {
      AuthoringTool.showErrorDialog(
        Messages.getString("Unable_to_load_world"), t);
    }
  }
  




















  public void setLoader(DirectoryTreeLoader loader)
  {
    m_loader = loader;
  }
  
  public void setExternalRoot(Element externalRoot)
  {
    m_externalRoot = externalRoot;
  }
  
  public Element getLoadedElement() {
    return m_loadedElement;
  }
}
