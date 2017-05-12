package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;


















public class DefaultElementTreeCellRenderer
  extends DefaultTreeCellRenderer
{
  public DefaultElementTreeCellRenderer() {}
  
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
  {
    String stringValue;
    String stringValue;
    if ((value instanceof Element)) {
      stringValue = (String)name.getValue();
    } else {
      stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);
    }
    
    this.hasFocus = hasFocus;
    setText(stringValue);
    if (sel) {
      setForeground(getTextSelectionColor());
    } else {
      setForeground(getTextNonSelectionColor());
    }
    
    setIcon(AuthoringToolResources.getIconForValue(value));
    
    setComponentOrientation(tree.getComponentOrientation());
    
    selected = sel;
    
    return this;
  }
}
