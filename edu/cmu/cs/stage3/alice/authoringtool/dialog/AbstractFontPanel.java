package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.util.FontChooser;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
















public abstract class AbstractFontPanel
  extends JPanel
{
  protected FontChooser fontChooser = new FontChooser(
    false, false);
  protected JTextArea textArea = new JTextArea();
  
  public AbstractFontPanel(String text, boolean isTextAreaEnabled) {
    fontChooser.setFontSize(28);
    fontChooser.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ev) {
        textArea.setFont(fontChooser.getChosenFont());
      }
      
    });
    setLayout(new GridBagLayout());
    textArea.setText(text);
    textArea.setEnabled(isTextAreaEnabled);
    textArea.selectAll();
    textArea.setFont(fontChooser.getChosenFont());
    
    JScrollPane textScrollPane = new JScrollPane();
    textScrollPane.getViewport().add(textArea, null);
    
    add(new JLabel(Messages.getString("Text_")), 
      new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      12, 0, 
      new Insets(0, 0, 4, 4), 0, 0));
    add(textScrollPane, new GridBagConstraints(1, 0, 1, 1, 1.0D, 1.0D, 
      18, 1, 
      new Insets(0, 0, 4, 0), 0, 0));
    add(new JLabel(Messages.getString("Font_")), 
      new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 
      12, 0, 
      new Insets(0, 0, 0, 4), 0, 0));
    add(fontChooser, new GridBagConstraints(1, 1, 1, 1, 1.0D, 0.0D, 
      18, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    
    setPreferredSize(new Dimension(500, 200));
  }
  
  public Font getChosenFont() {
    return fontChooser.getChosenFont();
  }
}
