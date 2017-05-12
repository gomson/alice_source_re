package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;



















public class ConditionalBehaviorPanel
  extends BasicBehaviorPanel
{
  protected JComponent beginPanel;
  protected JComponent duringPanel;
  protected JComponent endPanel;
  protected static final int INDENT = 15;
  
  public ConditionalBehaviorPanel() {}
  
  public void set(AbstractConditionalBehavior behavior, AuthoringTool authoringTool)
  {
    super.set(behavior, authoringTool);
  }
  
  protected void removeAllListening() {
    super.removeAllListening();
    if (m_containingPanel != null) {
      removeDragSourceComponent(m_containingPanel);
      m_containingPanel.removeMouseListener(behaviorMouseListener);
    }
    if (labelPanel != null) {
      removeDragSourceComponent(labelPanel);
      labelPanel.removeMouseListener(behaviorMouseListener);
    }
    if (beginPanel != null) {
      removeDragSourceComponent(beginPanel);
      beginPanel.removeMouseListener(behaviorMouseListener);
    }
    if (duringPanel != null) {
      removeDragSourceComponent(duringPanel);
      duringPanel.removeMouseListener(behaviorMouseListener);
    }
    if (endPanel != null) {
      removeDragSourceComponent(endPanel);
      endPanel.removeMouseListener(behaviorMouseListener);
    }
  }
  
  public void getHTML(StringBuffer toWriteTo, boolean useColor) {
    Color bgColor = COLOR;
    String strikeStart = "";
    String strikeEnd = "";
    if (!m_behavior.isEnabled.booleanValue()) {
      bgColor = AuthoringToolResources.getColor("disabledHTML");
      strikeStart = "<strike><font color=\"" + getHTMLColorString(AuthoringToolResources.getColor("disabledHTMLText")) + "\">";
      strikeEnd = "</font></strike>";
    }
    toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + " colspan=\"2\" >" + strikeStart);
    toWriteTo.append(GUIFactory.getHTMLStringForComponent(labelPanel));
    toWriteTo.append(strikeEnd + "</td>\n</tr>\n");
    toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + " align=\"right\">" + strikeStart + Messages.getString("_b_Begin___b_") + strikeEnd + "</td>\n");
    toWriteTo.append("<td bgcolor=" + getHTMLColorString(bgColor) + " width=\"100%\"><table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">");
    toWriteTo.append(GUIFactory.getHTMLStringForComponent(beginPanel));
    toWriteTo.append("</table>\n</td>\n</tr>\n");
    
    toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + " align=\"right\">" + strikeStart + Messages.getString("_b_During___b_") + strikeEnd + "</td>\n");
    toWriteTo.append("<td bgcolor=" + getHTMLColorString(bgColor) + " width=\"100%\"><table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">");
    toWriteTo.append(GUIFactory.getHTMLStringForComponent(duringPanel));
    toWriteTo.append("</table>\n</td>\n</tr>\n");
    
    toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + " align=\"right\">" + strikeStart + Messages.getString("_b_End___b_") + strikeEnd + "</td>\n");
    toWriteTo.append("<td bgcolor=" + getHTMLColorString(bgColor) + " width=\"100%\"><table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">");
    toWriteTo.append(GUIFactory.getHTMLStringForComponent(endPanel));
    toWriteTo.append("</table>\n</td>\n</tr>\n");
  }
  
  protected void guiInit()
  {
    super.guiInit();
    setBackground(COLOR);
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
    buildLabel(labelPanel);
    m_containingPanel.add(labelPanel, new GridBagConstraints(0, 0, 2, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
    
    m_containingPanel.add(new JLabel(Messages.getString("Begin_")), new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 13, 0, new Insets(0, 15, 0, 2), 0, 0));
    
    PopupItemFactory beginFactory = new SetPropertyImmediatelyFactory(m_behavior).beginResponse);
    beginPanel = GUIFactory.getPropertyViewController(m_behavior).beginResponse, false, true, true, beginFactory);
    m_containingPanel.add(beginPanel, new GridBagConstraints(1, 1, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    
    m_containingPanel.add(new JLabel(Messages.getString("During_")), new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 13, 0, new Insets(0, 15, 0, 2), 0, 0));
    PopupItemFactory duringFactory = new SetPropertyImmediatelyFactory(m_behavior).duringResponse);
    duringPanel = GUIFactory.getPropertyViewController(m_behavior).duringResponse, false, true, true, duringFactory);
    m_containingPanel.add(duringPanel, new GridBagConstraints(1, 2, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    
    m_containingPanel.add(new JLabel(Messages.getString("End_")), new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 13, 0, new Insets(0, 15, 0, 2), 0, 0));
    PopupItemFactory endFactory = new SetPropertyImmediatelyFactory(m_behavior).endResponse);
    endPanel = GUIFactory.getPropertyViewController(m_behavior).endResponse, false, true, true, endFactory);
    
    m_containingPanel.add(endPanel, new GridBagConstraints(1, 3, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    add(m_containingPanel, "Center");
    repaint();
    revalidate();
  }
}
