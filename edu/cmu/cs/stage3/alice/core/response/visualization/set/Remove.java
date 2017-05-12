package edu.cmu.cs.stage3.alice.core.response.visualization.set;
import edu.cmu.cs.stage3.alice.core.Collection;

public class Remove extends SetVisualizationWithItemAnimation { public class RuntimeRemove extends SetVisualizationWithItemAnimation.RuntimeSetVisualizationWithItemAnimation { public RuntimeRemove() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getCollectionvalues.remove(getItem());
    }
  }
  
  public Remove() {}
}
