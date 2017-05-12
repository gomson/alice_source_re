package edu.cmu.cs.stage3.alice.core.response.visualization.set;
import edu.cmu.cs.stage3.alice.core.Collection;

public class Add extends SetVisualizationWithItemAnimation { public class RuntimeAdd extends SetVisualizationWithItemAnimation.RuntimeSetVisualizationWithItemAnimation { public RuntimeAdd() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      Object item = getItem();
      if (!getCollectionvalues.contains(item))
      {

        getCollectionvalues.add(item);
      }
    }
  }
  
  public Add() {}
}
