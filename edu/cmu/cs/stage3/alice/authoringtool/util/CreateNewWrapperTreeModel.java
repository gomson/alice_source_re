package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.lang.Messages;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;





















public class CreateNewWrapperTreeModel
  implements TreeModel
{
  protected TreeModel wrappedTreeModel;
  protected Object createNewObject;
  
  public CreateNewWrapperTreeModel(TreeModel treeModelToWrap, Object createNewObject)
  {
    if ((treeModelToWrap != null) && (createNewObject != null)) {
      wrappedTreeModel = treeModelToWrap;
      this.createNewObject = createNewObject;
    } else {
      throw new IllegalArgumentException(Messages.getString("treeModelToWrap_and_createNewObject_cannot_be_null"));
    }
  }
  
  public Object getRoot() {
    return wrappedTreeModel.getRoot();
  }
  
  public Object getChild(Object parent, int index) {
    if ((parent == wrappedTreeModel.getRoot()) && (index == wrappedTreeModel.getChildCount(parent))) {
      return createNewObject;
    }
    return wrappedTreeModel.getChild(parent, index);
  }
  
  public int getChildCount(Object parent)
  {
    if (parent == wrappedTreeModel.getRoot()) {
      return wrappedTreeModel.getChildCount(parent) + 1;
    }
    return wrappedTreeModel.getChildCount(parent);
  }
  
  public boolean isLeaf(Object node)
  {
    if (node == createNewObject) {
      return true;
    }
    return wrappedTreeModel.isLeaf(node);
  }
  
  public void valueForPathChanged(TreePath path, Object newValue)
  {
    if (path.getLastPathComponent() != createNewObject) {
      wrappedTreeModel.valueForPathChanged(path, newValue);
    }
  }
  
  public int getIndexOfChild(Object parent, Object child) {
    if (child == createNewObject) {
      return wrappedTreeModel.getChildCount(parent);
    }
    return wrappedTreeModel.getIndexOfChild(parent, child);
  }
  
  public void addTreeModelListener(TreeModelListener l)
  {
    wrappedTreeModel.addTreeModelListener(l);
  }
  
  public void removeTreeModelListener(TreeModelListener l) {
    wrappedTreeModel.removeTreeModelListener(l);
  }
}
