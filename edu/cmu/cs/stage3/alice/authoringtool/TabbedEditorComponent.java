package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor;
import edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JTabbedPane;

public class TabbedEditorComponent extends javax.swing.JPanel
{
  protected AuthoringTool authoringTool;
  protected EditorManager editorManager;
  protected EditorDropTargetListener editorDropTargetListener = new EditorDropTargetListener();
  protected HashMap componentsToEditors = new HashMap();
  protected RightClickListener rightClickListener = new RightClickListener();
  protected NameListener nameListener = new NameListener();
  protected DeletionListener deletionListener = new DeletionListener();
  protected Runnable closeAllTabsRunnable = new Runnable() {
    public void run() {
      closeAllTabs();
    }
  };
  protected World world;
  private Configuration authoringToolConfig = Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
  
  public TabbedEditorComponent(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
    editorManager = authoringTool.getEditorManager();
    jbInit();
    guiInit();
  }
  
  private void guiInit()
  {
    tabbedPane.setUI(new edu.cmu.cs.stage3.alice.authoringtool.util.AliceTabbedPaneUI());
    setDropTarget(new DropTarget(this, editorDropTargetListener));
    tabbedPane.setDropTarget(new DropTarget(tabbedPane, editorDropTargetListener));
    tabbedPane.addMouseListener(rightClickListener);
    int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
    tabbedPane.setFont(new java.awt.Font("SansSerif", 0, (int)(16.0D * (fontSize / 12.0D))));
  }
  















  public void setWorld(World world)
  {
    stopListeningToTree(world);
    closeAllTabs();
    
    this.world = world;
    if (world != null) {
      startListeningToTree(world);
    }
  }
  
  public void editObject(Object object, Class editorClass, boolean switchToNewTab) {
    if ((object == null) || (editorClass == null)) {
      closeAllTabs();
    }
    else if (!isObjectBeingEdited(object)) {
      Editor editor = editorManager.getEditorInstance(editorClass);
      if (editor != null) {
        componentsToEditors.put(editor.getJComponent(), editor);
        EditorUtilities.editObject(editor, object);
        String repr = AuthoringToolResources.getReprForValue(object, true);
        Object iconObject = object;
        if ((object instanceof UserDefinedQuestion)) {
          UserDefinedQuestion udq = (UserDefinedQuestion)object;
          if (List.class.isAssignableFrom(udq.getValueClass())) {
            List list = (List)udq.getValue();
            iconObject = "types/lists/" + valueClass.getClassValue().getName();
          } else {
            iconObject = "types/" + udq.getValueClass().getName();
          }
        }
        javax.swing.ImageIcon icon = AuthoringToolResources.getIconForValue(iconObject);
        tabbedPane.addTab(repr, icon, editor.getJComponent());
        if (switchToNewTab) {
          tabbedPane.setSelectedComponent(editor.getJComponent());
          tabbedPane.getSelectedComponent().setVisible(true);
        }
        if ((object instanceof Element)) {
          name.addPropertyListener(nameListener);
        }
        


        authoringTool.saveTabs();
      } else {
        AuthoringTool.showErrorDialog(Messages.getString("failed_to_create_editor_for_") + object + ", " + editorClass, null);
      }
    } else if (switchToNewTab) {
      for (int i = 0; i < tabbedPane.getTabCount(); i++) {
        Component component = tabbedPane.getComponentAt(i);
        if (component != null) {
          Editor editor = (Editor)componentsToEditors.get(component);
          if ((editor != null) && 
            (editor.getObject() == object)) {
            tabbedPane.setSelectedIndex(i);
            tabbedPane.getSelectedComponent().setVisible(true);
            break;
          }
        }
      }
    }
  }
  

  public Object getObjectBeingEdited()
  {
    Component component = tabbedPane.getSelectedComponent();
    if (component != null) {
      Editor editor = (Editor)componentsToEditors.get(component);
      if (editor != null) {
        return editor.getObject();
      }
    }
    return null;
  }
  
  public Object getObjectBeingEditedAt(int index) {
    Component component = tabbedPane.getComponentAt(index);
    if (component != null) {
      Editor editor = (Editor)componentsToEditors.get(component);
      if (editor != null) {
        return editor.getObject();
      }
    }
    return null;
  }
  
  public Object[] getObjectsBeingEdited() {
    Component[] components = tabbedPane.getComponents();
    if (components != null) {
      ArrayList objects = new ArrayList();
      for (int i = 0; i < components.length; i++) {
        Editor editor = (Editor)componentsToEditors.get(components[i]);
        if (editor != null) {
          objects.add(editor.getObject());
        }
      }
      return objects.toArray();
    }
    return null;
  }
  
  public int getIndexOfObject(Object o) {
    Component[] components = tabbedPane.getComponents();
    if (components != null) {
      for (int i = 0; i < components.length; i++) {
        Editor editor = (Editor)componentsToEditors.get(components[i]);
        if ((editor != null) && 
          (editor.getObject().equals(o))) {
          return i;
        }
      }
    }
    
    return -1;
  }
  
  public Editor getCurrentEditor() {
    Component component = tabbedPane.getSelectedComponent();
    if (component != null) {
      return (Editor)componentsToEditors.get(component);
    }
    return null;
  }
  
  public Editor getEditorAt(int index) {
    Component component = tabbedPane.getComponentAt(index);
    if (component != null) {
      return (Editor)componentsToEditors.get(component);
    }
    return null;
  }
  
  public Editor[] getEditors() {
    Component[] components = tabbedPane.getComponents();
    if (components != null) {
      ArrayList editors = new ArrayList();
      for (int i = 0; i < components.length; i++) {
        Editor editor = (Editor)componentsToEditors.get(components[i]);
        if (editor != null) {
          editors.add(editor);
        }
      }
      return (Editor[])editors.toArray(new Editor[0]);
    }
    return null;
  }
  
  public int getIndexOfEditor(Editor editor) {
    Component[] components = tabbedPane.getComponents();
    if (components != null) {
      for (int i = 0; i < components.length; i++) {
        Editor e = (Editor)componentsToEditors.get(components[i]);
        if (editor.equals(e)) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public void closeTab(int index) {
    Component component = tabbedPane.getComponentAt(index);
    if (component != null) {
      Editor editor = (Editor)componentsToEditors.get(component);
      if (editor != null) {
        Object object = editor.getObject();
        tabbedPane.removeTabAt(index);
        EditorUtilities.editObject(editor, null);
        editorManager.releaseEditorInstance(editor);
        componentsToEditors.remove(component);
        if (((object instanceof Element)) && (!isObjectBeingEdited(object))) {
          name.removePropertyListener(nameListener);
        }
        



        authoringTool.saveTabs();
      }
    } else {
      AuthoringTool.showErrorDialog(Messages.getString("no_editor_to_close_at_") + index, null);
    }
  }
  
  public void closeAllTabs() {
    while (tabbedPane.getTabCount() > 0) {
      closeTab(0);
    }
  }
  
  public boolean isObjectBeingEdited(Object o) {
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
      Component component = tabbedPane.getComponentAt(i);
      if (component != null) {
        Editor editor = (Editor)componentsToEditors.get(component);
        if ((editor != null) && 
          (editor.getObject() == o)) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  public SceneEditor getCurrentSceneEditor() {
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
      Component component = tabbedPane.getComponentAt(i);
      if (component != null) {
        Editor editor = (Editor)componentsToEditors.get(component);
        if ((editor instanceof SceneEditor)) {
          return (SceneEditor)editor;
        }
      }
    }
    return null;
  }
  
  protected class RightClickListener extends edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter { protected RightClickListener() {}
    
    public void popupResponse(MouseEvent ev) { final int index = tabbedPane.getUI().tabForCoordinate(tabbedPane, ev.getX(), ev.getY());
      if ((index >= 0) && (index < tabbedPane.getTabCount())) {
        Runnable closeTabRunnable = new Runnable() {
          public void run() {
            closeTab(index);
          }
        };
        Vector structure = new Vector();
        structure.add(new edu.cmu.cs.stage3.util.StringObjectPair(Messages.getString("Close_") + tabbedPane.getTitleAt(index), closeTabRunnable));
        structure.add(new edu.cmu.cs.stage3.util.StringObjectPair(Messages.getString("Close_All"), closeAllTabsRunnable));
        edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(structure, tabbedPane, ev.getX(), ev.getY());
      }
    }
  }
  
  protected class EditorDropTargetListener implements java.awt.dnd.DropTargetListener {
    protected EditorDropTargetListener() {}
    
    protected void checkDrag(DropTargetDragEvent dtde) { if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) {
        dtde.acceptDrag(2);
        return; }
      if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) {
        dtde.acceptDrag(2);
        return; }
      if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass(CallToUserDefinedResponse.class))) {
        dtde.acceptDrag(2);
        return; }
      if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass(CallToUserDefinedQuestion.class))) {
        dtde.acceptDrag(2);
        return;
      }
      DataFlavor[] flavors = AuthoringToolResources.safeGetCurrentDataFlavors(dtde);
      if (flavors != null) {
        for (int i = 0; i < flavors.length; i++) {
          Class c = flavors[i].getRepresentationClass();
          if (EditorUtilities.getBestEditor(c) != null) {
            dtde.acceptDrag(2);
            return;
          }
        }
      }
      
      dtde.rejectDrag();
    }
    
    public void dragEnter(DropTargetDragEvent dtde) {
      checkDrag(dtde);
    }
    
    public void dragOver(DropTargetDragEvent dtde) {
      checkDrag(dtde);
    }
    
    public void dropActionChanged(DropTargetDragEvent dtde) {
      checkDrag(dtde);
    }
    

    public void dragExit(DropTargetEvent dte) {}
    
    public void drop(DropTargetDropEvent dtde)
    {
      try
      {
        Object o = null;
        if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass(CallToUserDefinedResponse.class))) {
          dtde.acceptDrop(2);
          Transferable transferable = dtde.getTransferable();
          o = transferable.getTransferData(AuthoringToolResources.getReferenceFlavorForClass(CallToUserDefinedResponse.class));
          o = userDefinedResponse.getUserDefinedResponseValue();
        } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass(CallToUserDefinedQuestion.class))) {
          dtde.acceptDrop(2);
          Transferable transferable = dtde.getTransferable();
          o = transferable.getTransferData(AuthoringToolResources.getReferenceFlavorForClass(CallToUserDefinedQuestion.class));
          o = userDefinedQuestion.getUserDefinedQuestionValue();
        } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.elementReferenceFlavor)) {
          dtde.acceptDrop(2);
          Transferable transferable = dtde.getTransferable();
          o = transferable.getTransferData(ElementReferenceTransferable.elementReferenceFlavor);
        } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) {
          dtde.acceptDrop(2);
          Transferable transferable = dtde.getTransferable();
          CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = (CallToUserDefinedResponsePrototype)transferable.getTransferData(CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor);
          o = callToUserDefinedResponsePrototype.getActualResponse();
        } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) {
          dtde.acceptDrop(2);
          Transferable transferable = dtde.getTransferable();
          CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = (CallToUserDefinedQuestionPrototype)transferable.getTransferData(CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor);
          o = callToUserDefinedQuestionPrototype.getActualQuestion();
        } else {
          DataFlavor[] flavors = AuthoringToolResources.safeGetCurrentDataFlavors(dtde);
          if (flavors != null) {
            for (int i = 0; i < flavors.length; i++) {
              Class c = flavors[i].getDefaultRepresentationClass();
              if (EditorUtilities.getBestEditor(c) != null) {
                dtde.acceptDrop(2);
                Transferable transferable = dtde.getTransferable();
                o = transferable.getTransferData(flavors[i]);
                break;
              }
            }
          }
        }
        if (o != null)
        {
          Class editorClass = EditorUtilities.getBestEditor(o.getClass());
          
          if (editorClass != null) {
            editObject(o, editorClass, true);
          }
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
    }
  }
  














  protected class NameListener
    implements edu.cmu.cs.stage3.alice.core.event.PropertyListener
  {
    protected NameListener() {}
    














    public void propertyChanging(PropertyEvent ev) {}
    













    public void propertyChanged(PropertyEvent ev)
    {
      for (int i = 0; i < tabbedPane.getTabCount(); i++) {
        Object object = getObjectBeingEditedAt(i);
        if (object == ev.getProperty().getOwner())
          tabbedPane.setTitleAt(i, AuthoringToolResources.getReprForValue(ev.getProperty().getOwner(), true));
      }
    }
  }
  
  protected class DeletionListener implements edu.cmu.cs.stage3.alice.core.event.ChildrenListener { protected DeletionListener() {}
    
    public void childrenChanging(ChildrenEvent ev) {}
    
    public void childrenChanged(ChildrenEvent ev) { if (ev.getChangeType() == 3) {
        stopListeningToTree(ev.getChild());
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
          Object object = getObjectBeingEditedAt(i);
          if ((object instanceof Element)) {
            Element element = (Element)object;
            if ((element == ev.getChild()) || (ev.getChild().isAncestorOf(element))) {
              closeTab(i);
              i--;
            }
          }
        }
      } else if (ev.getChangeType() == 1) {
        startListeningToTree(ev.getChild());
      }
    }
  }
  
  protected void startListeningToTree(Element element) {
    if (element != null) {
      Element[] descendants = element.getDescendants();
      for (int i = 0; i < descendants.length; i++) {
        descendants[i].addChildrenListener(deletionListener);
      }
    }
  }
  
  protected void stopListeningToTree(Element element) {
    if (element != null) {
      Element[] descendants = element.getDescendants();
      for (int i = 0; i < descendants.length; i++) {
        descendants[i].removeChildrenListener(deletionListener);
      }
    }
  }
  




  JTabbedPane tabbedPane = new JTabbedPane();
  BorderLayout borderLayout1 = new BorderLayout();
  javax.swing.border.Border border1;
  
  private void jbInit() {
    border1 = javax.swing.BorderFactory.createMatteBorder(8, 8, 8, 8, new java.awt.Color(255, 230, 180));
    setLayout(borderLayout1);
    setOpaque(false);
    add(tabbedPane, "Center");
  }
}
