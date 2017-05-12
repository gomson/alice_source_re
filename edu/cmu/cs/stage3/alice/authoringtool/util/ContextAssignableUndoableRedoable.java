package edu.cmu.cs.stage3.alice.authoringtool.util;

public abstract interface ContextAssignableUndoableRedoable
  extends UndoableRedoable
{
  public abstract void setContext(Object paramObject);
}
