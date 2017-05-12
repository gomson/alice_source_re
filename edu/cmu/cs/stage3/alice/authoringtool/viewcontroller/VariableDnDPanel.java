package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel.DnDGrip;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel;
import edu.cmu.cs.stage3.alice.core.IllegalNameValueException;
import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class VariableDnDPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel implements GUIElement, Releasable
{
  protected AuthoringTool authoringTool;
  protected Variable variable;
  protected JLabel nameLabel = new JLabel();
  protected JTextField textField = new JTextField();
  protected FocusListener focusListener = new java.awt.event.FocusAdapter() {
    public void focusLost(FocusEvent ev) {
      stopEditing();
    }
  };
  protected Vector popupStructure = new Vector();
  protected NamePropertyListener namePropertyListener = new NamePropertyListener();
  
  public VariableDnDPanel() {
    setBackground(AuthoringToolResources.getColor("variableDnDPanel"));
    
    add(nameLabel, "Center");
    addDragSourceComponent(nameLabel);
    
    java.awt.event.MouseListener mouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {
      public void popupResponse(MouseEvent ev) {
        updatePopupStructure();
        edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(popupStructure, VariableDnDPanel.this, ev.getX(), ev.getY());
      }
    };
    addMouseListener(mouseListener);
    nameLabel.addMouseListener(mouseListener);
    grip.addMouseListener(mouseListener);
    
    textField.setColumns(5);
    
    textField.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          stopEditing();
        }
        
      });
    textField.addKeyListener(
      new java.awt.event.KeyAdapter() {
        public void keyPressed(KeyEvent ev) {
          if (ev.getKeyCode() == 27) {
            cancelEditing();
          }
        }
      });
  }
  
  public void set(AuthoringTool authoringTool, Variable variable)
  {
    if (this.variable != null) {
      variablename.removePropertyListener(namePropertyListener);
    }
    
    this.authoringTool = authoringTool;
    this.variable = variable;
    nameLabel.setText(AuthoringToolResources.getReprForValue(variable));
    
    if (variable != null) {
      String iconName;
      String iconName;
      if (List.class.isAssignableFrom(variable.getValueClass())) {
        List list = (List)variable.getValue();
        String iconName; if (list != null) {
          iconName = "types/lists/" + valueClass.getClassValue().getName();
        }
        else {
          iconName = "types/lists";
        }
      } else {
        iconName = "types/" + variable.getValueClass().getName();
      }
      javax.swing.ImageIcon icon = AuthoringToolResources.getIconForValue(iconName);
      if (icon == null) {
        if (List.class.isAssignableFrom(variable.getValueClass())) {
          icon = AuthoringToolResources.getIconForValue("types/lists/other");
        } else {
          icon = AuthoringToolResources.getIconForValue("types/other");
        }
      }
      if (icon != null) {
        nameLabel.setIcon(icon);
      }
      setTransferable(TransferableFactory.createTransferable(variable));
      
      name.addPropertyListener(namePropertyListener);
    } else {
      setTransferable(null);
    }
  }
  
  public void editName() {
    if (variable.name.getStringValue() != null) {
      textField.setText(variable.name.getStringValue());
    }
    if (isAncestorOf(nameLabel)) {
      remove(nameLabel);
    }
    if (!isAncestorOf(textField)) {
      add(textField, "Center");
    }
    textField.requestFocus();
    textField.addFocusListener(focusListener);
    revalidate();
  }
  
  public void stopEditing() {
    String prevName = variable.name.getStringValue();
    textField.removeFocusListener(focusListener);
    String valueString = textField.getText();
    try {
      variable.name.set(valueString);
      remove(textField);
      add(nameLabel, "Center");
      nameLabel.requestFocus();
    } catch (IllegalNameValueException e) {
      DialogManager.showMessageDialog(e.getMessage(), Messages.getString("Error_setting_name"), 0);
      textField.setText(prevName);
    }
    














    revalidate();
  }
  
  public void cancelEditing() {
    textField.removeFocusListener(focusListener);
    
    remove(textField);
    add(nameLabel, "Center");
    
    revalidate();
  }
  
  public void updatePopupStructure() {
    popupStructure.clear();
    
    if (variable != null) {
      popupStructure.add(new StringObjectPair(Messages.getString("rename"), new Runnable() {
        public void run() {
          editName();
        }
        
      }));
      final WatcherPanel watcherPanel = authoringTool.getWatcherPanel();
      if (watcherPanel.isVariableBeingWatched(variable)) {
        popupStructure.add(new StringObjectPair(Messages.getString("stop_watching_this_variable"), new Runnable() {
          public void run() {
            watcherPanel.removeVariableBeingWatched(variable);
          }
        }));
      } else {
        popupStructure.add(new StringObjectPair(Messages.getString("watch_this_variable"), new Runnable() {
          public void run() {
            watcherPanel.addVariableToWatch(variable);
          }
        }));
      }
      popupStructure.add(new StringObjectPair(Messages.getString("delete"), new Runnable() {
        public void run() {
          ElementPopupUtilities.DeleteRunnable deleteRunnable = new ElementPopupUtilities.DeleteRunnable(variable, authoringTool);
          deleteRunnable.run();
          if (variable.getParent() == null) {
            variable.isWatch = false;
            watcherPanel.removeVariableBeingWatched(variable);
          }
        }
      }));
    }
  }
  
  public void goToSleep() {}
  
  public void wakeUp() {}
  
  public void clean() { setTransferable(null); }
  
  public void die()
  {
    clean();
  }
  

  public void release() { GUIFactory.releaseGUI(this); }
  
  class NamePropertyListener implements edu.cmu.cs.stage3.alice.core.event.PropertyListener { NamePropertyListener() {}
    
    public void propertyChanging(PropertyEvent ev) {}
    
    public void propertyChanged(PropertyEvent ev) { nameLabel.setText(variable.name.getStringValue()); }
  }
}
