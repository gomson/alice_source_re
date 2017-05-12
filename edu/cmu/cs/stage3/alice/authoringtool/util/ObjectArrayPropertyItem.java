package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;



















public class ObjectArrayPropertyItem
  extends Property
{
  protected ObjectArrayProperty objectArrayProperty;
  protected int index;
  protected Class type;
  
  public ObjectArrayPropertyItem(Element element, ObjectArrayProperty objectArrayProperty, int index, Class type)
  {
    super(element, "item " + index, null, objectArrayProperty.getComponentType());
    
    this.objectArrayProperty = objectArrayProperty;
    this.index = index;
    this.type = type;
  }
  
  public ObjectArrayProperty getObjectArrayProperty() {
    return objectArrayProperty;
  }
  
  public int getIndex() {
    return index;
  }
  
  public Object get() {
    return objectArrayProperty.get(index);
  }
  
  public Object getValue() {
    return objectArrayProperty.get(index);
  }
  
  public Class getValueClass() {
    return type;
  }
  
  public void set(Object value) throws IllegalArgumentException {
    objectArrayProperty.set(index, value);
  }
  
  public void dispose()
  {
    PropertyListener[] listeners = getPropertyListeners();
    for (int i = 0; i < listeners.length; i++) {
      removePropertyListener(listeners[i]);
    }
    objectArrayProperty = null;
  }
}
