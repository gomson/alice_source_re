package edu.cmu.cs.stage3.alice.core.response.visualization.list;
import edu.cmu.cs.stage3.alice.core.Collection;

public class InsertItemAtEnd extends ListVisualizationWithItemAnimation { public class RuntimeInsertItemAtEnd extends ListVisualizationWithItemAnimation.RuntimeListVisualizationWithItemAnimation { public RuntimeInsertItemAtEnd() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getCollectionvalues.addValue(getItem());
    }
  }
  
  public InsertItemAtEnd() {}
}
