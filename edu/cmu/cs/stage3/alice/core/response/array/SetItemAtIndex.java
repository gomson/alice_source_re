package edu.cmu.cs.stage3.alice.core.response.array;

import edu.cmu.cs.stage3.alice.core.Array;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;





















public class SetItemAtIndex
  extends ArrayItemResponse
{
  public final NumberProperty index = new NumberProperty(this, "index", new Integer(-1));
  public class RuntimeSetItemAtIndex extends ArrayItemResponse.RuntimeArrayItemResponse { public RuntimeSetItemAtIndex() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getArray().setItemValueAtIndex(index.intValue(), getItem());
    }
  }
  
  public SetItemAtIndex() {}
}
