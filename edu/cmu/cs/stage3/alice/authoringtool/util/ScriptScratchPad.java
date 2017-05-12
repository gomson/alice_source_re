package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;






















public class ScriptScratchPad
  extends JPanel
{
  protected ScriptEditorPane scriptEditorPane = new ScriptEditorPane();
  protected JScrollPane scriptScrollPane = new JScrollPane();
  
  public ScriptScratchPad() {
    jbInit();
    guiInit();
  }
  
  private void guiInit() {
    scriptScrollPane.setViewportView(scriptEditorPane);
    scriptPanel.add(scriptScrollPane, "Center");
    performAllButton.setAction(scriptEditorPane.performAllAction);
    performSelectedButton.setAction(scriptEditorPane.performSelectedAction);
  }
  
  public void setSandbox(Sandbox sandbox) {
    scriptEditorPane.setSandbox(sandbox);
  }
  
  public void setTitleEnabled(boolean b) {
    if (b) {
      add(scratchPadLabel, new GridBagConstraints(0, 0, 3, 1, 0.0D, 0.0D, 17, 0, new Insets(1, 4, 1, 4), 0, 0));
    } else {
      remove(scratchPadLabel);
    }
  }
  




  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel scriptPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton performAllButton = new JButton();
  JButton performSelectedButton = new JButton();
  JLabel scratchPadLabel = new JLabel();
  
  private void jbInit() {
    setLayout(gridBagLayout1);
    scriptPanel.setLayout(borderLayout1);
    performAllButton.setText(Messages.getString("perform_all__Ctrl_F4_"));
    performSelectedButton.setText(Messages.getString("perform_selected__F4_"));
    scratchPadLabel.setFont(new Font("Dialog", 1, 16));
    scratchPadLabel.setText(Messages.getString("Scratch_Pad"));
    add(scriptPanel, new GridBagConstraints(0, 1, 3, 1, 1.0D, 1.0D, 
      10, 1, new Insets(0, 0, 0, 0), 0, 0));
    add(performSelectedButton, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 
      10, 0, new Insets(4, 4, 4, 4), 0, 0));
    add(performAllButton, new GridBagConstraints(1, 2, 1, 1, 0.0D, 0.0D, 
      10, 0, new Insets(4, 4, 4, 4), 0, 0));
    add(scratchPadLabel, new GridBagConstraints(0, 0, 3, 1, 0.0D, 0.0D, 
      17, 0, new Insets(1, 4, 1, 4), 0, 0));
  }
}
