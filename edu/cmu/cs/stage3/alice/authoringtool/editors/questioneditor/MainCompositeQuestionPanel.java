package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.ComponentElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Return;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.dnd.DropTarget;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MainCompositeQuestionPanel
  extends MainCompositeElementPanel
{
  public ComponentQuestionPanel returnPanel;
  protected Return returnQuestion;
  protected JPanel questionArea;
  public MainCompositeQuestionPanel() {}
  
  protected class MainCompositeComponentQuestionPanel
    extends CompositeComponentQuestionPanel
  {
    protected MainCompositeComponentQuestionPanel() {}
    
    protected void updateGUI()
    {
      if (componentElements.size() > 1) {
        removeAll();
        resetGUI();
        for (int i = 0; i < componentElements.size(); i++) {
          Element currentElement = (Element)componentElements.getArrayValue()[i];
          if (currentElement != returnQuestion) {
            Component toAdd = makeGUI(currentElement);
            if (toAdd != null) {
              addElementPanel(toAdd, i);
            }
          }
        }
      }
      else {
        addDropTrough();
      }
      revalidate();
      repaint();
    }
    
    protected boolean componentsIsEmpty() {
      return componentElements.size() == 1;
    }
    
    protected int getLastElementLocation()
    {
      return componentElements.size() - 1;
    }
    
    protected boolean checkGUI() {
      Component[] c = getComponents();
      Element[] elements = (Element[])componentElements.get();
      int elementCount = getElementComponentCount();
      boolean aOkay = elements.length - 1 == elementCount;
      if (aOkay)
      {
        for (int i = 0; i < elements.length - 1; i++) {
          if ((c[i] instanceof CompositeElementPanel)) {
            if (i < elements.length - 1) {
              if (((CompositeElementPanel)c[i]).getElement() != elements[i]) {
                aOkay = false;
                break;
              }
            }
            else {
              aOkay = false;
              break;
            }
          }
          if ((c[i] instanceof ComponentElementPanel)) {
            if (i < elements.length - 1) {
              if (((ComponentElementPanel)c[i]).getElement() != elements[i]) {
                aOkay = false;
                break;
              }
            }
            else {
              aOkay = false;
              break;
            }
          }
        }
      }
      return aOkay;
    }
    
    protected void addToElement(Element toAdd, ObjectArrayProperty toAddTo, int location) {
      if (location < 0) {
        super.addToElement(toAdd, toAddTo, componentElements.size() - 1);
      }
      else {
        super.addToElement(toAdd, toAddTo, location);
      }
    }
  }
  



  protected Color getCustomBackgroundColor()
  {
    return AuthoringToolResources.getColor("userDefinedQuestionEditor");
  }
  
  protected String getHeaderHTML() {
    String htmlToReturn = AuthoringToolResources.getReprForValue(returnQuestion.valueClass.getClassValue()) + " " + super.getHeaderHTML();
    return htmlToReturn;
  }
  
  public void getHTML(StringBuffer toWriteTo, int colSpan, boolean useColor) {
    super.getHTML(toWriteTo, colSpan, useColor, false);
  }
  
  public void set(Element question, AuthoringTool authoringTool) {
    if ((question instanceof UserDefinedQuestion)) {
      UserDefinedQuestion setQuestion = (UserDefinedQuestion)question;
      if (setQuestion != null) {
        if (components.size() > 0) {
          if ((components.get(components.size() - 1) instanceof Return)) {
            returnQuestion = ((Return)components.get(components.size() - 1));
          }
          else {
            returnQuestion = new Return();
            returnQuestion.valueClass.set(valueClass.get());
            returnQuestion.value.set(AuthoringToolResources.getDefaultValueForClass((Class)valueClass.get()));
            returnQuestion.setParent(setQuestion);
            
            components.add(components.size(), returnQuestion);
          }
        }
        else {
          returnQuestion = new Return();
          returnQuestion.valueClass.set(valueClass.get());
          returnQuestion.value.set(AuthoringToolResources.getDefaultValueForClass((Class)valueClass.get()));
          returnQuestion.setParent(setQuestion);
          components.add(0, returnQuestion);
        }
        returnPanel.set(returnQuestion);
        disableDrag(returnPanel);
      }
      super.set(question, authoringTool);
    }
    else {
      throw new IllegalArgumentException();
    }
  }
  
  protected void generateGUI() {
    super.generateGUI();
    returnPanel = new ComponentQuestionPanel();
    returnPanel.setDragEnabled(false);
    if (questionArea == null) {
      questionArea = new JPanel();
      questionArea.setOpaque(true);
      questionArea.setBorder(null);
      questionArea.setLayout(new GridBagLayout());
      questionArea.setDropTarget(new DropTarget(questionArea, componentElementPanel));
      questionArea.setBackground(getBackground());
    }
  }
  
  protected void updateGUI() {
    removeAll();
    buildParameterPanel();
    buildVariablePanel();
    headerPanel.add(mainParameterPanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 11, 1, new Insets(0, 0, 0, 0), 0, 0));
    headerPanel.add(mainVariablePanel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 11, 1, new Insets(0, 0, 0, 0), 0, 0));
    questionArea.removeAll();
    questionArea.add(componentElementPanel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 18, 2, new Insets(0, 0, 0, 0), 0, 0));
    questionArea.add(returnPanel, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 18, 2, new Insets(0, 16, 0, 6), 0, 0));
    questionArea.add(Box.createVerticalGlue(), new GridBagConstraints(0, 2, 1, 1, 1.0D, 1.0D, 18, 1, new Insets(0, 0, 0, 0), 0, 0));
    scrollPane.setViewportView(questionArea);
    add(scrollPane, "Center");
    add(headerPanel, "North");
    if (CompositeElementEditor.IS_JAVA) {
      closeBrace.setText(" }");
      add(closeBrace, "South");
    }
    setBackground(getCustomBackgroundColor());
    questionArea.setBackground(getBackground());
    repaint();
    revalidate();
  }
  
  protected void variableInit() {
    super.variableInit();
    if ((m_element instanceof UserDefinedQuestion)) {
      UserDefinedQuestion proxy = (UserDefinedQuestion)m_element;
      m_components = components;
      m_isCommentedOut = null;
      componentElementPanel = new MainCompositeComponentQuestionPanel();
      componentElementPanel.set(m_components, this, authoringTool);
      componentElementPanel.setBackground(backgroundColor);
      addDragSourceComponent(componentElementPanel);
      componentElementPanel.addMouseListener(elementMouseListener);
    }
  }
  
  protected void disableDrag(Container c) {
    if ((c instanceof DnDGroupingPanel)) {
      ((DnDGroupingPanel)c).setDragEnabled(false);
    }
    for (int i = 0; i < c.getComponentCount(); i++) {
      if ((c.getComponent(i) instanceof DnDGroupingPanel)) {
        ((DnDGroupingPanel)c.getComponent(i)).setDragEnabled(false);
      }
      else if ((c instanceof Container)) {
        disableDrag(c);
      }
    }
  }
}
