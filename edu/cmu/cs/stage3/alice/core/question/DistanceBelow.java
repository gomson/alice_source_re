package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.SpatialRelation;




















public class DistanceBelow
  extends SpatialRelationDistanceQuestion
{
  public DistanceBelow() {}
  
  private static Class[] s_supportedCoercionClasses = { DistanceAbove.class, DistanceToTheLeftOf.class, DistanceToTheRightOf.class, DistanceInFrontOf.class, DistanceBehind.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected SpatialRelation getSpatialRelation() {
    return SpatialRelation.BELOW;
  }
}
