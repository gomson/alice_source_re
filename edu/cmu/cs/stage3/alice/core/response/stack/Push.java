package edu.cmu.cs.stage3.alice.core.response.stack;

import edu.cmu.cs.stage3.alice.core.Stack;















public class Push
  extends StackItemResponse
{
  public Push() {}
  
  public class RuntimePush
    extends StackItemResponse.RuntimeStackItemResponse
  {
    public RuntimePush() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getStack().pushValue(getItem());
    }
  }
}
