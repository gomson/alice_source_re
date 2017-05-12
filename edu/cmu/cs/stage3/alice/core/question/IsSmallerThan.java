package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Transformable;




















public class IsSmallerThan
  extends SubjectObjectQuestion
{
  public IsSmallerThan() {}
  
  private static Class[] s_supportedCoercionClasses = { IsLargerThan.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  public Class getValueClass() {
    return Boolean.class;
  }
  
  protected Object getValue(Transformable subjectValue, Transformable objectValue) {
    if (subjectValue.getVolume() < objectValue.getVolume()) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
