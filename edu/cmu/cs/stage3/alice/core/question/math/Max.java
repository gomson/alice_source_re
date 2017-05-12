package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion;
import edu.cmu.cs.stage3.lang.Messages;



















public class Max
  extends BinaryNumberResultingInNumberQuestion
{
  public Max() {}
  
  private static Class[] s_supportedCoercionClasses = { Min.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected double getValue(double aValue, double bValue) {
    edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("maximum_of_") + aValue + " " + Messages.getString("and_") + " " + bValue + Messages.getString("is_");
    return Math.max(aValue, bValue);
  }
}
