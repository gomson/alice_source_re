package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.MainCompositeQuestionPanel;
import edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ResponseEditor;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel.DnDGrip;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Composite;
import edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import edu.cmu.cs.stage3.alice.core.response.IfElseInOrder;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class CompositeElementPanel extends DnDGroupingPanel implements CompositeComponentOwner, edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.core.event.PropertyListener
{
  protected CompositeComponentElementPanel componentElementPanel;
  protected Element m_element;
  protected ObjectArrayProperty m_components;
  protected BooleanProperty m_isCommentedOut;
  protected String headerText = Messages.getString("Composite_Element");
  protected JPanel headerPanel;
  protected JLabel closeBrace = new JLabel("}");
  protected JPanel containingPanel;
  protected JButton expandButton;
  protected JComponent nameInputField;
  protected javax.swing.Action expandAction;
  protected JLabel headerLabel;
  protected boolean isExpanded = true;
  protected Color backgroundColor = new Color(255, 255, 255);
  protected int m_depth = 0;
  
  protected ImageIcon plus;
  protected ImageIcon minus;
  protected java.awt.Component glue;
  protected AuthoringTool authoringTool;
  protected java.awt.event.ActionListener actionListener;
  protected java.awt.event.MouseListener elementMouseListener = new java.awt.event.MouseAdapter()
  {
    public void mouseReleased(MouseEvent ev) {
      if (((ev.isPopupTrigger()) || (ev.getButton() == 3) || (
        (System.getProperty("os.name") != null) && (!System.getProperty("os.name").startsWith("Windows")) && (ev.isControlDown()))) && 
        (getParent() != null)) {
        Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.getDefaultStructure(m_element);
        if ((!ForEachElementPanel.class.isAssignableFrom(getClass())) && (!LoopNElementPanel.class.isAssignableFrom(getClass()))) {
          Runnable dissolveRunnable = new Runnable() {
            public void run() {
              dissolve();
            }
          };
          edu.cmu.cs.stage3.util.StringObjectPair dissolveEntry = new edu.cmu.cs.stage3.util.StringObjectPair(Messages.getString("dissolve"), dissolveRunnable);
          if (structure != null) {
            structure.add(dissolveEntry);
          }
        }
        if (structure != null) {
          edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.createAndShowElementPopupMenu(m_element, structure, CompositeElementPanel.this, ev.getX(), ev.getY());
        }
      }
    }
  };
  


  protected static String IS_EXPANDED_KEY = "edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor IS_EXPANDED_KEY";
  
  public CompositeElementPanel()
  {
    headerText = Messages.getString("Element_Response");
    actionListener = new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        if (isExpanded) {
          reduceComponentElementPanel();
        }
        else {
          expandComponentElementPanel();
        }
      }
    };
    backgroundColor = getCustomBackgroundColor();
    generateGUI();
  }
  
  public void set(Element element, AuthoringTool authoringToolIn) {
    clean();
    super.reset();
    authoringTool = authoringToolIn;
    m_element = element;
    variableInit();
    startListening();
    setHeaderLabel();
    updateGUI();
    setDropTargets();
  }
  
  protected Color getCustomBackgroundColor() {
    return backgroundColor;
  }
  
  protected void setDropTargets() {
    headerLabel.setDropTarget(new DropTarget(headerLabel, componentElementPanel));
    setDropTarget(new DropTarget(this, componentElementPanel));
    containingPanel.setDropTarget(new DropTarget(containingPanel, componentElementPanel));
    headerPanel.setDropTarget(new DropTarget(headerPanel, componentElementPanel));
    grip.setDropTarget(new DropTarget(grip, componentElementPanel));
    glue.setDropTarget(new DropTarget(glue, componentElementPanel));
    expandButton.setDropTarget(new DropTarget(expandButton, componentElementPanel));
  }
  
  protected void variableInit() {
    Object isExpandedValue = m_element.data.get(IS_EXPANDED_KEY);
    backgroundColor = getCustomBackgroundColor();
    if ((isExpandedValue instanceof Boolean)) {
      isExpanded = ((Boolean)isExpandedValue).booleanValue();
    }
    setTransferable(new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable(m_element));
    if ((m_element instanceof CompositeResponse)) {
      CompositeResponse proxy = (CompositeResponse)m_element;
      m_components = componentResponses;
      m_isCommentedOut = isCommentedOut;
      componentElementPanel = new edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.CompositeComponentResponsePanel();
      componentElementPanel.set(m_components, this, authoringTool);
      componentElementPanel.setBackground(backgroundColor);
      addDragSourceComponent(componentElementPanel);
      componentElementPanel.addMouseListener(elementMouseListener);
    }
    else if ((m_element instanceof Composite)) {
      Composite proxy = (Composite)m_element;
      m_components = components;
      m_isCommentedOut = isCommentedOut;
      componentElementPanel = new edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.CompositeComponentQuestionPanel();
      componentElementPanel.set(m_components, this, authoringTool);
      componentElementPanel.setBackground(backgroundColor);
      addDragSourceComponent(componentElementPanel);
      componentElementPanel.addMouseListener(elementMouseListener);
    }
  }
  
  public java.awt.Component getGrip() {
    return grip;
  }
  
  public java.awt.Container getParent() {
    return super.getParent();
  }
  
  protected void startListening() {
    if (m_element != null) {
      m_element.name.addPropertyListener(this);
    }
    if (m_isCommentedOut != null) {
      m_isCommentedOut.addPropertyListener(this);
    }
  }
  
  protected void stopListening() {
    if (m_element != null) {
      m_element.name.removePropertyListener(this);
    }
    if (m_isCommentedOut != null) {
      m_isCommentedOut.removePropertyListener(this);
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
    removeDragSourceComponent(componentElementPanel);
    componentElementPanel.removeMouseListener(elementMouseListener);
    grip.setDropTarget(null);
    removeDragSourceComponent(glue);
    glue.removeMouseListener(elementMouseListener);
    removeMouseListener(elementMouseListener);
    grip.removeMouseListener(elementMouseListener);
    expandButton.removeActionListener(actionListener);
    setTransferable(null);
    if (headerLabel != null) {
      headerLabel.removeMouseListener(elementMouseListener);
      removeDragSourceComponent(headerLabel);
    }
    if (containingPanel != null) {
      containingPanel.removeMouseListener(elementMouseListener);
      removeDragSourceComponent(containingPanel);
    }
    if (headerPanel != null) {
      headerPanel.removeMouseListener(elementMouseListener);
      removeDragSourceComponent(headerPanel);
    }
  }
  
  public void die() {
    clean();
    removeAllListening();
  }
  
  public void dissolve() {
    if ((m_element.getParent() instanceof CompositeResponse)) {
      authoringTool.getUndoRedoStack().startCompound();
      CompositeResponse parent = (CompositeResponse)m_element.getParent();
      int index = componentResponses.indexOf(m_element);
      if (((parent instanceof IfElseInOrder)) && 
        (!componentResponses.contains(m_element))) {
        index = elseComponentResponses.indexOf(m_element);
      }
      
      Object[] responses = m_components.getArrayValue();
      for (int i = 0; i < responses.length; i++) {
        if ((responses[i] instanceof Response)) {
          Response currentResponse = (Response)responses[i];
          currentResponse.removeFromParent();
          currentResponse.setParent(parent);
          componentResponses.add(index + i, currentResponse);
        }
      }
      m_element.removeFromParent();
      authoringTool.getUndoRedoStack().stopCompound();
    }
    else if ((m_element.getParent() instanceof Composite))
    {
      authoringTool.getUndoRedoStack().startCompound();
      Composite parent = (Composite)m_element.getParent();
      int index = parent.getIndexOfChild(m_element);
      if (((parent instanceof IfElse)) && 
        (!components.contains(m_element))) {
        index = elseComponents.indexOf(m_element);
      }
      

      Object[] questions = m_components.getArrayValue();
      for (int i = 0; i < questions.length; i++) {
        if ((questions[i] instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component)) {
          edu.cmu.cs.stage3.alice.core.question.userdefined.Component currentQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.Component)questions[i];
          currentQuestion.removeFromParent();
          currentQuestion.setParent(parent);
          components.add(index + i, currentQuestion);
        }
      }
      m_element.removeFromParent();
      authoringTool.getUndoRedoStack().stopCompound();
    }
    else if ((m_element.getParent() instanceof UserDefinedQuestion)) {
      authoringTool.getUndoRedoStack().startCompound();
      UserDefinedQuestion parent = (UserDefinedQuestion)m_element.getParent();
      int index = parent.getIndexOfChild(m_element);
      
      Object[] questions = m_components.getArrayValue();
      for (int i = 0; i < questions.length; i++) {
        if ((questions[i] instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component)) {
          edu.cmu.cs.stage3.alice.core.question.userdefined.Component currentQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.Component)questions[i];
          currentQuestion.removeFromParent();
          currentQuestion.setParent(parent);
          components.add(index + i, currentQuestion);
        }
      }
      m_element.removeFromParent();
      authoringTool.getUndoRedoStack().stopCompound();
    }
  }
  

  public void prePropertyChange(PropertyEvent propertyEvent) {}
  
  public void propertyChanging(PropertyEvent propertyEvent) {}
  
  public void propertyChanged(PropertyEvent propertyEvent)
  {
    if (propertyEvent.getProperty() == m_isCommentedOut) {
      revalidate();
      repaint();
    }
    else {
      updateGUI();
    }
  }
  
  public void setBackground(Color color) {
    super.setBackground(color);
    backgroundColor = color;
    if (containingPanel != null) containingPanel.setBackground(backgroundColor);
    if (headerLabel != null) headerLabel.setBackground(backgroundColor);
    if (headerPanel != null) headerPanel.setBackground(backgroundColor);
    if (componentElementPanel != null) componentElementPanel.setBackground(backgroundColor);
  }
  
  protected void generateGUI() {
    setOpaque(false);
    plus = new ImageIcon(CompositeElementPanel.class.getResource("images/plus.gif"));
    minus = new ImageIcon(CompositeElementPanel.class.getResource("images/minus.gif"));
    expandButton = new JButton();
    expandButton.setContentAreaFilled(false);
    expandButton.setMargin(new Insets(0, 0, 0, 0));
    expandButton.setFocusPainted(false);
    expandButton.setBorderPainted(false);
    expandButton.setBorder(null);
    expandButton.addActionListener(actionListener);
    glue = javax.swing.Box.createHorizontalGlue();
    addDragSourceComponent(glue);
    glue.addMouseListener(elementMouseListener);
    addMouseListener(elementMouseListener);
    grip.addMouseListener(elementMouseListener);
    if (headerLabel == null) {
      headerLabel = new JLabel();
      setHeaderLabel();
      headerLabel.setOpaque(false);
      headerLabel.addMouseListener(elementMouseListener);
      addDragSourceComponent(headerLabel);
    }
    if (containingPanel == null) {
      containingPanel = new JPanel();
      containingPanel.setLayout(new java.awt.BorderLayout());
      containingPanel.addMouseListener(elementMouseListener);
      containingPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
      containingPanel.setOpaque(false);
      addDragSourceComponent(containingPanel);
    }
    if (headerPanel == null) {
      headerPanel = new JPanel();
      headerPanel.setLayout(new java.awt.GridBagLayout());
      headerPanel.addMouseListener(elementMouseListener);
      headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
      headerPanel.setOpaque(false);
      addDragSourceComponent(headerPanel);
    }
  }
  
  protected void restoreDrag() {
    addDragSourceComponent(glue);
    addDragSourceComponent(headerPanel);
    addDragSourceComponent(containingPanel);
    addDragSourceComponent(headerLabel);
  }
  
  protected void updateGUI() {
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
    headerPanel.add(glue, new GridBagConstraints(3, 0, 1, 1, 1.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
    containingPanel.add(headerPanel, "North");
    if (isExpanded) {
      containingPanel.add(componentElementPanel, "Center");
      if (ResponseEditor.IS_JAVA) {
        containingPanel.add(closeBrace, "South");
      }
    }
    add(containingPanel, "Center");
    setBackground(getCustomBackgroundColor());
  }
  
  public boolean isExpanded() {
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
      if (ResponseEditor.IS_JAVA) {
        containingPanel.add(closeBrace, "South");
      }
      revalidate();
    }
  }
  
  public void setHeaderLabel()
  {
    if (headerLabel != null) {
      headerLabel.setText(headerText);
      if (ResponseEditor.IS_JAVA) {
        if (!isExpanded) {
          headerLabel.setText(headerText + " { " + getDots() + " }");
        }
        else {
          headerLabel.setText(headerText + " {");
        }
      }
    }
  }
  
  protected static int getEmptyRows(Object[] elements)
  {
    int count = 0;
    for (int i = 0; i < elements.length; i++) {
      if ((elements[i] instanceof CompositeResponse)) {
        if (componentResponses.size() == 0) {
          count++;
        }
        if (((elements[i] instanceof IfElseInOrder)) && 
          (elseComponentResponses.size() == 0)) {
          count++;
        }
      }
      else if ((elements[i] instanceof Composite)) {
        if (components.size() == 0) {
          count++;
        }
        if (((elements[i] instanceof IfElse)) && 
          (elseComponents.size() == 0)) {
          count++;
        }
      }
    }
    
    return count;
  }
  
  protected static int getTotalRowsToRenderForIfElse(Element ifElse, ObjectArrayProperty components) {
    int total = 0;
    Vector potentiallyEmpty = new Vector();
    if ((ifElse instanceof IfElseInOrder)) {
      IfElseInOrder ifElseResponse = (IfElseInOrder)ifElse;
      for (int i = 0; i < components.size(); i++) {
        if ((components.get(i) instanceof CompositeResponse)) {
          Element[] ds = ((Element)components.get(i)).getDescendants(Response.class);
          total += ds.length;
          total += ((Element)components.get(i)).getDescendants(IfElseInOrder.class).length;
          potentiallyEmpty.addAll(Arrays.asList(((Element)components.get(i)).getDescendants(CompositeResponse.class)));
          if (CompositeElementEditor.IS_JAVA) {
            total += ((Element)components.get(i)).getDescendants(CompositeResponse.class).length;
          }
        } else {
          total++;
        }
      }
    } else if ((ifElse instanceof IfElse)) {
      IfElse ifElseQuestion = (IfElse)ifElse;
      for (int i = 0; i < components.size(); i++) {
        total++;
        if ((components.get(i) instanceof IfElse)) {
          total++;
        }
        if ((components.get(i) instanceof Composite)) {
          potentiallyEmpty.add(components.get(i));
        }
        total += ((Element)components.get(i)).getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class).length;
        total += ((Element)components.get(i)).getDescendants(IfElse.class).length;
        potentiallyEmpty.addAll(Arrays.asList(((Element)components.get(i)).getDescendants(Composite.class)));
        if (CompositeElementEditor.IS_JAVA) {
          total += ((Element)components.get(i)).getDescendants(Composite.class).length;
        }
      }
    }
    int emptyRows = getEmptyRows(potentiallyEmpty.toArray());
    total += emptyRows;
    return total;
  }
  
  protected String getHTMLColorString(Color color) {
    String r = Integer.toHexString(color.getRed());
    String g = Integer.toHexString(color.getGreen());
    String b = Integer.toHexString(color.getBlue());
    
    if (r.length() == 1) {
      r = "0" + r;
    }
    if (g.length() == 1) {
      g = "0" + g;
    }
    if (b.length() == 1) {
      b = "0" + b;
    }
    return new String("#" + r + g + b);
  }
  
  protected String getHeaderHTML() {
    return GUIFactory.getHTMLStringForComponent(headerPanel);
  }
  
  public static int getTotalHTMLRows(Element element) {
    int totalRows = 0;
    if ((element instanceof Response)) {
      if ((element instanceof IfElseInOrder)) {
        totalRows = getTotalRowsToRenderForIfElse(element, componentResponses);
      } else {
        totalRows = element.getDescendants(Response.class).length - 1;
        totalRows += element.getDescendants(IfElseInOrder.class).length;
        totalRows += getEmptyRows(element.getDescendants(CompositeResponse.class));
        
        if (CompositeElementEditor.IS_JAVA) {
          totalRows += element.getDescendants(CompositeResponse.class).length - 1;
        }
      }
    } else if (((element instanceof Composite)) || ((element instanceof UserDefinedQuestion))) {
      if ((element instanceof IfElse)) {
        totalRows = getTotalRowsToRenderForIfElse(element, components);
      } else {
        totalRows = element.getDescendants(edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class).length - 1;
        totalRows += element.getDescendants(IfElse.class).length;
        totalRows += getEmptyRows(element.getDescendants(Composite.class));
        if (CompositeElementEditor.IS_JAVA) {
          totalRows += element.getDescendants(Composite.class).length - 1;
        }
      }
    }
    return totalRows;
  }
  
  public void getHTML(StringBuffer toWriteTo, int colSpan, boolean useColor, boolean isDisabled) {
    int totalRows = 0;
    String colorString = "";
    String borderColorString = getHTMLColorString(Color.lightGray);
    String styleString = "";
    String strikeStart = "";
    String strikeEnd = "";
    if (!isDisabled) {
      isDisabled = isDisabled();
    }
    if (useColor) {
      if (isDisabled) {
        colorString = " bgcolor=\"" + getHTMLColorString(AuthoringToolResources.getColor("disabledHTML")) + "\"";
      } else {
        colorString = " bgcolor=\"" + getHTMLColorString(getCustomBackgroundColor()) + "\"";
      }
    }
    if (isDisabled) {
      strikeStart = "<strike><font color=\"" + getHTMLColorString(AuthoringToolResources.getColor("disabledHTMLText")) + "\">";
      strikeEnd = "</font></strike>";
    }
    totalRows = getTotalHTMLRows(m_element);
    if ((this instanceof MainCompositeQuestionPanel)) {
      totalRows++;
    }
    styleString = " style=\"border-left: 1px solid " + borderColorString + "; border-top: 1px solid " + borderColorString + "; border-right: 1px solid " + borderColorString + "\"";
    toWriteTo.append("<td colspan=\"" + colSpan + "\"" + colorString + styleString + ">&nbsp;&nbsp;");
    
    toWriteTo.append(strikeStart + getHeaderHTML() + strikeEnd);
    if (((this instanceof IfElseElementPanel)) || (CompositeElementEditor.IS_JAVA)) {
      styleString = " style=\"border-left: 1px solid " + borderColorString + "\"";
    } else {
      styleString = " style=\"border-left: 1px solid " + borderColorString + "; border-bottom: 1px solid " + borderColorString + "\"";
    }
    toWriteTo.append("</td>\n</tr>\n<tr>\n<td width=\"20\" rowspan=\"" + totalRows + "\"" + colorString + styleString + ">&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
    for (int i = 0; i < componentElementPanel.getElementComponentCount(); i++) {
      if (i > 0) {
        toWriteTo.append("<tr>\n");
      }
      java.awt.Component currentComponent = componentElementPanel.getComponent(i);
      if ((currentComponent instanceof CompositeElementPanel)) {
        ((CompositeElementPanel)currentComponent).getHTML(toWriteTo, colSpan - 1, useColor, isDisabled);
      } else {
        String componentColorString = "";
        String componentStrikeStart = "";
        String componentStrikeEnd = "";
        boolean isComponentDisabled = (isDisabled) || (((ComponentElementPanel)currentComponent).isDisabled());
        if ((currentComponent instanceof ComponentElementPanel)) {
          if (isComponentDisabled) {
            componentColorString = " bgcolor=\"" + getHTMLColorString(AuthoringToolResources.getColor("disabledHTML")) + "\"";
            componentStrikeStart = "<strike><font color=\"" + getHTMLColorString(AuthoringToolResources.getColor("disabledHTMLText")) + "\">";
            componentStrikeEnd = "</font></strike>";
          } else {
            componentColorString = " bgcolor=\"" + getHTMLColorString(((ComponentElementPanel)currentComponent).getCustomBackgroundColor()) + "\"";
          }
        }
        styleString = " style=\"border-bottom: 1px solid " + borderColorString + "; border-right: 1px solid " + borderColorString + "; border-left: 1px solid " + borderColorString + "; border-top: 1px solid " + borderColorString + "\"";
        toWriteTo.append("<td width=\"100%\" colspan=\"" + (colSpan - 1) + "\"" + componentColorString + styleString + ">&nbsp;&nbsp;" + componentStrikeStart + GUIFactory.getHTMLStringForComponent(currentComponent) + componentStrikeEnd + "</td>\n");
      }
    }
    

    if (componentElementPanel.getElementComponentCount() == 0) {
      if (((this instanceof IfElseElementPanel)) || (CompositeElementEditor.IS_JAVA)) {
        styleString = " style=\"border-right: 1 solid " + borderColorString + "\"";
      } else {
        styleString = " style=\"border-right: 1 solid " + borderColorString + "; border-bottom: 1 solid " + borderColorString + "\"";
      }
      toWriteTo.append("<td width=\"100%\" colspan=\"" + (colSpan - 1) + "\"" + colorString + styleString + ">" + strikeStart + Messages.getString("_i__Do_Nothing___i_") + strikeEnd + "</td>\n</tr>\n");
    }
    if ((this instanceof MainCompositeQuestionPanel)) {
      MainCompositeQuestionPanel mainQuestion = (MainCompositeQuestionPanel)this;
      styleString = " style=\"border-bottom: 1 solid " + borderColorString + "; border-right: 1 solid " + borderColorString + "; border-left: 1 solid " + borderColorString + ";\"";
      colorString = " bgcolor=\"" + getHTMLColorString(AuthoringToolResources.getColor("Return")) + "\"";
      toWriteTo.append("<tr>\n<td width=\"100%\" colspan=\"" + colSpan + "\"" + colorString + styleString + ">" + 
        GUIFactory.getHTMLStringForComponent(returnPanel) + 
        "</td>\n</tr>\n");
    }
    if ((this instanceof IfElseElementPanel)) {
      IfElseElementPanel ifElse = (IfElseElementPanel)this;
      if ((m_element instanceof IfElse)) {
        totalRows = getTotalRowsToRenderForIfElse(m_element, m_element).elseComponents);
      } else if ((m_element instanceof IfElseInOrder)) {
        totalRows = getTotalRowsToRenderForIfElse(m_element, m_element).elseComponentResponses);
      }
      styleString = " style=\"border-left: 1 solid " + borderColorString + "; border-right: 1 solid " + borderColorString + "\"";
      
      toWriteTo.append("<tr>\n<td colspan=\"" + colSpan + "\"" + colorString + styleString + ">&nbsp;&nbsp;" + strikeStart + GUIFactory.getHTMLStringForComponent(elsePanel) + strikeEnd + "</td>\n");
      if (CompositeElementEditor.IS_JAVA) {
        styleString = " style=\"border-left: 1 solid " + borderColorString + "\"";
      } else {
        styleString = " style=\"border-left: 1 solid " + borderColorString + "; border-bottom: 1 solid " + borderColorString + "\"";
      }
      toWriteTo.append("</tr>\n<tr>\n<td width=\"20\" rowspan=\"" + totalRows + "\"" + colorString + styleString + ">&nbsp;</td>\n");
      for (int i = 0; i < elseComponentPanel.getElementComponentCount(); i++) {
        if (i > 0) {
          toWriteTo.append("<tr>\n");
        }
        java.awt.Component currentComponent = elseComponentPanel.getComponent(i);
        if ((currentComponent instanceof CompositeElementPanel)) {
          ((CompositeElementPanel)currentComponent).getHTML(toWriteTo, colSpan - 1, useColor, isDisabled);
        } else {
          String componentColorString = "";
          String componentStrikeStart = "";
          String componentStrikeEnd = "";
          boolean isComponentDisabled = (isDisabled) || (((ComponentElementPanel)currentComponent).isDisabled());
          if ((currentComponent instanceof ComponentElementPanel)) {
            if (isComponentDisabled) {
              componentColorString = " bgcolor=\"" + getHTMLColorString(AuthoringToolResources.getColor("disabledHTML")) + "\"";
              componentStrikeStart = "<strike><font color=\"" + getHTMLColorString(AuthoringToolResources.getColor("disabledHTMLText")) + "\">";
              componentStrikeEnd = "</font></strike>";
            } else {
              componentColorString = " bgcolor=\"" + getHTMLColorString(((ComponentElementPanel)currentComponent).getCustomBackgroundColor()) + "\"";
            }
          }
          styleString = " style=\"border-bottom: 1 solid " + borderColorString + "; border-right: 1 solid " + borderColorString + "; border-left: 1 solid " + borderColorString + "; border-top: 1 solid " + borderColorString + "\"";
          toWriteTo.append("<td colspan=\"" + (colSpan - 1) + "\"" + componentColorString + styleString + ">&nbsp;&nbsp;" + componentStrikeStart + GUIFactory.getHTMLStringForComponent(currentComponent) + componentStrikeEnd + "</td>\n");
        }
        

        toWriteTo.append("</tr>\n");
      }
      if (elseComponentPanel.getElementComponentCount() == 0) {
        if (!CompositeElementEditor.IS_JAVA) {
          styleString = " style=\"border-right: 1 solid " + borderColorString + "; border-bottom: 1 solid " + borderColorString + "\"";
        } else {
          styleString = " style=\"border-right: 1 solid " + borderColorString + "\"";
        }
        toWriteTo.append("<td colspan=\"" + (colSpan - 1) + "\"" + colorString + styleString + ">" + strikeStart + Messages.getString("_i__Do_Nothing___i_") + strikeEnd + "</td>\n</tr>\n");
      }
    }
    if (CompositeElementEditor.IS_JAVA) {
      styleString = " style=\"border-left: 1 solid " + borderColorString + "; border-bottom: 1 solid " + borderColorString + "; border-right: 1 solid " + borderColorString + "\"";
      toWriteTo.append("<tr><td colspan=\"" + colSpan + "\"" + colorString + styleString + ">" + strikeStart + "<b>&nbsp;&nbsp;}</b>" + strikeEnd + "</td></tr>");
    }
  }
  
  public String getDots()
  {
    String dots = "";
    for (int i = 0; i < m_components.size(); i++) {
      if (i == 0) {
        dots = dots + ".";
      }
      else {
        dots = dots + " .";
      }
    }
    return dots;
  }
  
  public void reduceComponentElementPanel() {
    if (isExpanded) {
      m_element.data.put(IS_EXPANDED_KEY, Boolean.FALSE);
      isExpanded = false;
      setHeaderLabel();
      expandButton.setIcon(plus);
      containingPanel.remove(componentElementPanel);
      if (ResponseEditor.IS_JAVA) {
        containingPanel.remove(closeBrace);
      }
      revalidate();
    }
  }
  
  public CompositeComponentElementPanel getComponentPanel()
  {
    return componentElementPanel;
  }
  

  protected void dndInit() {}
  
  public void addResponsePanel(JComponent toAdd, int position)
  {
    componentElementPanel.addElementPanel(toAdd, position);
  }
  
  public Element getElement() {
    return m_element;
  }
  
  public boolean isDisabled() {
    boolean isEnabledValue = true;
    if ((m_element instanceof Response)) {
      if (m_element).isCommentedOut.get() != null) {
        isEnabledValue = !m_element).isCommentedOut.getBooleanValue().booleanValue();
      }
    }
    else if (((m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component)) && 
      (m_element).isCommentedOut.get() != null)) {
      isEnabledValue = !m_element).isCommentedOut.getBooleanValue().booleanValue();
    }
    
    return !isEnabledValue;
  }
  
  public void paintForeground(java.awt.Graphics g) {
    super.paintForeground(g);
    if ((m_element != null) && 
      (isDisabled())) {
      java.awt.Rectangle bounds = new java.awt.Rectangle(0, 0, getWidth(), getHeight());
      edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.paintDisabledEffect(g, bounds);
    }
  }
}
