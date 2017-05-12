package edu.cmu.cs.stage3.alice.core.response.list;

import edu.cmu.cs.stage3.alice.core.List;















public class RemoveItemFromBeginning
  extends ListResponse
{
  public RemoveItemFromBeginning() {}
  
  public class RuntimeRemoveItemFromBeginning
    extends ListResponse.RuntimeListResponse
  {
    public RuntimeRemoveItemFromBeginning() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getList().removeItemFromBeginning();
    }
  }
}
