package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.SpatialRelation;




















public class DistanceToTheLeftOf
  extends SpatialRelationDistanceQuestion
{
  public DistanceToTheLeftOf() {}
  
  private static Class[] s_supportedCoercionClasses = { DistanceToTheRightOf.class, DistanceAbove.class, DistanceBelow.class, DistanceInFrontOf.class, DistanceBehind.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected SpatialRelation getSpatialRelation() {
    return SpatialRelation.LEFT_OF;
  }
}
