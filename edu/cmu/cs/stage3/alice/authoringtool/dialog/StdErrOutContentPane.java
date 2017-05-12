package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.alice.authoringtool.util.StyleStream;
import edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.GridBagConstraints;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
















public class StdErrOutContentPane
  extends AliceAlertContentPane
{
  public static final int HISTORY_MODE = 2;
  protected boolean errorDialog = false;
  
  protected AuthoringTool authoringTool;
  
  protected OutputComponent errOutputComponent;
  
  protected OutputComponent stdOutputComponent;
  protected String lastError;
  protected String lastOutput;
  protected String titleString;
  protected boolean isShowing = false;
  protected boolean errorContentAdded = false;
  protected boolean textContentAdded = false;
  protected boolean shouldListenToErrors = true;
  protected boolean shouldListenToPrint = true;
  
  protected class ErrOutputDocumentListener implements DocumentListener {
    protected ErrOutputDocumentListener() {}
    
    public void insertUpdate(DocumentEvent ev) {
      try { lastError = ev.getDocument().getText(ev.getOffset(), 
          ev.getLength());
        
        if (lastError.trim().startsWith(Messages.getString("Unable_to_handle_format"))) {
          lastError = 
          






            (Messages.getString("_n_nYour_sound_file_cannot_be_played_in_Alice__n") + Messages.getString("Please_find_an_audio_editor_to_convert_the_file_to_one_with_a_PCM_encoding__n") + Messages.getString("See_the_tutorial_on_converting_sound_files_at_our_Alice_website__n") + Messages.getString("Right_click_to_clear_the_messages_here__n_n") + lastError);
        }
        detailTextPane.getDocument().insertString(
          detailTextPane.getDocument().getLength(), lastError, 
          detailTextPane.stdErrStyle);
        
        if (lastError.startsWith("java.lang.ClassCastException")) {
          lastError = "OOPS!! Looks like we have a slight layout problem. Not a big deal, press OK to continue. \n\n";
          detailTextPane.getDocument().insertString(0, lastError, detailTextPane.stdErrStyle);
        }
      }
      catch (Exception localException) {}
      errorContentAdded = true;
      update();
    }
    
    public void removeUpdate(DocumentEvent ev) {
      update();
    }
    
    public void changedUpdate(DocumentEvent ev) {
      update();
    }
    
    private void update() {
      if (shouldListenToErrors) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (!isShowing) {
              isShowing = true;
              int i = 
                DialogManager.showDialog(StdErrOutContentPane.this);
            }
          }
        });
      }
    }
  }
  
  protected class StdOutputDocumentListener implements DocumentListener {
    protected StdOutputDocumentListener() {}
    
    public void insertUpdate(DocumentEvent ev) {
      try {
        lastOutput = ev.getDocument().getText(ev.getOffset(), 
          ev.getLength());
        detailTextPane.getDocument().insertString(
          detailTextPane.getDocument().getLength(), lastOutput, 
          detailTextPane.stdOutStyle);
      }
      catch (Exception localException) {}
      textContentAdded = true;
      update();
    }
    
    public void removeUpdate(DocumentEvent ev) {
      update();
    }
    
    public void changedUpdate(DocumentEvent ev) {
      update();
    }
    
    private void update() {
      if (shouldListenToPrint) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (!isShowing) {
              isShowing = true;
              int i = 
                DialogManager.showDialog(StdErrOutContentPane.this);
            }
          }
        });
      }
    }
  }
  

  public StdErrOutContentPane(AuthoringTool authoringTool, boolean errorDialog)
  {
    if (errorDialog) {
      titleString = Messages.getString("Alice___Error_Console");
      this.authoringTool = authoringTool;
      errOutputComponent = authoringTool.getStdErrOutputComponent();
      writeGenericAliceHeaderToTextPane();
      errOutputComponent.getTextPane().getDocument()
        .addDocumentListener(new ErrOutputDocumentListener());
    } else {
      titleString = Messages.getString("Alice___Text_Output");
      this.authoringTool = authoringTool;
      stdOutputComponent = authoringTool.getStdOutOutputComponent();
      
      stdOutputComponent.getTextPane().getDocument()
        .addDocumentListener(new StdOutputDocumentListener());
    }
  }
  
  protected void writeGenericAliceHeaderToTextPane() {
    detailTextPane.setText("");
    detailStream.println(Messages.getString("Alice_version__") + 
      JAlice.getVersion());
    String[] systemProperties = { "os.name", "os.version", "os.arch", 
      "java.vm.name", "java.vm.version", "user.dir" };
    for (int i = 0; i < systemProperties.length; i++) {
      detailStream.println(systemProperties[i] + ": " + 
        System.getProperty(systemProperties[i]));
    }
    detailStream.println();
  }
  
  public void preDialogShow(JDialog parentDialog) {
    super.preDialogShow(parentDialog);
  }
  
  public void stopReactingToPrint() {
    shouldListenToPrint = false;
  }
  
  public void startReactingToPrint()
  {
    if (stdOutputComponent != null) {
      stdOutputComponent.stdOutStream.flush();
    }
    shouldListenToPrint = true;
  }
  
  public void stopReactingToError() {
    shouldListenToErrors = false;
  }
  
  public void startReactingToError() {
    if (errOutputComponent != null) {
      errOutputComponent.stdErrStream.flush();
    }
    
    shouldListenToErrors = true;
  }
  
  public void postDialogShow(JDialog parentDialog) {
    isShowing = false;
    setMode(0);
    super.postDialogShow(parentDialog);
  }
  
  public int showStdErrDialog() {
    if (!isShowing) {
      isShowing = true;
      errorDialog = true;
      return DialogManager.showDialog(this);
    }
    return -1;
  }
  
  public int showStdOutDialog()
  {
    if (!isShowing) {
      isShowing = true;
      errorDialog = false;
      return DialogManager.showDialog(this);
    }
    return -1;
  }
  
  public String getTitle()
  {
    return titleString;
  }
  
  protected void setHistoryDetail()
  {
    add(detailScrollPane, "Center");
    buttonPanel.removeAll();
    buttonConstraints.gridx = 0;
    buttonPanel.add(cancelButton, buttonConstraints);
    

    buttonConstraints.gridx += 1;
    buttonPanel.add(copyButton, buttonConstraints);
    buttonConstraints.gridx += 1;
    buttonPanel.add(cancelButton, buttonConstraints);
    buttonConstraints.gridx += 1;
    glueConstraints.gridx = buttonConstraints.gridx;
    buttonPanel.add(buttonGlue, glueConstraints);
    
    if (errorContentAdded) {
      messageLabel.setText(
        Messages.getString("Something_bad_has_occurred_"));
    } else if (textContentAdded) {
      messageLabel.setText(
        Messages.getString("Nothing_bad_has_occurred_"));
    } else {
      messageLabel.setText(
        Messages.getString("Nothing_bad_has_occurred_"));
    }
  }
  
  protected void setLessDetail() {
    super.setLessDetail();
    messageLabel.setText(
      Messages.getString("An_unknown_error_has_occurred_"));
  }
  
  protected void setMoreDetail() {
    super.setMoreDetail();
    messageLabel.setText(
      Messages.getString("An_unknown_error_has_occurred_"));
  }
  
  protected void handleModeSwitch(int mode) {
    if (mode == 0) {
      setLessDetail();
    } else if (mode == 1) {
      setMoreDetail();
    } else if (mode == 2) {
      setHistoryDetail();
    } else {
      throw new IllegalArgumentException(
        Messages.getString("Illegal_mode__") + mode);
    }
    packDialog();
  }
}
