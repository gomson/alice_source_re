package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.Criterion;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;








public class FilteringElementTreeModel
  extends TreeModelSupport
  implements PropertyListener, ChildrenListener, AuthoringToolStateListener, TreeModel
{
  protected Element root;
  protected Object[] emptyPath = { new World() };
  protected LinkedList inclusionList;
  protected LinkedList exclusionList;
  
  public FilteringElementTreeModel() {}
  
  public LinkedList getInclusionList() {
    return inclusionList;
  }
  
  public void setInclusionList(LinkedList list) {
    inclusionList = list;
    update();
  }
  
  public LinkedList getExclusionList() {
    return exclusionList;
  }
  
  public void setExclusionList(LinkedList list) {
    exclusionList = list;
    update();
  }
  
  public boolean isAcceptedByFilter(Element element) {
    if (inclusionList == null) {
      return false;
    }
    

    if (exclusionList != null) {
      for (Iterator iter = exclusionList.iterator(); iter.hasNext();) {
        Object item = iter.next();
        if (((item instanceof Criterion)) && 
          (((Criterion)item).accept(element))) {
          return false;
        }
      }
    }
    


    if (inclusionList != null) {
      for (Iterator iter = inclusionList.iterator(); iter.hasNext();) {
        Object item = iter.next();
        if (((item instanceof Criterion)) && 
          (((Criterion)item).accept(element))) {
          return true;
        }
      }
    }
    

    return false;
  }
  
  public boolean isElementInTree(Element element) {
    if (element == root) {
      return true;
    }
    
    if (element == null) {
      return false;
    }
    
    if ((isElementInTree(element.getParent())) && (isAcceptedByFilter(element))) {
      return true;
    }
    return false;
  }
  



  public void update()
  {
    clearAllListening(root);
    startListeningToTree(root);
    
    if (root == null) {
      TreeModelEvent ev = new TreeModelEvent(this, emptyPath);
      fireTreeStructureChanged(ev);
    } else {
      TreeModelEvent ev = new TreeModelEvent(this, getPath(root));
      fireTreeStructureChanged(ev);
    }
  }
  
  public void setRoot(Element root) {
    clearAllListening(this.root);
    this.root = root;
    startListeningToTree(root);
    
    if (root == null) {
      TreeModelEvent ev = new TreeModelEvent(this, emptyPath);
      fireTreeStructureChanged(ev);
    } else {
      TreeModelEvent ev = new TreeModelEvent(this, getPath(root));
      fireTreeStructureChanged(ev);
    }
  }
  
  public Object[] getPath(Element element) {
    LinkedList list = new LinkedList();
    
    Element e = element;
    for (;;) {
      if (e == null) {
        return new Object[0];
      }
      list.addFirst(e);
      if (e == root) {
        break;
      }
      e = e.getParent();
    }
    
    return list.toArray();
  }
  
  public void setListeningEnabled(boolean enabled) {
    if (enabled) {
      startListeningToTree(root);
    } else {
      stopListeningToTree(root);
    }
  }
  



  public Object getRoot()
  {
    return root;
  }
  
  public boolean isLeaf(Object node) {
    if (node == null) {
      return true;
    }
    




    if (node == root) {
      return false;
    }
    if (!(node instanceof Element)) {
      throw new IllegalArgumentException(Messages.getString("nodes_must_be_edu_cmu_cs_stage3_alice_core_Elements"));
    }
    
    Element element = (Element)node;
    Element[] children = element.getChildren();
    for (int i = 0; i < children.length; i++) {
      if (isAcceptedByFilter(children[i])) {
        return false;
      }
    }
    
    return true;
  }
  
  public int getChildCount(Object parent) {
    if (!(parent instanceof Element)) {
      throw new IllegalArgumentException(Messages.getString("nodes_must_be_edu_cmu_cs_stage3_alice_core_Elements"));
    }
    
    int childCount = 0;
    Element element = (Element)parent;
    Element[] children = element.getChildren();
    for (int i = 0; i < children.length; i++) {
      if (isAcceptedByFilter(children[i])) {
        childCount++;
      }
    }
    
    return childCount;
  }
  
  public Object getChild(Object parent, int index) {
    if (!(parent instanceof Element)) {
      throw new IllegalArgumentException(Messages.getString("nodes_must_be_edu_cmu_cs_stage3_alice_core_Elements"));
    }
    
    int childCount = 0;
    Element element = (Element)parent;
    Element[] children = element.getChildren();
    
    for (int i = 0; i < children.length; i++) {
      if (isAcceptedByFilter(children[i])) {
        if (childCount == index) {
          return children[i];
        }
        childCount++;
      }
    }
    
    return null;
  }
  
  public int getIndexOfChild(Object parent, Object child) {
    if (!(parent instanceof Element)) {
      throw new IllegalArgumentException(Messages.getString("nodes_must_be_edu_cmu_cs_stage3_alice_core_Elements"));
    }
    if (!(child instanceof Element)) {
      throw new IllegalArgumentException(Messages.getString("nodes_must_be_edu_cmu_cs_stage3_alice_core_Elements"));
    }
    
    int childCount = 0;
    Element element = (Element)parent;
    Element[] children = element.getChildren();
    for (int i = 0; i < children.length; i++) {
      if (isAcceptedByFilter(children[i])) {
        if (children[i] == child) {
          return childCount;
        }
        childCount++;
      }
    }
    
    return -1;
  }
  
  public void valueForPathChanged(TreePath path, Object newValue) {
    if (((path.getLastPathComponent() instanceof Element)) && ((newValue instanceof String))) {
      Element element = (Element)path.getLastPathComponent();
      Object previousName = name.get();
      try {
        name.set(newValue);
      }
      catch (IllegalArgumentException e) {
        name.set(previousName);
      }
    } else {
      throw new RuntimeException(Messages.getString("FilteringElementTreeModel_only_allows_name_changes_through_the_model"));
    }
  }
  



  public void stateChanging(AuthoringToolStateChangedEvent ev)
  {
    if (ev.getCurrentState() == 3) {
      setListeningEnabled(false);
    } else
      setListeningEnabled(true); }
  
  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  
  public void stateChanged(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarted(AuthoringToolStateChangedEvent ev) {}
  public void worldStopped(AuthoringToolStateChangedEvent ev) {}
  public void worldPaused(AuthoringToolStateChangedEvent ev) {}
  public void worldSaved(AuthoringToolStateChangedEvent ev) {}
  public void childrenChanging(ChildrenEvent childrenEvent) {}
  public void childrenChanged(ChildrenEvent childrenEvent) { Element element = childrenEvent.getChild();
    Element parent = (Element)childrenEvent.getSource();
    Object[] path = getPath(parent);
    
    if (childrenEvent.getChangeType() == 1) {
      if (isAcceptedByFilter(element)) {
        startListeningToTree(element);
        
        int[] childIndices = { getIndexOfChild(parent, element) };
        Object[] children = { element };
        TreeModelEvent ev = new TreeModelEvent(this, path, childIndices, children);
        fireTreeNodesInserted(ev);
      }
    } else if (childrenEvent.getChangeType() == 3) {
      stopListeningToTree(element);
      


      TreeModelEvent ev = new TreeModelEvent(this, path);
      fireTreeStructureChanged(ev);
    } else if ((childrenEvent.getChangeType() == 2) && 
      (isElementInTree(element)))
    {
      TreeModelEvent ev = new TreeModelEvent(this, path);
      fireTreeStructureChanged(ev);
    }
  }
  

  public void propertyChanging(PropertyEvent propertyEvent) {}
  

  public void propertyChanged(PropertyEvent propertyEvent)
  {
    if (propertyEvent.getProperty() == getPropertygetOwnername) {
      Element element = propertyEvent.getProperty().getOwner();
      Element parent = element.getParent();
      Object[] path = getPath(parent);
      int[] childIndices = { getIndexOfChild(parent, element) };
      Object[] children = { element };
      if ((path == null) || (path.length == 0)) {
        path = getPath(element);
        childIndices = null;
        children = null;
      }
      TreeModelEvent ev = new TreeModelEvent(this, path, childIndices, children);
      fireTreeNodesChanged(ev);
    }
  }
  



  protected void startListeningToTree(Element element)
  {
    if (element != null) {
      element.addChildrenListener(this);
      name.addPropertyListener(this);
      
      Element[] children = element.getChildren();
      for (int i = 0; i < children.length; i++) {
        if (isAcceptedByFilter(children[i])) {
          startListeningToTree(children[i]);
        }
      }
    }
  }
  
  protected void stopListeningToTree(Element element) {
    if (element != null) {
      element.removeChildrenListener(this);
      name.removePropertyListener(this);
      
      Element[] children = element.getChildren();
      for (int i = 0; i < children.length; i++) {
        if (isAcceptedByFilter(children[i])) {
          stopListeningToTree(children[i]);
        }
      }
    }
  }
  
  protected void clearAllListening(Element element) {
    if (element != null) {
      element.removeChildrenListener(this);
      name.removePropertyListener(this);
      
      Element[] children = element.getChildren();
      for (int i = 0; i < children.length; i++) {
        clearAllListening(children[i]);
      }
    }
  }
  




  protected static int getClassDepth(Class superclass, Class subclass)
  {
    if (!superclass.isAssignableFrom(subclass)) {
      return -1;
    }
    
    Class temp = subclass;
    int i = 0;
    while ((temp != superclass) && (superclass.isAssignableFrom(temp))) {
      i++;
      temp = temp.getSuperclass();
    }
    
    return i;
  }
}
