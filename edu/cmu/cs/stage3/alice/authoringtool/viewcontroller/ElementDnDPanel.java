package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.core.Element;
import javax.swing.JLabel;















public class ElementDnDPanel
  extends DnDGroupingPanel
  implements GUIElement, Releasable
{
  protected Element element;
  protected ElementNamePropertyViewController nameViewController;
  protected JLabel iconLabel = new JLabel();
  
  public ElementDnDPanel() {
    setBackground(AuthoringToolResources.getColor("elementDnDPanel"));
    iconLabel.setOpaque(false);
  }
  
  public void set(Element element) {
    clean();
    this.element = element;
    nameViewController = GUIFactory.getElementNamePropertyViewController(element);
    nameViewController.setBorder(null);
    nameViewController.setOpaque(false);
    add(nameViewController, "Center");
    addDragSourceComponent(nameViewController);
    setTransferable(TransferableFactory.createTransferable(element));
  }
  
  public void editName() {
    nameViewController.editValue();
  }
  
  protected void startListening() {
    if (nameViewController != null) {
      nameViewController.startListening();
    }
  }
  
  protected void stopListening() {
    if (nameViewController != null) {
      nameViewController.stopListening();
    }
  }
  
  public void goToSleep() {
    stopListening();
  }
  
  public void wakeUp() {
    startListening();
  }
  
  public void clean() {
    removeDragSourceComponent(nameViewController);
    setTransferable(null);
    if (nameViewController != null) {
      remove(nameViewController);
    }
    nameViewController = null;
  }
  
  public void die() {
    clean();
  }
  
  public void release() {
    GUIFactory.releaseGUI(this);
  }
}
