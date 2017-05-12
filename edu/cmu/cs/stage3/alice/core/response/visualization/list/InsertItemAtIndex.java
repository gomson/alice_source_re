package edu.cmu.cs.stage3.alice.core.response.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class InsertItemAtIndex extends ListVisualizationWithItemAnimation {
  public final NumberProperty index = new NumberProperty(this, "index", new Integer(-1));
  public class RuntimeInsertItemAtIndex extends ListVisualizationWithItemAnimation.RuntimeListVisualizationWithItemAnimation { public RuntimeInsertItemAtIndex() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getCollectionvalues.addValue(index.intValue(), getItem());
    }
  }
  
  public InsertItemAtIndex() {}
}
