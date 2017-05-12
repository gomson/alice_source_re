package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.lang.Messages;






















public class NewQuestionContentPane
  extends NewNamedTypedElementContentPane
{
  public NewQuestionContentPane() {}
  
  public String getTitle()
  {
    String cappedQuestionString = AuthoringToolResources.QUESTION_STRING
      .substring(0, 1).toUpperCase() + 
      AuthoringToolResources.QUESTION_STRING
      .substring(1);
    return Messages.getString("New_") + cappedQuestionString;
  }
  
  protected void initVariables() {
    listsOnly = false;
    showValue = false;
  }
}
