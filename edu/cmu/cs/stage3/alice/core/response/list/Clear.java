package edu.cmu.cs.stage3.alice.core.response.list;

import edu.cmu.cs.stage3.alice.core.List;















public class Clear
  extends ListResponse
{
  public Clear() {}
  
  public class RuntimeClear
    extends ListResponse.RuntimeListResponse
  {
    public RuntimeClear() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getList().clear();
    }
  }
}
