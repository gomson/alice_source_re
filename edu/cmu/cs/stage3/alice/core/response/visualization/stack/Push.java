package edu.cmu.cs.stage3.alice.core.response.visualization.stack;
import edu.cmu.cs.stage3.alice.core.Collection;

public class Push extends StackVisualizationWithItemAnimation { public class RuntimePush extends StackVisualizationWithItemAnimation.RuntimeStackVisualizationWithItemAnimation { public RuntimePush() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getCollectionvalues.addValue(getItem());
    }
  }
  
  public Push() {}
}
