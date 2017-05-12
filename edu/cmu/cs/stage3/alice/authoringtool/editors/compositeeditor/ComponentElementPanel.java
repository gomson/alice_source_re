package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import javax.swing.BorderFactory;
























public class ComponentElementPanel
  extends DnDGroupingPanel
{
  protected Element m_element;
  
  public ComponentElementPanel()
  {
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    remove(grip);
  }
  
  public void set(Element element) {
    m_element = element;
    try {
      add(GUIFactory.getGUI(m_element));
    }
    catch (Exception e) {
      AuthoringTool.showErrorDialog(Messages.getString("An_error_occurred_while_creating_the_graphics_component_for_this_object_"), e);
    }
  }
  
  protected Color getCustomBackgroundColor()
  {
    if (getComponentCount() > 0) {
      return getComponent(0).getBackground();
    }
    return Color.white;
  }
  
  public boolean isDisabled() {
    if ((m_element instanceof Response))
      return m_element).isCommentedOut.booleanValue();
    if ((m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component)) {
      return m_element).isCommentedOut.booleanValue();
    }
    return false;
  }
  

  public void goToSleep() {}
  

  public void wakeUp() {}
  
  public void clean()
  {
    removeAll();
  }
  
  public void die() {
    clean();
  }
  
  public Element getElement() {
    return m_element;
  }
}
