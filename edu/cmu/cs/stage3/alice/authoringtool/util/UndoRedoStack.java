package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.util.List;

public abstract interface UndoRedoStack
  extends List
{
  public abstract void push(UndoableRedoable paramUndoableRedoable);
  
  public abstract UndoableRedoable pop_();
  
  public abstract UndoableRedoable undo();
  
  public abstract UndoableRedoable redo();
  
  public abstract UndoableRedoable removeUndoable(int paramInt);
  
  public abstract int getCurrentUndoableRedoableIndex();
  
  public abstract void clear();
}
