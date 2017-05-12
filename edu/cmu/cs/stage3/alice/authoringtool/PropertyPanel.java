package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor.VariableGroupEditor;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.OverridableElementProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.response.PropertyAnimation;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.Border;



public class PropertyPanel
  extends JPanel
{
  protected Element element;
  protected VariableGroupEditor variableGroupEditor = new VariableGroupEditor();
  
  public PropertyPanel() {
    jbInit();
    propertyGroupCombo.addItemListener(
      new ItemListener() {
        public void itemStateChanged(ItemEvent ev) {
          CardLayout cardLayout = (CardLayout)propertyManipulationPanel.getLayout();
          cardLayout.show(propertyManipulationPanel, (String)ev.getItem());
        }
      });
  }
  
  public Element getElement()
  {
    return element;
  }
  







  public void setElement(Element element)
  {
    this.element = element;
    
    refreshGUI();
  }
  
  public void refreshGUI() {
    propertyManipulationPanel.removeAll();
    propertyGroupCombo.removeAllItems();
    if (element != null) {
      Vector structure = AuthoringToolResources.getPropertyStructure(element, false);
      if (structure != null) {
        for (Iterator iter = structure.iterator(); iter.hasNext();) {
          StringObjectPair sop = (StringObjectPair)iter.next();
          String groupName = sop.getString();
          Vector propertyNames = (Vector)sop.getObject();
          
          JPanel subPanel = new JPanel();
          subPanel.setBackground(Color.white);
          subPanel.setLayout(new GridBagLayout());
          
          if (propertyNames != null) {
            int i = 0;
            for (Iterator jter = propertyNames.iterator(); jter.hasNext();) {
              String name = (String)jter.next();
              final Property property = element.getPropertyNamed(name);
              if (property != null) {
                PopupItemFactory factory = new PopupItemFactory() {
                  public Object createItem(final Object o) {
                    new Runnable() {
                      public void run() {
                        if (((val$property.getOwner() instanceof Transformable)) && (val$property == val$property.getOwner()).vehicle)) {
                          ((Transformable)val$property.getOwner()).setVehiclePreservingAbsoluteTransformation((ReferenceFrame)o);
                        } else {
                          PropertyAnimation response = new PropertyAnimation();
                          element.set(val$property.getOwner());
                          propertyName.set(val$property.getName());
                          value.set(o);
                          PropertyAnimation undoResponse = new PropertyAnimation();
                          element.set(val$property.getOwner());
                          propertyName.set(val$property.getName());
                          value.set(val$property.getValue());
                          AuthoringTool.getHack().performOneShot(response, undoResponse, new Property[] { val$property });
                        }
                      }
                    };
                  }
                };
                JComponent gui = GUIFactory.getPropertyGUI(property, true, false, factory);
                if (gui != null) {
                  GridBagConstraints constraints = new GridBagConstraints(0, i, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0);
                  subPanel.add(gui, constraints);
                  i++;
                } else {
                  AuthoringTool.showErrorDialog("Unable to create gui for property: " + property, null);
                }
              } else {
                AuthoringTool.showErrorDialog("no property on " + element + " named " + name, null);
              }
            }
            GridBagConstraints glueConstraints = new GridBagConstraints(0, i, 1, 1, 1.0D, 1.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0);
            subPanel.add(Box.createGlue(), glueConstraints);
          }
          
          propertyManipulationPanel.add(subPanel, element.name.get() + "'s " + groupName);
          propertyGroupCombo.addItem(element.name.get() + "'s " + groupName);
        }
      }
      
      if ((element instanceof Sandbox)) {
        variableGroupEditor.setVariableObjectArrayProperty(element).variables);
        propertyManipulationPanel.add(variableGroupEditor, element.name.get() + "'s variables");
        propertyGroupCombo.addItem(element.name.get() + "'s variables");
      }
    }
    revalidate();
    repaint();
  }
  




  BorderLayout borderLayout1 = new BorderLayout();
  JPanel propertySubPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JComboBox propertyGroupCombo = new JComboBox();
  Border border1;
  JScrollPane propertyScrollPane = new JScrollPane();
  Border border2;
  Border border3;
  Border border4;
  Border border5;
  JPanel propertyManipulationPanel = new JPanel();
  Border border6;
  Border border7;
  CardLayout cardLayout1 = new CardLayout();
  
  private void jbInit() {
    border1 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
    border2 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
    border3 = BorderFactory.createCompoundBorder(border2, border1);
    border4 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
    border5 = BorderFactory.createEmptyBorder(2, 2, 2, 2);
    border6 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
    border7 = BorderFactory.createCompoundBorder(border6, border5);
    setLayout(borderLayout1);
    propertySubPanel.setLayout(borderLayout2);
    propertySubPanel.setBorder(border1);
    setBackground(new Color(204, 204, 204));
    setMinimumSize(new Dimension(0, 0));
    propertyManipulationPanel.setLayout(cardLayout1);
    borderLayout2.setHgap(8);
    borderLayout2.setVgap(8);
    propertyManipulationPanel.setBackground(Color.white);
    add(propertySubPanel, "Center");
    propertySubPanel.add(propertyGroupCombo, "North");
    propertySubPanel.add(propertyScrollPane, "Center");
    propertyScrollPane.getViewport().add(propertyManipulationPanel, null);
  }
}
