package edu.cmu.cs.stage3.alice.core.response.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;





















public class InsertItemAtIndex
  extends ListItemResponse
{
  public final NumberProperty index = new NumberProperty(this, "index", new Integer(-1));
  public class RuntimeInsertItemAtIndex extends ListItemResponse.RuntimeListItemResponse { public RuntimeInsertItemAtIndex() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getList().insertItemValueAtIndex(index.intValue(), getItem());
    }
  }
  
  public InsertItemAtIndex() {}
}
