package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.math.Vector3;



















public class Position
  extends SubjectAsSeenByQuestion
{
  public Position() {}
  
  public Class getValueClass()
  {
    return Vector3.class;
  }
  
  protected Object getValue(Transformable subjectValue, ReferenceFrame asSeenByValue) {
    return subjectValue.getPosition(asSeenByValue);
  }
}
