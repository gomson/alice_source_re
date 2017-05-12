package edu.cmu.cs.stage3.alice.core.response.set;

import edu.cmu.cs.stage3.alice.core.property.ItemOfCollectionProperty;






















public class SetItemResponse
  extends SetResponse
{
  public final ItemOfCollectionProperty item = new ItemOfCollectionProperty(this, "item");
  public class RuntimeSetItemResponse extends SetResponse.RuntimeSetResponse { public RuntimeSetItemResponse() { super(); }
    
    protected Object getItem() { return item.getValue(); }
  }
  
  public SetItemResponse() {}
}
