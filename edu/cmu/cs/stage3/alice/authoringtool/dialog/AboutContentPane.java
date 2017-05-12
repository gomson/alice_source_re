package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;













public class AboutContentPane
  extends ContentPane
{
  private JButton m_okButton = new JButton(
    Messages.getString("OK"));
  
  public AboutContentPane() {
    JLabel imageLabel = new JLabel();
    URL url = JAlice.class
      .getResource("images/aboutAlice_" + AikMin.locale + ".png");
    if (url == null) {
      url = 
        JAlice.class.getResource("images/aboutAlice_English.png");
    }
    
    imageLabel.setIcon(new ImageIcon(url));
    setBackground(new Color(173, 202, 234));
    setPreferredSize(new Dimension(520, 410));
    
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gridwidth = 0;
    add(imageLabel, gbc);
    add(m_okButton, gbc);
  }
  


















  public String getTitle()
  {
    return 
      Messages.getString("About_Alice_version_") + JAlice.getVersion();
  }
  










  public void addOKActionListener(ActionListener l)
  {
    m_okButton.addActionListener(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    m_okButton.removeActionListener(l);
  }
}
