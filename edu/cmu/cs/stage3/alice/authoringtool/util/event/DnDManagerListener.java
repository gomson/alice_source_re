package edu.cmu.cs.stage3.alice.authoringtool.util.event;

import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceListener;

public abstract interface DnDManagerListener
  extends DragSourceListener, DragGestureListener
{
  public abstract void dragStarted();
}
