package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Transformable;




















public class IsNarrowerThan
  extends SubjectObjectQuestion
{
  public IsNarrowerThan() {}
  
  private static Class[] s_supportedCoercionClasses = { IsWiderThan.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  public Class getValueClass() {
    return Boolean.class;
  }
  
  protected Object getValue(Transformable subjectValue, Transformable objectValue) {
    if (subjectValue.getWidth() < objectValue.getWidth()) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
