package edu.cmu.cs.stage3.alice.core.question;











public class And
  extends BinaryBooleanResultingInBooleanQuestion
{
  public And() {}
  









  private static Class[] s_supportedCoercionClasses = { Or.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected boolean isShortCircuitable(boolean a) {
    return !a;
  }
  
  protected boolean getValue(boolean a, boolean b) {
    return (a) && (b);
  }
}
