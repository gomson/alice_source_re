package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;



















public class SuperSqrt
  extends UnaryNumberResultingInNumberQuestion
{
  public SuperSqrt() {}
  
  public final NumberProperty b = new NumberProperty(this, "b", new Double(2.0D));
  
  protected double getValue(double aValue) { return Math.pow(aValue, 1.0D / b.doubleValue()); }
}
