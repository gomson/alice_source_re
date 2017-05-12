package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.SpatialRelation;




















public class DistanceBehind
  extends SpatialRelationDistanceQuestion
{
  public DistanceBehind() {}
  
  private static Class[] s_supportedCoercionClasses = { DistanceInFrontOf.class, DistanceAbove.class, DistanceBelow.class, DistanceToTheLeftOf.class, DistanceToTheRightOf.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected SpatialRelation getSpatialRelation() {
    return SpatialRelation.BEHIND;
  }
}
