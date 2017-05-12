package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class WorldDifferencesCapsule implements edu.cmu.cs.stage3.alice.core.event.PropertyListener, ObjectArrayPropertyListener, ChildrenListener, AuthoringToolStateListener
{
  protected AuthoringTool authoringTool;
  protected World world;
  protected HashMap changedProperties = new HashMap();
  protected ArrayList changedObjectArrayProperties = new ArrayList();
  protected ArrayList changedElements = new ArrayList();
  

  protected HashMap changedElementPositions = new HashMap();
  

  protected ArrayList changeOrder = new ArrayList();
  protected boolean isListening;
  protected static final String elementChange = "elementChange";
  protected static final String propertyChange = "propertyChange";
  protected static final String objectArrayChange = "objectArrayChange";
  protected Object preChangeValue;
  
  public WorldDifferencesCapsule(AuthoringTool authoringTool, World world)
  {
    this.authoringTool = authoringTool;
    this.world = world;
    
    startListening();
  }
  

  public StencilStateCapsule getStateCapsule()
  {
    StencilStateCapsule capsule = new StencilStateCapsule();
    
    Iterator iter = changedElements.iterator();
    
    iter = changedObjectArrayProperties.iterator();
    
    while (iter.hasNext()) {
      Object obj = iter.next();
      
      if ((obj instanceof ObjectArrayPropertyUndoableRedoable)) {
        int type = changeType;
        int oldPos = oldIndex;
        int newPos = newIndex;
        Object o = value;
        
        Element value = null;
        if ((o instanceof Element)) { value = (Element)o;
        }
        o = ((ObjectArrayPropertyUndoableRedoable)obj).getAffectedObject();
        Element affected = null;
        if ((o instanceof Element)) { affected = (Element)o;
        }
        if (type == 1)
        {
          capsule.addExistantElement(value.getKey(world));
          
          capsule.putElementPosition(value.getKey(world), newPos);
          

          if ((value instanceof CallToUserDefinedResponse)) {
            Property params = value.getPropertyNamed("requiredActualParameters");
            Object udobj = params.getValue();
            if ((udobj instanceof Variable[])) {
              Variable[] vars = (Variable[])udobj;
              if (vars != null) {
                for (int i = 0; i < vars.length; i++)
                {
                  String valueRepr = AuthoringToolResources.getReprForValue(vars[i].getValue(), true);
                  capsule.putPropertyValue(vars[i].getKey(world), valueRepr);
                }
              }
            }
          }
          else {
            String[] visProps = AuthoringToolResources.getInitialVisibleProperties(value.getClass());
            if (visProps != null) {
              for (int i = 0; i < visProps.length; i++) {
                Property visProp = ((Response)value).getPropertyNamed(visProps[i]);
                String valueRepr = AuthoringToolResources.getReprForValue(visProp.get(), true);
                capsule.putPropertyValue(value.getKey(world) + "." + visProp.getName(), valueRepr);
              }
            }
          }
        } else if (type == 2) {
          capsule.putElementPosition(value.getKey(world), newPos);
        }
        else if (!world.isAncestorOf(value))
        {
          capsule.addNonExistantElement(value.getKey());
        }
      }
    }
    

    iter = changedProperties.keySet().iterator();
    while (iter.hasNext()) {
      Object o = iter.next();
      String keyAndProp = (String)o;
      
      int lastPd = keyAndProp.lastIndexOf(".");
      String key = keyAndProp.substring(0, lastPd);
      String propName = keyAndProp.substring(lastPd + 1, keyAndProp.length());
      
      if (propName.indexOf("data") == -1) {
        Element e = world.getDescendantKeyed(key);
        Property p = null;
        if (e != null) p = e.getPropertyNamed(propName);
        if (p != null) {
          if ((p.get() instanceof CallToUserDefinedResponse)) {
            Property resp = ((CallToUserDefinedResponse)p.get()).getPropertyNamed("userDefinedResponse");
            String valueRepr = AuthoringToolResources.getReprForValue(resp.get(), true);
            capsule.putPropertyValue(resp.getOwner().getKey(world) + ".userDefinedResponse", valueRepr);
            
            Property pars = ((CallToUserDefinedResponse)p.get()).getPropertyNamed("requiredActualParameters");
            
            Object udobj = pars.getValue();
            if ((udobj instanceof Variable[])) {
              Variable[] vars = (Variable[])udobj;
              if (vars != null) {
                for (int i = 0; i < vars.length; i++)
                {
                  valueRepr = AuthoringToolResources.getReprForValue(vars[i].getValue(), true);
                  capsule.putPropertyValue(vars[i].getKey(world), valueRepr);
                }
              }
            }
          }
          else {
            String valueRepr = AuthoringToolResources.getReprForValue(p.get(), true);
            
            capsule.putPropertyValue(key + "." + propName, valueRepr);
          }
        }
      }
    }
    

    return capsule;
  }
  

  public Vector getChangedPropertiesNamed(String propertyName)
  {
    Vector props = new Vector();
    
    Set changedProps = changedProperties.keySet();
    Iterator iter = changedProps.iterator();
    while (iter.hasNext()) {
      String propAndKey = (String)iter.next();
      int endName = propAndKey.lastIndexOf(".");
      String elName = propAndKey.substring(0, endName);
      String propName = propAndKey.substring(endName + 1, propAndKey.length());
      if (propAndKey.endsWith(propertyName)) {
        Element el = world.getDescendantKeyed(elName);
        props.addElement(el.getPropertyNamed(propName));
      }
    }
    
    return props;
  }
  
  public boolean otherPropertyChangesMade(Set correctPropertyChangeSet)
  {
    Set changedProps = changedProperties.keySet();
    Iterator iter = changedProps.iterator();
    while (iter.hasNext()) {
      String propAndKey = (String)iter.next();
      



      if (propAndKey.endsWith("triggerResponse")) {
        int lastPd = propAndKey.lastIndexOf(".");
        String key = propAndKey.substring(0, lastPd);
        String propName = propAndKey.substring(lastPd + 1, propAndKey.length());
        
        Element e = world.getDescendantKeyed(key);
        Property p = null;
        if (e != null) { p = e.getPropertyNamed(propName);
        }
        if ((p.get() instanceof CallToUserDefinedResponse)) {
          Property resp = ((CallToUserDefinedResponse)p.get()).getPropertyNamed("userDefinedResponse");
          
          propAndKey = resp.getOwner().getKey(world) + ".userDefinedResponse";
        }
      }
      


      if ((correctPropertyChangeSet != null) && (!correctPropertyChangeSet.contains(propAndKey)))
      {

        propAndKey.endsWith("data");
        if (propAndKey.endsWith("localTransformation"))
          return true;
        if (propAndKey.endsWith("name"))
          return true;
        if (propAndKey.endsWith("isCommentedOut")) {
          return true;
        }
        String key = propAndKey.substring(0, propAndKey.lastIndexOf("."));
        Element e = world.getDescendantKeyed(key);
        
        if ((e instanceof Behavior)) {
          return true;
        }
      }
    }
    



    return false;
  }
  



















  public boolean otherElementsShifted(Set correctElementsShifted)
  {
    return false;
  }
  
  public boolean otherElementsInsertedOrDeleted(String[] insertedNames, String[] deletedNames)
  {
    List insertedList = Arrays.asList(insertedNames);
    List deletedList = Arrays.asList(deletedNames);
    
    Vector illegalInserts = new Vector();
    
    for (Iterator iter = changedObjectArrayProperties.iterator(); iter.hasNext();) {
      Object obj = iter.next();
      
      if ((obj instanceof ObjectArrayPropertyUndoableRedoable)) {
        Object o = value;
        int type = changeType;
        Element e = (Element)o;
        String name = "";
        

        if (e.isDescendantOf(world))
          name = e.getKey(world); else {
          name = e.getKey();
        }
        if ((!insertedList.contains(name)) && (type == 1))
        {
          illegalInserts.addElement(name);
        }
        if ((!deletedList.contains(name)) && (type == 3) && 
          (illegalInserts.contains(name)))
        {

          illegalInserts.remove(name);
        }
      }
    }
    







    boolean insertsStillPresent = false;
    for (int i = 0; i < illegalInserts.size(); i++) {
      Element e = world.getDescendantKeyed((String)illegalInserts.elementAt(i));
      if (e != null) {
        insertsStillPresent = true;
      }
    }
    

    if (insertsStillPresent)
    {
      return true;
    }
    return false;
  }
  
  public synchronized void restoreWorld()
  {
    setIsListening(false);
    
    Iterator elementIterator = changedElements.iterator();
    
    Iterator objectArrayIterator = changedObjectArrayProperties.iterator();
    
    for (Iterator iter = changeOrder.iterator(); iter.hasNext();) {
      String changeType = (String)iter.next();
      if (changeType.equals("elementChange")) {
        if (elementIterator.hasNext()) {
          ((UndoableRedoable)elementIterator.next()).undo();
        }
      } else if ((changeType.equals("objectArrayChange")) && 
        (objectArrayIterator.hasNext())) {
        ((UndoableRedoable)objectArrayIterator.next()).undo();
      }
    }
    

    for (Iterator iter = changedProperties.keySet().iterator(); iter.hasNext();) {
      String propertyKey = (String)iter.next();
      Object oldValue = changedProperties.get(propertyKey);
      
      int dotIndex = propertyKey.lastIndexOf(".");
      String elementKey = propertyKey.substring(0, dotIndex);
      String propertyName = propertyKey.substring(dotIndex + 1);
      
      Element propertyOwner = world.getDescendantKeyed(elementKey);
      if (propertyOwner != null)
      {
        Property property = propertyOwner.getPropertyNamed(propertyName);
        
        property.set(oldValue);
      }
    }
    
    clear();
    
    setIsListening(true);
  }
  
  public synchronized void startListening() {
    authoringTool.addAuthoringToolStateListener(this);
    listenTo(world);
    setIsListening(true);
  }
  
  public synchronized void stopListening() {
    authoringTool.removeAuthoringToolStateListener(this);
    stopListeningTo(world);
    setIsListening(false);
  }
  
  public synchronized void dispose() {
    clear();
    stopListening();
    authoringTool = null;
    world = null;
  }
  
  public synchronized void setWorld(World world) {
    if (this.world != null) {
      stopListeningTo(this.world);
    }
    
    this.world = world;
    listenTo(world);
  }
  
  public synchronized void clear() {
    changedProperties.clear();
    changedObjectArrayProperties.clear();
    changedElements.clear();
  }
  

  protected synchronized void setIsListening(boolean isListening)
  {
    this.isListening = isListening;
  }
  
  public synchronized void propertyChanging(PropertyEvent propertyEvent)
  {
    if (isListening) {
      Property property = propertyEvent.getProperty();
      preChangeValue = propertyEvent.getProperty().get();
    }
  }
  
  public synchronized void propertyChanged(PropertyEvent propertyEvent) { if (isListening) {
      Property property = propertyEvent.getProperty();
      String propertyRepr = property.getOwner().getKey(world) + "." + property.getName();
      if (changedProperties.containsKey(propertyRepr)) {
        if (AuthoringToolResources.equals(property.get(), changedProperties.get(propertyRepr)))
        {
          changedProperties.remove(propertyRepr);
        }
      } else
        changedProperties.put(propertyRepr, preChangeValue);
    }
  }
  
  public synchronized void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
  
  public synchronized void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) {
    if (isListening) {
      changedObjectArrayProperties.add(0, new ObjectArrayPropertyUndoableRedoable(ev.getObjectArrayProperty(), ev.getChangeType(), ev.getOldIndex(), ev.getNewIndex(), ev.getItem()));
      changeOrder.add(0, "objectArrayChange");
    }
  }
  
  public synchronized void childrenChanging(ChildrenEvent childrenEvent) {}
  
  public synchronized void childrenChanged(ChildrenEvent childrenEvent) { if (isListening) {
      changedElements.add(0, new ChildChangeUndoableRedoable(childrenEvent));
      changeOrder.add(0, "elementChange");
      
      int changeType = childrenEvent.getChangeType();
      if (changeType == 1) {
        listenTo(childrenEvent.getChild());
      } else if (changeType == 3) {
        stopListeningTo(childrenEvent.getChild());
      }
    }
  }
  
  public synchronized void listenTo(Element element) {
    if (element != null) {
      Element[] elements = element.getDescendants();
      for (int i = 0; i < elements.length; i++) {
        Property[] properties = elements[i].getProperties();
        for (int j = 0; j < properties.length; j++) {
          if ((properties[j] instanceof ObjectArrayProperty)) {
            ((ObjectArrayProperty)properties[j]).addObjectArrayPropertyListener(this);
          } else {
            properties[j].addPropertyListener(this);
          }
        }
        boolean alreadyChildrenListening = false;
        ChildrenListener[] childrenListeners = elements[i].getChildrenListeners();
        for (int j = 0; j < childrenListeners.length; j++) {
          if (childrenListeners[j] == this) {
            alreadyChildrenListening = true;
          }
        }
        if (!alreadyChildrenListening) {
          elements[i].addChildrenListener(this);
        }
      }
    }
  }
  
  public synchronized void stopListeningTo(Element element) {
    if (element != null) {
      Element[] elements = element.getDescendants();
      for (int i = 0; i < elements.length; i++) {
        Property[] properties = elements[i].getProperties();
        for (int j = 0; j < properties.length; j++) {
          if ((properties[j] instanceof ObjectArrayProperty)) {
            ((ObjectArrayProperty)properties[j]).removeObjectArrayPropertyListener(this);
          } else {
            properties[j].removePropertyListener(this);
          }
        }
        elements[i].removeChildrenListener(this);
      }
    }
  }
  


  public void stateChanged(AuthoringToolStateChangedEvent ev)
  {
    if (ev.getCurrentState() == 3) {
      setIsListening(false);
    } else {
      setIsListening(true);
    }
  }
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void stateChanging(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarted(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopped(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPaused(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaved(AuthoringToolStateChangedEvent ev) {}
}
