package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;

















public class Queue
  extends Collection
{
  public Queue() {}
  
  public boolean isEmpty()
  {
    return values.isEmpty();
  }
  
  public int size() { return values.size(); }
  
  public Object front() {
    return values.get(0);
  }
  
  public Object frontValue() { return values.getValue(0); }
  
  public void enqueue(Object item) {
    values.add(-1, item);
  }
  
  public void enqueueValue(Object item) { values.addValue(-1, item); }
  
  public void dequeue() {
    values.remove(0);
  }
}
