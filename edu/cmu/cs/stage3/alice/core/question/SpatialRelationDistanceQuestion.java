package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SpatialRelation;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;




















public abstract class SpatialRelationDistanceQuestion
  extends SubjectObjectQuestion
{
  public SpatialRelationDistanceQuestion() {}
  
  public final ReferenceFrameProperty asSeenBy = new ReferenceFrameProperty(this, "asSeenBy", null);
  

  public Class getValueClass() { return Number.class; }
  
  protected abstract SpatialRelation getSpatialRelation();
  
  protected Object getValue(Transformable subjectValue, Transformable objectValue) {
    ReferenceFrame asSeenByValue = asSeenBy.getReferenceFrameValue();
    if (subjectValue == null) {
      return new Double(1.0D);
    }
    return new Double(subjectValue.getSpatialRelationDistance(getSpatialRelation(), objectValue, asSeenByValue));
  }
}
