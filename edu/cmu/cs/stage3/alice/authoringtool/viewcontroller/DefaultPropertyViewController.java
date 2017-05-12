package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Property;
import java.awt.Component;
import javax.swing.JLabel;

















public class DefaultPropertyViewController
  extends PropertyViewController
{
  public DefaultPropertyViewController() {}
  
  protected JLabel label = new JLabel();
  
  public void set(Property property, boolean includeDefaults, boolean allowExpressions, boolean includeOther, boolean omitPropertyName, PopupItemFactory factory) {
    super.set(property, includeDefaults, allowExpressions, includeOther, omitPropertyName, factory);
    setPopupEnabled(true);
    refreshGUI();
  }
  
  protected Component getNativeComponent() {
    return label;
  }
  
  protected Class getNativeClass() {
    return Object.class;
  }
  
  protected void updateNativeComponent() {
    label.setText(AuthoringToolResources.getReprForValue(property.get(), property));
  }
}
