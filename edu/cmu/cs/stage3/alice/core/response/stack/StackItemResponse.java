package edu.cmu.cs.stage3.alice.core.response.stack;

import edu.cmu.cs.stage3.alice.core.property.ItemOfCollectionProperty;






















public class StackItemResponse
  extends StackResponse
{
  public final ItemOfCollectionProperty item = new ItemOfCollectionProperty(this, "item");
  public class RuntimeStackItemResponse extends StackResponse.RuntimeStackResponse { public RuntimeStackItemResponse() { super(); }
    
    protected Object getItem() { return item.getValue(); }
  }
  
  public StackItemResponse() {}
}
