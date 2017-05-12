package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;

















public class Set
  extends Collection
{
  public Set() {}
  
  public boolean contains(Object item)
  {
    return values.contains(item);
  }
  
  public boolean containsValue(Object item) { return values.containsValue(item); }
  
  public boolean isEmpty() {
    return values.isEmpty();
  }
  
  public int size() { return values.size(); }
  
  public void add(Object item) {
    values.add(item);
  }
  
  public void addValue(Object item) { values.addValue(item); }
  
  public void remove(Object item) {
    values.remove(item);
  }
  
  public void removeValue(Object item) { values.removeValue(item); }
}
