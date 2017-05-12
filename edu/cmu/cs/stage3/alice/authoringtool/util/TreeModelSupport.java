package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;


























public class TreeModelSupport
{
  public TreeModelSupport() {}
  
  private Vector vector = new Vector();
  
  public void addTreeModelListener(TreeModelListener listener) {
    if ((listener != null) && (!vector.contains(listener))) {
      vector.addElement(listener);
    }
  }
  
  public void removeTreeModelListener(TreeModelListener listener) {
    if (listener != null) {
      vector.removeElement(listener);
    }
  }
  
  public void fireTreeNodesChanged(final TreeModelEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Enumeration listeners = vector.elements();
        while (listeners.hasMoreElements()) {
          TreeModelListener listener = (TreeModelListener)listeners.nextElement();
          listener.treeNodesChanged(e);
        }
      }
    });
  }
  
  public void fireTreeNodesInserted(final TreeModelEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Enumeration listeners = vector.elements();
        while (listeners.hasMoreElements()) {
          TreeModelListener listener = (TreeModelListener)listeners.nextElement();
          listener.treeNodesInserted(e);
        }
      }
    });
  }
  
  public void fireTreeNodesRemoved(final TreeModelEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Enumeration listeners = vector.elements();
        while (listeners.hasMoreElements()) {
          TreeModelListener listener = (TreeModelListener)listeners.nextElement();
          listener.treeNodesRemoved(e);
        }
      }
    });
  }
  
  public void fireTreeStructureChanged(final TreeModelEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Enumeration listeners = vector.elements();
        while (listeners.hasMoreElements()) {
          TreeModelListener listener = (TreeModelListener)listeners.nextElement();
          if ((listener != null) && (e != null)) {
            listener.treeStructureChanged(e);
          }
        }
      }
    });
  }
}
