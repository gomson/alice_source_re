package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;

















public class List
  extends Collection
{
  public List() {}
  
  public boolean contains(Object item)
  {
    return values.contains(item);
  }
  
  public boolean containsValue(Object item) { return values.containsValue(item); }
  
  public int firstIndexOfItem(Object item) {
    return values.indexOf(item);
  }
  
  public int firstIndexOfItemValue(Object item) { return values.indexOfValue(item); }
  
  public int firstIndexOfItem(Object item, int startFrom) {
    return values.indexOf(item, startFrom);
  }
  
  public int firstIndexOfItemValue(Object item, int startFrom) { return values.indexOfValue(item, startFrom); }
  
  public Object itemAtBeginning() {
    return values.get(0);
  }
  
  public Object itemValueAtBeginning() { return values.getValue(0); }
  
  public Object itemAtEnd() {
    return values.get(-1);
  }
  
  public Object itemValueAtEnd() { return values.getValue(-1); }
  
  public Object itemAtIndex(int index) {
    return values.get(index);
  }
  
  public Object itemValueAtIndex(int index) { return values.getValue(index); }
  
  public Object itemAtRandomIndex() {
    int index = (int)(Math.random() * size());
    return values.get(index);
  }
  
  public Object itemValueAtRandomIndex() { int index = (int)(Math.random() * size());
    return values.getValue(index);
  }
  
  public int lastIndexOfItem(Object item) { return values.lastIndexOf(item); }
  
  public int lastIndexOfItemValue(Object item) {
    return values.lastIndexOfValue(item);
  }
  
  public int lastIndexOfItem(Object item, int startFrom) { return values.lastIndexOf(item, startFrom); }
  
  public int lastIndexOfItemValue(Object item, int startFrom) {
    return values.lastIndexOfValue(item, startFrom);
  }
  
  public boolean isEmpty() {
    return values.isEmpty();
  }
  
  public int size() { return values.size(); }
  
  public void clear() {
    values.clear();
  }
  
  public void insertItemAtBeginning(Object item) { values.add(0, item); }
  
  public void insertItemValueAtBeginning(Object item) {
    values.addValue(0, item);
  }
  
  public void insertItemAtEnd(Object item) { values.add(-1, item); }
  
  public void insertItemValueAtEnd(Object item) {
    values.addValue(-1, item);
  }
  
  public void insertItemAtIndex(int index, Object item) { values.add(index, item); }
  
  public void insertItemValueAtIndex(int index, Object item) {
    values.addValue(index, item);
  }
  
  public void removeItemFromBeginning() { values.remove(0); }
  
  public void removeItemFromEnd() {
    values.remove(-1);
  }
  
  public void removeItemFromIndex(int index) { values.remove(index); }
}
