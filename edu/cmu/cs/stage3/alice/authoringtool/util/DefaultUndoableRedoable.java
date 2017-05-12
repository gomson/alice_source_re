package edu.cmu.cs.stage3.alice.authoringtool.util;





public class DefaultUndoableRedoable
  implements ContextAssignableUndoableRedoable
{
  Runnable undoRunnable;
  



  Runnable redoRunnable;
  



  Object affectedObject;
  



  Object context;
  




  public DefaultUndoableRedoable(Runnable undoRunnable, Runnable redoRunnable, Object affectedObject)
  {
    this.undoRunnable = undoRunnable;
    this.redoRunnable = redoRunnable;
    this.affectedObject = affectedObject;
  }
  
  public void setContext(Object context) {
    this.context = context;
  }
  
  public void undo() {
    undoRunnable.run();
  }
  
  public void redo() {
    redoRunnable.run();
  }
  
  public Object getAffectedObject() {
    return affectedObject;
  }
  
  public Object getContext() {
    return context;
  }
}
