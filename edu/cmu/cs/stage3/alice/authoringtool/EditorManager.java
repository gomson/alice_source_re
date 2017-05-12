package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;





















public class EditorManager
{
  protected AuthoringTool authoringTool;
  protected List availableEditors = new ArrayList();
  protected List inUseEditors = new ArrayList();
  
  public EditorManager(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
  }
  
  public Editor getBestEditorInstance(Class objectClass) {
    return getEditorInstance(EditorUtilities.getBestEditor(objectClass));
  }
  
  public Editor getEditorInstance(Class editorClass) {
    if (editorClass == null) {
      return null;
    }
    
    for (Iterator iter = availableEditors.listIterator(); iter.hasNext();) {
      Object editor = iter.next();
      if (editor.getClass() == editorClass) {
        iter.remove();
        inUseEditors.add(editor);
        return (Editor)editor;
      }
    }
    
    Editor editor = EditorUtilities.getEditorFromClass(editorClass);
    if (editor != null) {
      authoringTool.addAuthoringToolStateListener(editor);
      inUseEditors.add(editor);
      editor.setAuthoringTool(authoringTool);
    }
    return editor;
  }
  
  public void releaseEditorInstance(Editor editor) {
    if (inUseEditors.contains(editor)) {
      inUseEditors.remove(editor);
      if (!availableEditors.contains(editor)) {
        availableEditors.add(editor);
      }
    }
  }
  
  public void preloadEditor(Class editorClass) {
    releaseEditorInstance(getEditorInstance(editorClass));
  }
}
