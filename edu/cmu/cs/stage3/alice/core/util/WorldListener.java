package edu.cmu.cs.stage3.alice.core.util;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;












public abstract class WorldListener
{
  private World m_world;
  private ChildrenListener m_childrenListener = new ChildrenListener()
  {
    public void childrenChanging(ChildrenEvent e) { handleChildrenChanging(e); }
    
    public void childrenChanged(ChildrenEvent e) {
      if (e.getChangeType() == 1) {
        WorldListener.this.hookUp(e.getChild());
      } else if (e.getChangeType() == 3) {
        WorldListener.this.unhookUp(e.getChild());
      }
      handleChildrenChanged(e);
    }
  };
  private PropertyListener m_propertyListener = new PropertyListener() {
    public void propertyChanging(PropertyEvent e) {
      handlePropertyChanging(e);
    }
    
    public void propertyChanged(PropertyEvent e) { handlePropertyChanged(e); }
  };
  
  private ObjectArrayPropertyListener m_objectArrayPropertyListener = new ObjectArrayPropertyListener() {
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent e) {
      handleObjectArrayPropertyChanging(e);
    }
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent e) { handleObjectArrayPropertyChanged(e); } };
  
  public WorldListener() {}
  
  protected abstract void handleChildrenChanging(ChildrenEvent paramChildrenEvent);
  
  protected abstract void handleChildrenChanged(ChildrenEvent paramChildrenEvent);
  
  protected abstract void handlePropertyChanging(PropertyEvent paramPropertyEvent);
  
  protected abstract void handlePropertyChanged(PropertyEvent paramPropertyEvent);
  protected abstract void handleObjectArrayPropertyChanging(ObjectArrayPropertyEvent paramObjectArrayPropertyEvent);
  protected abstract void handleObjectArrayPropertyChanged(ObjectArrayPropertyEvent paramObjectArrayPropertyEvent);
  protected abstract boolean isPropertyListeningRequired(Property paramProperty);
  protected abstract boolean isObjectArrayPropertyListeningRequired(ObjectArrayProperty paramObjectArrayProperty);
  public World getWorld() { return m_world; }
  
  public void setWorld(World world) {
    if (m_world != world) {
      if (m_world != null) {
        unhookUp(m_world);
      }
      m_world = world;
      if (m_world != null) {
        hookUp(m_world);
      }
    }
  }
  
  private boolean isChildrenListenerHookedUp(Element element) {
    ChildrenListener[] childrenListeners = element.getChildrenListeners();
    for (int i = 0; i < childrenListeners.length; i++) {
      if (childrenListeners[i] == m_childrenListener) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isPropertyListenerHookedUp(Property property) { PropertyListener[] propertyListeners = property.getPropertyListeners();
    for (int i = 0; i < propertyListeners.length; i++) {
      if (propertyListeners[i] == m_propertyListener) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isObjectArrayPropertyListenerHookedUp(ObjectArrayProperty oap) { ObjectArrayPropertyListener[] oapListeners = oap.getObjectArrayPropertyListeners();
    for (int i = 0; i < oapListeners.length; i++) {
      if (oapListeners[i] == m_objectArrayPropertyListener) {
        return true;
      }
    }
    return false;
  }
  
  private void unhookUp(Element element) { if (isChildrenListenerHookedUp(element)) {
      element.removeChildrenListener(m_childrenListener);
    }
    
    Property[] properties = element.getProperties();
    for (int i = 0; i < properties.length; i++) {
      Property propertyI = properties[i];
      if ((isPropertyListeningRequired(propertyI)) && 
        (isPropertyListenerHookedUp(propertyI))) {
        propertyI.removePropertyListener(m_propertyListener);
      }
      
      if ((propertyI instanceof ObjectArrayProperty)) {
        ObjectArrayProperty oapI = (ObjectArrayProperty)propertyI;
        if ((isObjectArrayPropertyListeningRequired(oapI)) && 
          (isObjectArrayPropertyListenerHookedUp(oapI))) {
          oapI.removeObjectArrayPropertyListener(m_objectArrayPropertyListener);
        }
      }
    }
    
    Element[] children = element.getChildren();
    for (int i = 0; i < children.length; i++)
      unhookUp(children[i]);
  }
  
  private void hookUp(Element element) {
    if (!isChildrenListenerHookedUp(element))
    {


      element.addChildrenListener(m_childrenListener);
    }
    Property[] properties = element.getProperties();
    for (int i = 0; i < properties.length; i++) {
      Property propertyI = properties[i];
      if ((isPropertyListeningRequired(propertyI)) && 
        (!isPropertyListenerHookedUp(propertyI)))
      {

        propertyI.addPropertyListener(m_propertyListener);
      }
      
      if ((propertyI instanceof ObjectArrayProperty)) {
        ObjectArrayProperty oapI = (ObjectArrayProperty)propertyI;
        if ((isObjectArrayPropertyListeningRequired(oapI)) && 
          (!isObjectArrayPropertyListenerHookedUp(oapI)))
        {

          oapI.addObjectArrayPropertyListener(m_objectArrayPropertyListener);
        }
      }
    }
    
    Element[] children = element.getChildren();
    for (int i = 0; i < children.length; i++) {
      hookUp(children[i]);
    }
  }
}
