package edu.cmu.cs.stage3.alice.core.question.queue;

import edu.cmu.cs.stage3.alice.core.Queue;
import edu.cmu.cs.stage3.alice.core.property.QueueProperty;
import edu.cmu.cs.stage3.alice.core.question.NumberQuestion;



















public abstract class QueueNumberQuestion
  extends NumberQuestion
{
  public QueueNumberQuestion() {}
  
  public final QueueProperty queue = new QueueProperty(this, "queue", null);
  
  protected abstract int getValue(Queue paramQueue);
  
  public Object getValue() { Queue queueValue = queue.getQueueValue();
    if (queueValue != null) {
      return new Integer(getValue(queueValue));
    }
    return null;
  }
}
