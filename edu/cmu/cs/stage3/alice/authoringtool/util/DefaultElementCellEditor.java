package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;


















public class DefaultElementCellEditor
  extends DefaultCellEditor
{
  public DefaultElementCellEditor(JTextField textField)
  {
    super(textField);
  }
  
  public boolean shouldSelectCell(EventObject e) {
    return true;
  }
  
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
    
    if ((c instanceof JTextField)) {
      JTextField textField = (JTextField)c;
      textField.selectAll();
    }
    return c;
  }
}
