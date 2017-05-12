package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.CompositeComponentResponsePanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse;
import edu.cmu.cs.stage3.alice.core.response.IfElseInOrder;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public abstract class IfElseElementPanel extends CompositeElementPanel
{
  protected JPanel ifElsePanel;
  protected ObjectArrayProperty m_elseComponents;
  protected CompositeComponentElementPanel elseComponentPanel;
  protected JPanel elsePanel;
  protected JLabel elseEndBrace = new JLabel("}");
  protected JLabel elseLabel;
  protected JLabel endHeader;
  protected javax.swing.JComponent conditionalInput;
  protected BooleanProperty m_condition;
  protected IfElseDropTargetHandler ifElseDropHandler = new IfElseDropTargetHandler();
  protected Component elseGlue;
  protected static BufferedImage conditionalBackgroundImage;
  
  public IfElseElementPanel() {
    backgroundColor = AuthoringToolResources.getColor("IfElseInOrder");
    headerText = "If";
  }
  
  public Component getIfDoNothingPanel() {
    Component doNothing = componentElementPanel.getDropPanel();
    if (doNothing.getParent() == componentElementPanel) {
      return doNothing;
    }
    
    return null;
  }
  
  public Component getElseDoNothingPanel()
  {
    Component doNothing = elseComponentPanel.getDropPanel();
    if (doNothing.getParent() == elseComponentPanel) {
      return doNothing;
    }
    
    return null;
  }
  

  protected void setDropTargets()
  {
    headerLabel.setDropTarget(new DropTarget(headerLabel, componentElementPanel));
    setDropTarget(new DropTarget(this, ifElseDropHandler));
    containingPanel.setDropTarget(new DropTarget(containingPanel, ifElseDropHandler));
    headerPanel.setDropTarget(new DropTarget(headerPanel, componentElementPanel));
    grip.setDropTarget(new DropTarget(grip, ifElseDropHandler));
    glue.setDropTarget(new DropTarget(glue, ifElseDropHandler));
    expandButton.setDropTarget(new DropTarget(expandButton, componentElementPanel));
    elsePanel.setDropTarget(new DropTarget(elsePanel, elseComponentPanel));
    elseLabel.setDropTarget(new DropTarget(elseLabel, elseComponentPanel));
    endHeader.setDropTarget(new DropTarget(endHeader, elseComponentPanel));
    elseGlue.setDropTarget(new DropTarget(elseGlue, elseComponentPanel));
    ifElsePanel.setDropTarget(new DropTarget(ifElsePanel, ifElseDropHandler));
    elseEndBrace.setDropTarget(new DropTarget(elseEndBrace, ifElseDropHandler));
  }
  
  protected void variableInit() {
    super.variableInit();
    if ((m_element instanceof IfElseInOrder)) {
      IfElseInOrder proxy = (IfElseInOrder)m_element;
      m_condition = condition;
      m_elseComponents = elseComponentResponses;
      elseComponentPanel = new CompositeComponentResponsePanel();
      elseComponentPanel.set(m_elseComponents, this, authoringTool);
      elseComponentPanel.setBackground(backgroundColor);
      addDragSourceComponent(elseComponentPanel);
      elseComponentPanel.addMouseListener(elementMouseListener);
    }
    else if ((m_element instanceof IfElse)) {
      IfElse proxy = (IfElse)m_element;
      m_condition = condition;
      m_elseComponents = elseComponents;
      elseComponentPanel = new edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.CompositeComponentQuestionPanel();
      elseComponentPanel.set(m_elseComponents, this, authoringTool);
      elseComponentPanel.setBackground(backgroundColor);
      addDragSourceComponent(elseComponentPanel);
      elseComponentPanel.addMouseListener(elementMouseListener);
    }
  }
  
  public void release() {
    super.release();
    if (elseComponentPanel != null) {
      elseComponentPanel.release();
    }
  }
  
  protected void startListening() {
    super.startListening();
    if (m_condition != null) {
      m_condition.addPropertyListener(this);
    }
  }
  
  protected void stopListening() {
    super.stopListening();
    if (m_condition != null) {
      m_condition.removePropertyListener(this);
    }
  }
  
  public void goToSleep() {
    super.goToSleep();
    if (elseComponentPanel != null) {
      elseComponentPanel.goToSleep();
    }
  }
  
  public void wakeUp() {
    super.wakeUp();
    if (elseComponentPanel != null) {
      elseComponentPanel.wakeUp();
    }
  }
  
  public void clean() {
    super.clean();
    if (elseComponentPanel != null) {
      containingPanel.remove(elseComponentPanel);
      elseComponentPanel.release();
      elseComponentPanel = null;
    }
  }
  
  protected void removeAllListening() {
    super.removeAllListening();
    removeDragSourceComponent(elseComponentPanel);
    elseComponentPanel.removeMouseListener(elementMouseListener);
    removeDragSourceComponent(ifElsePanel);
    removeDragSourceComponent(elsePanel);
  }
  
  public void setHeaderLabel() {
    if (headerLabel != null) {
      headerLabel.setText(headerText);
      if (CompositeElementEditor.IS_JAVA) {
        headerLabel.setText("if (");
      }
    }
    if (elseLabel != null) {
      elseLabel.setText("Else");
      if (CompositeElementEditor.IS_JAVA) {
        elseLabel.setText("} else {");
      }
    }
    if (endHeader != null) {
      endHeader.setText("");
      if (CompositeElementEditor.IS_JAVA) {
        if (!isExpanded) {
          endHeader.setText(") { " + getDots() + " }");
        }
        else {
          endHeader.setText(") {");
        }
      }
    }
  }
  
  public String getDots()
  {
    String dots = "";
    int elses = m_elseComponents.size();
    for (int i = 0; i < m_components.size() + elses; i++) {
      if (i == 0) {
        dots = dots + ".";
      }
      else {
        dots = dots + " .";
      }
    }
    return dots;
  }
  
  protected void generateGUI() {
    super.generateGUI();
    if (elseGlue == null) {
      elseGlue = javax.swing.Box.createHorizontalGlue();
    }
    if (endHeader == null) {
      endHeader = new JLabel();
    }
    if (elseLabel == null) {
      elseLabel = new JLabel();
      Font localFont = elseLabel.getFont();
    }
    if (ifElsePanel == null) {
      ifElsePanel = new JPanel();
      ifElsePanel.setBorder(null);
      ifElsePanel.setLayout(new GridBagLayout());
      ifElsePanel.setOpaque(false);
    }
    if (elsePanel == null) {
      elsePanel = new JPanel();
      elsePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 13, 0, 0));
      elsePanel.setLayout(new GridBagLayout());
      elsePanel.add(elseLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
      elsePanel.add(elseGlue, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
      elsePanel.setOpaque(false);
    }
  }
  
  protected void restoreDrag() {
    super.restoreDrag();
    addDragSourceComponent(endHeader);
    addDragSourceComponent(elseLabel);
    addDragSourceComponent(ifElsePanel);
    addDragSourceComponent(elsePanel);
    addDragSourceComponent(elseGlue);
  }
  
  protected void updateGUI() {
    setHeaderLabel();
    containingPanel.removeAll();
    headerPanel.removeAll();
    restoreDrag();
    if (isExpanded) {
      expandButton.setIcon(minus);
    }
    else {
      expandButton.setIcon(plus);
    }
    headerPanel.add(expandButton, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
    headerPanel.add(headerLabel, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    PopupItemFactory pif = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(m_condition);
    conditionalInput = GUIFactory.getPropertyViewController(m_condition, true, true, AuthoringToolResources.shouldGUIOmitPropertyName(m_condition), pif);
    headerPanel.add(conditionalInput, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    headerPanel.add(endHeader, new GridBagConstraints(4, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    headerPanel.add(glue, new GridBagConstraints(5, 0, 1, 1, 1.0D, 0.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
    ifElsePanel.removeAll();
    ifElsePanel.add(componentElementPanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
    ifElsePanel.add(elsePanel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
    ifElsePanel.add(elseComponentPanel, new GridBagConstraints(0, 2, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
    if (isExpanded) {
      containingPanel.add(ifElsePanel, "Center");
      if (CompositeElementEditor.IS_JAVA) {
        containingPanel.add(closeBrace, "South");
      }
    }
    containingPanel.add(headerPanel, "North");
    add(containingPanel, "Center");
    setOpaque(false);
    setBackground(backgroundColor);
  }
  
  public void expandComponentElementPanel()
  {
    if (!isExpanded) {
      m_element.data.put(IS_EXPANDED_KEY, Boolean.TRUE);
      isExpanded = true;
      setHeaderLabel();
      expandButton.setIcon(minus);
      containingPanel.add(ifElsePanel, "Center");
      if (CompositeElementEditor.IS_JAVA) {
        containingPanel.add(closeBrace, "South");
      }
      revalidate();
    }
  }
  
  public void reduceComponentElementPanel()
  {
    if (isExpanded) {
      m_element.data.put(IS_EXPANDED_KEY, Boolean.FALSE);
      isExpanded = false;
      setHeaderLabel();
      expandButton.setIcon(plus);
      containingPanel.remove(ifElsePanel);
      if (CompositeElementEditor.IS_JAVA) {
        containingPanel.remove(closeBrace);
      }
      revalidate();
    }
  }
  
  protected class IfElseDropTargetHandler implements java.awt.dnd.DropTargetListener {
    protected IfElseDropTargetHandler() {}
    
    protected boolean onIf = true;
    
    protected boolean isInIf(DropTargetDragEvent dtde) {
      Point panelSpacePoint = SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), IfElseElementPanel.this);
      Point elsePoint = SwingUtilities.convertPoint(elsePanel, 0, 0, IfElseElementPanel.this);
      if (y < y) {
        onIf = true;
      }
      else {
        onIf = false;
      }
      return onIf;
    }
    
    public void dragEnter(DropTargetDragEvent dtde) {
      if (isInIf(dtde)) {
        componentElementPanel.dragEnter(dtde);
      }
      else {
        elseComponentPanel.dragEnter(dtde);
      }
    }
    
    public void dragExit(DropTargetEvent dtde) {
      if (onIf) {
        componentElementPanel.dragExit(dtde);
      }
      else {
        elseComponentPanel.dragExit(dtde);
      }
    }
    
    public void dragOver(DropTargetDragEvent dtde) {
      if (isInIf(dtde)) {
        componentElementPanel.dragOver(dtde);
      }
      else {
        elseComponentPanel.dragOver(dtde);
      }
    }
    
    public void drop(DropTargetDropEvent dtde) {
      if (onIf) {
        componentElementPanel.drop(dtde);
      }
      else {
        elseComponentPanel.drop(dtde);
      }
    }
    
    public void dropActionChanged(DropTargetDragEvent dtde) {
      if (isInIf(dtde)) {
        componentElementPanel.dropActionChanged(dtde);
      }
      else {
        elseComponentPanel.dropActionChanged(dtde);
      }
    }
  }
  



  protected static Dimension conditionalBackgroundImageSize = new Dimension(-1, -1);
  
  protected void createBackgroundImage(int width, int height) {
    conditionalBackgroundImageSize.setSize(width, height);
    conditionalBackgroundImage = new BufferedImage(width, height, 2);
    Graphics2D g = (Graphics2D)conditionalBackgroundImage.getGraphics();
    g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
    g.setColor(backgroundColor);
    g.fillRect(0, 0, width, height);
  }
  
  protected void paintTextureEffect(Graphics g, Rectangle bounds) {
    if ((width > conditionalBackgroundImageSizewidth) || (height > conditionalBackgroundImageSizeheight)) {
      createBackgroundImage(width, height);
    }
    g.setClip(x, y, width, height);
    g.drawImage(conditionalBackgroundImage, x, y, this);
  }
}
