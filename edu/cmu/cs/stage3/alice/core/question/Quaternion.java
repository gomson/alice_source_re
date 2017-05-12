package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;




















public class Quaternion
  extends SubjectAsSeenByQuestion
{
  public Quaternion() {}
  
  public Class getValueClass()
  {
    return edu.cmu.cs.stage3.math.Quaternion.class;
  }
  
  protected Object getValue(Transformable subjectValue, ReferenceFrame asSeenByValue) {
    return subjectValue.getOrientationAsQuaternion(asSeenByValue);
  }
}
