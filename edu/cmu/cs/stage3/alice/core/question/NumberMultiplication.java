package edu.cmu.cs.stage3.alice.core.question;











public class NumberMultiplication
  extends BinaryNumberResultingInNumberQuestion
{
  public NumberMultiplication() {}
  









  private static Class[] s_supportedCoercionClasses = { NumberAddition.class, NumberSubtraction.class, NumberDivision.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected double getValue(double aValue, double bValue) {
    return aValue * bValue;
  }
}
