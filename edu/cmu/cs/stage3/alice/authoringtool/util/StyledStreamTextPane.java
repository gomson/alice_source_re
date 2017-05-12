package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Position;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class StyledStreamTextPane extends javax.swing.JTextPane
{
  public Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle("default");
  
  public Style stdOutStyle;
  public Style stdErrStyle;
  DefaultStyledDocument document = new DefaultStyledDocument();
  Position endPosition;
  StyleStream defaultStream;
  
  public StyledStreamTextPane() {
    setDocument(document);
    endPosition = document.getEndPosition();
    
    StyleConstants.setFontFamily(defaultStyle, "Monospaced");
    stdOutStyle = addStyle("stdOut", defaultStyle);
    stdErrStyle = addStyle("stdErr", defaultStyle);
    StyleConstants.setForeground(stdErrStyle, AuthoringToolResources.getColor("stdErrTextColor"));
    
    defaultStream = getNewStyleStream(defaultStyle);
    
    setDropTarget(new java.awt.dnd.DropTarget(this, new OutputDropTargetListener()));
    addMouseListener(mouseListener);
  }
  
  public StyleStream getNewStyleStream(Style style) {
    try {
      return new StyleStream(this, style);
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace(); }
    return null;
  }
  

  public boolean getScrollableTracksViewportWidth()
  {
    Component parent = getParent();
    if (parent != null) {
      int parentWidth = getSizewidth;
      int preferredWidth = getUIgetPreferredSizewidth;
      return preferredWidth < parentWidth;
    }
    return false;
  }
  












































  protected final java.awt.event.MouseListener mouseListener = new CustomMouseAdapter() {
    protected void popupResponse(MouseEvent e) {
      JPopupMenu popup = createPopup();
      popup.show(e.getComponent(), e.getX(), e.getY());
      PopupMenuUtilities.ensurePopupIsOnScreen(popup);
    }
    
    private JPopupMenu createPopup() {
      Runnable clearAllRunnable = new Runnable() {
        public void run() {
          setText("");
        }
        
      };
      Vector structure = new Vector();
      structure.add(new edu.cmu.cs.stage3.util.StringObjectPair(Messages.getString("Clear_All"), clearAllRunnable));
      
      return PopupMenuUtilities.makePopupMenu(structure);
    }
  };
  


  class OutputDropTargetListener
    implements java.awt.dnd.DropTargetListener
  {
    public OutputDropTargetListener() {}
    


    public void dragEnter(DropTargetDragEvent dtde) {}
    

    public void dragOver(DropTargetDragEvent dtde) {}
    

    public void drop(DropTargetDropEvent dtde)
    {
      Transferable transferable = dtde.getTransferable();
      DataFlavor[] flavors = transferable.getTransferDataFlavors();
      
      if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
        try {
          dtde.acceptDrop(1073741824);
          ElementPrototype elementPrototype = (ElementPrototype)transferable.getTransferData(ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
          
          if (elementPrototype.getDesiredProperties().length > 0) {
            PopupItemFactory factory = new PopupItemFactory() {
              public Object createItem(final Object object) {
                new Runnable() {
                  public void run() {
                    if ((object instanceof ElementPrototype)) {
                      Element e = ((ElementPrototype)object).createNewElement();
                      if ((e instanceof Question)) {
                        defaultStream.println(((Question)e).getValue());
                      } else {
                        defaultStream.println(e);
                      }
                    } else {
                      defaultStream.println(object);
                    }
                  }
                };
              }
            };
            Vector structure = PopupMenuUtilities.makePrototypeStructure(elementPrototype, factory, null);
            JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
            popup.show(StyledStreamTextPane.this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          } else {
            Element e = elementPrototype.createNewElement();
            if ((e instanceof Question)) {
              defaultStream.println(((Question)e).getValue());
            } else {
              defaultStream.println(e);
            }
          }
          
          dtde.getDropTargetContext().dropComplete(true);
          return;
        } catch (UnsupportedFlavorException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__bad_flavor"), e);
        } catch (IOException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__IOException"), e);
        }
      }
      
      if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, DataFlavor.stringFlavor)) {
        try {
          dtde.acceptDrop(1);
          Object transferredObject = transferable.getTransferData(DataFlavor.stringFlavor);
          defaultStream.println(transferredObject);
          dtde.getDropTargetContext().dropComplete(true);
          return;
        } catch (UnsupportedFlavorException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__bad_flavor"), e);
        } catch (IOException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__IOException"), e);
        }
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, DataFlavor.getTextPlainUnicodeFlavor())) {
        try {
          dtde.acceptDrop(1);
          Reader transferredObject = DataFlavor.getTextPlainUnicodeFlavor().getReaderForText(transferable);
          defaultStream.println(transferredObject);
          
          final BufferedReader fileReader = new BufferedReader(transferredObject);
          Thread fileReaderThread = new Thread()
          {
            public void run()
            {
              try {
                for (;;) {
                  String line = fileReader.readLine();
                  if (line == null) {
                    break;
                  }
                  defaultStream.println(line);
                }
                String line;
                fileReader.close();
              } catch (IOException e) {
                AuthoringTool.showErrorDialog(Messages.getString("Error_reading_file_"), e);
              }
            }
          };
          fileReaderThread.start();
          
          dtde.getDropTargetContext().dropComplete(true);
          fileReader.close();
          return;
        } catch (UnsupportedFlavorException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__bad_flavor"), e);
        } catch (IOException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__IOException"), e);
        }
      }
      dtde.rejectDrop();
      dtde.getDropTargetContext().dropComplete(true);
    }
    
    public void dropActionChanged(DropTargetDragEvent dtde) {}
    
    public void dragExit(DropTargetEvent dte) {}
  }
}
