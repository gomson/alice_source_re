package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;








public class ElementTreeCellRenderer
  extends JPanel
  implements TreeCellRenderer
{
  protected DnDGroupingPanel dndPanel = new DnDGroupingPanel();
  protected JPanel elementPanel = new JPanel();
  protected JLabel iconLabel = new JLabel();
  protected JLabel elementLabel = new JLabel();
  protected Color selectedColor = AuthoringToolResources.getColor("objectTreeSelected");
  protected Color bgColor = AuthoringToolResources.getColor("objectTreeBackground");
  protected Color disabledColor = AuthoringToolResources.getColor("objectTreeDisabled");
  protected Color textColor = AuthoringToolResources.getColor("objectTreeText");
  protected Color disabledTextColor = AuthoringToolResources.getColor("objectTreeDisabledText");
  protected Color selectedTextColor = AuthoringToolResources.getColor("objectTreeSelectedText");
  
  public ElementTreeCellRenderer() {
    setOpaque(false);
    setLayout(new GridBagLayout());
    add(dndPanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(1, 0, 0, 0), 0, 0));
    
    dndPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    dndPanel.setBackground(bgColor);
    elementPanel.setLayout(new GridBagLayout());
    elementPanel.setOpaque(false);
    iconLabel.setOpaque(false);
    elementLabel.setBackground(selectedColor);
    elementLabel.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
    elementLabel.setOpaque(false);
    elementPanel.add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
    elementPanel.add(elementLabel, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    dndPanel.add(elementPanel, "Center");
  }
  
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    if ((value instanceof Element)) {
      Element element = (Element)value;
      
      boolean inScope = true;
      TreeModel treeModel = tree.getModel();
      if ((treeModel instanceof ScopedElementTreeModel)) {
        inScope = ((ScopedElementTreeModel)treeModel).isElementInScope(element);
      }
      
      ImageIcon icon = AuthoringToolResources.getIconForValue(element);
      if (inScope) {
        dndPanel.setBackground(bgColor);
        iconLabel.setIcon(icon);
      } else {
        dndPanel.setBackground(disabledColor);
        iconLabel.setIcon(AuthoringToolResources.getDisabledIcon(icon));
      }
      
      elementLabel.setText(name.getStringValue());
      if (selected) {
        elementLabel.setOpaque(true);
        if (inScope) {
          elementLabel.setForeground(selectedTextColor);
        } else {
          elementLabel.setForeground(disabledTextColor);
        }
      } else {
        elementLabel.setOpaque(false);
        if (inScope) {
          elementLabel.setForeground(textColor);
        } else {
          elementLabel.setForeground(disabledTextColor);
        }
      }
    } else {
      AuthoringTool.showErrorDialog(Messages.getString("Error__not_an_Element__") + value, null);
    }
    doLayout();
    return this;
  }
  
  public void setBackgroundColor(Color color) {
    bgColor = color;
    dndPanel.setBackground(color);
  }
}
