package edu.cmu.cs.stage3.alice.core.response.visualization.list;
import edu.cmu.cs.stage3.alice.core.Collection;

public class RemoveItemFromBeginning extends ListVisualizationAnimation { public class RuntimeRemoveItemFromBeginning extends ListVisualizationAnimation.RuntimeListVisualizationAnimation { public RuntimeRemoveItemFromBeginning() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getCollectionvalues.remove(0);
    }
  }
  
  public RemoveItemFromBeginning() {}
}
