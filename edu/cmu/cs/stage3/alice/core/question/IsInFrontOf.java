package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.SpatialRelation;




















public class IsInFrontOf
  extends IsInSpatialRelationTo
{
  public IsInFrontOf() {}
  
  private static Class[] s_supportedCoercionClasses = { IsBehind.class, IsAbove.class, IsBelow.class, IsLeftOf.class, IsRightOf.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected SpatialRelation getSpatialRelation() {
    return SpatialRelation.IN_FRONT_OF;
  }
}
