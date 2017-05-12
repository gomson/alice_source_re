package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentOwner;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.CopyFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import javax.swing.JComponent;



























public class CompositeComponentBehaviorPanel
  extends CompositeComponentElementPanel
{
  public CompositeComponentBehaviorPanel() {}
  
  public void set(ObjectArrayProperty elements, BehaviorGroupEditor owner, AuthoringTool authoringToolIn)
  {
    super.set(elements, owner, authoringToolIn);
  }
  
  protected boolean isInverted()
  {
    return true;
  }
  
  protected Component makeGUI(Element currentElement)
  {
    JComponent toAdd = null;
    if ((currentElement instanceof Behavior))
    {
      toAdd = GUIFactory.getGUI(currentElement);
      return toAdd;
    }
    
    return null;
  }
  

  public void drop(DropTargetDropEvent dtde)
  {
    Transferable transferable = dtde.getTransferable();
    boolean dropSuccess = true;
    try
    {
      int type = BehaviorGroupsEditor.checkTransferable(transferable);
      if ((type == -1) || (type == 1))
      {
        if ((m_owner.getParent() instanceof DropTargetListener)) {
          ((DropTargetListener)m_owner.getParent()).drop(dtde);
        }
      } else if (type == 2)
      {
        dtde.acceptDrop(2);
        Behavior b;
        Behavior b; if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, CopyFactoryTransferable.copyFactoryFlavor))
        {
          CopyFactory copyFactory = (CopyFactory)transferable.getTransferData(CopyFactoryTransferable.copyFactoryFlavor);
          b = (Behavior)copyFactory.manufactureCopy(componentElements.getOwner().getWorld());
        }
        else {
          b = (Behavior)transferable.getTransferData(ElementReferenceTransferable.behaviorReferenceFlavor);
        }
        performDrop(b, dtde);
      }
    }
    catch (Exception e)
    {
      AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_"), e);
      dropSuccess = false;
    }
    dtde.dropComplete(dropSuccess);
  }
  
  public void dragOver(DropTargetDragEvent dtde)
  {
    if ((!m_owner.isExpanded()) && ((m_owner.getParent() instanceof DropTargetListener))) {
      ((DropTargetListener)m_owner.getParent()).dragOver(dtde);
    }
    try {
      if (BehaviorGroupsEditor.checkDragEvent(dtde) != 2)
      {
        if ((m_owner.getParent() instanceof DropTargetListener)) {
          ((DropTargetListener)m_owner.getParent()).dragOver(dtde);
        }
      } else {
        insertDropPanel(dtde);
        dtde.acceptDrag(dtde.getDropAction());
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      dtde.rejectDrag();
      return;
    }
  }
}
