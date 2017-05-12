package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeComponentOwner;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities;
import edu.cmu.cs.stage3.alice.core.CopyFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Composite;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Print;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Return;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
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
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class CompositeComponentQuestionPanel extends CompositeComponentElementPanel
{
  public CompositeComponentQuestionPanel() {}
  
  public void set(ObjectArrayProperty elements, CompositeQuestionPanel owner, AuthoringTool authoringToolIn)
  {
    super.set(elements, owner, authoringToolIn);
  }
  
  protected java.awt.Component makeGUI(Element currentElement) {
    JComponent toAdd = null;
    if ((currentElement instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component)) {
      if ((currentElement instanceof Composite)) {
        toAdd = GUIFactory.getGUI(currentElement);

      }
      else if (currentElement != null) {
        toAdd = new ComponentQuestionPanel();
        ((ComponentQuestionPanel)toAdd).set(currentElement);
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
      ((m_owner.getParent() instanceof CompositeComponentQuestionPanel))) {
      ((CompositeComponentQuestionPanel)m_owner.getParent()).dragOver(dtde);
      return;
    }
    
    if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, QuestionEditor.componentReferenceFlavor)) {
      Transferable currentTransferable = DnDManager.getCurrentTransferable();
      Element currentQuestion = null;
      if (currentTransferable != null) {
        try {
          currentQuestion = (Element)currentTransferable.getTransferData(QuestionEditor.componentReferenceFlavor);
        } catch (Exception e) {
          dtde.rejectDrag();
          return;
        }
        if (currentQuestion == null) {
          dtde.rejectDrag();
          return;
        }
        if (((currentQuestion instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component)) && 
          (!isCopy) && (!isValidDrop(componentElements.getOwner(), currentQuestion))) {
          dtde.rejectDrag();
        }
        
      }
      else
      {
        dtde.rejectDrag();
        return;
      }
      if (isMove) {
        dtde.acceptDrag(2);
      }
      else if (isCopy) {
        dtde.acceptDrag(1);
      }
      insertDropPanel(dtde);
    }
    else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, CopyFactoryTransferable.copyFactoryFlavor)) {
      try {
        Transferable transferable = DnDManager.getCurrentTransferable();
        CopyFactory copyFactory = (CopyFactory)transferable.getTransferData(CopyFactoryTransferable.copyFactoryFlavor);
        Class valueClass = copyFactory.getValueClass();
        if (edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class.isAssignableFrom(valueClass)) {
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
    } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
      try {
        Transferable transferable = DnDManager.getCurrentTransferable();
        ElementPrototype prototype = (ElementPrototype)transferable.getTransferData(ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
        Class valueClass = prototype.getElementClass();
        if (edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class.isAssignableFrom(valueClass)) {
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
    } else if ((AuthoringToolResources.safeIsDataFlavorSupported(dtde, PropertyReferenceTransferable.propertyReferenceFlavor)) || 
      (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.variableReferenceFlavor))) {
      if (isMove) {
        dtde.acceptDrag(2);
        insertDropPanel(dtde);
      }
      else if (isCopy) {
        dtde.rejectDrag();
      }
    } else {
      dtde.rejectDrag();
      return;
    }
  }
  
  protected UserDefinedQuestion getTopQuestion(Element e) {
    if ((e instanceof UserDefinedQuestion)) {
      return (UserDefinedQuestion)e;
    }
    if (e.getParent() != null) {
      return getTopQuestion(e.getParent());
    }
    return null;
  }
  
  public void drop(final DropTargetDropEvent dtde) {
    HACK_started = false;
    boolean successful = true;
    Transferable transferable = dtde.getTransferable();
    int action = dtde.getDropAction();
    boolean isCopy = (action & 0x1) > 0;
    boolean isMove = (action & 0x2) > 0;
    if ((!m_owner.isExpanded()) && 
      ((m_owner.getParent() instanceof CompositeComponentQuestionPanel))) {
      ((CompositeComponentQuestionPanel)m_owner.getParent()).drop(dtde);
      return;
    }
    

    if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, CopyFactoryTransferable.copyFactoryFlavor)) {
      try {
        CopyFactory copyFactory = (CopyFactory)transferable.getTransferData(CopyFactoryTransferable.copyFactoryFlavor);
        Class valueClass = copyFactory.getValueClass();
        if (edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class.isAssignableFrom(valueClass)) {
          dtde.acceptDrop(1);
          successful = true;
          Element question = copyFactory.manufactureCopy(m_owner.getElement().getRoot());
          if (question != null) {
            performDrop(question, dtde);
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
        AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_"), t);
        successful = false;
      }
    } else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, QuestionEditor.componentReferenceFlavor)) {
      try {
        Element question = (Element)transferable.getTransferData(QuestionEditor.componentReferenceFlavor);
        successful = true;
        if (((question instanceof Composite)) && 
          (!isCopy) && (!isValidDrop(componentElements.getOwner(), question))) {
          successful = false;
        }
        
        if (!successful) break label999;
        if (isMove) {
          dtde.acceptDrop(2);
        }
        else if (isCopy) {
          dtde.acceptDrop(1);
        }
        performDrop(question, dtde);
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
    }
    else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
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
          ElementPrototype questionPrototype = (ElementPrototype)transferable.getTransferData(ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
          Class valueClass = questionPrototype.getElementClass();
          if (!edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class.isAssignableFrom(valueClass)) {
            dtde.rejectDrop();
            successful = false;
          }
          if (!successful) break label999;
          if (Return.class.isAssignableFrom(valueClass)) {
            StringObjectPair[] known = { new StringObjectPair("valueClass", getTopQuestiongetComponentPropertygetOwnervalueClass.get()) };
            ElementPrototype newPrototype = new ElementPrototype(valueClass, known, questionPrototype.getDesiredProperties());
            questionPrototype = newPrototype;
          }
          if (((questionPrototype.getDesiredProperties() == null) || (questionPrototype.getDesiredProperties().length < 1)) && 
            (!Print.class.isAssignableFrom(questionPrototype.getElementClass()))) {
            performDrop(questionPrototype.createNewElement(), dtde);
          }
          else {
            PopupItemFactory factory = new PopupItemFactory() {
              public Object createItem(final Object object) {
                new Runnable() {
                  public void run() {
                    if ((object instanceof ElementPrototype)) {
                      Element newQuestion = ((ElementPrototype)object).createNewElement();
                      
                      if ((newQuestion instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component)) {
                        performDrop(newQuestion, val$dtde);
                      }
                    }
                  }
                };
              }
            };
            Vector structure = null;
            if (Print.class.isAssignableFrom(questionPrototype.getElementClass())) {
              structure = PopupMenuUtilities.makeQuestionPrintStructure(factory, componentElements.getOwner());
            }
            else {
              structure = PopupMenuUtilities.makePrototypeStructure(questionPrototype, factory, componentElements.getOwner());
            }
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
      }
    } else if ((AuthoringToolResources.safeIsDataFlavorSupported(dtde, PropertyReferenceTransferable.propertyReferenceFlavor)) || 
      (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.variableReferenceFlavor))) {
      if (isMove) {
        dtde.acceptDrop(2);
        successful = true;
      }
      else if (isCopy) {
        dtde.rejectDrop();
        successful = false;
      }
      if (successful) {
        try { Property property;
          Property property;
          if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.variableReferenceFlavor)) {
            Variable variable = (Variable)transferable.getTransferData(ElementReferenceTransferable.variableReferenceFlavor);
            property = value;
          }
          else {
            property = (Property)transferable.getTransferData(PropertyReferenceTransferable.propertyReferenceFlavor);
          }
          PopupItemFactory factory = new PopupItemFactory() {
            public Object createItem(final Object object) {
              new Runnable() {
                public void run() {
                  if ((object instanceof ElementPrototype)) {
                    Element newQuestion = ((ElementPrototype)object).createNewElement();
                    if ((newQuestion instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component)) {
                      performDrop(newQuestion, val$dtde);
                    }
                  }
                }
              };
            }
          };
          Vector structure = PopupMenuUtilities.makePropertyAssignmentForUserDefinedQuestionStructure(property, factory, componentElements.getOwner());
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
    } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.expressionReferenceFlavor))
    {

      successful = false;
      dtde.rejectDrop();
    } else {
      dtde.rejectDrop(); }
    label999:
    dtde.dropComplete(successful);
  }
}
