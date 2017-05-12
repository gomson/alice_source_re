package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import javax.swing.JButton;













/**
 * @deprecated
 */
public class RightClickNameEditor
  extends StringPropertyViewController
{
  public RightClickNameEditor(final Element element)
  {
    super.set(name, false, true, true, new PopupItemFactory() {
      public Object createItem(final Object value) {
        new Runnable() {
          public void run() {
            val$element.name.set(value);
          }
        };
      }
    });
    popupStructure = new Vector();
    popupStructure.add(new StringObjectPair(Messages.getString("Rename"), new Runnable() {
      public void run() {
        editValue();
      }
    }));
    setPopupEnabled(false);
  }
  
  protected MouseListener getMouseListener() {
    new MouseAdapter() {
      public void mouseReleased(MouseEvent ev) {
        if ((isEnabled()) && ((ev.isPopupTrigger()) || (ev.getButton() == 3))) {
          popupButton.doClick();
        }
      }
    };
  }
  
  protected void updatePopupStructure() {}
}
