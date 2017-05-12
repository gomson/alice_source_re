package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.core.Behavior;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
























public class GenericBehaviorPanel
  extends BasicBehaviorPanel
{
  public GenericBehaviorPanel() {}
  
  public void set(Behavior behavior, AuthoringTool authoringTool)
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
    super.guiInit();
    if (m_containingPanel == null) {
      m_containingPanel = new GroupingPanel();
      m_containingPanel.setLayout(new GridBagLayout());
      m_containingPanel.setBackground(COLOR);
      m_containingPanel.addMouseListener(behaviorMouseListener);
    }
    addDragSourceComponent(m_containingPanel);
    m_containingPanel.removeAll();
    setBackground(COLOR);
    int base = 0;
    buildLabel(m_containingPanel);
    int x = m_containingPanel.getComponentCount();
    Component glue = Box.createHorizontalGlue();
    addDragSourceComponent(glue);
    m_containingPanel.add(glue, new GridBagConstraints(x, 0, 1, 1, 1.0D, 0.0D, 13, 1, new Insets(0, 0, 0, 0), 0, 0));
    add(m_containingPanel, "Center");
    repaint();
    revalidate();
  }
}
