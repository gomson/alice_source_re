package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.DeleteContentPane;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.UIManager;











public class PropertyReferenceListCellRenderer
  extends DefaultListCellRenderer
{
  public PropertyReferenceListCellRenderer() {}
  
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
  {
    setComponentOrientation(list.getComponentOrientation());
    
    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    if ((value instanceof PropertyReference)) {
      PropertyReference reference = (PropertyReference)value;
      String text = "";
      if ((reference.getProperty() instanceof ObjectArrayProperty)) {
        text = AuthoringToolResources.getReprForValue(reference.getProperty().getOwner(), true) + "." + reference.getProperty().getName() + "[" + index + "] -> " + AuthoringToolResources.getReprForValue(reference.getProperty().get(), true);
      } else {
        text = AuthoringToolResources.getReprForValue(reference.getProperty().getOwner(), true) + "." + reference.getProperty().getName() + " -> " + AuthoringToolResources.getReprForValue(reference.getProperty().get(), true);
      }
      text = DeleteContentPane.getDeleteString(reference);
      
      setIcon(null);
      setText(text);


    }
    else if ((value instanceof Icon)) {
      setIcon((Icon)value);
      setText("");
    } else {
      setIcon(null);
      setText(value == null ? "" : value.toString());
    }
    
    setEnabled(list.isEnabled());
    setFont(list.getFont());
    setBorder(cellHasFocus ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
    
    return this;
  }
}
