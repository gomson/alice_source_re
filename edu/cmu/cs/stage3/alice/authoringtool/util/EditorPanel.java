package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.Editor;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.EditorPanelEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.EditorPanelListener;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.JPanel;








public class EditorPanel
  extends JPanel
  implements AuthoringToolStateListener
{
  protected Editor activeEditor = null;
  protected Method activeEditorSetMethod = null;
  protected HashMap cachedEditors = new HashMap();
  protected EditStack editStack = new EditStack();
  protected HashSet listenerSet = new HashSet();
  
  protected AuthoringTool authoringTool;
  protected final ChildrenListener deletionListener = new ChildrenListener() {
    public void childrenChanging(ChildrenEvent ev) {}
    
    public void childrenChanged(ChildrenEvent ev) { if ((ev.getChangeType() == 3) && (ev.getChild() == getElementBeingEdited())) {
        editElement(null);
        ev.getParent().removeChildrenListener(this);
      }
    }
  };
  
  public final AbstractAction backAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      back();
    }
  };
  
  public final AbstractAction forwardAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      forward();
    }
  };
  
  public EditorPanel(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
    setLayout(new BorderLayout());
    actionInit();
  }
  
  private void actionInit() {
    backAction.putValue("ActionCommandKey", "back");
    backAction.putValue("Name", "Back");
    backAction.putValue("ShortDescription", "Back to last item");
    backAction.setEnabled(false);
    
    forwardAction.putValue("ActionCommandKey", "forward");
    forwardAction.putValue("Name", "Forward");
    forwardAction.putValue("ShortDescription", "Forward to next item");
    forwardAction.setEnabled(false);
  }
  
  public void addEditorPanelListener(EditorPanelListener listener) {
    listenerSet.add(listener);
  }
  
  public void removeEditorPanelListener(EditorPanelListener listener) {
    listenerSet.remove(listener);
  }
  
  public Editor loadEditor(Class editorClass) {
    Editor editor = null;
    
    if (editorClass != null) {
      editor = (Editor)cachedEditors.get(editorClass);
      if (editor == null) {
        try {
          editor = EditorUtilities.getEditorFromClass(editorClass);
          if (editor == null) {
            AuthoringTool.showErrorDialog(Messages.getString("Can_t_create_editor_of_type_") + editorClass.getName(), null);
          } else {
            cachedEditors.put(editorClass, editor);
            authoringTool.addAuthoringToolStateListener(editor);
            editor.setAuthoringTool(authoringTool);
          }
        } catch (Throwable t) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_creating_editor_for_type_") + editorClass.getName(), t);
        }
      }
    }
    
    return editor;
  }
  
  public void editElement(Element element) {
    editElement(element, true);
  }
  
  public void editElement(Element element, Class editorClass) {
    editElement(element, editorClass, true);
  }
  
  protected void editElement(Element element, boolean performPush) {
    if (element == null) {
      editElement(null, null, performPush);
    } else {
      Class bestEditorClass = EditorUtilities.getBestEditor(element.getClass());
      if (bestEditorClass == null) {
        AuthoringTool.showErrorDialog(Messages.getString("No_editor_found_for_") + element.getClass(), null);
      }
      editElement(element, bestEditorClass, performPush);
    }
  }
  
  protected void editElement(Element element, Class editorClass, boolean performPush) {
    if ((getElementBeingEdited() != null) && (getElementBeingEdited().getParent() != null)) {
      getElementBeingEdited().getParent().removeChildrenListener(deletionListener);
    }
    
    Editor editor = loadEditor(editorClass);
    

    if (activeEditor != editor)
    {
      if (activeEditor != null) {
        try {
          activeEditorSetMethod.invoke(activeEditor, new Object[1]);
        } catch (InvocationTargetException ite) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_cleaning_editor_"), ite);
        } catch (IllegalAccessException iae) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_cleaning_editor_"), iae);
        }
      }
      

      removeAll();
      activeEditor = editor;
      if (activeEditor != null) {
        activeEditorSetMethod = EditorUtilities.getSetMethodFromClass(editorClass);
        add("Center", activeEditor.getJComponent());
      } else {
        activeEditorSetMethod = null;
        for (Iterator iter = listenerSet.iterator(); iter.hasNext();) {
          EditorPanelEvent ev = new EditorPanelEvent(null);
          ((EditorPanelListener)iter.next()).elementChanged(ev);
        }
      }
      revalidate();
      repaint();
    }
    

    if ((activeEditor != null) && (activeEditor.getObject() != element)) {
      try {
        activeEditorSetMethod.invoke(activeEditor, new Object[] { element });
        if ((performPush) && (element != null)) {
          editStack.push(new EditItem(element, editorClass));
          updateActions();
        }
        for (Iterator iter = listenerSet.iterator(); iter.hasNext();) {
          EditorPanelEvent ev = new EditorPanelEvent(element);
          ((EditorPanelListener)iter.next()).elementChanged(ev);
        }
        if ((element != null) && (element.getParent() != null)) {
          element.getParent().addChildrenListener(deletionListener);
        }
      } catch (InvocationTargetException ite) {
        AuthoringTool.showErrorDialog(Messages.getString("Error_intializing_editor_"), ite);
      } catch (IllegalAccessException iae) {
        AuthoringTool.showErrorDialog(Messages.getString("Error_intializing_editor_"), iae);
      }
    }
  }
  
  public Element getElementBeingEdited() {
    if (activeEditor != null) {
      return (Element)activeEditor.getObject();
    }
    return null;
  }
  
  public Editor getActiveEditor()
  {
    return activeEditor;
  }
  
  public void back() {
    editStack.undo();
    updateActions();
  }
  
  public void forward() {
    editStack.redo();
    updateActions();
  }
  
  public boolean canGoBack() {
    return editStack.getCurrentUndoableRedoableIndex() > 0;
  }
  
  public boolean canGoForward() {
    return editStack.getCurrentUndoableRedoableIndex() != editStack.size() - 1;
  }
  
  protected void updateActions() {
    backAction.setEnabled(canGoBack());
    forwardAction.setEnabled(canGoForward());
  }
  
  class EditStack extends DefaultUndoRedoStack { EditStack() {}
    
    public UndoableRedoable undo() { UndoableRedoable ur = super.undo();
      UndoableRedoable newItem = editStack.getCurrentUndoableRedoable();
      if (ur != null) {
        editElement(((EditorPanel.EditItem)newItem).getElement(), ((EditorPanel.EditItem)newItem).getEditorClass(), false);
      }
      return ur;
    }
    
    public UndoableRedoable redo() {
      UndoableRedoable ur = super.redo();
      if (ur != null) {
        editElement(((EditorPanel.EditItem)ur).getElement(), ((EditorPanel.EditItem)ur).getEditorClass(), false);
      }
      return ur; } }
  
  public void stateChanging(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  class EditItem implements UndoableRedoable { protected Element element;
    protected Class editorClass;
    
    public EditItem(Element element, Class editorClass) { this.element = element;
      this.editorClass = editorClass;
    }
    
    public void undo() {}
    
    public void redo() {}
    
    public Object getAffectedObject() { return element; }
    
    public Object getContext()
    {
      return EditorPanel.this;
    }
    
    public Element getElement() {
      return element;
    }
    
    public Class getEditorClass() {
      return editorClass;
    }
  }
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  
  public void stateChanged(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {
    editStack.clear();
  }
  
  public void worldStarted(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopped(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPaused(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaved(AuthoringToolStateChangedEvent ev) {}
}
