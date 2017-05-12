package edu.cmu.cs.stage3.caitlin.personbuilder;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

















public class NamePanel
  extends JPanel
{
  protected JTextField m_nameField = null;
  protected JTextField m_createdByField = null;
  
  public NamePanel() {
    m_nameField = new JTextField();
    m_createdByField = new JTextField(Messages.getString("Anonymous"));
    
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    anchor = 18;
    fill = 2;
    
    gridwidth = -1;
    weightx = 0.0D;
    add(new JLabel(Messages.getString("Name_")), gbc);
    
    gridwidth = 0;
    weightx = 1.0D;
    add(m_nameField, gbc);
    
    gridwidth = -1;
    weightx = 0.0D;
    add(new JLabel(Messages.getString("Created_By_")), gbc);
    
    gridwidth = 0;
    weightx = 1.0D;
    add(m_createdByField, gbc);
  }
  
  public String getName()
  {
    return m_nameField.getText();
  }
  
  public String getCreatedBy() { return m_createdByField.getText(); }
}
