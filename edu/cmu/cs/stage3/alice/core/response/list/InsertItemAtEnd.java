package edu.cmu.cs.stage3.alice.core.response.list;

import edu.cmu.cs.stage3.alice.core.List;















public class InsertItemAtEnd
  extends ListItemResponse
{
  public InsertItemAtEnd() {}
  
  public class RuntimeInsertItemAtEnd
    extends ListItemResponse.RuntimeListItemResponse
  {
    public RuntimeInsertItemAtEnd() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getList().insertItemValueAtEnd(getItem());
    }
  }
}
