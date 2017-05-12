package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentOwner;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BehaviorGroupEditor extends GroupingPanel implements PropertyListener, GUIElement, CompositeComponentOwner
{
  protected CompositeComponentElementPanel componentElementPanel;
  protected Element m_element;
  protected ObjectArrayProperty m_components;
  protected String headerText = edu.cmu.cs.stage3.lang.Messages.getString("Events");
  protected GroupingPanel headerPanel;
  protected JPanel containingPanel;
  protected JButton expandButton;
  protected Action expandAction;
  protected JLabel headerLabel;
  protected boolean isExpanded = true;
  protected Color backgroundColor = new Color(255, 255, 255);
  protected ImageIcon plus;
  protected ImageIcon minus;
  protected Component glue;
  protected AuthoringTool authoringTool;
  protected ActionListener actionListener;
  protected boolean shouldShowLabel = true;
  

  protected static String IS_EXPANDED_KEY = "edu.cmu.cs.stage3.alice.authoringtool.editors.behavioreditor IS_EXPANDED_KEY";
  
  public BehaviorGroupEditor()
  {
    actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (isExpanded) {
          reduceComponentElementPanel();
        }
        else {
          expandComponentElementPanel();
        }
      }
    };
    setBorder(null);
    generateGUI();
  }
  
  public void set(Element element, AuthoringTool authoringToolIn) {
    clean();
    m_element = element;
    authoringTool = authoringToolIn;
    variableInit();
    startListening();
    setHeaderLabel();
    updateGUI();
    setDropTargets();
  }
  
  public Vector getBehaviorComponents() {
    Vector toReturn = new Vector();
    for (int i = 0; i < componentElementPanel.getComponentCount(); i++) {
      if ((componentElementPanel.getComponent(i) instanceof BasicBehaviorPanel)) {
        toReturn.add(componentElementPanel.getComponent(i));
      }
    }
    return toReturn;
  }
  
  public void setAuthoringTool(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
    if (componentElementPanel != null) {
      componentElementPanel.setAuthoringTool(authoringTool);
    }
  }
  
  public void setEmptyString(String emptyString)
  {
    if (componentElementPanel != null) {
      componentElementPanel.setEmptyString(emptyString);
    }
  }
  
  protected void setDropTargets() {
    headerLabel.setDropTarget(new DropTarget(headerLabel, componentElementPanel));
    setDropTarget(new DropTarget(this, componentElementPanel));
    containingPanel.setDropTarget(new DropTarget(containingPanel, componentElementPanel));
    headerPanel.setDropTarget(new DropTarget(headerPanel, componentElementPanel));
    glue.setDropTarget(new DropTarget(glue, componentElementPanel));
    expandButton.setDropTarget(new DropTarget(expandButton, componentElementPanel));
  }
  
  protected void variableInit() {
    Object isExpandedValue = m_element.data.get(IS_EXPANDED_KEY);
    headerText = m_element.name.getStringValue();
    if ((isExpandedValue instanceof Boolean)) {
      isExpanded = ((Boolean)isExpandedValue).booleanValue();
    }
    if ((m_element instanceof Sandbox)) {
      Sandbox proxy = (Sandbox)m_element;
      m_components = behaviors;
      componentElementPanel = new CompositeComponentBehaviorPanel();
      componentElementPanel.set(m_components, this, authoringTool);
      componentElementPanel.setBackground(backgroundColor);
    }
  }
  
  public Component getGrip() {
    return null;
  }
  
  public Container getParent() {
    return super.getParent();
  }
  
  protected void startListening() {
    if (m_element != null) {
      m_element.name.addPropertyListener(this);
    }
  }
  
  protected void stopListening() {
    if (m_element != null) {
      m_element.name.removePropertyListener(this);
    }
  }
  
  public void goToSleep() {
    stopListening();
    if (componentElementPanel != null) {
      componentElementPanel.goToSleep();
    }
  }
  
  public void wakeUp() {
    startListening();
    if (componentElementPanel != null) {
      componentElementPanel.wakeUp();
    }
  }
  
  public void release() {
    super.release();
    if (componentElementPanel != null) {
      componentElementPanel.release();
    }
    GUIFactory.releaseGUI(this);
  }
  
  public void clean() {
    goToSleep();
    if (componentElementPanel != null) {
      if (containingPanel != null) {
        containingPanel.remove(componentElementPanel);
      }
      componentElementPanel.release();
      componentElementPanel = null;
    }
  }
  
  protected void removeAllListening() {
    expandButton.removeActionListener(actionListener);
  }
  
  public void die()
  {
    clean();
    removeAllListening();
  }
  

  public void prePropertyChange(PropertyEvent propertyEvent) {}
  

  public void propertyChanging(PropertyEvent propertyEvent) {}
  
  public void propertyChanged(PropertyEvent propertyEvent)
  {
    if (propertyEvent.getProperty() == m_element.name) {
      headerText = m_element.name.getStringValue();
      headerLabel.setText(headerText);
      revalidate();
      repaint();
    }
  }
  
  public void setBackground(Color color) {
    super.setBackground(color);
    if (containingPanel != null) containingPanel.setBackground(backgroundColor);
    if (headerLabel != null) headerLabel.setBackground(backgroundColor);
    if (headerPanel != null) headerPanel.setBackground(backgroundColor);
    if (componentElementPanel != null) componentElementPanel.setBackground(backgroundColor);
  }
  
  protected void generateGUI() {
    setOpaque(false);
    setLayout(new GridBagLayout());
    plus = new ImageIcon(CompositeElementPanel.class.getResource("images/plus.gif"));
    minus = new ImageIcon(CompositeElementPanel.class.getResource("images/minus.gif"));
    expandButton = new JButton();
    expandButton.setContentAreaFilled(false);
    expandButton.setMargin(new Insets(0, 0, 0, 0));
    expandButton.setFocusPainted(false);
    expandButton.setBorderPainted(false);
    expandButton.setBorder(null);
    expandButton.addActionListener(actionListener);
    glue = Box.createHorizontalGlue();
    if (headerLabel == null) {
      headerLabel = new JLabel();
      setHeaderLabel();
      headerLabel.setOpaque(false);
    }
    if (containingPanel == null) {
      containingPanel = new JPanel();
      containingPanel.setLayout(new BorderLayout());
      containingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      containingPanel.setOpaque(false);
    }
    if (headerPanel == null) {
      headerPanel = new GroupingPanel();
      headerPanel.setLayout(new GridBagLayout());
      headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      headerPanel.setOpaque(false);
    }
  }
  
  public void removeLabel() {
    shouldShowLabel = false;
    containingPanel.remove(headerPanel);
    expandComponentElementPanel();
    revalidate();
    repaint();
  }
  
  public void addLabel() {
    shouldShowLabel = true;
    containingPanel.add(headerPanel, "North");
    revalidate();
    repaint();
  }
  
  protected void updateGUI() {
    containingPanel.removeAll();
    headerPanel.removeAll();
    if (isExpanded) {
      expandButton.setIcon(minus);
    }
    else {
      expandButton.setIcon(plus);
    }
    headerLabel.setText(headerText);
    headerPanel.add(expandButton, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
    headerPanel.add(headerLabel, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 4, 0, 2), 0, 0));
    headerPanel.add(glue, new GridBagConstraints(2, 0, 1, 1, 1.0D, 1.0D, 18, 1, new Insets(0, 0, 0, 0), 0, 0));
    if (shouldShowLabel) {
      containingPanel.add(headerPanel, "North");
    }
    if (isExpanded) {
      containingPanel.add(componentElementPanel, "Center");
    }
    add(containingPanel, new GridBagConstraints(1, 0, 1, 1, 1.0D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
    setBackground(backgroundColor);
  }
  
  public boolean isExpanded()
  {
    if (isExpanded) {
      return true;
    }
    return false;
  }
  
  public void expandComponentElementPanel() {
    if (!isExpanded) {
      m_element.data.put(IS_EXPANDED_KEY, Boolean.TRUE);
      isExpanded = true;
      setHeaderLabel();
      expandButton.setIcon(minus);
      containingPanel.add(componentElementPanel, "Center");
      revalidate();
    }
  }
  
  public void setHeaderLabel() {
    if (headerLabel != null) {
      headerLabel.setText(headerText);
    }
  }
  
  public void reduceComponentElementPanel() {
    if (isExpanded) {
      m_element.data.put(IS_EXPANDED_KEY, Boolean.FALSE);
      isExpanded = false;
      setHeaderLabel();
      expandButton.setIcon(plus);
      containingPanel.remove(componentElementPanel);
      revalidate();
    }
  }
  
  public CompositeComponentElementPanel getComponentPanel()
  {
    return componentElementPanel;
  }
  
  public void addResponsePanel(JComponent toAdd, int position) {
    componentElementPanel.addElementPanel(toAdd, position);
  }
  
  public Element getElement() {
    return m_element;
  }
}
