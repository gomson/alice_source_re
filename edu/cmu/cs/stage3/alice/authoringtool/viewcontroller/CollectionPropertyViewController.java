package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.CollectionEditorPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;





public class CollectionPropertyViewController
  extends JButton
  implements GUIElement, Releasable
{
  protected Property property;
  protected boolean omitPropertyName;
  protected PropertyListener propertyListener = new PropertyListener() {
    public void propertyChanging(PropertyEvent ev) {}
    
    public void propertyChanged(PropertyEvent ev) { refreshGUI(); }
  };
  
  public CollectionPropertyViewController()
  {
    addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          if (property != null) {
            CollectionEditorPanel collectionEditorPanel = GUIFactory.getCollectionEditorPanel();
            collectionEditorPanel.setCollection((Collection)property.getValue());
            DialogManager.showMessageDialog(collectionEditorPanel, Messages.getString("Collection_Editor"), -1);
          }
        }
      });
  }
  
  public void set(Property property, boolean omitPropertyName)
  {
    clean();
    this.property = property;
    this.omitPropertyName = omitPropertyName;
    setBackground(AuthoringToolResources.getColor("propertyViewControllerBackground"));
    setMargin(new Insets(0, 4, 0, 4));
    startListening();
    refreshGUI();
  }
  
  public Property getProperty() {
    return property;
  }
  
  public void goToSleep() {
    stopListening();
  }
  
  public void wakeUp() {
    startListening();
  }
  
  public void clean() {
    stopListening();
  }
  
  public void die() {
    stopListening();
  }
  
  public void release() {
    GUIFactory.releaseGUI(this);
  }
  
  public void startListening() {
    if (property != null) {
      property.addPropertyListener(propertyListener);
      if (property.getValue() != null) {
        property.getValue()).values.addPropertyListener(propertyListener);
      }
    }
  }
  
  public void stopListening() {
    if (property != null) {
      property.removePropertyListener(propertyListener);
      if (property.getValue() != null) {
        property.getValue()).values.removePropertyListener(propertyListener);
      }
    }
  }
  
  protected void refreshGUI() {
    Object value = property.get();
    StringBuffer repr = new StringBuffer();
    
    if (!omitPropertyName) {
      repr.append(AuthoringToolResources.getReprForValue(property) + " = ");
    }
    
    if ((value instanceof Expression)) {
      repr.append(AuthoringToolResources.getNameInContext((Element)value, property.getOwner()));
    } else if (value == null) {
      repr.append(Messages.getString("_None_"));
    } else if ((value instanceof Collection)) {
      Object[] items = values.getArrayValue();
      if (items != null) {
        for (int i = 0; i < items.length; i++) {
          repr.append(AuthoringToolResources.getReprForValue(items[i]));
          if (i < items.length - 1) {
            repr.append(", ");
          }
        }
      } else {
        repr.append(Messages.getString("_None_"));
      }
    } else {
      throw new RuntimeException(Messages.getString("Bad_value__") + value);
    }
    
    setText(repr.toString());
    revalidate();
    repaint();
  }
  
  protected String getHTMLColorString(Color color) {
    String r = Integer.toHexString(color.getRed());
    String g = Integer.toHexString(color.getGreen());
    String b = Integer.toHexString(color.getBlue());
    
    if (r.length() == 1) {
      r = "0" + r;
    }
    if (g.length() == 1) {
      g = "0" + g;
    }
    if (b.length() == 1) {
      b = "0" + b;
    }
    return new String("#" + r + g + b);
  }
  
  public void getHTML(StringBuffer toWriteTo) {
    String tempString = "";
    
    tempString = tempString + "<span style=\"background-color: " + getHTMLColorString(getBackground()) + "\">";
    
    String labelText = getText();
    if (getFont().isBold()) {
      tempString = tempString + "<b>" + labelText + "</b>";
    }
    if (getFont().isItalic()) {
      tempString = tempString + "<i>" + labelText + "</i>";
    }
    tempString = tempString + "</span>";
    tempString = " " + tempString + " ";
    
    toWriteTo.append(tempString);
  }
}
