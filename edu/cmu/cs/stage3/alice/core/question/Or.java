package edu.cmu.cs.stage3.alice.core.question;











public class Or
  extends BinaryBooleanResultingInBooleanQuestion
{
  public Or() {}
  









  private static Class[] s_supportedCoercionClasses = { And.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected boolean isShortCircuitable(boolean a) {
    return a;
  }
  
  protected boolean getValue(boolean a, boolean b) {
    return (a) || (b);
  }
}
