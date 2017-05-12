package edu.cmu.cs.stage3.alice.core.response.set;

import edu.cmu.cs.stage3.alice.core.Set;















public class Add
  extends SetItemResponse
{
  public Add() {}
  
  public class RuntimeAdd
    extends SetItemResponse.RuntimeSetItemResponse
  {
    public RuntimeAdd() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      Set set = getSet();
      Object item = getItem();
      if (!set.containsValue(item))
      {

        set.addValue(item);
      }
    }
  }
}
