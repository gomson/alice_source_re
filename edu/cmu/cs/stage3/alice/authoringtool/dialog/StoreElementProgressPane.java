package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.progress.ProgressCancelException;
import edu.cmu.cs.stage3.progress.ProgressPane;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
















public class StoreElementProgressPane
  extends ProgressPane
{
  private Element m_element;
  private File m_file;
  private Dictionary m_filnameToByteArrayMap;
  private boolean m_wasSuccessful = false;
  
  public StoreElementProgressPane(String title, String preDescription) {
    super(title, preDescription);
  }
  
  public boolean wasSuccessful() {
    return m_wasSuccessful;
  }
  
  protected void construct() throws ProgressCancelException
  {
    m_wasSuccessful = false;
    try {
      m_element.store(m_file, this, m_filnameToByteArrayMap);
      m_wasSuccessful = true;
    } catch (ProgressCancelException pce) {
      throw pce;
    } catch (Throwable t) {
      StringBuffer sb = new StringBuffer();
      sb.append(
        Messages.getString("An_error_has_occurred_while_attempting_to_save_your_world__n_n"));
      sb.append(
        Messages.getString("This_is_a_critical_situation_that_needs_to_be_dealt_with_immediately__n_n"));
      if ((t instanceof IOException)) {
        sb.append(
          Messages.getString("This_may_be_the_result_of_not_having_enough_space_on_the_target_drive__n"));
        sb.append(
          Messages.getString("If_possible__n____attempt_to_save_your_world_to_a_different_drive__or_n____free_up_some_space_and__Save_As__to_a_different_file__n_n"));
        sb.append(Messages.getString("NOTE__If_unsuccessful__please"));
      } else {
        sb.append(Messages.getString("NOTE__Please"));
      }
      sb.append(" " + 
        Messages.getString("check_for_a_directory_co_located_with__nyour_world_named__Backups_of__YourWorldNameHere___which__nshould_contain_previously_saved_versions_of_your_world__n"));
      
      sb.append(
        Messages.getString("_nWe_at_the_Alice_Team_apologize_for_any_work_you_have_lost_n"));
      sb.append(
        Messages.getString("_nPlease_accept_our_sincerest_apologies___The_Alice_Team_"));
      
      AuthoringTool.showErrorDialog(sb.toString(), t);
    }
  }
  
  public void setElement(Element element) {
    m_element = element;
  }
  
  public void setFile(File file) {
    m_file = file;
  }
  
  public void setFilnameToByteArrayMap(Dictionary filnameToByteArrayMap)
  {
    m_filnameToByteArrayMap = filnameToByteArrayMap;
  }
}
