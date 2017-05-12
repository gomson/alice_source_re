package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentOwner;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities;
import edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.CopyFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import edu.cmu.cs.stage3.alice.core.response.PoseAnimation;
import edu.cmu.cs.stage3.alice.core.response.SoundResponse;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JPopupMenu;

public class CompositeComponentResponsePanel extends CompositeComponentElementPanel
{
  public CompositeComponentResponsePanel() {}
  
  public void set(ObjectArrayProperty elements, CompositeResponsePanel owner, AuthoringTool authoringToolIn)
  {
    super.set(elements, owner, authoringToolIn);
  }
  
  protected java.awt.Component makeGUI(Element currentElement) {
    javax.swing.JComponent toAdd = null;
    if ((currentElement instanceof Response)) {
      if ((currentElement instanceof CompositeResponse)) {
        toAdd = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(currentElement);

      }
      else if (currentElement != null) {
        toAdd = new ComponentResponsePanel();
        ((ComponentResponsePanel)toAdd).set(currentElement);
      }
      else {
        return null;
      }
      
      return toAdd;
    }
    
    return null;
  }
  
  public void dragOver(DropTargetDragEvent dtde)
  {
    java.awt.Component sourceComponent = DnDManager.getCurrentDragComponent();
    int action = dtde.getDropAction();
    boolean isCopy = (action & 0x1) > 0;
    boolean isMove = (action & 0x2) > 0;
    if ((!m_owner.isExpanded()) && 
      ((m_owner.getParent() instanceof CompositeComponentResponsePanel))) {
      ((CompositeComponentResponsePanel)m_owner.getParent()).dragOver(dtde);
      return;
    }
    
    if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.responseReferenceFlavor)) {
      try {
        Transferable transferable = DnDManager.getCurrentTransferable();
        Response response = (Response)transferable.getTransferData(ElementReferenceTransferable.responseReferenceFlavor);
        boolean isValid = checkLoop(response);
        if (isValid) {
          if (isMove) {
            dtde.acceptDrag(2);
          }
          else if (isCopy) {
            dtde.acceptDrag(1);
          }
          insertDropPanel(dtde);
        }
        else {
          dtde.rejectDrag();
        }
      } catch (UnsupportedFlavorException e) {
        dtde.rejectDrag();
      } catch (IOException e) {
        dtde.rejectDrag();
      } catch (Throwable t) {
        dtde.rejectDrag();
      }
    }
    else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, CopyFactoryTransferable.copyFactoryFlavor)) {
      try {
        Transferable transferable = DnDManager.getCurrentTransferable();
        CopyFactory copyFactory = (CopyFactory)transferable.getTransferData(CopyFactoryTransferable.copyFactoryFlavor);
        Class valueClass = copyFactory.getValueClass();
        if (Response.class.isAssignableFrom(valueClass)) {
          dtde.acceptDrag(2);
          insertDropPanel(dtde);
        }
        else {
          dtde.rejectDrag();
        }
      } catch (UnsupportedFlavorException e) {
        dtde.rejectDrag();
      } catch (IOException e) {
        dtde.rejectDrag();
      } catch (Throwable t) {
        dtde.rejectDrag();
      }
    } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.questionReferenceFlavor)) {
      dtde.rejectDrag();
    }
    else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.elementReferenceFlavor)) {
      try {
        Transferable transferable = DnDManager.getCurrentTransferable();
        Element element = (Element)transferable.getTransferData(ElementReferenceTransferable.elementReferenceFlavor);
        if ((!(element instanceof Behavior)) && (!(element instanceof edu.cmu.cs.stage3.alice.core.World)) && (!(element instanceof TextureMap)) && (!(element instanceof edu.cmu.cs.stage3.alice.core.Group))) {
          if (checkLoop(element)) {
            dtde.acceptDrag(2);
            insertDropPanel(dtde);
          }
          else {
            dtde.rejectDrag();
          }
        }
        else {
          dtde.rejectDrag();
        }
      } catch (UnsupportedFlavorException e) {
        dtde.rejectDrag();
      } catch (IOException e) {
        dtde.rejectDrag();
      } catch (Throwable t) {
        dtde.rejectDrag();
      }
    } else if ((AuthoringToolResources.safeIsDataFlavorSupported(dtde, ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor)) || 
      (AuthoringToolResources.safeIsDataFlavorSupported(dtde, PropertyReferenceTransferable.propertyReferenceFlavor)) || 
      (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.variableReferenceFlavor))) {
      if (isMove) {
        dtde.acceptDrag(2);
        insertDropPanel(dtde);
      }
      else if (isCopy) {
        dtde.rejectDrag();
      }
    } else {
      AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class));
      dtde.rejectDrag();
      return;
    }
  }
  
  public void drop(final DropTargetDropEvent dtde) {
    HACK_started = false;
    boolean successful = true;
    
    int action = dtde.getDropAction();
    boolean isCopy = (action & 0x1) > 0;
    boolean isMove = (action & 0x2) > 0;
    if ((!m_owner.isExpanded()) && 
      ((m_owner.getParent() instanceof CompositeComponentElementPanel))) {
      ((CompositeComponentElementPanel)m_owner.getParent()).drop(dtde);
      return;
    }
    
    Transferable transferable = dtde.getTransferable();
    
    if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, CopyFactoryTransferable.copyFactoryFlavor)) {
      try {
        CopyFactory copyFactory = (CopyFactory)transferable.getTransferData(CopyFactoryTransferable.copyFactoryFlavor);
        Class valueClass = copyFactory.getValueClass();
        if (Response.class.isAssignableFrom(valueClass)) {
          dtde.acceptDrop(1);
          successful = true;
          Response response = (Response)copyFactory.manufactureCopy(m_owner.getElement().getRoot(), null, null, m_owner.getElement());
          if (response != null) {
            performDrop(response, dtde);
          }
        }
        else {
          successful = false;
          dtde.rejectDrop();
        }
      } catch (UnsupportedFlavorException e) {
        AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_a_bad_flavor_"), e);
        successful = false;
      } catch (IOException e) {
        AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_an_IO_error_"), e);
        successful = false;
      } catch (Throwable t) {
        AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed__Methods_with_parameters_cannot_be_copied_"), t);
        successful = false;
      }
    }
    else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.responseReferenceFlavor))
    {
      try {
        Response response = (Response)transferable.getTransferData(ElementReferenceTransferable.responseReferenceFlavor);
        if (((response instanceof CompositeResponse)) && 
          (!isCopy) && (!isValidDrop(s_currentComponentPanel.getElement(), response))) {
          successful = false;
        }
        
        if (!successful) break label1580;
        if (isMove) {
          dtde.acceptDrop(2);
        }
        else if (isCopy) {
          dtde.acceptDrop(1);
        }
        performDrop(response, dtde);
        successful = true;
      }
      catch (UnsupportedFlavorException e) {
        AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_a_bad_flavor_"), e);
        successful = false;
      } catch (IOException e) {
        AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_an_IO_error_"), e);
        successful = false;
      } catch (Throwable t) {
        AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed__Methods_with_parameters_cannot_be_copied_"), t);
        successful = false;
      }
    }
    else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor)) {
      if (isMove) {
        dtde.acceptDrop(2);
        successful = true;
      }
      else if (isCopy) {
        dtde.rejectDrop();
        successful = false;
      }
      if (successful) {
        try {
          ResponsePrototype responsePrototype = (ResponsePrototype)transferable.getTransferData(ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor);
          if (((responsePrototype.getDesiredProperties() == null) || (responsePrototype.getDesiredProperties().length < 1)) && 
            (!edu.cmu.cs.stage3.alice.core.response.Print.class.isAssignableFrom(responsePrototype.getResponseClass()))) {
            performDrop(responsePrototype.createNewResponse(), dtde);
          } else if (responsePrototype.getDesiredProperties().length > 3) {
            performDrop(responsePrototype.createNewResponse(), dtde);
          }
          else {
            PopupItemFactory factory = new PopupItemFactory() {
              public Object createItem(final Object object) {
                new Runnable() {
                  public void run() {
                    if ((object instanceof ResponsePrototype)) {
                      performDrop(((ResponsePrototype)object).createNewResponse(), val$dtde);
                    }
                    else if ((object instanceof ElementPrototype)) {
                      Element newResponse = ((ElementPrototype)object).createNewElement();
                      if ((newResponse instanceof Response)) {
                        performDrop(newResponse, val$dtde);
                      }
                    }
                  }
                };
              }
            };
            Vector structure = null;
            if (edu.cmu.cs.stage3.alice.core.response.Print.class.isAssignableFrom(responsePrototype.getResponseClass())) {
              structure = PopupMenuUtilities.makeResponsePrintStructure(factory, componentElements.getOwner());
            }
            else {
              structure = PopupMenuUtilities.makePrototypeStructure(responsePrototype, factory, componentElements.getOwner());
            }
            JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
            popup.addPopupMenuListener(this);
            inserting = true;
            popup.show(dtde.getDropTargetContext().getComponent(), (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          }
        } catch (UnsupportedFlavorException e) {
          AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_a_bad_flavor_"), e);
          successful = false;
        } catch (IOException e) {
          AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_an_IO_error_"), e);
          successful = false;
        } catch (Throwable t) {
          AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_"), t);
          successful = false;
        }
      }
    } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, PropertyReferenceTransferable.propertyReferenceFlavor)) {
      if (isMove) {
        dtde.acceptDrop(2);
        successful = true;
      }
      else if (isCopy) {
        dtde.rejectDrop();
        successful = false;
      }
      if (successful)
      {
        try
        {

          Property property = (Property)transferable.getTransferData(PropertyReferenceTransferable.propertyReferenceFlavor);
          Class animationClass; StringObjectPair[] known; Class animationClass; if ((property instanceof edu.cmu.cs.stage3.alice.core.property.VehicleProperty))
          {

            StringObjectPair[] newKnown = { new StringObjectPair("element", property.getOwner()), new StringObjectPair("propertyName", property.getName()), new StringObjectPair("duration", new Double(0.0D)) };
            StringObjectPair[] known = newKnown;
            animationClass = edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation.class;
          }
          else {
            StringObjectPair[] newKnown = { new StringObjectPair("element", property.getOwner()), new StringObjectPair("propertyName", property.getName()) };
            known = newKnown;
            animationClass = edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class;
          }
          PopupItemFactory factory = new PopupItemFactory() {
            public Object createItem(final Object object) {
              new Runnable() {
                public void run() {
                  if ((object instanceof ResponsePrototype)) {
                    performDrop(((ResponsePrototype)object).createNewResponse(), val$dtde);
                  }
                  else if ((object instanceof ElementPrototype)) {
                    Element newResponse = ((ElementPrototype)object).createNewElement();
                    if ((newResponse instanceof Response)) {
                      performDrop(newResponse, val$dtde);
                    }
                  }
                }
              };
            }
          };
          String[] desired = { "value" };
          
          ResponsePrototype rp = new ResponsePrototype(animationClass, known, desired);
          Vector structure = PopupMenuUtilities.makePrototypeStructure(rp, factory, componentElements.getOwner());
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.addPopupMenuListener(this);
          inserting = true;
          popup.show(dtde.getDropTargetContext().getComponent(), (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
        } catch (UnsupportedFlavorException e) {
          AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_a_bad_flavor_"), e);
          successful = false;
        } catch (IOException e) {
          AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_an_IO_error_"), e);
          successful = false;
        } catch (Throwable t) {
          AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_"), t);
          successful = false;
        }
      }
    } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.variableReferenceFlavor)) {
      if (isMove) {
        dtde.acceptDrop(2);
        successful = true;
      }
      else if (isCopy) {
        dtde.rejectDrop();
        successful = false;
      }
      if (successful) {
        try {
          edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)transferable.getTransferData(ElementReferenceTransferable.variableReferenceFlavor);
          if (!checkLoop(variable)) {
            dtde.rejectDrop();
            successful = false;
          }
          else {
            PopupItemFactory factory = new PopupItemFactory() {
              public Object createItem(final Object object) {
                new Runnable() {
                  public void run() {
                    if ((object instanceof ResponsePrototype)) {
                      performDrop(((ResponsePrototype)object).createNewResponse(), val$dtde);
                    }
                    else if ((object instanceof ElementPrototype)) {
                      Element newResponse = ((ElementPrototype)object).createNewElement();
                      if ((newResponse instanceof Response)) {
                        performDrop(newResponse, val$dtde);
                      }
                    }
                  }
                };
              }
            };
            Vector structure = PopupMenuUtilities.makeExpressionResponseStructure(variable, factory, componentElements.getOwner());
            JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
            popup.addPopupMenuListener(this);
            inserting = true;
            popup.show(dtde.getDropTargetContext().getComponent(), (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          }
        } catch (UnsupportedFlavorException e) {
          AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_a_bad_flavor_"), e);
          successful = false;
        } catch (IOException e) {
          AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_an_IO_error_"), e);
          successful = false;
        } catch (Throwable t) {
          AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_"), t);
          successful = false;
        }
      }
    }
    else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.expressionReferenceFlavor))
    {

      dtde.rejectDrop();
      successful = false;
    } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.elementReferenceFlavor))
    {
      try {
        Element element = (Element)transferable.getTransferData(ElementReferenceTransferable.elementReferenceFlavor);
        if (((element instanceof Behavior)) || ((element instanceof edu.cmu.cs.stage3.alice.core.World)) || ((element instanceof TextureMap))) {
          dtde.rejectDrop();
          successful = false;
        }
        if (isMove) {
          dtde.acceptDrop(2);
          successful = true;
        }
        else if (isCopy) {
          dtde.rejectDrop();
          successful = false;
        }
        if (!successful) break label1580;
        if ((element instanceof edu.cmu.cs.stage3.alice.core.Sound)) {
          SoundResponse r = new SoundResponse();
          sound.set(element);
          subject.set(element.getParent());
          performDrop(r, dtde);
        }
        else if ((element instanceof edu.cmu.cs.stage3.alice.core.Pose)) {
          PoseAnimation r = new PoseAnimation();
          pose.set(element);
          subject.set(element.getParent());
          performDrop(r, dtde);
        }
        else {
          PopupItemFactory factory = new PopupItemFactory() {
            public Object createItem(final Object object) {
              new Runnable() {
                public void run() {
                  if ((object instanceof ResponsePrototype)) {
                    performDrop(((ResponsePrototype)object).createNewResponse(), val$dtde);
                  }
                  else if ((object instanceof ElementPrototype)) {
                    Element newResponse = ((ElementPrototype)object).createNewElement();
                    if ((newResponse instanceof Response)) {
                      performDrop(newResponse, val$dtde);
                    }
                  }
                }
              };
            }
          };
          Vector structure = PopupMenuUtilities.makeResponseStructure(element, factory, componentElements.getOwner());
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.addPopupMenuListener(this);
          inserting = true;
          popup.show(dtde.getDropTargetContext().getComponent(), (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
        }
      }
      catch (UnsupportedFlavorException e) {
        AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_a_bad_flavor_"), e);
        successful = false;
      } catch (IOException e) {
        AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_because_of_an_IO_error_"), e);
        successful = false;
      } catch (Throwable t) {
        AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_"), t);
        successful = false;
      }
    } else {
      dtde.rejectDrop();
      successful = false; }
    label1580:
    dtde.dropComplete(successful);
  }
}
