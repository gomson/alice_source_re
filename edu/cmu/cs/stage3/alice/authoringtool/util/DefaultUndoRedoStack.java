package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.util.LinkedList;
import java.util.ListIterator;


















public class DefaultUndoRedoStack
  extends LinkedList
  implements UndoRedoStack
{
  private int currentIndex = -1;
  
  public DefaultUndoRedoStack() {}
  
  public synchronized void push(UndoableRedoable ur) { if (currentIndex < size() - 1) {
      removeRange(currentIndex + 1, size());
    }
    addLast(ur);
    currentIndex = (size() - 1);
  }
  
  public synchronized UndoableRedoable pop_() {
    if (currentIndex == size() - 1) {
      currentIndex -= 1;
    }
    return (UndoableRedoable)removeLast();
  }
  
  public synchronized UndoableRedoable undo()
  {
    if (currentIndex > -1) {
      UndoableRedoable ur = (UndoableRedoable)get(currentIndex);
      
      ur.undo();
      currentIndex -= 1;
      return ur;
    }
    return null;
  }
  

  public synchronized UndoableRedoable redo()
  {
    if (currentIndex < size() - 1) {
      currentIndex += 1;
      UndoableRedoable ur = (UndoableRedoable)get(currentIndex);
      
      ur.redo();
      return ur;
    }
    return null;
  }
  
  public synchronized UndoableRedoable removeUndoable(int index)
  {
    if ((index < 0) || (index > size() - 1)) {
      return null;
    }
    if (index > currentIndex) {
      return (UndoableRedoable)remove(index);
    }
    
    UndoableRedoable removedItem = null;
    ListIterator iter = listIterator();
    while (iter.nextIndex() <= currentIndex) {
      iter.next();
    }
    while (iter.previousIndex() >= index) {
      UndoableRedoable ur = (UndoableRedoable)iter.previous();
      ur.undo();
      removedItem = ur;
    }
    iter.remove();
    currentIndex -= 1;
    while (iter.nextIndex() <= currentIndex) {
      UndoableRedoable ur = (UndoableRedoable)iter.next();
      ur.redo();
    }
    
    return removedItem;
  }
  
  public synchronized int getCurrentUndoableRedoableIndex() {
    return currentIndex;
  }
  
  public synchronized UndoableRedoable getCurrentUndoableRedoable() {
    return (UndoableRedoable)get(currentIndex);
  }
  
  public synchronized void clear() {
    super.clear();
    currentIndex = -1;
  }
}
