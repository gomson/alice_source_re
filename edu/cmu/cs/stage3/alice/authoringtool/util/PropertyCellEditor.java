package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.table.TableCellEditor;

/**
 * @deprecated
 */
public class PropertyCellEditor implements TableCellEditor, CellEditorListener, javax.swing.event.PopupMenuListener
{
  protected EnumerableEditor enumerableEditor = new EnumerableEditor();
  protected ColorEditor colorEditor = new ColorEditor();
  protected ElementEditor elementEditor = new ElementEditor();
  protected NumberEditor numberEditor = new NumberEditor();
  protected BooleanEditor booleanEditor = new BooleanEditor();
  
  protected DefaultCellEditor stringEditor = new DefaultCellEditor(new JTextField());
  protected DefaultEditor defaultEditor = new DefaultEditor();
  


  protected TableCellEditor currentEditor = null;
  protected Class currentValueClass = null;
  protected boolean isNullValid;
  protected EventListenerList listenerList = new EventListenerList();
  
  protected Hashtable classesToEditors = new Hashtable();
  protected Element element = null;
  
  public PropertyCellEditor() {
    classesToEditors.put(java.awt.Color.class, colorEditor);
    classesToEditors.put(edu.cmu.cs.stage3.alice.scenegraph.Color.class, colorEditor);
    classesToEditors.put(Boolean.class, booleanEditor);
    classesToEditors.put(edu.cmu.cs.stage3.util.Enumerable.class, enumerableEditor);
    classesToEditors.put(Element.class, elementEditor);
    classesToEditors.put(Number.class, numberEditor);
    classesToEditors.put(String.class, stringEditor);
    classesToEditors.put(edu.cmu.cs.stage3.alice.core.ReferenceFrame.class, elementEditor);
    

    for (Enumeration enum0 = classesToEditors.elements(); enum0.hasMoreElements();) {
      TableCellEditor editor = (TableCellEditor)enum0.nextElement();
      editor.removeCellEditorListener(this);
      editor.addCellEditorListener(this);
    }
    defaultEditor.addCellEditorListener(this);
  }
  
  public Element getElement() {
    return element;
  }
  
  public void setElement(Element element) {
    this.element = element;
  }
  
  public Object getCellEditorValue() {
    if (currentEditor != null) {
      return currentEditor.getCellEditorValue();
    }
    return null;
  }
  
  public boolean isCellEditable(EventObject ev)
  {
    if ((ev instanceof java.awt.event.MouseEvent)) {
      return ((java.awt.event.MouseEvent)ev).getClickCount() >= 1;
    }
    return true;
  }
  

  public boolean shouldSelectCell(EventObject anEvent)
  {
    return true;
  }
  
  public boolean stopCellEditing()
  {
    if (currentEditor != null) {
      return currentEditor.stopCellEditing();
    }
    return true;
  }
  
  public void cancelCellEditing()
  {
    if (currentEditor != null) {
      currentEditor.cancelCellEditing();
    }
  }
  
  public void addCellEditorListener(CellEditorListener l) {
    listenerList.add(CellEditorListener.class, l);
  }
  
  public void removeCellEditorListener(CellEditorListener l) {
    listenerList.remove(CellEditorListener.class, l);
  }
  

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
  {
    Class valueClass = null;
    javax.swing.table.TableModel model = table.getModel();
    if ((model instanceof TypedTableModel)) {
      valueClass = ((TypedTableModel)model).getTypeAt(row, column);
      isNullValid = ((TypedTableModel)model).isNullValidAt(row, column);
    } else {
      isNullValid = true;
    }
    
    if ((valueClass == null) && 
      (value != null)) {
      valueClass = value.getClass();
    }
    


    currentValueClass = valueClass;
    if (valueClass != null) {
      currentEditor = ((TableCellEditor)classesToEditors.get(valueClass));
    } else {
      valueClass = Object.class;
      currentEditor = null;
    }
    




    if (currentEditor == null) {
      for (Enumeration enum0 = classesToEditors.keys(); enum0.hasMoreElements();) {
        Class editorClass = (Class)enum0.nextElement();
        if (editorClass.isAssignableFrom(valueClass)) {
          currentEditor = ((TableCellEditor)classesToEditors.get(editorClass));
          break;
        }
      }
    }
    if (currentEditor == null) {
      currentEditor = defaultEditor;
    }
    

    Component editorComponent = currentEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
    return editorComponent;
  }
  
  public void editingStopped(ChangeEvent changeEvent)
  {
    hackPopupTimer.stop();
    

    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == CellEditorListener.class) {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((CellEditorListener)listeners[(i + 1)]).editingStopped(changeEvent);
      }
    }
  }
  
  public void editingCanceled(ChangeEvent changeEvent)
  {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == CellEditorListener.class) {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((CellEditorListener)listeners[(i + 1)]).editingCanceled(changeEvent);
      }
    }
  }
  




  public void popupMenuCanceled(PopupMenuEvent ev)
  {
    if (currentEditor != null) {
      currentEditor.cancelCellEditing();
    }
  }
  

  private final Timer hackPopupTimer = new Timer(200, 
    new ActionListener() {
    public void actionPerformed(ActionEvent ev) {
      if (currentEditor != null) {
        currentEditor.cancelCellEditing();
      }
    }
  }
    );
  







  public void popupMenuWillBecomeInvisible(PopupMenuEvent ev)
  {
    hackPopupTimer.setRepeats(false);
    hackPopupTimer.start();
  }
  
  public void popupMenuWillBecomeVisible(PopupMenuEvent ev) {
    hackPopupTimer.stop();
  }
  


  class DefaultEditor
    extends DefaultCellEditor
  {
    protected Object currentObject = null;
    
    public DefaultEditor()
    {
      super();
      








      JButton button = new JButton("");
      button.setBackground(java.awt.Color.white);
      button.setBorderPainted(false);
      button.setMargin(new java.awt.Insets(0, 0, 0, 0));
      
      editorComponent = button;
      
      button.addActionListener(createActionListener());
    }
    

    protected ActionListener createActionListener()
    {
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          JPopupMenu popup = createPopupMenu();
          if (popup != null) {
            popup.show(editorComponent, 0, 0);
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
            popup.addPopupMenuListener(PropertyCellEditor.this);
          }
        }
      };
    }
    
    protected JPopupMenu createPopupMenu() {
      Vector structure = createPopupStructure();
      if (structure != null) {
        return PopupMenuUtilities.makePopupMenu(structure);
      }
      return null;
    }
    
    protected Vector createPopupStructure()
    {
      Vector structure = createExpressionStructure();
      if ((structure != null) && (isNullValid) && 
        (structure.size() > 0)) {
        structure.insertElementAt(new StringObjectPair("Separator", JSeparator.class), 0);
      }
      

      return structure;
    }
    
    protected Vector createExpressionStructure() {
      return null;
    }
    
    public Object getCellEditorValue()
    {
      return currentObject;
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      currentObject = value;
      return editorComponent;
    }
    
    class ObjectRunnable implements Runnable {
      Object object;
      
      public ObjectRunnable(Object object) {
        this.object = object;
      }
      
      public void run() {
        currentObject = object;
        fireEditingStopped();
      }
    }
  }
  
  class EnumerableEditor extends PropertyCellEditor.DefaultEditor { EnumerableEditor() { super(); }
    
    protected Vector createPopupStructure() { Vector structure = new Vector();
      edu.cmu.cs.stage3.util.Enumerable[] items = edu.cmu.cs.stage3.util.Enumerable.getItems(currentValueClass);
      for (int i = 0; i < items.length; i++) {}
      


      Vector expressionStructure = createExpressionStructure();
      if ((expressionStructure != null) && (expressionStructure.size() > 0)) {
        String className = currentValueClass.getName();
        structure.add(new StringObjectPair("Seperator", JSeparator.class));
        structure.add(new StringObjectPair("Expressions which evaluate to " + className, expressionStructure));
      }
      
      if ((isNullValid) && 
        (structure.size() > 0)) {
        structure.insertElementAt(new StringObjectPair("Separator", JSeparator.class), 0);
      }
      


      return structure;
    }
  }
  
  class ColorEditor extends PropertyCellEditor.DefaultEditor { ColorEditor() { super(); }
    final JColorChooser colorChooser = new JColorChooser();
    ActionListener okListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        currentObject = colorChooser.getColor();
      }
    };
    final javax.swing.JDialog dialog = JColorChooser.createDialog(editorComponent, "Pick a Color", true, colorChooser, okListener, null);
    
    Runnable customRunnable = new Runnable()
    {
      public void run() {
        hackPopupTimer.stop();
        if ((currentObject instanceof java.awt.Color)) {
          colorChooser.setColor((java.awt.Color)currentObject);
        }
        
        dialog.show();
        fireEditingStopped();
      }
    };
    
    protected JPopupMenu createPopupMenu() {
      JMenu menu = new JMenu("");
      

























































      return menu.getPopupMenu();
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      if ((value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color)) {
        edu.cmu.cs.stage3.alice.scenegraph.Color c = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
        value = new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
      }
      return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
  }
  
  class ElementEditor extends PropertyCellEditor.DefaultEditor { ElementEditor() { super(); }
    








































    protected Vector createPopupStructure()
    {
      return null;
    }
  }
  
  class NumberEditor extends DefaultCellEditor {
    protected Object currentNumber = null;
    
    protected JPanel panel = new JPanel();
    protected JTextField textField = new JTextField();
    protected JButton button = new JButton("Element...");
    
    public NumberEditor() {
      super();
      
      panel.setLayout(new java.awt.BorderLayout());
      panel.add(textField, "Center");
      panel.add(button, "East");
      
      editorComponent = panel;
      
      textField.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String input = textField.getText();
            try {
              Double value = Double.valueOf(input);
              currentNumber = value;
            }
            catch (NumberFormatException ex) {
              if (currentNumber != null) {
                textField.setText(currentNumber.toString());
              } else {
                textField.setText("");
              }
            }
            fireEditingStopped();


          }
          


        });
      button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Vector structure = null;
            
            if (structure != null) {
              JMenu menu = PopupMenuUtilities.makeMenu("", structure);
              if (menu != null) {
                JPopupMenu popup = menu.getPopupMenu();
                popup.show(button, 0, 0);
                PopupMenuUtilities.ensurePopupIsOnScreen(popup);
                popup.addPopupMenuListener(PropertyCellEditor.this);
              }
            }
          }
        });
    }
    
    public Object getCellEditorValue()
    {
      return currentNumber;
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      currentNumber = value;
      
      if ((currentNumber instanceof Number)) {
        textField.setText(currentNumber.toString());
      } else {
        textField.setText("");
      }
      
      Element[] elements = element.getRoot().search(new edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAssignableToCriterion(Number.class));
      if (elements.length > 0) {
        button.setEnabled(true);
      } else {
        button.setEnabled(false);
      }
      
      return editorComponent;
    }
    
    class NumberExpressionRunnable implements Runnable {
      edu.cmu.cs.stage3.alice.core.Expression expression;
      
      public NumberExpressionRunnable(edu.cmu.cs.stage3.alice.core.Expression expression) {
        this.expression = expression;
      }
      
      public void run() {
        if (Number.class.isAssignableFrom(expression.getValueClass())) {
          currentNumber = expression;
        }
        fireEditingStopped();
      }
    }
  }
  
  class BooleanEditor extends PropertyCellEditor.DefaultEditor { BooleanEditor() { super(); }
    
    protected Vector createPopupStructure() { Vector structure = new Vector();
      



      Vector expressionStructure = createExpressionStructure();
      if ((expressionStructure != null) && (expressionStructure.size() > 0)) {
        structure.add(new StringObjectPair("Seperator", JSeparator.class));
        structure.add(new StringObjectPair("Expressions which evaluate to Boolean", expressionStructure));
      }
      
      return structure;
    }
  }
}
