package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.SpatialRelation;




















public class DistanceInFrontOf
  extends SpatialRelationDistanceQuestion
{
  public DistanceInFrontOf() {}
  
  private static Class[] s_supportedCoercionClasses = { DistanceBehind.class, DistanceAbove.class, DistanceBelow.class, DistanceToTheLeftOf.class, DistanceToTheRightOf.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected SpatialRelation getSpatialRelation() {
    return SpatialRelation.IN_FRONT_OF;
  }
}
