package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JTextField;






public abstract class TextFieldEditablePropertyViewController
  extends PropertyViewController
{
  protected JTextField textField = new JTextField();
  protected FocusListener focusListener = new FocusAdapter() {
    public void focusLost(FocusEvent ev) {
      stopEditing();
    }
  };
  protected boolean allowEasyEditWithClick = true;
  
  public TextFieldEditablePropertyViewController() {
    textField.setColumns(5);
    textField.setMinimumSize(new Dimension(80, 18));
    textField.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          stopEditing();
          popupButton.requestFocus();
        }
        
      });
    textField.addKeyListener(
      new KeyAdapter() {
        public void keyPressed(KeyEvent ev) {
          if (ev.getKeyCode() == 27) {
            ev.consume();
            cancelEditing();
            popupButton.requestFocus();
          }
        }
      });
  }
  
  public void set(Property property, boolean includeDefaults, boolean allowExpressions, boolean includeOther, boolean omitPropertyName, PopupItemFactory factory)
  {
    super.set(property, includeDefaults, allowExpressions, includeOther, omitPropertyName, factory);
  }
  
  protected MouseListener getMouseListener() {
    new MouseAdapter() {
      public void mousePressed(MouseEvent ev) {
        if ((allowEasyEditWithClick) && ((isAncestorOf(getNativeComponent())) || (property.get() == null))) {
          editValue();
        } else {
          popupButton.doClick();
        }
      }
    };
  }
  
  public void editValue() {
    if (editingEnabled) {
      if (property.getValue() != null) {
        String propertyKey = "edu.cmu.cs.stage3.alice.authoringtool.userRepr." + property.getName();
        Object userRepr = property.getOwner().data.get(propertyKey);
        if ((userRepr instanceof String)) {
          textField.setText((String)userRepr);
        } else {
          textField.setText(property.getValue().toString());
        }
      }
      if (isAncestorOf(getNativeComponent())) {
        remove(getNativeComponent());
      } else if (isAncestorOf(expressionLabel)) {
        remove(expressionLabel);
      }
      if (!isAncestorOf(textField)) {
        add(textField, "Center");
      }
      revalidate();
      textField.requestFocus();
      textField.addFocusListener(focusListener);
      textField.selectAll();
    }
  }
  
  public void stopEditing() {
    textField.removeFocusListener(focusListener);
    String valueString = textField.getText();
    
    remove(textField);
    add(getNativeComponent(), "Center");
    getNativeComponent().requestFocus();
    
    setValueFromString(valueString);
    refreshGUI();
  }
  
  public void cancelEditing() {
    textField.removeFocusListener(focusListener);
    
    remove(textField);
    add(getNativeComponent(), "Center");
    
    refreshGUI();
  }
  
  protected void refreshGUI() {
    if (isAncestorOf(textField)) {
      remove(textField);
    }
    super.refreshGUI();
  }
  
  protected abstract void setValueFromString(String paramString);
}
