package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

















public class DefaultElementTreeCellEditor
  extends DefaultTreeCellEditor
{
  public DefaultElementTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer)
  {
    super(tree, renderer);
  }
  
  public DefaultElementTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer, TreeCellEditor editor) {
    super(tree, renderer, editor);
  }
  
  public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
    if ((value instanceof Element)) {
      return super.getTreeCellEditorComponent(tree, name.getStringValue(), isSelected, expanded, leaf, row);
    }
    return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
  }
  
  protected void prepareForEditing()
  {
    super.prepareForEditing();
    if ((editingComponent instanceof JTextField)) {
      ((JTextField)editingComponent).selectAll();
    }
  }
}
