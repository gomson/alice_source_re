package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.util.ArrayList;
import java.util.ListIterator;



















public class CompoundUndoableRedoable
  implements ContextAssignableUndoableRedoable
{
  protected ArrayList items = new ArrayList();
  protected Object context;
  
  public CompoundUndoableRedoable() {}
  
  public void setContext(Object context) {
    this.context = context;
  }
  
  public void addItem(UndoableRedoable item) {
    if ((item instanceof ChildChangeUndoableRedoable)) {
      ChildChangeUndoableRedoable localChildChangeUndoableRedoable = (ChildChangeUndoableRedoable)item;
    }
    items.add(item);
  }
  
  public void undo() {
    ListIterator iter = items.listIterator();
    while (iter.hasNext()) {
      iter.next();
    }
    while (iter.hasPrevious()) {
      UndoableRedoable item = (UndoableRedoable)iter.previous();
      item.undo();
    }
  }
  
  public void redo() {
    ListIterator iter = items.listIterator();
    while (iter.hasNext()) {
      UndoableRedoable item = (UndoableRedoable)iter.next();
      item.redo();
    }
  }
  
  public Object getAffectedObject() {
    return this;
  }
  
  public Object getContext() {
    return context;
  }
}
