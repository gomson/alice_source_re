package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ListProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Composite;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class ForEachElementPanel
  extends CompositeElementPanel implements ObjectArrayPropertyListener
{
  protected boolean nameListening = false;
  protected JLabel endHeader;
  protected String endHeaderText;
  protected String middleHeaderText;
  protected JComponent listInput;
  protected JComponent variable;
  protected ListProperty m_list;
  protected VariableProperty m_each;
  protected static BufferedImage forEachBackgroundImage;
  
  public ForEachElementPanel() {
    backgroundColor = AuthoringToolResources.getColor("ForEachInOrder");
    headerText = Messages.getString("For_all");
    endHeaderText = Messages.getString("at_a_time");
    middleHeaderText = Messages.getString("__one");
  }
  
  protected void variableInit()
  {
    super.variableInit();
    if ((m_element instanceof edu.cmu.cs.stage3.alice.core.response.ForEach)) {
      edu.cmu.cs.stage3.alice.core.response.ForEach proxy = (edu.cmu.cs.stage3.alice.core.response.ForEach)m_element;
      m_list = list;
      m_each = each;
    }
    else if ((m_element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)) {
      edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach proxy = (edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)m_element;
      m_list = list;
      m_each = each;
    }
  }
  
  protected void setDropTargets() {
    super.setDropTargets();
    endHeader.setDropTarget(new DropTarget(endHeader, componentElementPanel));
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
  
  protected void startListening() {
    super.startListening();
    if (m_list != null) {
      m_list.addPropertyListener(this);
      if (m_list.get() != null) {
        m_list.get()).name.addPropertyListener(this);
        nameListening = true;
      }
    }
    if (m_each != null) {
      m_each.addPropertyListener(this);
    }
    listenToChildren(m_components);
  }
  
  protected void stopListening() {
    super.stopListening();
    if (m_list != null) {
      m_list.removePropertyListener(this);
      if (m_list.get() != null) {
        m_list.get()).name.removePropertyListener(this);
        nameListening = true;
      }
    }
    if (m_each != null) {
      m_each.removePropertyListener(this);
    }
    stopListenToChildren(m_components);
  }
  
  public int countPreviousInstances(Element parent, Object toCheck) {
    if (parent == null) {
      return 0;
    }
    ListProperty list = null;
    if ((parent instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)) {
      list = list;
    }
    if ((parent instanceof edu.cmu.cs.stage3.alice.core.response.ForEach)) {
      list = list;
    }
    if ((list != null) && (list.get() == toCheck)) {
      if ((parent.getParent() != null) && (!(parent.getParent() instanceof Sandbox))) {
        return countPreviousInstances(parent.getParent(), toCheck) + 1;
      }
      
      return 1;
    }
    
    if ((parent.getParent() != null) && (!(parent.getParent() instanceof Sandbox))) {
      return countPreviousInstances(parent.getParent(), toCheck);
    }
    
    return 0;
  }
  
  private String makeVariableName(Element inputList)
  {
    int numPrevious = 0;
    if ((m_element.getParent() != null) && (!(m_element.getParent() instanceof Sandbox))) {
      numPrevious = countPreviousInstances(m_element.getParent(), inputList);
    }
    String toAdd = "";
    if (numPrevious > 0) {
      toAdd = "_#" + (numPrevious + 1);
    }
    return Messages.getString("item_from_") + name.getStringValue() + toAdd;
  }
  
  public void setHeaderLabel() {
    if (headerLabel != null) {
      headerLabel.setText(headerText);
    }
    if (endHeader != null) {
      endHeader.setText(endHeaderText);
      if (CompositeElementEditor.IS_JAVA) {
        if (!isExpanded) {
          endHeader.setText(endHeaderText + " { " + getDots() + " }");
        }
        else {
          endHeader.setText(endHeaderText + " {");
        }
      }
    }
  }
  
  protected void generateGUI()
  {
    super.generateGUI();
    if (endHeader == null) {
      endHeader = new JLabel(endHeaderText);
    }
  }
  
  protected void restoreDrag() {
    super.restoreDrag();
    addDragSourceComponent(endHeader);
  }
  
  protected void updateName() {
    Variable v = (Variable)m_each.getValue();
    name.set(makeVariableName((Element)m_list.get()));
    headerPanel.remove(variable);
    variable = GUIFactory.getVariableDnDPanel((Variable)m_each.get());
    headerPanel.add(variable, new GridBagConstraints(5, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
  }
  
  protected void updateGUI() {
    super.updateGUI();
    PopupItemFactory pif = new SetPropertyImmediatelyFactory(m_list)
    {
      public void run(Object v)
      {
        if (!(v instanceof List))
        {
          if ((v instanceof Variable)) {
            if ((value.get() instanceof List)) {}

          }
          else
          {
            return; }
        }
        super.run(v);
      }
    };
    if (m_list.get() != null) {
      setVariable();
    }
    variable = GUIFactory.getVariableDnDPanel((Variable)m_each.get());
    listInput = GUIFactory.getPropertyViewController(m_list, false, true, AuthoringToolResources.shouldGUIOmitPropertyName(m_list), pif);
    headerPanel.remove(glue);
    headerPanel.add(listInput, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    headerPanel.add(new JLabel(middleHeaderText), new GridBagConstraints(4, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    headerPanel.add(variable, new GridBagConstraints(5, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    headerPanel.add(endHeader, new GridBagConstraints(6, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    headerPanel.add(glue, new GridBagConstraints(7, 0, 1, 1, 1.0D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
  }
  



  protected static Dimension forEachBackgroundImageSize = new Dimension(-1, -1);
  
  protected boolean isTopOccurrance(Element parent, Object toCheck) {
    if (parent == null) {
      return true;
    }
    ListProperty list = null;
    if ((parent instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)) {
      list = list;
    }
    if ((parent instanceof edu.cmu.cs.stage3.alice.core.response.ForEach)) {
      list = list;
    }
    if ((list != null) && (list.get() == toCheck)) {
      return false;
    }
    return isTopOccurrance(parent.getParent(), toCheck);
  }
  
  protected void setVariableName()
  {
    Element l = (Element)m_list.get();
    String newName = makeVariableName(l);
    m_each.get()).name.set(newName);
  }
  
  private void setVariable() {
    if ((m_each.get() != null) && (m_list.get() != null)) {
      Element l = (Element)m_list.get();
      Class valueClass = null;
      if ((l instanceof List)) {
        valueClass = (Class)valueClass.get();
      }
      else if ((l instanceof Variable)) {
        valueClass = (Class)value.get()).valueClass.get();
      }
      m_each.get()).valueClass.set(valueClass);
      setVariableName();
    }
  }
  

  public void objectArrayPropertyChanging(ObjectArrayPropertyEvent objectArrayPropertyEvent) {}
  

  protected void setAllNames(ObjectArrayProperty currentContainer, int currentLevel)
  {
    String baseName = Messages.getString("item_from_") + m_list.get()).name.getStringValue();
    for (int i = 0; i < currentContainer.size(); i++) {
      Object list = null;
      Element var = null;
      if ((currentContainer.get(i) instanceof edu.cmu.cs.stage3.alice.core.response.ForEach)) {
        list = getlist.get();
        var = (Element)geteach.get();
      }
      else if ((currentContainer.get(i) instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)) {
        list = getlist.get();
        var = (Element)geteach.get();
      }
      if (list == m_list.get()) {
        name.set(baseName + "_#" + currentLevel);
      }
      if ((currentContainer.get(i) instanceof CompositeResponse))
      {
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
    if (isTopOccurrance(m_element.getParent(), m_list.get())) {
      m_each.getElementValue().name.set(Messages.getString("item_from_") + m_list.get()).name.getStringValue());
      setAllNames(m_components, 2);
    }
  }
  
  public void propertyChanged(PropertyEvent propertyEvent)
  {
    if ((propertyEvent.getProperty() == m_each) && 
      (m_each.get() != null)) {
      Element l = (Element)m_list.get();
      Class valueClass = null;
      if ((l instanceof List)) {
        valueClass = (Class)valueClass.get();
      }
      else if ((l instanceof Variable)) {
        valueClass = (Class)value.get()).valueClass.get();
      }
      m_each.get()).valueClass.set(valueClass);
      setVariableName();
    }
    
    if (propertyEvent.getProperty() == m_list) {
      if ((m_list.get() != null) && (!nameListening)) {
        m_list.get()).name.addPropertyListener(this);
        nameListening = true;
      }
      setVariable();
    }
    else if ((m_list.get() != null) && (propertyEvent.getProperty() == m_list.get()).name)) {
      setVariableName();
    }
    else {
      super.propertyChanged(propertyEvent);
    }
  }
  
  protected void createBackgroundImage(int width, int height) {
    forEachBackgroundImageSize.setSize(width, height);
    forEachBackgroundImage = new BufferedImage(width, height, 2);
    Graphics2D g = (Graphics2D)forEachBackgroundImage.getGraphics();
    g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
    g.setColor(backgroundColor);
    g.fillRect(0, 0, width, height);
  }
  
  protected void paintTextureEffect(Graphics g, Rectangle bounds) {
    if ((width > forEachBackgroundImageSizewidth) || (height > forEachBackgroundImageSizeheight)) {
      createBackgroundImage(width, height);
    }
    g.setClip(x, y, width, height);
    g.drawImage(forEachBackgroundImage, x, y, this);
  }
}
