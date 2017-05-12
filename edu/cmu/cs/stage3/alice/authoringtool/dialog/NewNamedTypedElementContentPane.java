package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyEditor;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.TypeChooser;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPropertyViewController;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController;
import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;

public abstract class NewNamedTypedElementContentPane extends NewNamedElementContentPane
{
  private static final int INSET = 8;
  private TypeChooser m_typeChooser;
  private PopupItemFactory factory;
  private Element context;
  private JPanel valuePanel;
  private JComponent placeholder;
  private JComponent pad;
  private JComponent valueComponent;
  private JComponent valuesComponent;
  protected JLabel valueLabel;
  private JComponent valueViewController;
  public JCheckBox makeCollectionCheckBox;
  private JComboBox collectionTypeCombo;
  private ObjectArrayPropertyEditor objectArrayPropertyEditor;
  private JScrollPane objectArrayScrollPane;
  private Variable m_variable;
  protected boolean listsOnly;
  protected boolean showValue;
  
  public NewNamedTypedElementContentPane() {}
  
  protected void initVariables()
  {
    listsOnly = false;
    showValue = true;
  }
  
  protected void initTopComponents(GridBagConstraints gbc) {
    super.initTopComponents(gbc);
    initVariables();
    
    Configuration authoringToolConfig = 
      Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
    
    m_typeChooser = new TypeChooser(getValidityChecker());
    
    valuePanel = new JPanel();
    valuePanel.setLayout(new java.awt.GridBagLayout());
    valueLabel = new JLabel();
    valuesComponent = new JPanel();
    valuesComponent.setBorder(null);
    valuesComponent.setOpaque(false);
    valuesComponent.setLayout(new BorderLayout());
    
    valueComponent = new JPanel();
    valueComponent.setBorder(null);
    valueComponent.setOpaque(false);
    valueComponent.setLayout(new BorderLayout());
    
    m_variable = new Variable();
    factory = new SetPropertyImmediatelyFactory(m_variable.value);
    
    objectArrayPropertyEditor = new ObjectArrayPropertyEditor();
    objectArrayScrollPane = new JScrollPane(objectArrayPropertyEditor);
    objectArrayScrollPane.setPreferredSize(new Dimension(1, 150));
    
    makeCollectionCheckBox = new JCheckBox(Messages.getString("make_a"));
    int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
    makeCollectionCheckBox.setFont(new java.awt.Font("SansSerif", 
      1, (int)(12 * fontSize / 12.0D)));
    makeCollectionCheckBox
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          NewNamedTypedElementContentPane.this.refreshValuePanel();
        }
      });
    m_typeChooser.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(ChangeEvent ev) {
        NewNamedTypedElementContentPane.this.refreshValuePanel();
      }
      
    });
    collectionTypeCombo = new JComboBox();
    collectionTypeCombo.addItem(new makeObj("List"));
    collectionTypeCombo.addItem(new makeObj("Array"));
    collectionTypeCombo
      .setRenderer(new DefaultListCellRenderer()
      {
        public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
          if ((value instanceof Class)) {
            String className = ((Class)value).getName();
            value = className.substring(className
              .lastIndexOf(".") + 1);
          }
          return super.getListCellRendererComponent(list, value, 
            index, isSelected, cellHasFocus);
        }
        
      });collectionTypeCombo
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          NewNamedTypedElementContentPane.this.refreshValuePanel();
        }
      });
    pad = new JPanel();
    placeholder = new JPanel();
    
    gridwidth = -1;
    insets.right = 0;
    add(new JLabel(Messages.getString("Type_")), gbc);
    insets.right = 8;
    gridwidth = 0;
    weightx = 1.0D;
    add(m_typeChooser, gbc);
    weightx = 0.0D;
    gridwidth = 0;
    layoutValuePanel();
    add(valuePanel, gbc);
  }
  
  public void reset(Element context) {
    super.reset(context);
    this.context = context;
    m_variable = new Variable();
    factory = new SetPropertyImmediatelyFactory(
      m_variable.value);
    layoutValuePanel();
  }
  
  public void preDialogShow(JDialog dialog) {
    super.preDialogShow(dialog);
    if (!getListsOnly()) {
      makeCollectionCheckBox.setSelected(false);
    }
  }
  
  public void postDialogShow(JDialog dialog) {
    super.postDialogShow(dialog);
  }
  
  public boolean isReadyToDispose(int option) {
    if (option == 0) {
      refreshValuePanel();
      m_typeChooser.addCurrentTypeToList();
    } else if (option == 2) {
      m_variable = null;
    } else {
      m_variable = null;
    }
    return true;
  }
  
  public boolean getShowValue() {
    return showValue;
  }
  
  public boolean getListsOnly() {
    return listsOnly;
  }
  
  private void updateCollection() {
    Class type = m_typeChooser.getType();
    makeObj obj = (makeObj)collectionTypeCombo.getSelectedItem();
    Class collectionType = obj.getItem();
    Collection collection = null;
    if (m_variable.value.get() != null)
    {
      if (collectionType.isAssignableFrom(m_variable.value.get().getClass())) {
        collection = (Collection)m_variable.value
          .get();
        if (valueClass.getClassValue() == type) break label226;
        values.clear();
        valueClass.set(type);
        break label226;
      } }
    try {
      collection = 
        (Collection)collectionType.newInstance();
    }
    catch (Exception e) {
      AuthoringTool.showErrorDialog(
      
        Messages.getString("Could_not_create_a_collection_of_type_") + collectionType, e);
      collection = new List();
    }
    valueClass.set(type);
    

    if ((valueViewController instanceof PropertyViewController))
    {
      ((PropertyViewController)valueViewController).release();
      valueViewController = null;
    }
    
    m_variable.value.set(null);
    m_variable.valueClass.set(collectionType);
    m_variable.value.set(collection);
    label226:
    objectArrayPropertyEditor.setType(type);
    objectArrayPropertyEditor.setObjectArrayProperty(values);
  }
  
  private void updateVariableValue() {
    Class type = m_typeChooser.getType();
    Object currentValue = m_variable.value.get();
    if ((currentValue == null) || 
      (!type.isAssignableFrom(currentValue.getClass()))) {
      if ((valueViewController instanceof PropertyViewController))
      {
        ((PropertyViewController)valueViewController).release();
        valueViewController = null;
      }
      m_variable.value.set(null);
      m_variable.valueClass.set(type);
      m_variable.value
        .set(
        AuthoringToolResources.getDefaultValueForClass(type));
      





















      valueViewController = 
        GUIFactory.getPropertyViewController(m_variable.value, true, false, 
        true, factory);
      if ((valueViewController instanceof ElementPropertyViewController))
      {
        ((ElementPropertyViewController)valueViewController).setRoot(context.getRoot());
      }
    }
  }
  
  private void layoutValuePanel()
  {
    valuePanel.removeAll();
    if (listsOnly) {
      makeCollectionCheckBox.setSelected(true);
      collectionTypeCombo
        .setSelectedItem("List");
      makeCollectionCheckBox.setEnabled(false);
      collectionTypeCombo.setEnabled(false);
    } else {
      makeCollectionCheckBox.setSelected(false);
      makeCollectionCheckBox.setEnabled(true);
      collectionTypeCombo.setEnabled(true);
    }
    GridBagConstraints gbcValue = new GridBagConstraints();
    fill = 1;
    insets.top = 8;
    if (showValue) {
      insets.left = 8;
      insets.right = 8;
      valuePanel.add(valueLabel, gbcValue);
      valuePanel.add(valueComponent, gbcValue);
      weightx = 1.0D;
      valuePanel.add(pad, gbcValue);
      weightx = 0.0D;
      insets.right = 0;
      valuePanel.add(makeCollectionCheckBox, gbcValue);
      gridwidth = 0;
      insets.left = 8;
      insets.right = 8;
      valuePanel.add(collectionTypeCombo, gbcValue);
      weightx = 0.0D;
      weighty = 1.0D;
      valuePanel.add(valuesComponent, gbcValue);
    } else {
      insets.right = 8;
      weightx = 1.0D;
      valuePanel.add(pad, gbcValue);
      weightx = 0.0D;
      valuePanel.add(makeCollectionCheckBox, gbcValue);
      gridwidth = 0;
      valuePanel.add(collectionTypeCombo, gbcValue);
      weighty = 1.0D;
      valuePanel.add(placeholder, gbcValue);
    }
    refreshValuePanel();
  }
  
  private void refreshValuePanel() {
    if (m_typeChooser.getType() != null) {
      if (makeCollectionCheckBox.isSelected()) {
        updateCollection();
      } else {
        updateVariableValue();
      }
    }
    valuesComponent.removeAll();
    valueComponent.removeAll();
    if (makeCollectionCheckBox.isSelected()) {
      valueLabel.setText(Messages.getString("Values_"));
      valueComponent.add(placeholder, "Center");
      valuesComponent.add(objectArrayScrollPane, 
        "Center");
    } else {
      valueLabel.setText(Messages.getString("Value_"));
      if (valueViewController != null) {
        valueComponent.add(valueViewController, 
          "Center");
      }
      valuesComponent.add(placeholder, "Center");
    }
    packDialog();
  }
  
  public void setListsOnly(boolean b) {
    if (listsOnly != b) {
      listsOnly = b;
      layoutValuePanel();
    }
  }
  
  public void setShowValue(boolean showValue) {
    if (this.showValue != showValue) {
      this.showValue = showValue;
      layoutValuePanel();
    }
  }
  
  public Variable getVariable() {
    if (m_variable == null) {
      return null;
    }
    if (((makeCollectionCheckBox.isSelected()) || (listsOnly)) && 
      ((m_variable.value.get() instanceof Collection)))
    {
      m_variable.addChild((Collection)m_variable.value
        .get());
    }
    
    m_variable.name.set(getNameValue());
    return m_variable;
  }
  
  public Class getTypeValue() {
    return m_typeChooser.getType();
  }
  

  private class makeObj {
    public String s;
    
    public makeObj(String item) { s = item; }
    
    public Class<?> getItem() {
      if (s == "List") {
        return List.class;
      }
      return edu.cmu.cs.stage3.alice.core.Array.class; }
    
    public String toString() { return Messages.getString(s); }
  }
}
