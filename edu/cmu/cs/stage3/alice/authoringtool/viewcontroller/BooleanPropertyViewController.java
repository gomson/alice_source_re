package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.question.And;
import edu.cmu.cs.stage3.alice.core.question.Not;
import edu.cmu.cs.stage3.alice.core.question.Or;
import edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse;
import edu.cmu.cs.stage3.alice.core.response.IfElseInOrder;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;








public class BooleanPropertyViewController
  extends PropertyViewController
{
  protected JCheckBox checkBox = new JCheckBox();
  protected JLabel booleanLabel = new JLabel();
  protected Color originalForegroundColor;
  
  public BooleanPropertyViewController() {
    originalForegroundColor = booleanLabel.getForeground();
    
    checkBox.setOpaque(false);
    checkBox.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          ((Runnable)factory.createItem(checkBox.isSelected() ? Boolean.TRUE : Boolean.FALSE)).run();
        }
      });
  }
  
  public void set(Property property, boolean includeDefaults, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory)
  {
    super.set(property, includeDefaults, allowExpressions, false, omitPropertyName, factory);
    if (omitPropertyName) {
      add(Box.createHorizontalStrut(8), "West");
    }
    
    refreshGUI();
  }
  
  public void setEditingEnabled(boolean editingEnabled) {
    super.setEditingEnabled(editingEnabled);
    checkBox.setEnabled(editingEnabled);
  }
  
  protected Component getNativeComponent() {
    return booleanLabel;
  }
  
  protected Class getNativeClass() {
    return Boolean.class;
  }
  



  protected void updateNativeComponent()
  {
    if ((!(property.getOwner() instanceof IfElseInOrder)) && 
      (!(property.getOwner() instanceof IfElse)) && 
      (!(property.getOwner() instanceof And)) && 
      (!(property.getOwner() instanceof Or)) && 
      (!(property.getOwner() instanceof Not)))
    {






      booleanLabel.setForeground(originalForegroundColor);
    }
    booleanLabel.setText(AuthoringToolResources.getReprForValue(property.getValue(), property, property.getOwner().data));
  }
}
