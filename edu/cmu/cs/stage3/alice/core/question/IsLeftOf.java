package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.SpatialRelation;




















public class IsLeftOf
  extends IsInSpatialRelationTo
{
  public IsLeftOf() {}
  
  private static Class[] s_supportedCoercionClasses = { IsRightOf.class, IsInFrontOf.class, IsBehind.class, IsAbove.class, IsBelow.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected SpatialRelation getSpatialRelation() {
    return SpatialRelation.LEFT_OF;
  }
}
