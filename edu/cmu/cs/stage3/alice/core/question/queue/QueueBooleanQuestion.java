package edu.cmu.cs.stage3.alice.core.question.queue;

import edu.cmu.cs.stage3.alice.core.Queue;
import edu.cmu.cs.stage3.alice.core.property.QueueProperty;
import edu.cmu.cs.stage3.alice.core.question.BooleanQuestion;

















public abstract class QueueBooleanQuestion
  extends BooleanQuestion
{
  public QueueBooleanQuestion() {}
  
  protected abstract boolean getValue(Queue paramQueue);
  
  public final QueueProperty queue = new QueueProperty(this, "queue", null);
  
  public Object getValue() {
    Queue queueValue = queue.getQueueValue();
    if (queueValue != null) {
      if (getValue(queueValue)) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
    
    return Boolean.FALSE;
  }
}
