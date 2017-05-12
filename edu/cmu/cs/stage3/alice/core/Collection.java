package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;




















public abstract class Collection
  extends Element
{
  public Collection() {}
  
  public final ObjectArrayProperty values = new ObjectArrayProperty(this, "values", null, [Ljava.lang.Object.class);
  public final ClassProperty valueClass = new ClassProperty(this, "valueClass", null);
  
  protected void propertyChanged(Property property, Object value) {
    if (property == valueClass) {
      values.setComponentType((Class)value);
    } else {
      super.propertyChanged(property, value);
    }
  }
  

  public void append(Object o)
  {
    values.add(o);
  }
  
  public void insert(Number index, Object o)
  {
    values.add(index.intValue(), o);
  }
}
