package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.IllegalNameValueException;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import javax.swing.JButton;













public class ElementNamePropertyViewController
  extends StringPropertyViewController
{
  public ElementNamePropertyViewController()
  {
    setDefaultPopupStructure();
  }
  
  public void stopEditing() {
    try {
      super.stopEditing();
    } catch (IllegalNameValueException e) {
      DialogManager.showMessageDialog(e.getMessage(), Messages.getString("Error_setting_name"), 0);
      editValue();
    }
  }
  
  public void set(final Element element) {
    super.set(name, false, true, true, new PopupItemFactory() {
      public Object createItem(final Object value) {
        new Runnable() {
          public void run() {
            val$element.name.set(value);
          }
        };
      }
    });
    setPopupEnabled(false);
  }
  
  public void setDefaultPopupStructure() {
    popupStructure = new Vector();
    popupStructure.add(new StringObjectPair(Messages.getString("Rename"), new Runnable() {
      public void run() {
        editValue();
      }
    }));
  }
  
  public void setPopupStructure(Vector structure) {
    popupStructure = structure;
  }
  
  protected MouseListener getMouseListener() {
    new MouseAdapter() {
      public void mouseReleased(MouseEvent ev) {
        if (((ev.isPopupTrigger()) || (ev.getButton() == 3) || ((AikMin.isMAC()) && (ev.isControlDown()))) && 
          (isEnabled())) {
          popupButton.doClick();
        }
      }
    };
  }
  
  protected void updatePopupStructure() {}
}
