package edu.cmu.cs.stage3.alice.core.response.visualization.queue;
import edu.cmu.cs.stage3.alice.core.Collection;

public class Dequeue extends QueueVisualizationAnimation { public class RuntimeDequeue extends QueueVisualizationAnimation.RuntimeQueueVisualizationAnimation { public RuntimeDequeue() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getCollectionvalues.remove(0);
    }
  }
  
  public Dequeue() {}
}
