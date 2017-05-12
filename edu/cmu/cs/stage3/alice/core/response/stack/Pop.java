package edu.cmu.cs.stage3.alice.core.response.stack;

import edu.cmu.cs.stage3.alice.core.Stack;















public class Pop
  extends StackResponse
{
  public Pop() {}
  
  public class RuntimePop
    extends StackResponse.RuntimeStackResponse
  {
    public RuntimePop() { super(); }
    
    public void epilogue(double t) {
      super.epilogue(t);
      getStack().pop();
    }
  }
}
