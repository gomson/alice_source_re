package edu.cmu.cs.stage3.alice.core.question.queue;

import edu.cmu.cs.stage3.alice.core.Queue;

















public class IsEmpty
  extends QueueBooleanQuestion
{
  public IsEmpty() {}
  
  protected boolean getValue(Queue queueValue)
  {
    return queueValue.isEmpty();
  }
}
