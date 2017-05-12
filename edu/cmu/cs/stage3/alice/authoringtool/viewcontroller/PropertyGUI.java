package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.core.Property;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;











public class PropertyGUI
  extends JPanel
  implements GUIElement, Releasable
{
  protected PropertyDnDPanel propertyDnDPanel;
  protected JComponent propertyViewController;
  protected JLabel equalsLabel = new JLabel(" = ");
  
  public PropertyGUI() {
    setLayout(new BoxLayout(this, 0));
    setOpaque(false);
  }
  
  public void set(AuthoringTool authoringTool, Property property, boolean includeDefaults, boolean allowExpressions, PopupItemFactory factory) {
    clean();
    
    propertyDnDPanel = GUIFactory.getPropertyDnDPanel(property);
    propertyViewController = GUIFactory.getPropertyViewController(property, includeDefaults, allowExpressions, true, factory);
    
    add(propertyDnDPanel);
    add(equalsLabel);
    add(propertyViewController);
    add(Box.createHorizontalGlue());
  }
  
  public void goToSleep() {}
  
  public void wakeUp() {}
  
  public void clean() { removeAll();
    if (propertyDnDPanel != null) {
      propertyDnDPanel.release();
    }
    if ((propertyViewController instanceof Releasable)) {
      ((Releasable)propertyViewController).release();
    }
    propertyDnDPanel = null;
    propertyViewController = null;
  }
  
  public void die() {
    clean();
  }
  
  public void release() {
    GUIFactory.releaseGUI(this);
  }
  
  public void remove(Component c) {
    super.remove(c);
  }
}
