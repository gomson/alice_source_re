package edu.cmu.cs.stage3.alice.core.response.visualization.queue;

import edu.cmu.cs.stage3.alice.core.property.QueueOfModelsVisualizationProperty;

public class QueueVisualizationAnimation extends edu.cmu.cs.stage3.alice.core.response.Animation {
  public final QueueOfModelsVisualizationProperty subject = new QueueOfModelsVisualizationProperty(this, "subject", null);
  public class RuntimeQueueVisualizationAnimation extends edu.cmu.cs.stage3.alice.core.response.Animation.RuntimeAnimation { public RuntimeQueueVisualizationAnimation() { super(); }
    
    protected edu.cmu.cs.stage3.alice.core.Collection getCollection() { return subject.getCollectionOfModelsVisualizationValue().getItemsCollection(); }
  }
  
  public QueueVisualizationAnimation() {}
}
