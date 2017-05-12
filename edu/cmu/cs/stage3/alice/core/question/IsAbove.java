package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.SpatialRelation;




















public class IsAbove
  extends IsInSpatialRelationTo
{
  public IsAbove() {}
  
  private static Class[] s_supportedCoercionClasses = { IsBelow.class, IsInFrontOf.class, IsBehind.class, IsLeftOf.class, IsRightOf.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected SpatialRelation getSpatialRelation() {
    return SpatialRelation.ABOVE;
  }
}
