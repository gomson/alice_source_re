package edu.cmu.cs.stage3.alice.core.response.array;

import edu.cmu.cs.stage3.alice.core.property.ItemOfCollectionProperty;






















public class ArrayItemResponse
  extends ArrayResponse
{
  public final ItemOfCollectionProperty item = new ItemOfCollectionProperty(this, "item");
  public class RuntimeArrayItemResponse extends ArrayResponse.RuntimeArrayResponse { public RuntimeArrayItemResponse() { super(); }
    
    protected Object getItem() { return item.getValue(); }
  }
  
  public ArrayItemResponse() {}
}
