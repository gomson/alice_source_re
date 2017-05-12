package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SpatialRelation;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;




















public abstract class IsInSpatialRelationTo
  extends SubjectObjectQuestion
{
  public IsInSpatialRelationTo() {}
  
  public final ReferenceFrameProperty asSeenBy = new ReferenceFrameProperty(this, "asSeenBy", null);
  
  protected abstract SpatialRelation getSpatialRelation();
  
  public Class getValueClass() { return Boolean.class; }
  
  protected Object getValue(Transformable subjectValue, Transformable objectValue)
  {
    ReferenceFrame asSeenByValue = asSeenBy.getReferenceFrameValue();
    if (subjectValue.isInSpatialRelationTo(getSpatialRelation(), objectValue, asSeenByValue)) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
