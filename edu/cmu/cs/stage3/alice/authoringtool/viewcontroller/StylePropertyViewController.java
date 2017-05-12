package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Style;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Component;
import javax.swing.JLabel;


















public class StylePropertyViewController
  extends PropertyViewController
{
  protected JLabel styleLabel = new JLabel();
  


  protected Class valueClass;
  



  public StylePropertyViewController() {}
  



  public void set(Property property, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory)
  {
    super.set(property, true, true, true, omitPropertyName, factory);
    valueClass = property.getValueClass();
    if (!Style.class.isAssignableFrom(valueClass)) {
      throw new IllegalArgumentException(Messages.getString("valueClass_of_property_") + property + " " + Messages.getString("is_not_a_Style__instead__") + valueClass);
    }
    setPopupEnabled(true);
    refreshGUI();
  }
  
  protected Component getNativeComponent() {
    return styleLabel;
  }
  
  protected Class getNativeClass() {
    return Style.class;
  }
  
  protected void updateNativeComponent() {
    String text = AuthoringToolResources.getReprForValue(property.get(), property, property.getOwner().data);
    styleLabel.setText(text);
  }
}
