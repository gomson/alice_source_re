package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;




















public class IsCloseTo
  extends SubjectObjectQuestion
{
  public IsCloseTo() {}
  
  private static Class[] s_supportedCoercionClasses = { IsFarFrom.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses; }
  
  public final NumberProperty threshold = new NumberProperty(this, "threshold", new Double(1.0D));
  
  public Class getValueClass() {
    return Boolean.class;
  }
  
  protected Object getValue(Transformable subjectValue, Transformable objectValue) {
    double thresholdValue = threshold.doubleValue();
    if (subjectValue.getDistanceSquaredTo(objectValue) < thresholdValue * thresholdValue) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
