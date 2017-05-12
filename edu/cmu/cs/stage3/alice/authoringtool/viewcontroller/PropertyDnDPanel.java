package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel.DnDGrip;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JLabel;






public class PropertyDnDPanel
  extends DnDGroupingPanel
  implements GUIElement, Releasable
{
  protected AuthoringTool authoringTool;
  protected Property property;
  protected JLabel nameLabel = new JLabel();
  protected Vector popupStructure = new Vector();
  
  public PropertyDnDPanel() {
    setBackground(AuthoringToolResources.getColor("propertyDnDPanel"));
    
    add(nameLabel, "Center");
    addDragSourceComponent(nameLabel);
    
    MouseListener mouseListener = new CustomMouseAdapter() {
      public void popupResponse(MouseEvent ev) {
        updatePopupStructure();
        PopupMenuUtilities.createAndShowPopupMenu(popupStructure, PropertyDnDPanel.this, ev.getX(), ev.getY());
      }
    };
    addMouseListener(mouseListener);
    nameLabel.addMouseListener(mouseListener);
    grip.addMouseListener(mouseListener);
  }
  
  public void set(AuthoringTool authoringTool, Property property) {
    this.authoringTool = authoringTool;
    this.property = property;
    nameLabel.setText(Messages.getString(AuthoringToolResources.getReprForValue(property).replace(" ", "_")));
    String iconName = "types/" + property.getValueClass().getName();
    ImageIcon icon = AuthoringToolResources.getIconForValue(iconName);
    if (icon == null) {
      icon = AuthoringToolResources.getIconForValue("types/other");
    }
    


    setTransferable(TransferableFactory.createTransferable(property));
  }
  
  public void updatePopupStructure() {
    popupStructure.clear();
    
    final WatcherPanel watcherPanel = authoringTool.getWatcherPanel();
    
    if (watcherPanel.isPropertyBeingWatched(property)) {
      popupStructure.add(new StringObjectPair(Messages.getString("stop_watching_this_property"), new Runnable() {
        public void run() {
          watcherPanel.removePropertyBeingWatched(property);
        }
      }));
    } else
      popupStructure.add(new StringObjectPair(Messages.getString("watch_this_property"), new Runnable() {
        public void run() {
          watcherPanel.addPropertyToWatch(property);
        }
      }));
  }
  
  public void goToSleep() {}
  
  public void wakeUp() {}
  
  public void clean() {
    setTransferable(null);
  }
  
  public void die() {
    clean();
  }
  
  public void release() {
    GUIFactory.releaseGUI(this);
  }
}
