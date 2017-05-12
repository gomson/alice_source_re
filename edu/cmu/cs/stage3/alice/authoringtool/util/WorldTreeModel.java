package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.IllegalNameValueException;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.util.LinkedList;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;






public class WorldTreeModel
  extends TreeModelSupport
  implements AuthoringToolStateListener, TreeModel
{
  protected Element root;
  protected Object[] emptyPath = { new World() };
  protected ChildrenListener childrenListener = new ChildrenListener();
  protected NameListener nameListener = new NameListener();
  
  public WorldTreeModel() {}
  
  public World HACK_getOriginalRoot() { return (World)emptyPath[0]; }
  
  public Element getCurrentScope()
  {
    return currentScope;
  }
  
  public void setCurrentScope(Element scope) {
    currentScope = scope;
  }
  
  public boolean isElementInScope(Element element) {
    if (currentScope != null) {
      if (element == currentScope)
        return true;
      if (element.isDescendantOf(currentScope)) {
        return true;
      }
    }
    
    return false;
  }
  
  public void setRoot(Element root) {
    stopListeningToTree(this.root);
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
        return emptyPath;
      }
      
      list.addFirst(e);
      if (e == root) {
        break;
      }
      e = e.getParent();
    }
    
    if (list.isEmpty()) {
      return emptyPath;
    }
    return list.toArray();
  }
  
  public void setListeningEnabled(boolean enabled)
  {
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
    
    return getChildCount(node) <= 0;
  }
  
  public int getChildCount(Object parent) {
    if (!(parent instanceof Element)) {
      throw new IllegalArgumentException(Messages.getString("nodes_must_be_edu_cmu_cs_stage3_alice_core_Elements"));
    }
    
    int childCount = 0;
    Element element = (Element)parent;
    String[] childrenProperties = AuthoringToolResources.getWorldTreeChildrenPropertiesStructure(element.getClass());
    if (childrenProperties != null) {
      for (int i = 0; i < childrenProperties.length; i++) {
        Property p = element.getPropertyNamed(childrenProperties[i]);
        if ((p instanceof ObjectArrayProperty)) {
          ObjectArrayProperty oap = (ObjectArrayProperty)p;
          if (Element.class.isAssignableFrom(oap.getComponentType()))
          {
            for (int j = 0; j < oap.size(); j++)
            {
              if (element.hasChild((Element)oap.get(j))) {
                childCount++;
              }
            }
          }
        }
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
    String[] childrenProperties = AuthoringToolResources.getWorldTreeChildrenPropertiesStructure(element.getClass());
    if (childrenProperties != null) {
      for (int i = 0; i < childrenProperties.length; i++) {
        Property p = element.getPropertyNamed(childrenProperties[i]);
        if ((p instanceof ObjectArrayProperty)) {
          ObjectArrayProperty oap = (ObjectArrayProperty)p;
          if (Element.class.isAssignableFrom(oap.getComponentType())) {
            for (int j = 0; j < oap.size(); j++) {
              if (element.hasChild((Element)oap.get(j))) {
                if (childCount == index) {
                  return oap.get(j);
                }
                childCount++;
              }
            }
          }
        }
      }
    }
    
    return null;
  }
  
  public int getIndexOfChild(Object parent, Object child) {
    if (parent == null) {
      return -1;
    }
    if (!(parent instanceof Element)) {
      throw new IllegalArgumentException(Messages.getString("nodes_must_be_edu_cmu_cs_stage3_alice_core_Elements"));
    }
    if (!(child instanceof Element)) {
      throw new IllegalArgumentException(Messages.getString("nodes_must_be_edu_cmu_cs_stage3_alice_core_Elements"));
    }
    
    int childCount = 0;
    Element element = (Element)parent;
    String[] childrenProperties = AuthoringToolResources.getWorldTreeChildrenPropertiesStructure(element.getClass());
    if (childrenProperties != null) {
      for (int i = 0; i < childrenProperties.length; i++) {
        Property p = element.getPropertyNamed(childrenProperties[i]);
        if ((p instanceof ObjectArrayProperty)) {
          ObjectArrayProperty oap = (ObjectArrayProperty)p;
          if (Element.class.isAssignableFrom(oap.getComponentType())) {
            for (int j = 0; j < oap.size(); j++) {
              if (element.hasChild((Element)oap.get(j))) {
                if (child == oap.get(j)) {
                  return childCount;
                }
                childCount++;
              }
            }
          }
        }
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
      } catch (IllegalNameValueException e) {
        AuthoringTool.showErrorDialog(e.getMessage(), e);
        name.set(previousName);
      }
    } else {
      throw new RuntimeException(Messages.getString("only_allows_name_changes_through_the_model"));
    }
  }
  



  public void stateChanging(AuthoringToolStateChangedEvent ev)
  {
    if (ev.getCurrentState() == 3) {
      setListeningEnabled(false);
    } else {
      setListeningEnabled(true);
    }
  }
  

  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  
  public class ChildrenListener
    implements ObjectArrayPropertyListener
  {
    public ChildrenListener() {}
    
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev)
    {
      Element element = (Element)ev.getItem();
      Element parent = ev.getObjectArrayProperty().getOwner();
      Object[] path = getPath(parent);
      
      if (ev.getChangeType() == 1) {
        if (element.getParent() == parent) {
          startListeningToTree(element);
          
          int[] childIndices = { getIndexOfChild(parent, element) };
          Object[] children = { element };
          




          TreeModelEvent tmev = new TreeModelEvent(this, path);
          fireTreeStructureChanged(tmev);
        }
      } else if (ev.getChangeType() == 3) {
        stopListeningToTree(element);
        


        TreeModelEvent tmev = new TreeModelEvent(this, path);
        fireTreeStructureChanged(tmev);
      } else if (ev.getChangeType() == 2)
      {
        TreeModelEvent tmev = new TreeModelEvent(this, path);
        fireTreeStructureChanged(tmev);
      }
    }
  }
  
  public class NameListener implements PropertyListener { public NameListener() {}
    
    public void propertyChanging(PropertyEvent ev) {}
    
    public void propertyChanged(PropertyEvent ev) { Element element = ev.getProperty().getOwner();
      Element parent = element.getParent();
      Object[] path = getPath(parent);
      int[] childIndices = { getIndexOfChild(parent, element) };
      Object[] children = { element };
      if ((path == null) || (path.length == 0)) {
        path = getPath(element);
        childIndices = null;
        children = null;
      }
      TreeModelEvent tmev = new TreeModelEvent(this, path, childIndices, children);
      fireTreeNodesChanged(tmev); } }
  
  public void stateChanged(AuthoringToolStateChangedEvent ev) {}
  public void worldLoaded(AuthoringToolStateChangedEvent ev) {}
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {}
  public void worldStarted(AuthoringToolStateChangedEvent ev) {}
  public void worldStopped(AuthoringToolStateChangedEvent ev) {}
  public void worldPaused(AuthoringToolStateChangedEvent ev) {}
  public void worldSaved(AuthoringToolStateChangedEvent ev) {}
  protected void startListeningToTree(Element element) { if (element != null) {
      name.addPropertyListener(nameListener);
      
      String[] childrenProperties = AuthoringToolResources.getWorldTreeChildrenPropertiesStructure(element.getClass());
      if (childrenProperties != null) {
        for (int i = 0; i < childrenProperties.length; i++) {
          Property p = element.getPropertyNamed(childrenProperties[i]);
          if ((p instanceof ObjectArrayProperty)) {
            ObjectArrayProperty oap = (ObjectArrayProperty)p;
            if (Element.class.isAssignableFrom(oap.getComponentType())) {
              oap.addObjectArrayPropertyListener(childrenListener);
              for (int j = 0; j < oap.size(); j++) {
                if (element.hasChild((Element)oap.get(j))) {
                  startListeningToTree((Element)oap.get(j));
                }
              }
            }
          }
        }
      }
    }
  }
  
  protected void stopListeningToTree(Element element) {
    if (element != null) {
      name.removePropertyListener(nameListener);
      
      String[] childrenProperties = AuthoringToolResources.getWorldTreeChildrenPropertiesStructure(element.getClass());
      if (childrenProperties != null) {
        for (int i = 0; i < childrenProperties.length; i++) {
          Property p = element.getPropertyNamed(childrenProperties[i]);
          if ((p instanceof ObjectArrayProperty)) {
            ObjectArrayProperty oap = (ObjectArrayProperty)p;
            if (Element.class.isAssignableFrom(oap.getComponentType())) {
              oap.removeObjectArrayPropertyListener(childrenListener);
              for (int j = 0; j < oap.size(); j++) {
                if (element.hasChild((Element)oap.get(j))) {
                  stopListeningToTree((Element)oap.get(j));
                }
              }
            }
          }
        }
      }
    }
  }
  









  protected Element currentScope;
  







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
