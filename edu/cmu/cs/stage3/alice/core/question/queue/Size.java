package edu.cmu.cs.stage3.alice.core.question.queue;

import edu.cmu.cs.stage3.alice.core.Queue;

















public class Size
  extends QueueNumberQuestion
{
  public Size() {}
  
  public int getValue(Queue queueValue)
  {
    return queueValue.size();
  }
}
