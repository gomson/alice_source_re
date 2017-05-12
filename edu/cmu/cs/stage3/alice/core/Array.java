package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;

















public class Array
  extends Collection
{
  public Array() {}
  
  public Object itemAtIndex(int i)
  {
    return values.get(i);
  }
  
  public Object itemValueAtIndex(int i) { return values.getValue(i); }
  
  public void setItemAtIndex(int i, Object item) {
    values.set(i, item);
  }
  
  public void setItemValueAtIndex(int i, Object item) { values.set(i, item); }
  
  public int size() {
    return values.size();
  }
}
