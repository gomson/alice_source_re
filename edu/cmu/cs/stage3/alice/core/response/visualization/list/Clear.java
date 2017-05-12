package edu.cmu.cs.stage3.alice.core.response.visualization.list;
import edu.cmu.cs.stage3.alice.core.Collection;

public class Clear extends ListVisualizationAnimation { public class RuntimeClear extends ListVisualizationAnimation.RuntimeListVisualizationAnimation { public RuntimeClear() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getCollectionvalues.clear();
    }
  }
  
  public Clear() {}
}
