package edu.cmu.cs.stage3.alice.core.response.queue;

import edu.cmu.cs.stage3.alice.core.Queue;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.property.QueueProperty;



















public class QueueResponse
  extends Response
{
  public final QueueProperty queue = new QueueProperty(this, "queue", null);
  public class RuntimeQueueResponse extends Response.RuntimeResponse { public RuntimeQueueResponse() { super(); }
    
    protected Queue getQueue() { return queue.getQueueValue(); }
  }
  
  public QueueResponse() {}
}
