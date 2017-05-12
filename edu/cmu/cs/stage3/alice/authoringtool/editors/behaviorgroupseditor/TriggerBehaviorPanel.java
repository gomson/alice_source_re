package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.behavior.TriggerBehavior;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ResponseProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JComponent;
import javax.swing.JLabel;












public class TriggerBehaviorPanel
  extends BasicBehaviorPanel
  implements ComponentListener
{
  JComponent triggerPanel;
  private boolean isSecondLine = false;
  private Component topParent = null;
  private Component containingPanel = null;
  private boolean shouldCheckSize = true;
  private JLabel lastLabel;
  
  public TriggerBehaviorPanel()
  {
    addComponentListener(this);
  }
  
  public void set(TriggerBehavior behavior, AuthoringTool authoringTool) {
    super.set(behavior, authoringTool);
  }
  

  public void getHTML(StringBuffer toWriteTo, boolean useColor)
  {
    Color bgColor = COLOR;
    String strikeStart = "";
    String strikeEnd = "";
    if (!m_behavior.isEnabled.booleanValue()) {
      bgColor = AuthoringToolResources.getColor("disabledHTML");
      strikeStart = "<strike><font color=\"" + getHTMLColorString(AuthoringToolResources.getColor("disabledHTMLText")) + "\">";
      strikeEnd = "</font></strike>";
    }
    
    Response response = (Response)m_behavior).triggerResponse.get();
    toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + " colspan=\"2\">" + strikeStart);
    labelPanel.remove(lastLabel);
    toWriteTo.append(GUIFactory.getHTMLStringForComponent(labelPanel));
    labelPanel.add(lastLabel, new GridBagConstraints(labelPanel.getComponentCount(), 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 0), 0, 0));
    toWriteTo.append(strikeEnd + "</td>\n</tr>\n");
    toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + " align=\"right\">" + strikeStart + Messages.getString("_b_Do___b_") + strikeEnd + "</td>\n");
    toWriteTo.append("<td bgcolor=" + getHTMLColorString(bgColor) + " width=\"100%\"><table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">");
    toWriteTo.append(GUIFactory.getHTMLStringForComponent(triggerPanel));
    toWriteTo.append("</table>\n</td>\n</tr>\n");
  }
  
  protected void removeAllListening() {
    super.removeAllListening();
    if (m_containingPanel != null) {
      removeDragSourceComponent(m_containingPanel);
      m_containingPanel.removeMouseListener(behaviorMouseListener);
    }
    if (triggerPanel != null) {
      removeDragSourceComponent(triggerPanel);
      triggerPanel.removeMouseListener(behaviorMouseListener);
      removeComponentListener(this);
      triggerPanel.removeComponentListener(this);
    }
  }
  
  public void release() {
    super.release();
  }
  
  protected void guiInit() {
    super.guiInit();
    if (m_containingPanel == null) {
      m_containingPanel = new GroupingPanel();
      m_containingPanel.setBorder(null);
      m_containingPanel.setLayout(new GridBagLayout());
      m_containingPanel.setBackground(COLOR);
      m_containingPanel.addMouseListener(behaviorMouseListener);
    }
    remove(m_containingPanel);
    addDragSourceComponent(m_containingPanel);
    m_containingPanel.removeAll();
    if (labelPanel == null) {
      labelPanel = new GroupingPanel();
      labelPanel.setBorder(null);
      labelPanel.setLayout(new GridBagLayout());
      labelPanel.setBackground(COLOR);
      labelPanel.addMouseListener(behaviorMouseListener);
    }
    addDragSourceComponent(labelPanel);
    labelPanel.removeAll();
    setBackground(COLOR);
    buildLabel(labelPanel);
    int x = labelPanel.getComponentCount();
    lastLabel = new JLabel();
    if (isSecondLine) {
      lastLabel.setText(",");
    }
    else {
      lastLabel.setText(Messages.getString("___do"));
    }
    labelPanel.add(lastLabel, new GridBagConstraints(x, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 0), 0, 0));
    PopupItemFactory triggerFactory = new SetPropertyImmediatelyFactory(m_behavior).triggerResponse);
    triggerPanel = GUIFactory.getPropertyViewController(m_behavior).triggerResponse, false, true, true, triggerFactory);
    triggerPanel.addComponentListener(this);
    m_containingPanel.add(labelPanel, new GridBagConstraints(0, 0, 2, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    if (isSecondLine) {
      m_containingPanel.add(new JLabel(Messages.getString("do_")), new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 13, 0, new Insets(0, 15, 0, 2), 0, 0));
      m_containingPanel.add(triggerPanel, new GridBagConstraints(1, 1, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    }
    else {
      m_containingPanel.add(triggerPanel, new GridBagConstraints(x + 1, 0, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    }
    
    add(m_containingPanel, "Center");
    repaint();
    revalidate();
  }
  
  private Component getTopParent(Component c) {
    if (c == null) {
      return null;
    }
    if ((c instanceof BehaviorGroupsEditor)) {
      return c;
    }
    return getTopParent(c.getParent());
  }
  
  private void recheckLength() {
    if (containingPanel == null) {
      topParent = getTopParent(getParent());
      if (topParent != null) {
        topParent.addComponentListener(this);
      }
      if ((topParent instanceof BehaviorGroupsEditor)) {
        containingPanel = ((BehaviorGroupsEditor)topParent).getContainingPanel();
      }
    }
    if (topParent == null) {
      return;
    }
    int calculatedWidth = labelPanel.getWidth() + triggerPanel.getWidth() + 45 + 8;
    if (isSecondLine) {
      calculatedWidth += 23;
    }
    
    if (isSecondLine) {
      if (calculatedWidth < topParent.getWidth()) {
        isSecondLine = false;
        guiInit();
      }
      
    }
    else if (calculatedWidth > topParent.getWidth()) {
      isSecondLine = true;
      guiInit();
    }
  }
  

  public void componentHidden(ComponentEvent e) {}
  

  public void componentMoved(ComponentEvent e) {}
  
  public void componentResized(ComponentEvent e)
  {
    if (shouldCheckSize) {
      recheckLength();
    }
  }
  
  public void componentShown(ComponentEvent e) {}
}
