package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import java.awt.Component;
import javax.swing.JLabel;

















public class ElementPropertyViewController
  extends PropertyViewController
{
  protected JLabel elementLabel = new JLabel();
  protected Element root = null;
  





  public ElementPropertyViewController() {}
  




  public void set(Property property, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory)
  {
    super.set(property, true, allowExpressions, false, omitPropertyName, factory);
    setPopupEnabled(true);
    refreshGUI();
  }
  


  public void setRoot(Element root)
  {
    this.root = root;
  }
  
  protected void updatePopupStructure() {
    popupStructure = PopupMenuUtilities.makePropertyStructure(property, factory, includeDefaults, allowExpressions, includeOther, root);
  }
  
  protected Component getNativeComponent() {
    return elementLabel;
  }
  
  protected Class getNativeClass() {
    return Element.class;
  }
  
  protected void updateNativeComponent() {
    elementLabel.setText(AuthoringToolResources.getNameInContext((Element)property.get(), property.getOwner()));
  }
}
