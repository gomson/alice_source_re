package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.util.HashSet;
import java.util.Iterator;













public class DnDManager
{
  private static Transferable currentTransferable;
  private static Component currentDragComponent;
  private static HashSet listeners = new HashSet();
  private static DnDListener dndListener = new DnDListener();
  
  public DnDManager() {}
  
  public static Transferable getCurrentTransferable() { return currentTransferable; }
  
  private static DragSourceContext currentContext;
  public static void setCurrentTransferable(Transferable transferable) {
    currentTransferable = transferable;
  }
  
  public static Component getCurrentDragComponent() {
    return currentDragComponent;
  }
  
  public static DragSourceContext getCurrentDragContext() {
    return currentContext;
  }
  
  public static void addListener(DnDManagerListener listener) {
    synchronized (dndListener) {
      listeners.add(listener);
    }
  }
  
  public static void removeListener(DnDManagerListener listener) {
    synchronized (dndListener) {
      listeners.remove(listener);
    }
  }
  
  public static DnDListener getInternalListener() {
    return dndListener;
  }
  
  public static void fireDragGestureRecognized(DragGestureEvent ev) {
    for (Iterator iter = listeners.iterator(); iter.hasNext();) {
      ((DragGestureListener)iter.next()).dragGestureRecognized(ev);
    }
  }
  
  public static void fireDragStarted(Transferable transferable, Component dragComponent) {
    currentTransferable = transferable;
    currentDragComponent = dragComponent;
    for (Iterator iter = listeners.iterator(); iter.hasNext();)
      ((DnDManagerListener)iter.next()).dragStarted();
  }
  
  public static class DnDListener implements DragSourceListener {
    public DnDListener() {}
    
    public synchronized void dragEnter(DragSourceDragEvent dsde) {
      DnDManager.currentTransferable = dsde.getDragSourceContext().getTransferable();
      DnDManager.currentDragComponent = dsde.getDragSourceContext().getComponent();
      DnDManager.currentContext = dsde.getDragSourceContext();
      for (Iterator iter = DnDManager.listeners.iterator(); iter.hasNext();) {
        ((DragSourceListener)iter.next()).dragEnter(dsde);
      }
    }
    
    public synchronized void dragExit(DragSourceEvent dse)
    {
      DnDManager.currentTransferable = dse.getDragSourceContext().getTransferable();
      DnDManager.currentDragComponent = dse.getDragSourceContext().getComponent();
      for (Iterator iter = DnDManager.listeners.iterator(); iter.hasNext();) {
        ((DragSourceListener)iter.next()).dragExit(dse);
      }
    }
    
    public synchronized void dragOver(DragSourceDragEvent dsde)
    {
      DnDManager.currentTransferable = dsde.getDragSourceContext().getTransferable();
      DnDManager.currentDragComponent = dsde.getDragSourceContext().getComponent();
      for (Iterator iter = DnDManager.listeners.iterator(); iter.hasNext();) {
        ((DragSourceListener)iter.next()).dragOver(dsde);
      }
    }
    
    public synchronized void dropActionChanged(DragSourceDragEvent dsde)
    {
      DnDManager.currentTransferable = dsde.getDragSourceContext().getTransferable();
      DnDManager.currentDragComponent = dsde.getDragSourceContext().getComponent();
      for (Iterator iter = DnDManager.listeners.iterator(); iter.hasNext();) {
        ((DragSourceListener)iter.next()).dropActionChanged(dsde);
      }
    }
    
    public synchronized void dragDropEnd(DragSourceDropEvent dsde)
    {
      DnDManager.currentTransferable = null;
      DnDManager.currentDragComponent = null;
      for (Iterator iter = DnDManager.listeners.iterator(); iter.hasNext();) {
        ((DragSourceListener)iter.next()).dragDropEnd(dsde);
      }
    }
  }
}
