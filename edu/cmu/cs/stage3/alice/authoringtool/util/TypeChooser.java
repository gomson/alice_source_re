package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.StringTypePair;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;







public class TypeChooser
  extends JPanel
{
  private Class type;
  private ButtonGroup buttonGroup;
  private HashMap typeMap = new HashMap();
  private HashSet changeListeners = new HashSet();
  private JRadioButton numberButton = new JRadioButton(Messages.getString("Number"));
  private JRadioButton booleanButton = new JRadioButton(Messages.getString("Boolean"));
  private JRadioButton objectButton = new JRadioButton(Messages.getString("Object"));
  private JRadioButton otherButton = new JRadioButton(Messages.getString("Other___"));
  private JComboBox otherCombo = new JComboBox();
  private CheckForValidityCallback okButtonCallback;
  
  public TypeChooser(CheckForValidityCallback okButtonCallback)
  {
    otherCombo.setEditable(false);
    this.okButtonCallback = okButtonCallback;
    setLayout(new GridBagLayout());
    
    GridBagConstraints gbc = new GridBagConstraints();
    anchor = 18;
    
    gridx = 0;
    add(numberButton, gbc);
    add(booleanButton, gbc);
    add(objectButton, gbc);
    add(otherButton, gbc);
    gridx = 1;
    gridy = 3;
    weightx = 1.0D;
    add(otherCombo, gbc);
    
    buttonGroup = new ButtonGroup();
    buttonGroup.add(numberButton);
    buttonGroup.add(booleanButton);
    buttonGroup.add(objectButton);
    buttonGroup.add(otherButton);
    
    ActionListener radioListener = new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == numberButton) {
          type = Number.class;
          otherCombo.setEnabled(false);
          fireStateChanged(numberButton);
          TypeChooser.this.checkTypeValidity();
        } else if (ev.getSource() == booleanButton) {
          type = Boolean.class;
          otherCombo.setEnabled(false);
          fireStateChanged(booleanButton);
          TypeChooser.this.checkTypeValidity();
        } else if (ev.getSource() == objectButton) {
          type = Model.class;
          otherCombo.setEnabled(false);
          fireStateChanged(objectButton);
          TypeChooser.this.checkTypeValidity();
        } else if (ev.getSource() == otherButton) {
          otherCombo.setEnabled(true);
          parseOtherType();
        }
      }
    };
    numberButton.addActionListener(radioListener);
    booleanButton.addActionListener(radioListener);
    objectButton.addActionListener(radioListener);
    otherButton.addActionListener(radioListener);
    otherCombo.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
        parseOtherType();
      }
      

    });
    StringTypePair[] defaultVariableTypes = AuthoringToolResources.getDefaultVariableTypes();
    for (int i = 0; i < defaultVariableTypes.length; i++) {
      typeMap.put(defaultVariableTypes[i].getString().trim(), defaultVariableTypes[i].getType());
      otherCombo.addItem(new makeObj(defaultVariableTypes[i].getString()));
    }
    
    ((JTextField)otherCombo.getEditor().getEditorComponent()).getDocument().addDocumentListener(
      new DocumentListener() {
        public void changedUpdate(DocumentEvent ev) { parseOtherType(); }
        public void insertUpdate(DocumentEvent ev) { parseOtherType(); }
        public void removeUpdate(DocumentEvent ev) { parseOtherType();
        }

      });
    numberButton.setSelected(true);
    type = Number.class;
    otherCombo.setEnabled(false);
  }
  

  protected void parseOtherType()
  {
    makeObj typeString = (makeObj)otherCombo.getSelectedItem();
    Class newType = (Class)typeMap.get(typeString.getItem());
    if (newType == null) {
      try {
        newType = Class.forName(typeString.getItem());
      } catch (ClassNotFoundException e) {
        newType = null;
      }
    }
    
    if (newType == null) {
      otherCombo.getEditor().getEditorComponent().setForeground(Color.red);
    } else {
      otherCombo.getEditor().getEditorComponent().setForeground(Color.black);
    }
    
    if (type != newType) {
      type = newType;
      fireStateChanged(otherCombo);
    }
    checkTypeValidity();
  }
  
  private void checkTypeValidity() {
    okButtonCallback.setValidity(this, type != null);
  }
  
  public Class getType() {
    if (otherButton.isSelected()) {
      parseOtherType();
    }
    return type;
  }
  
  public void addCurrentTypeToList() {
    if ((otherButton.isSelected()) && (type != null))
    {
      makeObj typeString = (makeObj)otherCombo.getSelectedItem();
      if (!typeMap.containsKey(typeString.getItem())) {
        otherCombo.addItem(typeString.getItem());
        typeMap.put(typeString.getItem(), type);
      }
    }
  }
  
  public void addChangeListener(ChangeListener listener) {
    changeListeners.add(listener);
  }
  
  public void removeChangeListener(ChangeListener listener) {
    changeListeners.remove(listener);
  }
  
  protected void fireStateChanged(Object source) {
    ChangeEvent ev = new ChangeEvent(source);
    for (Iterator iter = changeListeners.iterator(); iter.hasNext();) {
      ((ChangeListener)iter.next()).stateChanged(ev);
    }
  }
  

  private class makeObj {
    public String s;
    
    public makeObj(String item) { s = item; }
    
    public String getItem() { return s; }
    public String toString() { return Messages.getString(s.replace(" ", "_")); }
  }
}
