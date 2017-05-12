package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Composite;
import edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import edu.cmu.cs.stage3.alice.core.response.LoopNInOrder;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class LoopNElementPanel extends CompositeElementPanel implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener
{
  private JLabel timesLabel;
  protected JComponent endInput;
  protected JComponent countInput;
  protected JComponent startInput;
  protected JComponent incrementInput;
  protected JComponent indexTile;
  protected NumberProperty m_start;
  protected NumberProperty m_increment;
  protected NumberProperty m_end;
  protected VariableProperty m_index;
  protected JLabel fromLabel;
  protected JLabel upToLabel;
  protected JLabel incrementLabel;
  protected JPanel complicatedPanel;
  protected JPanel simplePanel;
  protected JLabel complicatedEndBrace;
  protected JButton switchButton;
  protected String toComplicatedString = Messages.getString("show_complicated_version");
  protected String toSimpleString = Messages.getString("show_simple_version");
  protected boolean isComplicated = false;
  
  protected static String IS_COMPLICATED_LOOP_KEY = "edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor IS_COMPLICATED_LOOP_KEY";
  protected static BufferedImage countLoopBackgroundImage;
  
  public LoopNElementPanel() {
    headerText = Messages.getString("Loop");
    backgroundColor = AuthoringToolResources.getColor("LoopNInOrder");
  }
  
  protected int countPreviousInstances(Component parent, Object toCheck) {
    if (parent == null) {
      return 0;
    }
    if ((parent instanceof LoopNElementPanel)) {
      return countPreviousInstances(parent.getParent(), toCheck) + 1;
    }
    return 0;
  }
  
  protected String getIndexName() {
    String toReturn = Messages.getString("index");
    int count = countPreviousInstances(getParent(), this);
    if (count > 0) {
      toReturn = toReturn + String.valueOf(count);
    }
    return toReturn;
  }
  
  protected void variableInit() {
    super.variableInit();
    if ((m_element instanceof LoopNInOrder)) {
      LoopNInOrder proxy = (LoopNInOrder)m_element;
      m_end = end;
      m_start = start;
      m_increment = increment;
      m_index = index;
      Variable localVariable = (Variable)m_index.getValue();

    }
    else if ((m_element instanceof LoopN)) {
      LoopN proxy = (LoopN)m_element;
      m_end = end;
      m_start = start;
      m_increment = increment;
      m_index = index;
    }
    Object isComplicatedValue = m_element.data.get(IS_COMPLICATED_LOOP_KEY);
    if ((isComplicatedValue instanceof Boolean)) {
      isComplicated = ((Boolean)isComplicatedValue).booleanValue();
    }
    if (isComplicated) {
      switchButton.setText(toSimpleString);
    }
    else {
      switchButton.setText(toComplicatedString);
    }
  }
  
  protected void startListening() {
    super.startListening();
    if (m_end != null) {
      m_end.addPropertyListener(this);
    }
    listenToChildren(m_components);
  }
  
  protected void stopListening() {
    super.stopListening();
    if (m_end != null) {
      m_end.removePropertyListener(this);
    }
    stopListenToChildren(m_components);
  }
  
  protected void listenToChildren(ObjectArrayProperty components) {
    if (components != null) {
      components.addObjectArrayPropertyListener(this);
      for (int i = 0; i < components.size(); i++) {
        if ((components.get(i) instanceof CompositeResponse)) {
          CompositeResponse current = (CompositeResponse)components.get(i);
          listenToChildren(componentResponses);
        }
        else if ((components.get(i) instanceof Composite)) {
          Composite current = (Composite)components.get(i);
          listenToChildren(components);
        }
      }
    }
  }
  
  protected void stopListenToChildren(ObjectArrayProperty components) {
    if (components != null) {
      components.removeObjectArrayPropertyListener(this);
      
      for (int i = 0; i < components.size(); i++) {
        if ((components.get(i) instanceof CompositeResponse)) {
          CompositeResponse current = (CompositeResponse)components.get(i);
          stopListenToChildren(componentResponses);
        }
        else if ((components.get(i) instanceof Composite)) {
          Composite current = (Composite)components.get(i);
          stopListenToChildren(components);
        }
      }
    }
  }
  
  protected void removeAllListening() {
    super.removeAllListening();
    removeDragSourceComponent(timesLabel);
    removeDragSourceComponent(complicatedPanel);
    removeDragSourceComponent(simplePanel);
    removeDragSourceComponent(fromLabel);
    removeDragSourceComponent(upToLabel);
    removeDragSourceComponent(incrementLabel);
    removeDragSourceComponent(complicatedEndBrace);
  }
  
  protected void setDropTargets() {
    super.setDropTargets();
    timesLabel.setDropTarget(new DropTarget(timesLabel, componentElementPanel));
    complicatedPanel.setDropTarget(new DropTarget(complicatedPanel, componentElementPanel));
    simplePanel.setDropTarget(new DropTarget(simplePanel, componentElementPanel));
    fromLabel.setDropTarget(new DropTarget(fromLabel, componentElementPanel));
    upToLabel.setDropTarget(new DropTarget(upToLabel, componentElementPanel));
    complicatedEndBrace.setDropTarget(new DropTarget(complicatedEndBrace, componentElementPanel));
    incrementLabel.setDropTarget(new DropTarget(incrementLabel, componentElementPanel));
    indexTile.setDropTarget(new DropTarget(indexTile, componentElementPanel));
  }
  
  public void setHeaderLabel()
  {
    if (headerLabel != null) {
      headerLabel.setText(headerText);
      if (CompositeElementEditor.IS_JAVA) {
        if (isComplicated) {
          headerLabel.setText("for (int");
        } else {
          int start = 0;
          if (m_start != null) {
            start = m_start.intValue();
          }
          headerLabel.setText("for (int " + getIndexName() + "=" + start + "; " + getIndexName() + "<");
        }
      }
    }
    if (timesLabel != null) {
      Number number = m_end.getNumberValue();
      if ((number != null) && (number.intValue() <= 1)) {
        timesLabel.setText(Messages.getString("time"));
      }
      else {
        timesLabel.setText(Messages.getString("times"));
      }
      if (CompositeElementEditor.IS_JAVA) {
        int increment = 1;
        if (m_increment != null) {
          increment = m_increment.intValue();
        }
        if (!isExpanded) {
          if (increment == 1) {
            timesLabel.setText("; " + getIndexName() + "++) { " + getDots() + " }");
          } else {
            timesLabel.setText("; " + getIndexName() + "+=" + increment + ") { " + getDots() + " }");
          }
          
        }
        else if (increment == 1) {
          timesLabel.setText("; " + getIndexName() + "++) {");
        } else {
          timesLabel.setText("; " + getIndexName() + "+=" + increment + ") {");
        }
      }
    }
    
    if (fromLabel != null) {
      if (CompositeElementEditor.IS_JAVA) {
        fromLabel.setText("=");
      }
      else {
        fromLabel.setText(Messages.getString("from"));
      }
    }
    if (upToLabel != null) {
      if (CompositeElementEditor.IS_JAVA) {
        upToLabel.setText("; " + getIndexName() + "<");
      }
      else {
        upToLabel.setText(Messages.getString("up_to__but_not_including_"));
      }
    }
    if (incrementLabel != null) {
      if (CompositeElementEditor.IS_JAVA) {
        incrementLabel.setText("; " + getIndexName() + " +=");
      } else {
        incrementLabel.setText(Messages.getString("incrementing_by"));
      }
    }
    if (complicatedEndBrace != null) {
      if (CompositeElementEditor.IS_JAVA) {
        complicatedEndBrace.setText("){");
      } else {
        complicatedEndBrace.setText("");
      }
    }
  }
  
  protected void generateGUI()
  {
    super.generateGUI();
    timesLabel = new JLabel();
    fromLabel = new JLabel();
    upToLabel = new JLabel();
    incrementLabel = new JLabel();
    complicatedEndBrace = new JLabel();
    complicatedPanel = new JPanel();
    complicatedPanel.setOpaque(false);
    complicatedPanel.setBorder(null);
    complicatedPanel.setLayout(new GridBagLayout());
    simplePanel = new JPanel();
    simplePanel.setOpaque(false);
    simplePanel.setBorder(null);
    simplePanel.setLayout(new GridBagLayout());
    switchButton = new JButton();
    switchButton.setPreferredSize(new Dimension(180, 21));
    switchButton.setBackground(new Color(240, 240, 255));
    switchButton.setMargin(new Insets(2, 2, 2, 2));
    switchButton.setForeground(new Color(90, 110, 110));
    switchButton.setOpaque(true);
    switchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (isComplicated) {
          isComplicated = false;
          switchButton.setText(toComplicatedString);
          m_element.data.put(LoopNElementPanel.IS_COMPLICATED_LOOP_KEY, Boolean.FALSE);
        }
        else {
          isComplicated = true;
          switchButton.setText(toSimpleString);
          m_element.data.put(LoopNElementPanel.IS_COMPLICATED_LOOP_KEY, Boolean.TRUE);
        }
        updateGUI();
      }
    });
  }
  
  protected void restoreDrag() {
    super.restoreDrag();
    addDragSourceComponent(timesLabel);
    addDragSourceComponent(complicatedPanel);
    addDragSourceComponent(simplePanel);
    addDragSourceComponent(fromLabel);
    addDragSourceComponent(upToLabel);
    addDragSourceComponent(complicatedEndBrace);
    addDragSourceComponent(incrementLabel);
  }
  
  protected void updateName() {
    Variable v = (Variable)m_index.getValue();
    name.set(getIndexName());
    complicatedPanel.remove(indexTile);
    indexTile = GUIFactory.getVariableDnDPanel((Variable)m_index.get());
    complicatedPanel.add(indexTile, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
  }
  
  protected boolean isTopOccurrance(Element parent) {
    if (parent == null) {
      return true;
    }
    if ((parent instanceof LoopN)) {
      return false;
    }
    if ((parent instanceof LoopNInOrder)) {
      return false;
    }
    return isTopOccurrance(parent.getParent());
  }
  

  public void objectArrayPropertyChanging(ObjectArrayPropertyEvent objectArrayPropertyEvent) {}
  

  protected void setAllNames(ObjectArrayProperty currentContainer, int currentLevel)
  {
    String baseName = Messages.getString("index");
    for (int i = 0; i < currentContainer.size(); i++) {
      Element var = null;
      if ((currentContainer.get(i) instanceof LoopNInOrder)) {
        var = (Element)getindex.get();
      }
      else if ((currentContainer.get(i) instanceof LoopN)) {
        var = (Element)getindex.get();
      }
      if (var != null) {
        name.set(baseName + "_#" + currentLevel);
      }
      if ((currentContainer.get(i) instanceof CompositeResponse)) {
        setAllNames(getcomponentResponses, currentLevel + 1);
      }
      else if ((currentContainer.get(i) instanceof Composite)) {
        setAllNames(getcomponents, currentLevel + 1);
      }
    }
  }
  
  public void objectArrayPropertyChanged(ObjectArrayPropertyEvent objectArrayPropertyEvent) {
    stopListening();
    startListening();
    if (isTopOccurrance(m_element.getParent())) {
      Variable v = (Variable)m_index.getValue();
      name.set(Messages.getString("index"));
      setAllNames(m_components, 2);
    }
  }
  
  protected void updateGUI()
  {
    super.updateGUI();
    
    PopupItemFactory pifCount = new SetPropertyImmediatelyFactory(m_end);
    countInput = GUIFactory.getPropertyViewController(m_end, true, true, AuthoringToolResources.shouldGUIOmitPropertyName(m_end), pifCount);
    PopupItemFactory pifstart = new SetPropertyImmediatelyFactory(m_start);
    startInput = GUIFactory.getPropertyViewController(m_start, true, true, AuthoringToolResources.shouldGUIOmitPropertyName(m_start), pifstart);
    
    PopupItemFactory pifInc = new SetPropertyImmediatelyFactory(m_increment);
    incrementInput = GUIFactory.getPropertyViewController(m_increment, true, true, AuthoringToolResources.shouldGUIOmitPropertyName(m_increment), pifInc);
    
    PopupItemFactory pifEnd = new SetPropertyImmediatelyFactory(m_end);
    endInput = GUIFactory.getPropertyViewController(m_end, true, true, AuthoringToolResources.shouldGUIOmitPropertyName(m_end), pifEnd);
    
    indexTile = GUIFactory.getVariableDnDPanel((Variable)m_index.get());
    setHeaderLabel();
    complicatedPanel.removeAll();
    complicatedPanel.add(indexTile, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    complicatedPanel.add(fromLabel, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    complicatedPanel.add(startInput, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    complicatedPanel.add(upToLabel, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    complicatedPanel.add(endInput, new GridBagConstraints(4, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    complicatedPanel.add(incrementLabel, new GridBagConstraints(5, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    complicatedPanel.add(incrementInput, new GridBagConstraints(6, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    complicatedPanel.add(complicatedEndBrace, new GridBagConstraints(7, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    
    simplePanel.removeAll();
    simplePanel.add(countInput, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    simplePanel.add(timesLabel, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
    
    headerPanel.remove(glue);
    if (isComplicated) {
      headerPanel.add(complicatedPanel, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    }
    else {
      headerPanel.add(simplePanel, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    }
    
    headerPanel.add(switchButton, new GridBagConstraints(4, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 8, 0, 2), 0, 0));
    headerPanel.add(glue, new GridBagConstraints(5, 0, 1, 1, 1.0D, 0.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
  }
  



  protected static Dimension countLoopBackgroundImageSize = new Dimension(-1, -1);
  
  protected void createBackgroundImage(int width, int height) {
    countLoopBackgroundImageSize.setSize(width, height);
    countLoopBackgroundImage = new BufferedImage(width, height, 2);
    Graphics2D g = (Graphics2D)countLoopBackgroundImage.getGraphics();
    g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
    g.setColor(backgroundColor);
    g.fillRect(0, 0, width, height);
  }
  




  protected void paintTextureEffect(Graphics g, Rectangle bounds)
  {
    if ((width > countLoopBackgroundImageSizewidth) || (height > countLoopBackgroundImageSizeheight)) {
      createBackgroundImage(width, height);
    }
    g.setClip(x, y, width, height);
    g.drawImage(countLoopBackgroundImage, x, y, this);
  }
}
