package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Transformable;




















public class IsWiderThan
  extends SubjectObjectQuestion
{
  public IsWiderThan() {}
  
  private static Class[] s_supportedCoercionClasses = { IsNarrowerThan.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  public Class getValueClass() {
    return Boolean.class;
  }
  
  protected Object getValue(Transformable subjectValue, Transformable objectValue) {
    if (subjectValue.getWidth() > objectValue.getWidth()) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
