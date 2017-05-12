package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Queue;

public class QueueProperty extends CollectionProperty
{
  public QueueProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, Queue defaultValue) {
    super(owner, name, defaultValue, Queue.class);
  }
  
  public Queue getQueueValue() { return (Queue)getCollectionValue(); }
}
