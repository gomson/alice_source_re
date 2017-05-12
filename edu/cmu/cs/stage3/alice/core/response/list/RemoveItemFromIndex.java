package edu.cmu.cs.stage3.alice.core.response.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;





















public class RemoveItemFromIndex
  extends ListResponse
{
  public final NumberProperty index = new NumberProperty(this, "index", new Integer(-1));
  public class RuntimeRemoveItemFromIndex extends ListResponse.RuntimeListResponse { public RuntimeRemoveItemFromIndex() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getList().removeItemFromIndex(index.intValue());
    }
  }
  
  public RemoveItemFromIndex() {}
}
