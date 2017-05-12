package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.SpatialRelation;




















public class IsRightOf
  extends IsInSpatialRelationTo
{
  public IsRightOf() {}
  
  private static Class[] s_supportedCoercionClasses = { IsLeftOf.class, IsInFrontOf.class, IsBehind.class, IsAbove.class, IsBelow.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected SpatialRelation getSpatialRelation() {
    return SpatialRelation.RIGHT_OF;
  }
}
