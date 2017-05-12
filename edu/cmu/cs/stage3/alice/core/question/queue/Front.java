package edu.cmu.cs.stage3.alice.core.question.queue;

import edu.cmu.cs.stage3.alice.core.Queue;

















public class Front
  extends QueueObjectQuestion
{
  public Front() {}
  
  protected Object getValue(Queue queueValue)
  {
    return queueValue.frontValue();
  }
}
