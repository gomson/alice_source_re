package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.SpatialRelation;




















public class DistanceToTheRightOf
  extends SpatialRelationDistanceQuestion
{
  public DistanceToTheRightOf() {}
  
  private static Class[] s_supportedCoercionClasses = { DistanceToTheLeftOf.class, DistanceAbove.class, DistanceBelow.class, DistanceInFrontOf.class, DistanceBehind.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected SpatialRelation getSpatialRelation() {
    return SpatialRelation.RIGHT_OF;
  }
}
