package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class MainCompositeElementPanel extends CompositeElementPanel implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener
{
  protected Color backgroundColor = AuthoringToolResources.getColor("userDefinedResponseEditor");
  
  protected JPanel parameterPanel;
  
  protected JPanel variablePanel;
  
  protected JPanel mainParameterPanel;
  
  protected JPanel mainVariablePanel;
  protected JButton newParameterButton;
  protected JButton newVariableButton;
  protected JScrollPane scrollPane;
  protected DropTargetHandler parameterDropHandler;
  protected DropTargetHandler variableDropHandler;
  protected JLabel methodNameLabel;
  protected JLabel noParametersLabel;
  protected JLabel noVariablesLabel;
  private ObjectArrayProperty requiredParameters;
  private ObjectArrayProperty keywordParameters;
  private ObjectArrayProperty localVariables;
  private int lineLocation = -1;
  private int lineHeight = 24;
  private int verticalLineLocation = 5;
  private boolean paintParameter = false;
  private boolean paintVariable = false;
  

  public MainCompositeElementPanel()
  {
    setOpaque(false);
    setLayout(new java.awt.BorderLayout());
    setDragEnabled(false);
    setBorder(null);
    elementMouseListener = null;
  }
  
  public void set(Element element, AuthoringTool authoringTool)
  {
    super.set(element, authoringTool);
  }
  
  protected void setDropTargets() {}
  
  protected void variableInit()
  {
    super.variableInit();
    setVariableObjects(m_element);
  }
  
  protected void startListening() {
    super.startListening();
    if (keywordParameters != null) {
      keywordParameters.addObjectArrayPropertyListener(this);
    }
    if (requiredParameters != null) {
      requiredParameters.addObjectArrayPropertyListener(this);
    }
    if (localVariables != null) {
      localVariables.addObjectArrayPropertyListener(this);
    }
  }
  
  protected void stopListening() {
    super.stopListening();
    if (keywordParameters != null) {
      keywordParameters.removeObjectArrayPropertyListener(this);
    }
    if (requiredParameters != null) {
      requiredParameters.removeObjectArrayPropertyListener(this);
    }
    if (localVariables != null) {
      localVariables.removeObjectArrayPropertyListener(this);
    }
  }
  
  protected void removeAllListening() {
    super.removeAllListening();
    variablePanel.setDropTarget(null);
    parameterPanel.setDropTarget(null);
    parameterDropHandler = null;
    variableDropHandler = null;
  }
  
  protected boolean isValidName(String name, ObjectArrayProperty group) {
    for (int i = 0; i < group.size(); i++) {
      if (name == getname.getStringValue()) {
        return false;
      }
    }
    return true;
  }
  
  public void objectArrayPropertyChanging(ObjectArrayPropertyEvent e) {}
  
  public void objectArrayPropertyChanged(ObjectArrayPropertyEvent e)
  {
    if (parameterPanel != null) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          buildParameterPanel();
        }
      });
    }
    
    if (variablePanel != null) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          buildVariablePanel();
        }
      });
    }
  }
  
  protected String getHeaderHTML() {
    String htmlToReturn = "<b>" + methodNameLabel.getText() + "</b>&nbsp;(&nbsp;";
    for (int i = 0; i < requiredParameters.size(); i++) {
      Class iconClass = requiredParameters.get(i)).valueClass.getClassValue();
      
      boolean isList = false;
      if (Collection.class.isAssignableFrom(iconClass)) {
        iconClass = (Class)requiredParameters.get(i)).getValue()).valueClass.get();
        isList = true;
      }
      String htmlName = AuthoringToolResources.getHTMLName(iconClass.getName());
      if (isList) {
        htmlName = htmlName + AuthoringToolResources.getHTMLName("edu.cmu.cs.stage3.alice.core.Collection");
      }
      htmlToReturn = htmlToReturn + htmlName + " <b>" + requiredParameters.get(i)).name.getStringValue() + "</b>";
      if (i + 1 != requiredParameters.size()) {
        htmlToReturn = htmlToReturn + ", ";
      }
    }
    if (CompositeElementEditor.IS_JAVA) {
      htmlToReturn = htmlToReturn + ") <b>{</b>\n<br>&nbsp;&nbsp;&nbsp;&nbsp;";
    } else {
      htmlToReturn = htmlToReturn + ")\n<br>&nbsp;&nbsp;&nbsp;&nbsp;";
    }
    htmlToReturn = htmlToReturn + GUIFactory.getHTMLStringForComponent(mainVariablePanel);
    return htmlToReturn;
  }
  
  private void setVariableObjects(Element element)
  {
    if ((element instanceof UserDefinedResponse)) {
      UserDefinedResponse r = (UserDefinedResponse)element;
      keywordParameters = keywordFormalParameters;
      requiredParameters = requiredFormalParameters;
      localVariables = localVariables;
    }
    else if ((element instanceof UserDefinedQuestion)) {
      UserDefinedQuestion r = (UserDefinedQuestion)element;
      keywordParameters = keywordFormalParameters;
      requiredParameters = requiredFormalParameters;
      localVariables = localVariables;
    }
    if ((keywordParameters != null) && (requiredParameters != null) && (localVariables != null)) {
      keywordParameters.addObjectArrayPropertyListener(this);
      requiredParameters.addObjectArrayPropertyListener(this);
      localVariables.addObjectArrayPropertyListener(this);
      parameterDropHandler.setProperty(requiredParameters);
      variableDropHandler.setProperty(localVariables);
    }
  }
  
  protected int buildVariablePanel(String seperator, JPanel toCreate, ObjectArrayProperty group, JButton button, DropTargetHandler dropHandler, int count)
  {
    int itemCount = 0;
    if ((group != null ? 1 : 0) == (group.size() > 0 ? 1 : 0))
    {

      for (int i = 0; i < group.size(); i++) {
        if ((group.get(i) instanceof Variable)) {
          Variable currentVariable = (Variable)group.get(i);
          edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory variablePIF = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(value);
          JComponent variableGUI = null;
          







          if (toCreate != parameterPanel) {
            variableGUI = GUIFactory.getVariableGUI(currentVariable, true, variablePIF);
          }
          else {
            variableGUI = GUIFactory.getVariableDnDPanel(currentVariable);
          }
          itemCount++;
          variableGUI.setDropTarget(new DropTarget(variableGUI, dropHandler));
          if ((variableGUI instanceof Container)) {
            Container variableContainer = variableGUI;
            for (int j = 0; j < variableContainer.getComponentCount(); j++) {
              variableContainer.getComponent(j).setDropTarget(new DropTarget(variableContainer.getComponent(j), dropHandler));
              if ((variableContainer.getComponent(j) instanceof Container)) {
                Container secondaryContainer = (Container)variableContainer.getComponent(j);
                for (int k = 0; k < secondaryContainer.getComponentCount(); k++) {
                  secondaryContainer.getComponent(k).setDropTarget(new DropTarget(secondaryContainer.getComponent(k), dropHandler));
                }
              }
            }
          }
          if (itemCount != group.size()) {
            JPanel holder = new JPanel();
            holder.setBorder(null);
            holder.setLayout(new java.awt.FlowLayout(0, 0, 0));
            holder.setBackground(toCreate.getBackground());
            holder.setDropTarget(new DropTarget(holder, dropHandler));
            JLabel comma = new JLabel(" " + seperator);
            comma.setDropTarget(new DropTarget(comma, dropHandler));
            holder.add(variableGUI);
            holder.add(comma);
            toCreate.add(holder);
            holder.setDropTarget(new DropTarget(holder, dropHandler));
          }
          else {
            toCreate.add(variableGUI);
          }
        }
      }
    }
    else {
      Component nonePanel = noParametersLabel;
      if (group == localVariables) {
        nonePanel = noVariablesLabel;
      }
      nonePanel.setDropTarget(new DropTarget(nonePanel, dropHandler));
      toCreate.add(nonePanel);
    }
    return itemCount;
  }
  
  protected int buildJavaVariablePanel(String seperator, JPanel toCreate, ObjectArrayProperty group, JButton button, DropTargetHandler dropHandler, int count) {
    int itemCount = 0;
    if (group != null)
    {
      for (int i = 0; i < group.size(); i++) {
        if ((group.get(i) instanceof Variable)) {
          Variable currentVariable = (Variable)group.get(i);
          edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory variablePIF = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(value);
          String className = currentVariable.getValueClass().getName();
          boolean isList = false;
          if (Collection.class.isAssignableFrom(currentVariable.getValueClass())) {
            className = ((Class)getValuevalueClass.get()).getName();
            isList = true;
          }
          String typeName = AuthoringToolResources.getHTMLName(className);
          if (typeName == null) {
            typeName = currentVariable.getValueClass().getName();
            typeName = typeName.substring(typeName.lastIndexOf('.') + 1);
          }
          if (isList) {
            typeName = typeName + "[]";
          }
          JLabel typeLabel = new JLabel(typeName + " ");
          JComponent variableGUI = null;
          
          if (toCreate != parameterPanel) {
            variableGUI = GUIFactory.getVariableGUI(currentVariable, true, variablePIF);
          }
          else {
            variableGUI = GUIFactory.getVariableDnDPanel(currentVariable);
          }
          itemCount++;
          variableGUI.setDropTarget(new DropTarget(variableGUI, dropHandler));
          if ((variableGUI instanceof Container)) {
            Container variableContainer = variableGUI;
            for (int j = 0; j < variableContainer.getComponentCount(); j++) {
              variableContainer.getComponent(j).setDropTarget(new DropTarget(variableContainer.getComponent(j), dropHandler));
              if ((variableContainer.getComponent(j) instanceof Container)) {
                Container secondaryContainer = (Container)variableContainer.getComponent(j);
                for (int k = 0; k < secondaryContainer.getComponentCount(); k++) {
                  secondaryContainer.getComponent(k).setDropTarget(new DropTarget(secondaryContainer.getComponent(k), dropHandler));
                }
              }
            }
          }
          if (itemCount != group.size()) {
            JPanel holder = new JPanel();
            holder.setBorder(null);
            holder.setLayout(new java.awt.FlowLayout(0, 0, 0));
            holder.setBackground(toCreate.getBackground());
            holder.setDropTarget(new DropTarget(holder, dropHandler));
            JLabel comma = new JLabel(" " + seperator + " ");
            comma.setDropTarget(new DropTarget(comma, dropHandler));
            typeLabel.setDropTarget(new DropTarget(typeLabel, dropHandler));
            holder.add(typeLabel);
            holder.add(variableGUI);
            holder.add(comma);
            toCreate.add(holder);
          }
          else {
            JPanel holder = new JPanel();
            holder.setBorder(null);
            holder.setLayout(new java.awt.FlowLayout(0, 0, 0));
            holder.setBackground(toCreate.getBackground());
            holder.setDropTarget(new DropTarget(holder, dropHandler));
            typeLabel.setDropTarget(new DropTarget(typeLabel, dropHandler));
            holder.add(typeLabel);
            holder.add(variableGUI);
            toCreate.add(holder);
          }
        }
      }
    }
    return itemCount;
  }
  
  public void setBackground(Color color) {
    super.setBackground(color);
    if (parameterPanel != null) parameterPanel.setBackground(color);
    if (variablePanel != null) variablePanel.setBackground(color);
    if (mainVariablePanel != null) mainVariablePanel.setBackground(color);
    if (mainParameterPanel != null) mainParameterPanel.setBackground(color);
    if (componentElementPanel != null) componentElementPanel.setBackground(color);
  }
  
  private void clearReferences(ObjectArrayProperty toClear) {
    for (int i = 0; i < toClear.size(); i++) {
      if ((toClear.get(i) instanceof Variable)) {
        Variable variableToClear = (Variable)toClear.get(i);
        if (!(value.get() instanceof Collection)) {
          value.set(null);
        }
      }
    }
  }
  
  protected void buildParameterPanel() {
    parameterPanel.removeAll();
    
    String functionName = m_element.name.getStringValue();
    

    if (CompositeElementEditor.IS_JAVA) {
      functionName = functionName.replace(' ', '_');
      String typeName = "void";
      if ((this instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.MainCompositeQuestionPanel)) {
        UserDefinedQuestion currentQuestion = (UserDefinedQuestion)m_element;
        String className = currentQuestion.getValueClass().getName();
        boolean isList = false;
        if (Collection.class.isAssignableFrom(currentQuestion.getValueClass())) {
          className = ((Class)getValuevalueClass.get()).getName();
          isList = true;
        }
        typeName = AuthoringToolResources.getHTMLName(className);
        if (typeName == null) {
          typeName = currentQuestion.getValueClass().getName();
          typeName = typeName.substring(typeName.lastIndexOf('.') + 1);
        }
        if (isList) {
          typeName = typeName + "[]";
        }
      }
      methodNameLabel.setText("public " + typeName + " " + functionName);
      parameterPanel.add(methodNameLabel);
      parameterPanel.add(new JLabel("("));
      
      buildJavaVariablePanel(",", parameterPanel, requiredParameters, newParameterButton, parameterDropHandler, 3);
      JLabel brace = new JLabel("{");
      brace.setDropTarget(new DropTarget(brace, parameterDropHandler));
      JLabel paren = new JLabel(")");
      paren.setDropTarget(new DropTarget(paren, parameterDropHandler));
      parameterPanel.add(paren);
      
      parameterPanel.add(brace);
    }
    else {
      methodNameLabel.setText(m_element.getTrimmedKey());
      parameterPanel.add(methodNameLabel);
      buildVariablePanel(",", parameterPanel, requiredParameters, newParameterButton, parameterDropHandler, 3);
    }
    
    newParameterButton.setDropTarget(new DropTarget(newParameterButton, parameterDropHandler));
    


    parameterPanel.validate();
    parameterPanel.repaint();
  }
  
  protected void buildVariablePanel() {
    variablePanel.removeAll();
    if (CompositeElementEditor.IS_JAVA) {
      int count = buildJavaVariablePanel(";", variablePanel, localVariables, newVariableButton, variableDropHandler, 1);
      if (count > 0) {
        JLabel semi = new JLabel(";");
        semi.setDropTarget(new DropTarget(semi, parameterDropHandler));
        variablePanel.add(semi);
      }
    }
    else {
      buildVariablePanel(",", variablePanel, localVariables, newVariableButton, variableDropHandler, 1);
    }
    
    newVariableButton.setDropTarget(new DropTarget(newVariableButton, variableDropHandler));
    



    variablePanel.revalidate();
    variablePanel.repaint();
  }
  
  private Component getAnchor(Component current) {
    if ((current == null) || ((current instanceof javax.swing.JTabbedPane))) {
      return current;
    }
    
    return getAnchor(current.getParent());
  }
  
  protected void generateGUI()
  {
    Component anchor = getAnchor(this);
    int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
    int len = Messages.getString("create_new_parameter").length();
    int buttonWidth = fontSize * 13 + len;
    int buttonHeight = fontSize * 2 + 2;
    
    if (newParameterButton == null) {
      newParameterButton = new JButton(Messages.getString("create_new_parameter"));
      newParameterButton.setBackground(new Color(240, 240, 255));
      newParameterButton.setOpaque(true);
      newParameterButton.setMargin(new Insets(2, 2, 2, 2));
      
      newParameterButton.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
      newParameterButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
      newParameterButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
      




      newParameterButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent ev) {
          Variable variable = authoringTool.showNewVariableDialog(Messages.getString("Create_New_Parameter"), requiredParameters.getOwner(), false, false);
          if ((variable != null) && 
            (requiredParameters != null)) {
            authoringTool.getUndoRedoStack().startCompound();
            try
            {
              requiredParameters.getOwner().addChild(variable);
              requiredParameters.add(variable);
            } finally {
              authoringTool.getUndoRedoStack().stopCompound();
            }
            
          }
        }
      });
      newParameterButton.setDropTarget(new DropTarget(newParameterButton, parameterDropHandler));
      newParameterButton.setToolTipText("<html><body>" + 
        Messages.getString("_p_Open_the_New_Parameter_Dialogue_Box__p_") + "<br>" + 
        Messages.getString("_p_Parameters_allow_you_to_send_information__p_") + 
        Messages.getString("_p_to_a_method_when_you_run_it__You_may_choose__p_") + 
        Messages.getString("_p_among_several_types_of_information_to_send__p_") + 
        Messages.getString("_p__like_numbers__objects__and_Booleans____p_") + 
        "</body></html>");
    }
    if (newVariableButton == null) {
      newVariableButton = new JButton(Messages.getString("create_new_variable"));
      newVariableButton.setBackground(new Color(240, 240, 255));
      newVariableButton.setOpaque(true);
      newVariableButton.setMargin(new Insets(2, 2, 2, 2));
      
      newVariableButton.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
      newVariableButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
      newVariableButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
      





      newVariableButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent ev) {
          Variable variable = authoringTool.showNewVariableDialog(Messages.getString("Create_New_Local_Variable"), localVariables.getOwner(), false, true);
          if ((variable != null) && 
            (localVariables != null)) {
            authoringTool.getUndoRedoStack().startCompound();
            try {
              localVariables.getOwner().addChild(variable);
              localVariables.add(variable);
            } finally {
              authoringTool.getUndoRedoStack().stopCompound();
            }
            
          }
        }
      });
      newVariableButton.setDropTarget(new DropTarget(newVariableButton, variableDropHandler));
      newVariableButton.setToolTipText("<html><body>" + 
        Messages.getString("_p_Open_the_New_Variable_Dialogue_Box__p_") + "<br>" + 
        Messages.getString("_p_Variables_allow_you_to_store_information__p_") + 
        Messages.getString("_p_in_a_method_when_it_runs__You_may_choose__p_") + 
        Messages.getString("_p_among_several_types_of_information__like__p_") + 
        Messages.getString("_p_numbers__objects__and_Booleans____p_") + 
        "</body></html>");
    }
    if (scrollPane == null) {
      scrollPane = new JScrollPane(
        20, 
        30)
        {


          public void printComponent(Graphics g) {}


        };
        scrollPane.getViewport().setOpaque(false);
        
        scrollPane.setOpaque(false);
        scrollPane.setBorder(null);
      }
      if (parameterDropHandler == null) {
        parameterDropHandler = new DropTargetHandler(requiredParameters);
      }
      if (parameterPanel == null) {
        parameterPanel = new JPanel() {
          public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if ((lineLocation > -1) && (paintParameter))
            {
              g.setColor(Color.black);
              g.fillRect(lineLocation, verticalLineLocation, 2, lineHeight);
            }
          }
        };
        parameterPanel.setBackground(backgroundColor);
        parameterPanel.setBorder(null);
        parameterPanel.setLayout(new edu.cmu.cs.stage3.awt.DynamicFlowLayout(0, anchor, javax.swing.JTabbedPane.class, 152));
        parameterPanel.setDropTarget(new DropTarget(parameterPanel, parameterDropHandler));
        parameterDropHandler.setPanel(parameterPanel);
      }
      if (variableDropHandler == null) {
        variableDropHandler = new DropTargetHandler(localVariables);
      }
      if (variablePanel == null) {
        variablePanel = new JPanel() {
          public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if ((lineLocation > -1) && (paintVariable))
            {
              g.setColor(Color.black);
              g.fillRect(lineLocation, verticalLineLocation, 2, lineHeight);
            }
            
          }
        };
        variablePanel.setBackground(backgroundColor);
        variablePanel.setBorder(null);
        variablePanel.setLayout(new edu.cmu.cs.stage3.awt.DynamicFlowLayout(0, anchor, javax.swing.JTabbedPane.class, 152));
        variablePanel.setDropTarget(new DropTarget(variablePanel, variableDropHandler));
        variableDropHandler.setPanel(variablePanel);
      }
      if (headerPanel == null) {
        headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
        headerPanel.setLayout(new java.awt.GridBagLayout());
      }
      
      methodNameLabel = new JLabel();
      Font nameFont = new Font("SansSerif", 1, (int)(14 * fontSize / 12.0D));
      methodNameLabel.setFont(nameFont);
      methodNameLabel.setDropTarget(new DropTarget(methodNameLabel, parameterDropHandler));
      
      noParametersLabel = new JLabel(Messages.getString("No_parameters"));
      noParametersLabel.setDropTarget(new DropTarget(noParametersLabel, parameterDropHandler));
      Font noFont = new Font("SansSerif", 2, (int)(12 * fontSize / 12.0D));
      noParametersLabel.setFont(noFont);
      noVariablesLabel = new JLabel(Messages.getString("No_variables"));
      noVariablesLabel.setDropTarget(new DropTarget(noVariablesLabel, variableDropHandler));
      noVariablesLabel.setFont(noFont);
      
      mainParameterPanel = new JPanel();
      mainParameterPanel.setBackground(backgroundColor);
      mainParameterPanel.setOpaque(true);
      mainParameterPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
      mainParameterPanel.setLayout(new java.awt.GridBagLayout());
      mainParameterPanel.add(parameterPanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 18, 1, new Insets(0, 0, 0, 0), 0, 0));
      mainParameterPanel.add(newParameterButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 12, 0, new Insets(4, 4, 4, 4), 0, 0));
      



      mainVariablePanel = new JPanel();
      mainVariablePanel.setOpaque(true);
      mainVariablePanel.setBackground(backgroundColor);
      mainVariablePanel.setBorder(null);
      mainVariablePanel.setLayout(new java.awt.GridBagLayout());
      mainVariablePanel.add(variablePanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 18, 1, new Insets(0, 0, 0, 0), 0, 0));
      mainVariablePanel.add(newVariableButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 12, 0, new Insets(4, 4, 4, 4), 0, 0));
    }
    




    private void DEBUG_printTree(Container c)
    {
      for (int i = 0; i < c.getComponentCount(); i++) {
        if (c.getComponent(i).getHeight() > 0) {
          Color bc = c.getComponent(i).getBackground();
          if ((bc.getRed() == bc.getBlue()) && (bc.getBlue() == bc.getGreen()))
          {
            if (!(c.getComponent(i) instanceof edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel)) {
              System.out.println(c.getComponent(i));
              System.out.println(c.getComponent(i).isOpaque());
            }
            if ((c.getComponent(i) instanceof Container)) {
              if ((c.getComponent(i) instanceof JScrollPane)) {
                JScrollPane sp = (JScrollPane)c.getComponent(i);
                DEBUG_printTree(sp.getViewport());
              }
              else {
                DEBUG_printTree((Container)c.getComponent(i));
              }
            }
          }
        }
      }
    }
    
    protected void updateGUI()
    {
      removeAll();
      buildParameterPanel();
      buildVariablePanel();
      headerPanel.add(mainParameterPanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 11, 1, new Insets(0, 0, 0, 0), 0, 0));
      headerPanel.add(mainVariablePanel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 11, 1, new Insets(0, 0, 0, 0), 0, 0));
      scrollPane.setViewportView(componentElementPanel);
      scrollPane.getViewport().setOpaque(false);
      add(scrollPane, "Center");
      add(headerPanel, "North");
      if (CompositeElementEditor.IS_JAVA) {
        closeBrace.setText(" }");
        add(closeBrace, "South");
      }
      
      setBackground(getCustomBackgroundColor());
      revalidate();
      repaint();
    }
    
    protected boolean isInGroup(Object toCheck, ObjectArrayProperty group) {
      if ((toCheck instanceof Element)) {
        return group.contains(toCheck);
      }
      return false;
    }
    
    public void propertyChanged(PropertyEvent propertyEvent) {
      if (isInGroup(propertyEvent.getProperty().getOwner(), requiredParameters))
      {
        buildParameterPanel();
      }
      else if (isInGroup(propertyEvent.getProperty().getOwner(), localVariables)) {
        buildVariablePanel();
      }
      else if (propertyEvent.getProperty() == m_element.name) {
        buildParameterPanel();
      }
      repaint();
      revalidate();
    }
    
    private Variable isPromotable(java.awt.datatransfer.Transferable transferring) {
      if ((transferring != null) && (AuthoringToolResources.safeIsDataFlavorSupported(transferring, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor))) {
        try {
          Variable toCheck = (Variable)transferring.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor);
          if ((localVariables.contains(toCheck)) || (requiredParameters.contains(toCheck))) {
            return toCheck;
          }
        }
        catch (Exception e) {
          return null;
        }
      }
      return null;
    }
    
    protected class DropTargetHandler implements java.awt.dnd.DropTargetListener
    {
      private ObjectArrayProperty m_group;
      private JPanel containingPanel;
      private int variablePosition;
      boolean isParameter = false;
      




      public DropTargetHandler(ObjectArrayProperty group)
      {
        m_group = group;
        if (group == requiredParameters) {
          isParameter = true;
        }
      }
      
      public void setProperty(ObjectArrayProperty group) {
        m_group = group;
        if (group == requiredParameters) {
          isParameter = true;
        }
        else {
          isParameter = false;
        }
      }
      
      public void setPanel(JPanel panel) {
        containingPanel = panel;
      }
      
      protected int getStartIndex() {
        int start = 0;
        for (int i = 0; i < containingPanel.getComponentCount(); i++) {
          Component c = containingPanel.getComponent(i);
          if ((c == methodNameLabel) || (c == noParametersLabel) || (c == noVariablesLabel)) {
            start++;
          }
          else
            return start;
        }
        return start;
      }
      
      private void turnNoPanelOn() {
        if (isParameter) {
          if (!noParametersLabel.isOpaque()) {
            noParametersLabel.setBackground(Color.white);
            noParametersLabel.setOpaque(true);
            noParametersLabel.repaint();
          }
          
        }
        else if (!noVariablesLabel.isOpaque()) {
          noVariablesLabel.setBackground(Color.white);
          noVariablesLabel.setOpaque(true);
          noVariablesLabel.repaint();
        }
      }
      
      private void turnNoPanelOff()
      {
        if (isParameter) {
          if (noParametersLabel.isOpaque()) {
            noParametersLabel.setBackground(Color.white);
            noParametersLabel.setOpaque(false);
            noParametersLabel.repaint();
          }
          
        }
        else if (noVariablesLabel.isOpaque()) {
          noVariablesLabel.setBackground(Color.white);
          noVariablesLabel.setOpaque(false);
          noVariablesLabel.repaint();
        }
      }
      
      protected void calculateLineLocation(int mouseX, int mouseY)
      {
        int numSpots = m_group.size() + 1;
        int[] spots = new int[numSpots];
        int[] centers = new int[numSpots];
        int startIndex = getStartIndex();
        if (m_group.size() == 0) {
          variablePosition = 0;
          verticalLineLocation = -1;
          lineLocation = -1;
          turnNoPanelOn();
        }
        else {
          turnNoPanelOff();
          JComponent firstComponent = (JComponent)containingPanel.getComponent(startIndex);
          spots[0] = (getBoundsx - getInsetsleft - 4);
          centers[0] = (5 + lineHeight / 2);
          int currentIndex = 1;
          for (int i = 0; i < numSpots - 1; i++) {
            JComponent c = (JComponent)containingPanel.getComponent(startIndex);
            Insets insets = c.getInsets();
            
            spots[currentIndex] = (getBoundsx + getBoundswidth + left);
            centers[currentIndex] = (getBoundsy + lineHeight / 2);
            currentIndex++;
            startIndex++;
          }
          int closestSpot = -2;
          int minDist = Integer.MAX_VALUE;
          for (int i = 0; i < numSpots; i++) {
            int d = Math.abs(mouseX - spots[i]) + Math.abs(mouseY - centers[i]);
            
            if (d < minDist) {
              minDist = d;
              closestSpot = i;
            }
          }
          
          variablePosition = closestSpot;
          verticalLineLocation = (centers[closestSpot] - lineHeight / 2);
          lineLocation = spots[closestSpot];
        }
      }
      
      public void dragEnter(DropTargetDragEvent dtde) {
        if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor)) {
          dtde.acceptDrag(2);
        }
        else {
          turnNoPanelOff();
          lineLocation = -1;
          paintParameter = false;
          paintVariable = false;
          dtde.rejectDrag();
          containingPanel.repaint();
        }
      }
      
      public void dragExit(java.awt.dnd.DropTargetEvent dte) {
        lineLocation = -1;
        paintParameter = false;
        paintVariable = false;
        turnNoPanelOff();
        containingPanel.repaint();
      }
      
      public void dragOver(DropTargetDragEvent dtde) {
        if (MainCompositeElementPanel.this.isPromotable(edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable()) != null) {
          dtde.acceptDrag(2);
          Point p = SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), containingPanel);
          int lineTemp = lineLocation;
          calculateLineLocation(x, y);
          if (lineTemp != lineLocation) {
            if (isParameter) {
              paintParameter = true;
              paintVariable = false;
            }
            else {
              paintParameter = false;
              paintVariable = true;
            }
            containingPanel.repaint();
          }
        }
        else {
          lineLocation = -1;
          paintParameter = false;
          paintVariable = false;
          turnNoPanelOff();
          dtde.rejectDrag();
          containingPanel.repaint();
        }
      }
      
      protected ObjectArrayProperty getGroup(Variable toRemove) {
        ObjectArrayProperty containingArray = null;
        if ((toRemove.getParent() instanceof UserDefinedResponse)) {
          UserDefinedResponse parent = (UserDefinedResponse)toRemove.getParent();
          if (keywordFormalParameters.contains(toRemove)) {
            containingArray = keywordFormalParameters;
          }
          else if (localVariables.contains(toRemove)) {
            containingArray = localVariables;
          }
          else if (requiredFormalParameters.contains(toRemove)) {
            containingArray = requiredFormalParameters;
          }
          else if (requiredFormalParameters.contains(toRemove)) {
            containingArray = requiredFormalParameters;
          }
        }
        if ((toRemove.getParent() instanceof UserDefinedQuestion)) {
          UserDefinedQuestion parent = (UserDefinedQuestion)toRemove.getParent();
          if (keywordFormalParameters.contains(toRemove)) {
            containingArray = keywordFormalParameters;
          }
          else if (localVariables.contains(toRemove)) {
            containingArray = localVariables;
          }
          else if (requiredFormalParameters.contains(toRemove)) {
            containingArray = requiredFormalParameters;
          }
          else if (requiredFormalParameters.contains(toRemove)) {
            containingArray = requiredFormalParameters;
          }
        }
        return containingArray;
      }
      
      public void drop(DropTargetDropEvent dtde) {
        Variable toAdd = MainCompositeElementPanel.this.isPromotable(edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable());
        paintParameter = false;
        paintVariable = false;
        boolean successful = true;
        if (toAdd != null) {
          dtde.acceptDrop(2);
          Point p = SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), containingPanel);
          calculateLineLocation(x, y);
          ObjectArrayProperty containingArray = getGroup(toAdd);
          if (variablePosition > m_group.size()) {
            variablePosition = m_group.size();
          }
          if (containingArray == m_group) {
            int to = variablePosition;
            int from = containingArray.indexOf(toAdd);
            if (from < to) {
              to--;
            }
            containingArray.shift(from, to);
          }
          else if (containingArray != null) {
            containingArray.remove(toAdd);
            m_group.add(variablePosition, toAdd);
          }
          turnNoPanelOff();
          lineLocation = -1;
        }
        else {
          turnNoPanelOff();
          lineLocation = -1;
          dtde.rejectDrop();
          successful = false;
        }
        
        dtde.dropComplete(successful);
      }
      
      public void dropActionChanged(DropTargetDragEvent dtde)
      {
        if (MainCompositeElementPanel.this.isPromotable(edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable()) != null) {
          dtde.acceptDrag(2);
        }
        else {
          dtde.rejectDrag();
        }
      }
    }
    

    public Component getWorkSpace()
    {
      return scrollPane;
    }
    
    public Component getParameterPanel() {
      return parameterPanel;
    }
    
    public Component getVariablePanel() {
      return variablePanel;
    }
  }
