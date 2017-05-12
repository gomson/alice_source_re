package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;













public class CapturedImageContentPane
  extends ContentPane
{
  private ImagePanel m_capturedImagePanel = new ImagePanel();
  private JCheckBox m_dontShowCheckBox = new JCheckBox(
    Messages.getString("don_t_show_this_anymore"));
  private JLabel m_storeLocationLabel = new JLabel(
    Messages.getString("storeLocation"));
  private JButton m_okButton = new JButton(
    Messages.getString("OK"));
  
  public CapturedImageContentPane() {
    setLayout(new GridBagLayout());
    
    m_okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CapturedImageContentPane.this.syncConfigWithCheckBox();
      }
      
    });
    GridBagConstraints gbc = new GridBagConstraints();
    fill = 0;
    anchor = 18;
    gridheight = 3;
    gridwidth = -1;
    insets.left = 8;
    insets.top = 8;
    add(m_capturedImagePanel, gbc);
    insets.right = 8;
    
    gridheight = 1;
    gridwidth = 0;
    add(new JLabel(Messages.getString("Image_captured_to_")), 
      gbc);
    add(m_storeLocationLabel, gbc);
    weighty = 1.0D;
    add(new JLabel(), gbc);
    weighty = 0.0D;
    
    anchor = 10;
    add(m_okButton, gbc);
    anchor = 16;
    insets.bottom = 8;
    add(m_dontShowCheckBox, gbc);
  }
  
  public String getTitle() {
    return Messages.getString("Image_captured_and_stored");
  }
  
  public void preDialogShow(JDialog dialog) {
    super.preDialogShow(dialog);
    syncCheckBoxWithConfig();
  }
  
  public void setStoreLocation(String storeLocation) {
    m_storeLocationLabel.setText(storeLocation);
  }
  
  public void setImage(Image image) {
    m_capturedImagePanel.setImage(image);
  }
  
  public void addOKActionListener(ActionListener l) {
    m_okButton.addActionListener(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    m_okButton.removeActionListener(l);
  }
  
  private void syncCheckBoxWithConfig() {
    Configuration authoringToolConfig = 
      Configuration.getLocalConfiguration(JAlice.class
      .getPackage());
    m_dontShowCheckBox.setSelected(
      !authoringToolConfig.getValue("screenCapture.informUser").equalsIgnoreCase("true"));
  }
  
  private void syncConfigWithCheckBox() {
    Configuration authoringToolConfig = 
      Configuration.getLocalConfiguration(JAlice.class
      .getPackage());
    String s;
    String s; if (m_dontShowCheckBox.isSelected()) {
      s = "false";
    } else {
      s = "true";
    }
    authoringToolConfig.setValue("screenCapture.informUser", s);
  }
}
