package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Transformable;

public class Pose extends SubjectQuestion { public Pose() {}
  public Class getValueClass() { return edu.cmu.cs.stage3.alice.core.Pose.class; }
  
  protected Object getValue(Transformable subjectValue)
  {
    return edu.cmu.cs.stage3.alice.core.Pose.manufacturePose(subjectValue, subjectValue);
  }
}
