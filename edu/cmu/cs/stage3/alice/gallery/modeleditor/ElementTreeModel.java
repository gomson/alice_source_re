package edu.cmu.cs.stage3.alice.gallery.modeleditor;

import edu.cmu.cs.stage3.alice.core.Element;

class ElementTreeModel implements javax.swing.tree.TreeModel { private Element m_root; private java.util.Vector m_treeModelListeners = new java.util.Vector();
  
  ElementTreeModel() {}
  private Object[] getPath(Element element) { java.util.Vector v = new java.util.Vector();
    while (element != m_root.getParent()) {
      v.insertElementAt(element, 0);
      element = element.getParent();
    }
    return v.toArray();
  }
  
  private boolean isAccepted(Element e) {
    if ((e instanceof edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray))
      return false;
    if ((e instanceof edu.cmu.cs.stage3.alice.core.Response))
      return e instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
    if ((e instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component)) {
      return false;
    }
    return true;
  }
  
  private void fireTreeStructureChanged(Object[] path) {
    javax.swing.event.TreeModelEvent e = new javax.swing.event.TreeModelEvent(this, path);
    java.util.Enumeration enum0 = m_treeModelListeners.elements();
    while (enum0.hasMoreElements()) {
      javax.swing.event.TreeModelListener l = (javax.swing.event.TreeModelListener)enum0.nextElement();
      l.treeStructureChanged(e);
    }
  }
  
  public void setRoot(Element root) { m_root = root;
    fireTreeStructureChanged(getPath(m_root));
  }
  
  public void addTreeModelListener(javax.swing.event.TreeModelListener l) { m_treeModelListeners.addElement(l); }
  

  public void removeTreeModelListener(javax.swing.event.TreeModelListener l) { m_treeModelListeners.removeElement(l); }
  
  public Object getChild(Object parent, int index) {
    Element parentElement = (Element)parent;
    
    int i = 0;
    for (int lcv = 0; lcv < parentElement.getChildCount(); lcv++) {
      Element childAtLCV = parentElement.getChildAt(lcv);
      if (isAccepted(childAtLCV)) {
        if (i == index) {
          return childAtLCV;
        }
        i++;
      }
    }
    return null;
  }
  
  public int getChildCount(Object parent) { Element parentElement = (Element)parent;
    
    int i = 0;
    for (int lcv = 0; lcv < parentElement.getChildCount(); lcv++) {
      Element childAtLCV = parentElement.getChildAt(lcv);
      if (isAccepted(childAtLCV)) {
        i++;
      }
    }
    return i;
  }
  
  public int getIndexOfChild(Object parent, Object child) { Element parentElement = (Element)parent;
    
    int i = 0;
    for (int lcv = 0; lcv < parentElement.getChildCount(); lcv++) {
      Element childAtLCV = parentElement.getChildAt(lcv);
      if (childAtLCV == child) {
        return i;
      }
      if (isAccepted(childAtLCV)) {
        i++;
      }
    }
    return -1;
  }
  
  public Object getRoot() { return m_root; }
  
  public boolean isLeaf(Object node) {
    return getChildCount(node) == 0;
  }
  
  public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {}
  
  public void removeDescendant(Element descendant)
  {
    Object[] path = getPath(descendant.getParent());
    descendant.removeFromParent();
    fireTreeStructureChanged(path);
  }
}
