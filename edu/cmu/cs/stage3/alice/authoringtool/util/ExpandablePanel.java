package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import java.awt.BorderLayout;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;













public class ExpandablePanel
  extends JPanel
{
  protected static HashSet collapsedSet = new HashSet();
  
  protected JPanel topPanel = new JPanel();
  protected JPanel mainPanel = new JPanel();
  protected JToggleButton expandButton = new JToggleButton();
  protected JLabel titleLabel = new JLabel();
  protected ImageIcon plusIcon;
  protected ImageIcon minusIcon;
  protected ImageIcon squareIcon;
  protected ExpandButtonListener expandButtonListener = new ExpandButtonListener();
  
  public ExpandablePanel() {
    guiInit();
  }
  
  private void guiInit() {
    setLayout(new BorderLayout());
    add(topPanel, "North");
    add(mainPanel, "Center");
    setOpaque(false);
    
    topPanel.setLayout(new BorderLayout());
    topPanel.add(expandButton, "West");
    topPanel.add(titleLabel, "Center");
    titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    topPanel.setOpaque(false);
    
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
    mainPanel.setOpaque(false);
    
    plusIcon = AuthoringToolResources.getIconForValue("plus");
    minusIcon = AuthoringToolResources.getIconForValue("minus");
    squareIcon = AuthoringToolResources.getIconForValue("square");
    expandButton.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
    expandButton.setOpaque(false);
    expandButton.setIcon(plusIcon);
    expandButton.setSelectedIcon(minusIcon);
    expandButton.setPressedIcon(squareIcon);
    expandButton.setSelected(true);
    expandButton.setFocusPainted(false);
    expandButton.setContentAreaFilled(false);
    expandButton.addChangeListener(expandButtonListener);
    expandButton.setBorderPainted(false);
    titleLabel.setOpaque(false);
  }
  
  public void setTitle(String title) {
    titleLabel.setText(title);
    if (collapsedSet.contains(title)) {
      expandButton.setSelected(false);
    }
  }
  
  public String getTitle() {
    return titleLabel.getText();
  }
  
  public void setContent(JComponent component) {
    mainPanel.removeAll();
    mainPanel.add(component, "Center");
  }
  
  public void setExpanded(boolean b) {
    if (b) {
      if (!isAncestorOf(mainPanel)) {
        add(mainPanel, "Center");
        collapsedSet.remove(titleLabel.getText());
        if (!expandButton.isSelected()) {
          expandButton.setSelected(true);
        }
      }
    }
    else if (isAncestorOf(mainPanel)) {
      remove(mainPanel);
      collapsedSet.add(titleLabel.getText());
      if (expandButton.isSelected()) {
        expandButton.setSelected(false);
      }
    }
    
    revalidate();
    repaint();
  }
  
  protected class ExpandButtonListener implements ChangeListener { protected ExpandButtonListener() {}
    
    public void stateChanged(ChangeEvent ev) { setExpanded(expandButton.isSelected()); }
  }
}
