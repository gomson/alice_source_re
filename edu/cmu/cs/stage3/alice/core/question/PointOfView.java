package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import javax.vecmath.Matrix4d;



















public class PointOfView
  extends SubjectAsSeenByQuestion
{
  public PointOfView() {}
  
  public Class getValueClass()
  {
    return Matrix4d.class;
  }
  
  protected Object getValue(Transformable subjectValue, ReferenceFrame asSeenByValue) {
    return subjectValue.getPointOfView(asSeenByValue);
  }
}
