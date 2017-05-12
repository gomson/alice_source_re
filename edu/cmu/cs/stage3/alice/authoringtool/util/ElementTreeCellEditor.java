package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.IllegalNameValueException;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreeCellEditor;

public class ElementTreeCellEditor extends ElementTreeCellRenderer implements TreeCellEditor
{
  protected JTextField textField;
  protected HashSet cellEditorListeners;
  protected Element element;
  protected long lastClickTime;
  protected long editDelay = 500L;
  
  public ElementTreeCellEditor() {}
  
  protected synchronized void initializeIfNecessary() { if (textField == null) {
      textField = new JTextField();
      cellEditorListeners = new HashSet();
      
      elementPanel.remove(elementLabel);
      elementPanel.add(textField, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
      
      textField.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent ev) {
            stopCellEditing();
          }
          
        });
      textField.addKeyListener(
        new java.awt.event.KeyAdapter() {
          public void keyPressed(KeyEvent ev) {
            if (ev.getKeyCode() == 27) {
              cancelCellEditing();
            }
            
          }
        });
      textField.addFocusListener(
        new java.awt.event.FocusAdapter() {
          public void focusLost(FocusEvent ev) {
            if (!ev.isTemporary()) {
              stopCellEditing();
            }
            
          }
          
        });
      dndPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mousePressed(MouseEvent ev) {
          stopCellEditing();
        }
      });
    }
  }
  
  public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row) {
    initializeIfNecessary();
    if ((value instanceof Element)) {
      element = ((Element)value);
      iconLabel.setIcon(AuthoringToolResources.getIconForValue(element));
      textField.setText(element.name.getStringValue());
    } else {
      AuthoringTool.showErrorDialog(Messages.getString("Error__not_an_Element__") + value, null);
    }
    return this;
  }
  
  public void addCellEditorListener(CellEditorListener listener) {
    initializeIfNecessary();
    cellEditorListeners.add(listener);
  }
  
  public void removeCellEditorListener(CellEditorListener listener) {
    initializeIfNecessary();
    cellEditorListeners.remove(listener);
  }
  
  public void cancelCellEditing() {
    initializeIfNecessary();
    textField.setText(element.name.getStringValue());
    fireCellEditingCancelled();
  }
  
  public boolean stopCellEditing() {
    initializeIfNecessary();
    try {
      element.name.set(textField.getText());
      fireCellEditingStopped();
      return true;
    }
    catch (IllegalNameValueException e) {
      DialogManager.showMessageDialog(e.getMessage(), Messages.getString("Error_setting_name"), 0); }
    return false;
  }
  
  public Object getCellEditorValue()
  {
    initializeIfNecessary();
    return textField.getText();
  }
  
  public synchronized boolean isCellEditable(EventObject ev) {
    boolean isSelected = false;
    if (((ev instanceof MouseEvent)) && ((ev.getSource() instanceof JTree))) {
      MouseEvent mev = (MouseEvent)ev;
      JTree tree = (JTree)ev.getSource();
      int row = tree.getRowForLocation(mev.getX(), mev.getY());
      isSelected = tree.isRowSelected(row);
    }
    
    if ((ev instanceof MouseEvent)) {
      long time = System.currentTimeMillis();
      
      MouseEvent mev = (MouseEvent)ev;
      if (SwingUtilities.isLeftMouseButton(mev)) {
        if (mev.getClickCount() > 2)
          return true;
        if ((isSelected) && (time - lastClickTime > editDelay)) {
          return true;
        }
      }
      lastClickTime = time;
    } else if (ev == null) {
      return true;
    }
    
    return false;
  }
  
  public boolean shouldSelectCell(EventObject ev) {
    return true;
  }
  
  public void selectText() {
    initializeIfNecessary();
    
    textField.selectAll();
  }
  
  protected void fireCellEditingCancelled() {
    initializeIfNecessary();
    ChangeEvent ev = new ChangeEvent(this);
    for (Iterator iter = cellEditorListeners.iterator(); iter.hasNext();) {
      ((CellEditorListener)iter.next()).editingCanceled(ev);
    }
  }
  
  protected void fireCellEditingStopped() {
    initializeIfNecessary();
    ChangeEvent ev = new ChangeEvent(this);
    for (Iterator iter = cellEditorListeners.iterator(); iter.hasNext();) {
      ((CellEditorListener)iter.next()).editingStopped(ev);
    }
  }
}
