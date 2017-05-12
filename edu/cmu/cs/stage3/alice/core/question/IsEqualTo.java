package edu.cmu.cs.stage3.alice.core.question;











public class IsEqualTo
  extends AbstractIsEqualTo
{
  public IsEqualTo() {}
  










  private static Class[] s_supportedCoercionClasses = { IsNotEqualTo.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected boolean getValue(Object aValue, Object bValue) {
    return isEqualTo(aValue, bValue);
  }
}
