package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.Enumerable;
import java.awt.Component;
import javax.swing.JLabel;


















public class EnumerablePropertyViewController
  extends PropertyViewController
{
  protected JLabel enumerableLabel = new JLabel();
  


  protected Class valueClass;
  



  public EnumerablePropertyViewController() {}
  



  public void set(Property property, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory)
  {
    super.set(property, true, allowExpressions, false, omitPropertyName, factory);
    valueClass = PopupMenuUtilities.getDesiredValueClass(property);
    if (!Enumerable.class.isAssignableFrom(valueClass)) {
      throw new IllegalArgumentException(Messages.getString("valueClass_of_property_") + property + " " + Messages.getString("is_not_an_Enumerable__instead__") + valueClass);
    }
    setPopupEnabled(true);
    refreshGUI();
  }
  
  protected Component getNativeComponent() {
    return enumerableLabel;
  }
  
  protected Class getNativeClass() {
    return Enumerable.class;
  }
  
  protected void updateNativeComponent() {
    String text = AuthoringToolResources.getReprForValue(property.get(), property);
    enumerableLabel.setText(text);
  }
}
