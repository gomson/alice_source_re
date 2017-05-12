package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;




















public abstract class SubjectAsSeenByQuestion
  extends SubjectQuestion
{
  public SubjectAsSeenByQuestion() {}
  
  public final ReferenceFrameProperty asSeenBy = new ReferenceFrameProperty(this, "asSeenBy", null);
  
  protected abstract Object getValue(Transformable paramTransformable, ReferenceFrame paramReferenceFrame);
  
  protected Object getValue(Transformable subjectValue) { ReferenceFrame asSeenByValue = asSeenBy.getReferenceFrameValue();
    return getValue(subjectValue, asSeenByValue);
  }
}
