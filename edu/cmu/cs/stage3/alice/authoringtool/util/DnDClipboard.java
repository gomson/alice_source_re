package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Sound;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class DnDClipboard extends JPanel
{
  protected ImageIcon clipboardIcon = new ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource("images/clipboard.gif"));
  protected ImageIcon clipboardWithPaperIcon = new ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource("images/clipboardWithPaper.gif"));
  protected Dimension size;
  protected Transferable transferable;
  protected DragSource dragSource = new DragSource();
  protected boolean underDrag = false;
  protected boolean paintDropPotential = false;
  protected DropPotentialFeedbackListener dropPotentialFeedbackListener = new DropPotentialFeedbackListener();
  
  public DnDClipboard() {
    setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
    size = new Dimension(clipboardIcon.getIconWidth(), clipboardIcon.getIconHeight());
    setOpaque(false);
    dragSource.createDefaultDragGestureRecognizer(this, 1073741827, new ClipboardDragGestureListener());
    setDropTarget(new java.awt.dnd.DropTarget(this, new ClipboardDropTargetListener()));
    DnDManager.addListener(dropPotentialFeedbackListener);
    
    setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Copy_Paste_Clipboard_p__p_Drag_and_drop_tiles__b_to__b__the_clipboard_to_copy_them__p_Drag_and_drop_tiles__b_from__b__the_clipboard_to_paste_them_") + "</font></html>");
    
    addMouseListener(new CustomMouseAdapter() {
      public void singleClickResponse(MouseEvent ev) {
        edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(getToolTipText());
      }
    });
  }
  
  public void setTransferable(Transferable transferable) {
    this.transferable = transferable;
    repaint();
  }
  
  public void clear() { setTransferable(null); }
  
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Insets insets = getInsets();
    if (paintDropPotential) {
      Dimension size = getSize();
      if (underDrag) {
        g.setColor(AuthoringToolResources.getColor("dndHighlight2"));
      } else {
        g.setColor(AuthoringToolResources.getColor("dndHighlight"));
      }
      g.drawRect(0, 0, width - 1, height - 1);
      g.drawRect(1, 1, width - 3, height - 3);
    }
    if ((transferable != null) || (underDrag)) {
      clipboardWithPaperIcon.paintIcon(this, g, left, top);
    } else {
      clipboardIcon.paintIcon(this, g, left, top);
    }
  }
  
  public Dimension getMinimumSize() {
    Insets insets = getInsets();
    return new Dimension(size.width + left + right, size.height + top + bottom);
  }
  
  public Dimension getPreferredSize() {
    Insets insets = getInsets();
    return new Dimension(size.width + left + right, size.height + top + bottom);
  }
  
  public Dimension getMaximumSize() {
    Insets insets = getInsets();
    return new Dimension(size.width + left + right, size.height + top + bottom);
  }
  
  public class ClipboardDragGestureListener implements java.awt.dnd.DragGestureListener { public ClipboardDragGestureListener() {}
    
    public void dragGestureRecognized(DragGestureEvent dge) { DnDManager.fireDragGestureRecognized(dge);
      if (transferable != null) {
        dge.startDrag(DragSource.DefaultCopyDrop, transferable, DnDManager.getInternalListener());
        DnDManager.fireDragStarted(transferable, DnDClipboard.this);
      }
    }
  }
  
  public class ClipboardDropTargetListener implements java.awt.dnd.DropTargetListener {
    public ClipboardDropTargetListener() {}
    
    private boolean checkDrag(DropTargetDragEvent dtde) { try { Transferable transferable = dtde.getTransferable();
        Element element = (Element)transferable.getTransferData(ElementReferenceTransferable.elementReferenceFlavor);
        if (((element instanceof Sound)) || 
          ((element instanceof Model)) || 
          ((element instanceof World))) {
          dtde.rejectDrag();
          return false; }
        if ((element instanceof CallToUserDefinedResponse)) {
          element.getChildCount();
        }
      }
      catch (Exception localException) {}
      



      if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.elementReferenceFlavor)) {
        dtde.acceptDrag(dtde.getDropAction());
        paintDropPotential = true;
        repaint();
        return true;
      }
      dtde.rejectDrag();
      return false;
    }
    
    public void dragEnter(DropTargetDragEvent dtde) {
      checkDrag(dtde);
      underDrag = true;
      repaint();
    }
    
    public void dragExit(DropTargetEvent dte) {
      underDrag = false;
      repaint();
    }
    
    public void dragOver(DropTargetDragEvent dtde) {
      underDrag = checkDrag(dtde);
      repaint();
    }
    
    public void dropActionChanged(DropTargetDragEvent dtde) {
      checkDrag(dtde);
    }
    
    public void drop(DropTargetDropEvent dtde) {
      Transferable transferable = dtde.getTransferable();
      
      if (((DnDManager.getCurrentDragComponent() instanceof DnDClipboard)) && (DnDManager.getCurrentDragComponent() != DnDClipboard.this)) {
        DnDClipboard clipboard = (DnDClipboard)DnDManager.getCurrentDragComponent();
        clipboard.setTransferable(null);
      } else {
        java.awt.datatransfer.DataFlavor elementReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(Element.class);
        try {
          if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, elementReferenceFlavor)) {
            Element element = (Element)transferable.getTransferData(elementReferenceFlavor);
            transferable = edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable(element.createCopyFactory());
          }
        } catch (IOException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_dropping_on_clipboard_"), e);
        } catch (UnsupportedFlavorException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_dropping_on_clipboard_"), e);
        }
      }
      
      DnDClipboard.this.transferable = transferable;
      
      underDrag = false;
      repaint();
      
      dtde.acceptDrop(dtde.getDropAction());
      dtde.getDropTargetContext().dropComplete(true);
    }
  }
  
  protected boolean checkTransferable(Transferable transferable) {
    try {
      Element element = (Element)transferable.getTransferData(ElementReferenceTransferable.elementReferenceFlavor);
      if (((element instanceof Sound)) || 
        ((element instanceof Model)) || 
        ((element instanceof World)))
        return false;
      if ((element instanceof CallToUserDefinedResponse)) {
        element.getChildCount();
      }
    }
    catch (Exception localException) {}
    

    if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.elementReferenceFlavor)) {
      return true;
    }
    return false;
  }
  
  protected class DropPotentialFeedbackListener implements edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener { protected DropPotentialFeedbackListener() {}
    
    private void doCheck() { Transferable transferable = DnDManager.getCurrentTransferable();
      paintDropPotential = checkTransferable(transferable);
      repaint();
    }
    

    public void dragGestureRecognized(DragGestureEvent dge) {}
    
    public void dragStarted()
    {
      doCheck();
    }
    

    public void dragEnter(DragSourceDragEvent dsde) {}
    
    public void dragExit(DragSourceEvent dse)
    {
      doCheck();
    }
    

    public void dragOver(DragSourceDragEvent dsde) {}
    

    public void dropActionChanged(DragSourceDragEvent dsde) {}
    

    public void dragDropEnd(DragSourceDropEvent dsde)
    {
      paintDropPotential = false;
      repaint();
    }
  }
}
