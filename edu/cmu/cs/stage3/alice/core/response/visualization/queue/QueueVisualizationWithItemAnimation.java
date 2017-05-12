package edu.cmu.cs.stage3.alice.core.response.visualization.queue;

import edu.cmu.cs.stage3.alice.core.property.ModelProperty;

public class QueueVisualizationWithItemAnimation extends QueueVisualizationAnimation {
  public final ModelProperty item = new ModelProperty(this, "item", null);
  public class RuntimeQueueVisualizationWithItemAnimation extends QueueVisualizationAnimation.RuntimeQueueVisualizationAnimation { public RuntimeQueueVisualizationWithItemAnimation() { super(); }
    
    public edu.cmu.cs.stage3.alice.core.Model getItem() { return item.getModelValue(); }
  }
  
  public QueueVisualizationWithItemAnimation() {}
}
