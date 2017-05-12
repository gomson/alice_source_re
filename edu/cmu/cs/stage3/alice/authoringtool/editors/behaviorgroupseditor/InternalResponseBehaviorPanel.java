package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.core.behavior.InternalResponseBehavior;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
























public class InternalResponseBehaviorPanel
  extends BasicBehaviorPanel
{
  Component automaticPanel;
  
  public InternalResponseBehaviorPanel() {}
  
  public void set(InternalResponseBehavior behavior, AuthoringTool authoringTool)
  {
    super.set(behavior, authoringTool);
  }
  
  protected void removeAllListening() {
    super.removeAllListening();
    if (m_containingPanel != null) {
      removeDragSourceComponent(m_containingPanel);
      m_containingPanel.removeMouseListener(behaviorMouseListener);
    }
  }
  
  protected void guiInit() {
    if (m_containingPanel == null) {
      m_containingPanel = new GroupingPanel();
      m_containingPanel.setLayout(new GridBagLayout());
      m_containingPanel.setBackground(COLOR);
      m_containingPanel.addMouseListener(behaviorMouseListener);
      m_containingPanel.setBorder(null);
    }
    remove(m_containingPanel);
    addDragSourceComponent(m_containingPanel);
    m_containingPanel.removeAll();
    if (labelPanel == null) {
      labelPanel = new GroupingPanel();
      labelPanel.setLayout(new GridBagLayout());
      labelPanel.setBackground(COLOR);
      labelPanel.addMouseListener(behaviorMouseListener);
      labelPanel.setBorder(null);
    }
    addDragSourceComponent(labelPanel);
    labelPanel.removeAll();
    setBackground(COLOR);
    int base = 0;
    buildLabel(labelPanel);
    Component glue = Box.createHorizontalGlue();
    addDragSourceComponent(glue);
    m_containingPanel.add(labelPanel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
    m_containingPanel.add(glue, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 13, 1, new Insets(0, 0, 0, 0), 0, 0));
    add(m_containingPanel, "Center");
    repaint();
    revalidate();
  }
}
