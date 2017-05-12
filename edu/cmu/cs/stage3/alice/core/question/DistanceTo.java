package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Transformable;




















public class DistanceTo
  extends SubjectObjectQuestion
{
  public DistanceTo() {}
  
  public Class getValueClass()
  {
    return Number.class;
  }
  
  protected Object getValue(Transformable subjectValue, Transformable objectValue) {
    return new Double(subjectValue.getDistanceTo(objectValue));
  }
}
