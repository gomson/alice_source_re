package edu.cmu.cs.stage3.alice.core.response.list;

import edu.cmu.cs.stage3.alice.core.List;















public class InsertItemAtBeginning
  extends ListItemResponse
{
  public InsertItemAtBeginning() {}
  
  public class RuntimeInsertItemAtBeginning
    extends ListItemResponse.RuntimeListItemResponse
  {
    public RuntimeInsertItemAtBeginning() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getList().insertItemValueAtBeginning(getItem());
    }
  }
}
