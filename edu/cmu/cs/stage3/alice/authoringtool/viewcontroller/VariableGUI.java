package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.core.Variable;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;











public class VariableGUI
  extends GroupingPanel
  implements GUIElement
{
  protected VariableDnDPanel variableDnDPanel;
  protected JComponent variableViewController;
  protected JLabel equalsLabel = new JLabel(" = ");
  
  public VariableGUI() {
    setLayout(new BoxLayout(this, 0));
    setOpaque(false);
    setBorder(null);
  }
  
  public void set(AuthoringTool authoringTool, Variable variable, boolean includeDefaults, PopupItemFactory factory) {
    clean();
    
    variableDnDPanel = GUIFactory.getVariableDnDPanel(variable);
    variableViewController = GUIFactory.getPropertyViewController(value, includeDefaults, false, true, factory);
    
    add(variableDnDPanel);
    add(equalsLabel);
    add(variableViewController);
    add(Box.createHorizontalGlue());
  }
  
  public void goToSleep() {}
  
  public void wakeUp() {}
  
  public void clean() { removeAll();
    if (variableDnDPanel != null) {
      variableDnDPanel.release();
    }
    if ((variableViewController instanceof Releasable)) {
      ((Releasable)variableViewController).release();
    }
    variableDnDPanel = null;
    variableViewController = null;
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
