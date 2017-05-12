package edu.cmu.cs.stage3.alice.core.response.set;

import edu.cmu.cs.stage3.alice.core.Set;















public class Remove
  extends SetItemResponse
{
  public Remove() {}
  
  public class RuntimeRemove
    extends SetItemResponse.RuntimeSetItemResponse
  {
    public RuntimeRemove() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getSet().removeValue(getItem());
    }
  }
}
