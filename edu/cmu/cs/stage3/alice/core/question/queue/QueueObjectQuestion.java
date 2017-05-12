package edu.cmu.cs.stage3.alice.core.question.queue;

import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Queue;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.QueueProperty;
















public abstract class QueueObjectQuestion
  extends Question
{
  public QueueObjectQuestion() {}
  
  protected abstract Object getValue(Queue paramQueue);
  
  public final QueueProperty queue = new QueueProperty(this, "queue", null);
  

  public Class getValueClass() { return queue.getQueueValue().valueClass.getClassValue(); }
  
  public Object getValue() {
    Queue queueValue = queue.getQueueValue();
    if (queueValue != null) {
      return getValue(queueValue);
    }
    return null;
  }
}
