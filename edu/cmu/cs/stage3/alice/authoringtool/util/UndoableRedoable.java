package edu.cmu.cs.stage3.alice.authoringtool.util;

public abstract interface UndoableRedoable
{
  public abstract void undo();
  
  public abstract void redo();
  
  public abstract Object getAffectedObject();
  
  public abstract Object getContext();
}
