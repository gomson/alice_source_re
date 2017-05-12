package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Element;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;















public class NameTextField
  extends JTextField
{
  private CheckForValidityCallback okButtonCallback;
  private Element m_parent;
  
  public NameTextField(CheckForValidityCallback okButtonCallback)
  {
    this.okButtonCallback = okButtonCallback;
    getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        NameTextField.this.refresh();
      }
      
      public void insertUpdate(DocumentEvent e) { NameTextField.this.refresh(); }
      
      public void removeUpdate(DocumentEvent e) {
        NameTextField.this.refresh();
      }
    });
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        NameTextField.this.textFieldAction();
      }
    });
  }
  
  public void setParent(Element parent) {
    m_parent = parent;
    refresh();
  }
  
  private void textFieldAction() {
    okButtonCallback.doAction();
  }
  
  private boolean isNameValid() {
    if (m_parent != null) {
      String name = getText();
      if ((Element.isPotentialNameValid(name)) && 
        (m_parent.getChildNamedIgnoreCase(name) == null)) {
        return true;
      }
    }
    
    return false;
  }
  
  private void refresh() { if (isNameValid()) {
      okButtonCallback.setValidity(this, true);
      setForeground(Color.black);
    } else {
      okButtonCallback.setValidity(this, false);
      setForeground(Color.red);
    }
  }
}
