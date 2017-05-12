package edu.cmu.cs.stage3.alice.core.response.list;

import edu.cmu.cs.stage3.alice.core.List;















public class RemoveItemFromEnd
  extends ListResponse
{
  public RemoveItemFromEnd() {}
  
  public class RuntimeRemoveItemFromEnd
    extends ListResponse.RuntimeListResponse
  {
    public RuntimeRemoveItemFromEnd() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getList().removeItemFromEnd();
    }
  }
}
