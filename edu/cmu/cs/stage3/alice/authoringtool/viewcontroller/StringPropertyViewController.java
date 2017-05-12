package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Property;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;














public class StringPropertyViewController
  extends TextFieldEditablePropertyViewController
{
  protected JLabel stringLabel = new JLabel();
  protected boolean emptyStringWritesNull;
  private Dimension minSize = new Dimension(20, 16);
  
  public StringPropertyViewController() {
    stringLabel.setMinimumSize(minSize);
    textField.setColumns(20);
  }
  
  public void set(Property property, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory) {
    set(property, allowExpressions, omitPropertyName, true, factory);
  }
  
  public void set(Property property, boolean includeDefaults, boolean allowExpressions, boolean omitPropertyName, boolean emptyStringWritesNull, PopupItemFactory factory) {
    super.set(property, includeDefaults, allowExpressions, true, omitPropertyName, factory);
    this.emptyStringWritesNull = ((emptyStringWritesNull) && (!AuthoringToolResources.shouldGUIOmitNone(property)));
    if ((edu.cmu.cs.stage3.alice.core.response.Comment.class.isAssignableFrom(property.getOwner().getClass())) || (edu.cmu.cs.stage3.alice.core.question.userdefined.Comment.class.isAssignableFrom(property.getOwner().getClass()))) {
      stringLabel.setForeground(AuthoringToolResources.getColor("commentForeground"));
      int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
      stringLabel.setFont(new Font("Helvetica", 1, (int)(13 * fontSize / 12.0D)));
    } else {
      stringLabel.setForeground(UIManager.getColor("Label.foreground"));
      stringLabel.setFont(UIManager.getFont("Label.font"));
    }
    refreshGUI();
  }
  
  public void set(Property property, boolean allowExpressions, boolean omitPropertyName, boolean emptyStringWritesNull, PopupItemFactory factory) {
    set(property, false, allowExpressions, omitPropertyName, emptyStringWritesNull, factory);
  }
  
  protected void setValueFromString(String valueString) {
    if ((valueString.trim().equals("")) && 
      (emptyStringWritesNull)) {
      valueString = null;
    }
    
    ((Runnable)factory.createItem(valueString)).run();
  }
  
  protected Component getNativeComponent() {
    return stringLabel;
  }
  
  protected Class getNativeClass() {
    return String.class;
  }
  
  protected void updateNativeComponent() {
    stringLabel.setText(property.get().toString());
  }
  




  protected void refreshGUI()
  {
    stringLabel.setPreferredSize(null);
    if (isAncestorOf(textField)) {
      remove(textField);
    }
    super.refreshGUI();
  }
}
