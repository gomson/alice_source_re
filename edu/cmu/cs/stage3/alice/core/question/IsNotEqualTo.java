package edu.cmu.cs.stage3.alice.core.question;











public class IsNotEqualTo
  extends AbstractIsEqualTo
{
  public IsNotEqualTo() {}
  









  private static Class[] s_supportedCoercionClasses = { IsEqualTo.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected boolean getValue(Object aValue, Object bValue) {
    return !isEqualTo(aValue, bValue);
  }
}
