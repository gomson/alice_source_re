package edu.cmu.cs.stage3.alice.core.response.visualization.list;
import edu.cmu.cs.stage3.alice.core.Collection;

public class RemoveItemFromEnd extends ListVisualizationAnimation { public class RuntimeRemoveItemFromEnd extends ListVisualizationAnimation.RuntimeListVisualizationAnimation { public RuntimeRemoveItemFromEnd() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getCollectionvalues.remove(getCollectionvalues.size() - 1);
    }
  }
  
  public RemoveItemFromEnd() {}
}
