package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.util.CheckForValidityCallback;
import edu.cmu.cs.stage3.alice.authoringtool.util.NameTextField;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;












public abstract class NewNamedElementContentPane
  extends ContentPane
{
  private JButton m_okButton = new JButton(
    Messages.getString("OK"));
  private JButton m_cancelButton = new JButton(
    Messages.getString("Cancel"));
  private HashMap validityHashmap = new HashMap();
  private CheckForValidityCallback validityChecker = new CheckForValidityCallback() {
    public void setValidity(Object source, boolean value) {
      NewNamedElementContentPane.this.okButtonEnabler(source, value);
    }
    
    public void doAction() {
      if (m_okButton.isEnabled()) {
        m_okButton.doClick();
      }
    }
  };
  
  private NameTextField m_nameTextField = new NameTextField(
    validityChecker);
  
  public NewNamedElementContentPane() {
    m_nameTextField.setMinimumSize(new Dimension(150, 26));
    m_nameTextField.setPreferredSize(new Dimension(150, 26));
    
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    initTopComponents(gbc);
    initBottomComponents(gbc);
  }
  
  protected void initTopComponents(GridBagConstraints gbc) {
    anchor = 18;
    fill = 2;
    gridwidth = -1;
    insets.top = 8;
    insets.left = 8;
    add(new JLabel(Messages.getString("Name_")), gbc);
    insets.right = 8;
    gridwidth = 0;
    weightx = 1.0D;
    add(m_nameTextField, gbc);
    weightx = 0.0D;
  }
  
  protected void initBottomComponents(GridBagConstraints gbc) {
    JPanel okCancelPanel = new JPanel();
    GridBagConstraints gbcOKCancel = new GridBagConstraints();
    okCancelPanel.add(m_okButton, gbcOKCancel);
    okCancelPanel.add(m_cancelButton, gbcOKCancel);
    anchor = 15;
    add(okCancelPanel, gbc);
  }
  
  public void addOKActionListener(ActionListener l) {
    m_okButton.addActionListener(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    m_okButton.removeActionListener(l);
  }
  
  public void addCancelActionListener(ActionListener l) {
    m_cancelButton.addActionListener(l);
  }
  
  public void removeCancelActionListener(ActionListener l) {
    m_cancelButton.removeActionListener(l);
  }
  
  public void reset(Element parent) {
    m_nameTextField.setText("");
    m_nameTextField.setParent(parent);
    m_nameTextField.grabFocus();
  }
  
  public String getNameValue() {
    return m_nameTextField.getText();
  }
  
  private void okButtonEnabler(Object source, boolean value) {
    validityHashmap.put(source, new Boolean(value));
    Iterator valueIterator = validityHashmap.values().iterator();
    boolean isEnableable = true;
    while (valueIterator.hasNext()) {
      Object currentValue = valueIterator.next();
      if (((currentValue instanceof Boolean)) && 
        (!((Boolean)currentValue).booleanValue())) {
        isEnableable = false;
        break;
      }
    }
    
    m_okButton.setEnabled(isEnableable);
  }
  
  protected CheckForValidityCallback getValidityChecker() {
    return validityChecker;
  }
}
