package edu.cmu.cs.stage3.alice.core.response.visualization.list;
import edu.cmu.cs.stage3.alice.core.Collection;

public class InsertItemAtBeginning extends ListVisualizationWithItemAnimation { public class RuntimeInsertItemAtBeginning extends ListVisualizationWithItemAnimation.RuntimeListVisualizationWithItemAnimation { public RuntimeInsertItemAtBeginning() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getCollectionvalues.addValue(0, getItem());
    }
  }
  
  public InsertItemAtBeginning() {}
}
