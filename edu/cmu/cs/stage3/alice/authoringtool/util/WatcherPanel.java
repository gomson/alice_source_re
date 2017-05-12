package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;










public class WatcherPanel
  extends JPanel
{
  protected List variablesToWatch = new ArrayList();
  protected List propertiesToWatch = new ArrayList();
  protected Element root;
  
  public WatcherPanel() {
    setBackground(Color.white);
    setLayout(new GridBagLayout());
    addContainerListener(GUIElementContainerListener.getStaticListener());
  }
  
  public boolean isThereSomethingToWatch() {
    return variablesToWatch.size() + propertiesToWatch.size() > 0;
  }
  
  public void clear() {
    removeAllVariablesBeingWatched();
    removeAllPropertiesBeingWatched();
  }
  
  public void addVariableToWatch(Variable variable) {
    isWatch = true;
    variablesToWatch.add(variable);
    refreshGUI();
  }
  
  public void removeVariableBeingWatched(Variable variable) {
    isWatch = false;
    variablesToWatch.remove(variable);
    refreshGUI();
  }
  
  public void removeAllVariablesBeingWatched() {
    variablesToWatch.clear();
    refreshGUI();
  }
  
  public boolean isVariableBeingWatched(Variable variable) {
    return variablesToWatch.contains(variable);
  }
  
  public void addPropertyToWatch(Property property) {
    propertiesToWatch.add(property);
    refreshGUI();
  }
  
  public void removePropertyBeingWatched(Property property) {
    propertiesToWatch.remove(property);
    refreshGUI();
  }
  
  public void removeAllPropertiesBeingWatched() {
    propertiesToWatch.clear();
    refreshGUI();
  }
  
  public boolean isPropertyBeingWatched(Property property) {
    return propertiesToWatch.contains(property);
  }
  
  protected void refreshGUI() {
    removeAll();
    int count = 0;
    for (Iterator iter = variablesToWatch.iterator(); iter.hasNext();) {
      Variable variable = (Variable)iter.next();
      PopupItemFactory factory = new SetPropertyImmediatelyFactory(value);
      JComponent gui = GUIFactory.getVariableGUI(variable, true, factory);
      if (gui != null) {
        add(gui, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
      } else {
        AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_variable__") + variable, null);
      }
    }
    
    add(Box.createVerticalStrut(8), new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
    
    for (Iterator iter = propertiesToWatch.iterator(); iter.hasNext();) {
      Property property = (Property)iter.next();
      PopupItemFactory factory = new SetPropertyImmediatelyFactory(property);
      JComponent gui = GUIFactory.getPropertyGUI(property, true, false, factory);
      if (gui != null) {
        add(gui, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
      } else {
        AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_property__") + property, null);
      }
    }
    
    Component glue = Box.createGlue();
    add(glue, new GridBagConstraints(0, count++, 1, 1, 1.0D, 1.0D, 17, 1, new Insets(2, 2, 2, 2), 0, 0));
    
    revalidate();
    repaint();
  }
}
