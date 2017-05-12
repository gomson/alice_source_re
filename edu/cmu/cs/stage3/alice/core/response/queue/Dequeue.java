package edu.cmu.cs.stage3.alice.core.response.queue;

import edu.cmu.cs.stage3.alice.core.Queue;















public class Dequeue
  extends QueueResponse
{
  public Dequeue() {}
  
  public class RuntimeDequeue
    extends QueueResponse.RuntimeQueueResponse
  {
    public RuntimeDequeue() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getQueue().dequeue();
    }
  }
}
