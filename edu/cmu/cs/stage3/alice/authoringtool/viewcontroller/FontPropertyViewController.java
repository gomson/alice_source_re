package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.FontPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Text3D;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;






public class FontPropertyViewController
  extends JButton
  implements GUIElement, Releasable
{
  protected Property property;
  protected boolean omitPropertyName;
  protected PropertyListener propertyListener = new PropertyListener() {
    public void propertyChanging(PropertyEvent ev) {}
    
    public void propertyChanged(PropertyEvent ev) { refreshGUI(); }
  };
  
  public FontPropertyViewController()
  {
    addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          if (property != null) {
            boolean isFor3DText = property.getOwner() instanceof Text3D;
            String sampleText = null;
            if (isFor3DText) {
              sampleText = property.getOwner()).text.getStringValue();
            }
            Font currentFont = null;
            if ((property.getValue() instanceof Font)) {
              currentFont = (Font)property.getValue();
            }
            FontPanel fontPanel = new FontPanel(currentFont, !isFor3DText, true, sampleText);
            if (DialogManager.showConfirmDialog(fontPanel, Messages.getString("Choose_a_Font"), 2, -1) == 0) {
              Font font = fontPanel.getChosenFont();
              if (font != null) {
                property.set(font);
              }
            }
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
    }
  }
  
  public void stopListening() {
    if (property != null) {
      property.removePropertyListener(propertyListener);
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
    } else if ((value instanceof Font)) {
      Font font = (Font)value;
      repr.append(font.getFontName());
      if (!(property.getOwner() instanceof Text3D)) {
        repr.append(", " + font.getSize());
      }
    } else {
      throw new RuntimeException(Messages.getString("Bad_value__") + value);
    }
    
    setText(repr.toString());
    revalidate();
    repaint();
  }
}
