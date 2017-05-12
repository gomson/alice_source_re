package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Dimension;




















public class Height
  extends SizeAlongDimensionQuestion
{
  public Height() {}
  
  private static Class[] s_supportedCoercionClasses = { Width.class, Depth.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected Dimension getDimension() {
    return Dimension.TOP_TO_BOTTOM;
  }
}
