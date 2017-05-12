package edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
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
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComponent;

public class VariableGroupEditor extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener, java.awt.dnd.DropTargetListener
{
  protected ObjectArrayProperty variables;
  protected JButton newVariableButton = new JButton(Messages.getString("create_new_variable"));
  
  protected AuthoringTool authoringTool;
  
  protected int lineLocation = -1;
  protected int variablePosition = 0;
  
  public static final Configuration variableConfig = Configuration.getLocalConfiguration(VariableGroupEditor.class.getPackage());
  
  public VariableGroupEditor() {
    guiInit();
  }
  
  private void guiInit() {
    setLayout(new java.awt.GridBagLayout());
    setBackground(Color.white);
    newVariableButton.setBackground(new Color(240, 240, 255));
    newVariableButton.setMargin(new Insets(2, 4, 2, 4));
    newVariableButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent ev) {
          if (authoringTool != null) {
            Variable variable = authoringTool.showNewVariableDialog(Messages.getString("create_new_variable"), variables.getOwner());
            if (variable != null) {
              authoringTool.getUndoRedoStack().startCompound();
              try {
                variables.getOwner().addChild(variable);
                variables.add(variable);
              } finally {
                authoringTool.getUndoRedoStack().stopCompound();
              }
              
            }
          }
        }
      });
    addContainerListener(edu.cmu.cs.stage3.alice.authoringtool.util.GUIElementContainerListener.getStaticListener());
    setDropTarget(new DropTarget(this, this));
    newVariableButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Open_New_Variable_Dialog__p__p_Variables_allow_you_to_store_information__p_You_may_choose_among_several_types_p_of_information__like_Numbers__Booleans__and_Objects__") + "</font></html>");
    
    refreshGUI();
  }
  
  public JComponent getJComponent() {
    return this;
  }
  
  public void setVariableObjectArrayProperty(ObjectArrayProperty variables) {
    if (this.variables != null) {
      this.variables.removeObjectArrayPropertyListener(this);
    }
    
    this.variables = variables;
    
    if (this.variables != null) {
      this.variables.addObjectArrayPropertyListener(this);
    }
    refreshGUI();
  }
  
  public void setAuthoringTool(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
  }
  
  public JButton getNewVariableButton() {
    return newVariableButton;
  }
  
  public void refreshGUI() {
    removeAll();
    if (variables != null) {
      int count = 0;
      for (int i = 0; i < variables.size(); i++) {
        if ((variables.get(i) instanceof Variable)) {
          Variable variable = (Variable)variables.get(i);
          edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(value);
          







          JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getVariableGUI(variable, true, factory);
          if (gui != null) {
            add(gui, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
          } else {
            AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_variable__") + variable, null);
          }
        }
      }
      
      add(newVariableButton, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(8, 2, 8, 2), 0, 0));
      newVariableButton.setDropTarget(new DropTarget(newVariableButton, this));
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
    int numSpots = variables.size() + 1;
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
    
    variablePosition = closestSpot;
    lineLocation = spots[closestSpot];
  }
  
  public void dragEnter(DropTargetDragEvent dtde) {
    Transferable transferable = dtde.getTransferable();
    try {
      Variable variable = (Variable)transferable.getTransferData(ElementReferenceTransferable.variableReferenceFlavor);
      if ((variable.getParent() instanceof UserDefinedResponse)) {
        lineLocation = -1;
        dtde.rejectDrag();
      }
      else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.variableReferenceFlavor)) {
        dtde.acceptDrag(2);
        int mouseY = convertPointgetDropTargetContextgetComponentgetLocationy;
        calculateLineLocation(mouseY);
      } else {
        lineLocation = -1;
        dtde.rejectDrag();
      }
    }
    catch (Exception localException) {}
    
    repaint();
  }
  
  public void dragOver(DropTargetDragEvent dtde) {
    Transferable transferable = dtde.getTransferable();
    try {
      Variable variable = (Variable)transferable.getTransferData(ElementReferenceTransferable.variableReferenceFlavor);
      if ((variable.getParent() instanceof UserDefinedResponse)) {
        lineLocation = -1;
        dtde.rejectDrag();
      }
      else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.variableReferenceFlavor)) {
        dtde.acceptDrag(2);
        int mouseY = convertPointgetDropTargetContextgetComponentgetLocationy;
        calculateLineLocation(mouseY);
      } else {
        lineLocation = -1;
        dtde.rejectDrag();
      }
    }
    catch (Exception localException) {}
    
    repaint();
  }
  
  public void drop(DropTargetDropEvent dtde) {
    Transferable transferable = dtde.getTransferable();
    
    if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.variableReferenceFlavor)) {
      dtde.acceptDrop(2);
      try {
        Variable variable = (Variable)transferable.getTransferData(ElementReferenceTransferable.variableReferenceFlavor);
        if ((variable.getParent() instanceof UserDefinedResponse)) {
          dtde.dropComplete(false);
        } else {
          if (variables.contains(variable)) {
            if (variablePosition > variables.indexOf(variable)) {
              variablePosition -= 1;
            }
            variables.remove(variable);
          }
          variables.add(variablePosition, variable);
          dtde.dropComplete(true);
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
    } else {
      dtde.rejectDrop();
      dtde.dropComplete(false);
    }
    lineLocation = -1;
    repaint();
  }
  
  public void dropActionChanged(DropTargetDragEvent dtde) {
    if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.variableReferenceFlavor)) {
      dtde.acceptDrag(2);
    } else {
      dtde.rejectDrag();
    }
  }
  
  public void dragExit(java.awt.dnd.DropTargetEvent dte) {
    lineLocation = -1;
    repaint();
  }
  


  public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
  


  public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev)
  {
    refreshGUI();
  }
  
  public void stateChanging(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  
  public void stateChanged(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarted(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopped(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPaused(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaved(AuthoringToolStateChangedEvent ev) {}
}
