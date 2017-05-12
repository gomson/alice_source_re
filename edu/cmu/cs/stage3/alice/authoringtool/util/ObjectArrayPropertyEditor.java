package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ObjectArrayPropertyItemTransferable;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ObjectArrayPropertyEditor extends JPanel implements edu.cmu.cs.stage3.alice.core.event.PropertyListener, java.awt.dnd.DropTargetListener
{
  protected ObjectArrayProperty objectArrayProperty;
  protected JButton newItemButton = new JButton(Messages.getString("new_item"));
  protected JButton removeItemButton = new JButton(Messages.getString("remove_item"));
  protected Class type = Object.class;
  
  protected int lineLocation = -1;
  protected int position = 0;
  
  public ObjectArrayPropertyEditor() {
    guiInit();
  }
  
  private void guiInit() {
    setLayout(new java.awt.GridBagLayout());
    setBackground(Color.white);
    newItemButton.setBackground(new Color(240, 240, 255));
    newItemButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          objectArrayProperty.add(AuthoringToolResources.getDefaultValueForClass(type));
          newItemButton.scrollRectToVisible(newItemButton.getBounds());
        }
        
      });
    removeItemButton.setBackground(new Color(240, 240, 255));
    removeItemButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          if (objectArrayProperty.size() > 0)
            objectArrayProperty.remove(objectArrayProperty.size() - 1);
          removeItemButton.scrollRectToVisible(removeItemButton.getBounds());
        }
        
      });
    setDropTarget(new DropTarget(this, this));
    refreshGUI();
  }
  
  public void setObjectArrayProperty(ObjectArrayProperty objectArrayProperty) {
    if (this.objectArrayProperty != null) {
      this.objectArrayProperty.removePropertyListener(this);
    }
    
    this.objectArrayProperty = objectArrayProperty;
    
    if (this.objectArrayProperty != null) {
      this.objectArrayProperty.addPropertyListener(this);
    }
    refreshGUI();
  }
  
  public void setType(Class type) {
    this.type = type;
  }
  
  public void refreshGUI() {
    removeAll();
    if (objectArrayProperty != null) {
      Object[] items = objectArrayProperty.getArrayValue();
      int count = 0;
      if (items != null) {
        for (int i = 0; i < items.length; i++) {
          Element owner = objectArrayProperty.getOwner();
          if ((owner == null) || (!(owner.getRoot() instanceof edu.cmu.cs.stage3.alice.core.World))) {
            owner = AuthoringTool.getHack().getWorld();
          }
          ObjectArrayPropertyItem item = new ObjectArrayPropertyItem(owner, objectArrayProperty, i, type);
          PopupItemFactory factory = new SetPropertyImmediatelyFactory(item);
          javax.swing.JComponent gui = GUIFactory.getPropertyGUI(item, true, false, factory);
          if (gui != null) {
            add(gui, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
          } else {
            AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_item__") + item, null);
          }
        }
      }
      
      add(newItemButton, new GridBagConstraints(0, count, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(8, 2, 8, 2), 0, 0));
      newItemButton.setDropTarget(new DropTarget(newItemButton, this));
      add(removeItemButton, new GridBagConstraints(1, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(8, 2, 8, 2), 0, 0));
      removeItemButton.setDropTarget(new DropTarget(removeItemButton, this));
      Component glue = javax.swing.Box.createGlue();
      add(glue, new GridBagConstraints(0, count++, 1, 1, 1.0D, 1.0D, 17, 1, new Insets(2, 2, 2, 2), 0, 0));
      glue.setDropTarget(new DropTarget(glue, this));
    }
    revalidate();
    repaint();
  }
  
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    if (lineLocation > -1) {
      Rectangle bounds = getBounds();
      g.setColor(Color.black);
      g.fillRect(0, lineLocation, width, 2);
    }
  }
  




  protected void calculateLineLocation(int mouseY)
  {
    int numSpots = objectArrayProperty.size() + 1;
    int[] spots = new int[numSpots];
    spots[0] = 0;
    for (int i = 1; i < numSpots; i++) {
      Component c = getComponent(i - 1);
      spots[i] = (getBoundsy + getBoundsheight + 1);
    }
    
    int closestSpot = -1;
    int minDist = Integer.MAX_VALUE;
    for (int i = 0; i < numSpots; i++) {
      int d = Math.abs(mouseY - spots[i]);
      if (d < minDist) {
        minDist = d;
        closestSpot = i;
      }
    }
    
    position = closestSpot;
    lineLocation = spots[closestSpot];
  }
  
  public void dragEnter(DropTargetDragEvent dtde) {
    if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor)) {
      dtde.acceptDrag(2);
      int mouseY = convertPointgetDropTargetContextgetComponentgetLocationy;
      calculateLineLocation(mouseY);
    } else {
      lineLocation = -1;
      dtde.rejectDrag();
    }
    repaint();
  }
  
  public void dragOver(DropTargetDragEvent dtde) {
    if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor)) {
      dtde.acceptDrag(2);
      int mouseY = convertPointgetDropTargetContextgetComponentgetLocationy;
      calculateLineLocation(mouseY);
    } else {
      lineLocation = -1;
      dtde.rejectDrag();
    }
    repaint();
  }
  
  public void drop(DropTargetDropEvent dtde) {
    Transferable transferable = dtde.getTransferable();
    
    if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor)) {
      dtde.acceptDrop(2);
      try {
        ObjectArrayPropertyItem item = (ObjectArrayPropertyItem)transferable.getTransferData(ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor);
        Object value = item.get();
        if (position > item.getIndex()) {
          position -= 1;
        }
        item.getObjectArrayProperty().remove(item.getIndex());
        item.getObjectArrayProperty().add(position, value);
        dtde.dropComplete(true);
      } catch (UnsupportedFlavorException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__bad_flavor"), e);
        dtde.dropComplete(false);
      } catch (IOException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__IOException"), e);
        dtde.dropComplete(false);
      } catch (Throwable t) {
        AuthoringTool.showErrorDialog(Messages.getString("Error_moving_item_"), t);
        dtde.dropComplete(false);
      }
    } else {
      dtde.rejectDrop();
      dtde.dropComplete(false);
    }
    lineLocation = -1;
    repaint();
  }
  
  public void dropActionChanged(DropTargetDragEvent dtde) {
    if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor)) {
      dtde.acceptDrag(2);
    } else {
      dtde.rejectDrag();
    }
  }
  
  public void dragExit(java.awt.dnd.DropTargetEvent dte) {
    lineLocation = -1;
    repaint();
  }
  


  public void propertyChanging(PropertyEvent propertyEvent) {}
  

  public void propertyChanged(PropertyEvent propertyEvent)
  {
    refreshGUI();
  }
}
