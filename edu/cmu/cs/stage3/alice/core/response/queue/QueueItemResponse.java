package edu.cmu.cs.stage3.alice.core.response.queue;

import edu.cmu.cs.stage3.alice.core.property.ItemOfCollectionProperty;






















public class QueueItemResponse
  extends QueueResponse
{
  public final ItemOfCollectionProperty item = new ItemOfCollectionProperty(this, "item");
  public class RuntimeQueueItemResponse extends QueueResponse.RuntimeQueueResponse { public RuntimeQueueItemResponse() { super(); }
    
    protected Object getItem() { return item.getValue(); }
  }
  
  public QueueItemResponse() {}
}
