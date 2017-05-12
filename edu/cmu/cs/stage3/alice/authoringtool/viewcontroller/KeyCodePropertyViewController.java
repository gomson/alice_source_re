package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;















public class KeyCodePropertyViewController
  extends PropertyViewController
{
  public KeyCodePropertyViewController() {}
  
  protected JLabel keyLabel = new JLabel();
  
  public void set(Property property, boolean allowExpressions, PopupItemFactory factory) {
    super.set(property, true, allowExpressions, false, true, factory);
    refreshGUI();
  }
  
  protected Component getNativeComponent() {
    return keyLabel;
  }
  
  protected Class getNativeClass() {
    return Integer.class;
  }
  
  protected void updateNativeComponent() {
    ImageIcon icon = AuthoringToolResources.getIconForValue(property.get());
    if (icon != null) {
      keyLabel.setText(null);
      keyLabel.setIcon(icon);
    } else {
      keyLabel.setText(AuthoringToolResources.getReprForValue(property.get(), property, property.getOwner().data));
      keyLabel.setIcon(null);
    }
  }
  
  public void getHTML(StringBuffer toWriteTo) {
    toWriteTo.append(AuthoringToolResources.getReprForValue(property.get(), property, property.getOwner().data));
  }
}
