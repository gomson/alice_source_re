package edu.cmu.cs.stage3.alice.core.response.visualization.queue;
import edu.cmu.cs.stage3.alice.core.Collection;

public class Enqueue extends QueueVisualizationWithItemAnimation { public class RuntimeEnqueue extends QueueVisualizationWithItemAnimation.RuntimeQueueVisualizationWithItemAnimation { public RuntimeEnqueue() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getCollectionvalues.addValue(getItem());
    }
  }
  
  public Enqueue() {}
}
