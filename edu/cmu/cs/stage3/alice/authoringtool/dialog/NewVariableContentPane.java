package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.lang.Messages;























public class NewVariableContentPane
  extends NewNamedTypedElementContentPane
{
  public NewVariableContentPane() {}
  
  private String m_title = Messages.getString("New_Variable");
  
  public String getTitle() {
    return m_title;
  }
  
  public void setTitle(String title) {
    m_title = title;
  }
  
  protected void initVariables() {
    listsOnly = false;
    showValue = true;
  }
}
