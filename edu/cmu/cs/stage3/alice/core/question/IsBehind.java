package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.SpatialRelation;




















public class IsBehind
  extends IsInSpatialRelationTo
{
  public IsBehind() {}
  
  private static Class[] s_supportedCoercionClasses = { IsInFrontOf.class, IsAbove.class, IsBelow.class, IsLeftOf.class, IsRightOf.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected SpatialRelation getSpatialRelation() {
    return SpatialRelation.BEHIND;
  }
}
