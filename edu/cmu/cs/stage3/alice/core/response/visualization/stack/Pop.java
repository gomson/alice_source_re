package edu.cmu.cs.stage3.alice.core.response.visualization.stack;
import edu.cmu.cs.stage3.alice.core.Collection;

public class Pop extends StackVisualizationAnimation { public class RuntimePop extends StackVisualizationAnimation.RuntimeStackVisualizationAnimation { public RuntimePop() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getCollectionvalues.remove(getCollectionvalues.size() - 1);
    }
  }
  
  public Pop() {}
}
