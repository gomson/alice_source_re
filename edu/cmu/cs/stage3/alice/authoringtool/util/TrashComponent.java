package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ObjectArrayPropertyItemTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TrashComponent extends JPanel implements java.awt.dnd.DropTargetListener
{
  protected ImageIcon trashOpenIcon = AuthoringToolResources.getIconForValue("trashOpen");
  protected ImageIcon trashClosedIcon = AuthoringToolResources.getIconForValue("trashClosed");
  protected JLabel trashLabel = new JLabel(trashClosedIcon);
  protected AuthoringTool authoringTool;
  protected boolean paintDropPotential = false;
  protected DropPotentialFeedbackListener dropPotentialFeedbackListener = new DropPotentialFeedbackListener();
  protected boolean beingDroppedOn = false;
  
  public TrashComponent(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
    setLayout(new java.awt.BorderLayout());
    setOpaque(false);
    add(trashLabel, "Center");
    setDropTarget(new DropTarget(this, this));
    trashLabel.setDropTarget(new DropTarget(trashLabel, this));
    DnDManager.addListener(dropPotentialFeedbackListener);
    
    setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Trash_p__p_Drag_and_drop_tiles_here_to_delete_them_") + "</font></html>");
    
    addMouseListener(new CustomMouseAdapter() {
      public void singleClickResponse(MouseEvent ev) {
        edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(getToolTipText());
      }
    });
  }
  
  public Dimension getMaximumSize() {
    return trashLabel.getMaximumSize();
  }
  
  public void paintComponent(Graphics g) {
    if (paintDropPotential)
    {
      Dimension size = getSize();
      if (beingDroppedOn) {
        g.setColor(AuthoringToolResources.getColor("dndHighlight2"));
      } else {
        g.setColor(AuthoringToolResources.getColor("dndHighlight"));
      }
      g.drawRect(0, 0, width - 1, height - 1);
      g.drawRect(1, 1, width - 3, height - 3);
    }
    

    super.paintComponent(g);
  }
  




  protected boolean checkTransferable(Transferable transferable)
  {
    if ((DnDManager.getCurrentDragComponent() instanceof DnDClipboard))
      return true;
    if ((AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.elementReferenceFlavor)) || 
      (AuthoringToolResources.safeIsDataFlavorSupported(transferable, ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor)) || 
      (AuthoringToolResources.safeIsDataFlavorSupported(transferable, CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) || 
      (AuthoringToolResources.safeIsDataFlavorSupported(transferable, CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)))
    {
      return true;
    }
    return false;
  }
  
  protected boolean checkDrag(DropTargetDragEvent dtde)
  {
    if ((DnDManager.getCurrentDragComponent() instanceof DnDClipboard)) {
      dtde.acceptDrag(2);
      trashLabel.setIcon(trashOpenIcon);
      return true; }
    if ((AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.elementReferenceFlavor)) || 
      (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor)) || 
      (AuthoringToolResources.safeIsDataFlavorSupported(dtde, CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) || 
      (AuthoringToolResources.safeIsDataFlavorSupported(dtde, CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)))
    {
      dtde.acceptDrag(2);
      trashLabel.setIcon(trashOpenIcon);
      return true;
    }
    dtde.rejectDrag();
    return false;
  }
  
  public void dragEnter(DropTargetDragEvent dtde)
  {
    if (checkDrag(dtde)) {
      beingDroppedOn = true;
      repaint();
    }
  }
  
  public void dragExit(DropTargetEvent dte) {
    if (beingDroppedOn) {
      beingDroppedOn = false;
      repaint();
    }
    trashLabel.setIcon(trashClosedIcon);
  }
  
  public void dragOver(DropTargetDragEvent dtde) {
    if (!checkDrag(dtde)) {
      beingDroppedOn = false;
      repaint();
    }
  }
  
  public void drop(DropTargetDropEvent dtde) {
    try {
      if ((DnDManager.getCurrentDragComponent() instanceof DnDClipboard)) {
        dtde.acceptDrop(2);
        DnDClipboard clipboard = (DnDClipboard)DnDManager.getCurrentDragComponent();
        clipboard.setTransferable(null);
        dtde.dropComplete(true);
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.elementReferenceFlavor)) {
        dtde.acceptDrop(2);
        Transferable transferable = dtde.getTransferable();
        Element element = (Element)transferable.getTransferData(ElementReferenceTransferable.elementReferenceFlavor);
        
        if ((element instanceof Variable)) {
          WatcherPanel watcherPanel = authoringTool.getWatcherPanel();
          Variable variable = (Variable)element;
          if (watcherPanel.isVariableBeingWatched(variable)) {
            watcherPanel.removeVariableBeingWatched(variable);
          }
        }
        
        ElementPopupUtilities.DeleteRunnable deleteRunnable = new ElementPopupUtilities.DeleteRunnable(element, authoringTool);
        deleteRunnable.run();
        dtde.dropComplete(true);
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) {
        dtde.acceptDrop(2);
        Transferable transferable = dtde.getTransferable();
        CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = (CallToUserDefinedResponsePrototype)transferable.getTransferData(CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor);
        
        ElementPopupUtilities.DeleteRunnable deleteRunnable = new ElementPopupUtilities.DeleteRunnable(callToUserDefinedResponsePrototype.getActualResponse(), authoringTool);
        deleteRunnable.run();
        dtde.dropComplete(true);
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) {
        dtde.acceptDrop(2);
        Transferable transferable = dtde.getTransferable();
        CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = (CallToUserDefinedQuestionPrototype)transferable.getTransferData(CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor);
        
        ElementPopupUtilities.DeleteRunnable deleteRunnable = new ElementPopupUtilities.DeleteRunnable(callToUserDefinedQuestionPrototype.getActualQuestion(), authoringTool);
        deleteRunnable.run();
        dtde.dropComplete(true);
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor)) {
        dtde.acceptDrop(2);
        Transferable transferable = dtde.getTransferable();
        ObjectArrayPropertyItem item = (ObjectArrayPropertyItem)transferable.getTransferData(ObjectArrayPropertyItemTransferable.objectArrayPropertyItemFlavor);
        objectArrayProperty.remove(item.getIndex());
        dtde.dropComplete(true);
      } else {
        dtde.rejectDrop();
        dtde.dropComplete(false);
      }
    } catch (UnsupportedFlavorException e) {
      AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__bad_flavor"), e);
      dtde.dropComplete(false);
    } catch (IOException e) {
      AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__IOException"), e);
      dtde.dropComplete(false);
    } catch (Throwable t) {
      AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work_"), t);
      dtde.dropComplete(false);
    }
    trashLabel.setIcon(trashClosedIcon);
  }
  

  public void dropActionChanged(DropTargetDragEvent dtde) { checkDrag(dtde); }
  
  protected class DropPotentialFeedbackListener implements DnDManagerListener {
    protected DropPotentialFeedbackListener() {}
    
    private void doCheck() { Transferable transferable = DnDManager.getCurrentTransferable();
      boolean transferableHasPotential = checkTransferable(transferable);
      if (paintDropPotential != transferableHasPotential) {
        paintDropPotential = transferableHasPotential;
        repaint();
      }
    }
    

    public void dragGestureRecognized(DragGestureEvent dge) {}
    
    public void dragStarted()
    {
      beingDroppedOn = false;
      doCheck();
    }
    
    public void dragEnter(DragSourceDragEvent dsde) {
      doCheck();
    }
    
    public void dragExit(DragSourceEvent dse) {
      doCheck();
    }
    

    public void dragOver(DragSourceDragEvent dsde) {}
    
    public void dropActionChanged(DragSourceDragEvent dsde)
    {
      doCheck();
    }
    
    public void dragDropEnd(DragSourceDropEvent dsde) {
      paintDropPotential = false;
      repaint();
    }
  }
}
