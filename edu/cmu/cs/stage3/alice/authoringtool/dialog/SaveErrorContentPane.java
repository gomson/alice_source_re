package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Font;
import javax.swing.JTextArea;




















public class SaveErrorContentPane
  extends ErrorContentPane
{
  public SaveErrorContentPane() {}
  
  public String getTitle()
  {
    return Messages.getString("Alice___Save_Error___CRITICAL");
  }
  
  protected void init() {
    super.init();
    messageLabel.setFont(new Font("SansSerif", 1, 
      16));
    messageLabel.setPreferredSize(null);
    messageLabel.setLineWrap(false);
  }
}
