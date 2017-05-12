package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;

















public class Stack
  extends Collection
{
  public Stack() {}
  
  public boolean isEmpty()
  {
    return values.isEmpty();
  }
  
  public int size() { return values.size(); }
  
  public Object top() {
    return values.get(-1);
  }
  
  public Object topValue() { return values.getValue(-1); }
  
  public void push(Object item) {
    values.add(-1, item);
  }
  
  public void pushValue(Object item) { values.addValue(-1, item); }
  
  public void pop() {
    values.remove(-1);
  }
}
