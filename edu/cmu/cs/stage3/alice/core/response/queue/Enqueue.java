package edu.cmu.cs.stage3.alice.core.response.queue;

import edu.cmu.cs.stage3.alice.core.Queue;















public class Enqueue
  extends QueueItemResponse
{
  public Enqueue() {}
  
  public class RuntimeEnqueue
    extends QueueItemResponse.RuntimeQueueItemResponse
  {
    public RuntimeEnqueue() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getQueue().enqueueValue(getItem());
    }
  }
}
