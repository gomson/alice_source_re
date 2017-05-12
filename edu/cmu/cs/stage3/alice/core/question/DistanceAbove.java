package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.SpatialRelation;




















public class DistanceAbove
  extends SpatialRelationDistanceQuestion
{
  public DistanceAbove() {}
  
  private static Class[] s_supportedCoercionClasses = { DistanceBelow.class, DistanceToTheLeftOf.class, DistanceToTheRightOf.class, DistanceInFrontOf.class, DistanceBehind.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected SpatialRelation getSpatialRelation() {
    return SpatialRelation.ABOVE;
  }
}
