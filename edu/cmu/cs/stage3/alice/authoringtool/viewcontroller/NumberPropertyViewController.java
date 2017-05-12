package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JLabel;











public class NumberPropertyViewController
  extends TextFieldEditablePropertyViewController
{
  public NumberPropertyViewController() {}
  
  protected JLabel numberLabel = new JLabel();
  
  public void set(Property property, boolean includeDefaults, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory) {
    super.set(property, includeDefaults, allowExpressions, true, omitPropertyName, factory);
    allowEasyEditWithClick = false;
    refreshGUI();
  }
  
  protected void setValueFromString(String valueString) {
    Double value = AuthoringToolResources.parseDouble(valueString);
    
    if (value != null) {
      ((Runnable)factory.createItem(value)).run();
      String propertyKey = "edu.cmu.cs.stage3.alice.authoringtool.userRepr." + property.getName();
      property.getOwner().data.put(propertyKey, valueString);
    } else {
      AuthoringTool.showErrorDialog(Messages.getString("I_don_t_understand_this_number__") + valueString, null, false);
    }
  }
  
  protected Component getNativeComponent() {
    return numberLabel;
  }
  
  protected MouseListener getMouseListener() {
    new MouseAdapter() {
      public void mouseReleased(MouseEvent ev) {
        if ((ev.getX() >= 0) && (ev.getX() < ev.getComponent().getWidth()) && (ev.getY() >= 0) && (ev.getY() < ev.getComponent().getHeight()) && 
          (isEnabled())) {
          popupButton.doClick();
        }
      }
    };
  }
  
  protected Class getNativeClass()
  {
    return Number.class;
  }
  
  protected void updateNativeComponent() {
    numberLabel.setText(AuthoringToolResources.getReprForValue(property.get(), property, property.getOwner().data));
  }
  
  protected void refreshGUI() {
    if (isAncestorOf(textField)) {
      remove(textField);
    }
    super.refreshGUI();
  }
}
