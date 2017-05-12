package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Dimension;




















public class Depth
  extends SizeAlongDimensionQuestion
{
  public Depth() {}
  
  private static Class[] s_supportedCoercionClasses = { Width.class, Height.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected Dimension getDimension() {
    return Dimension.FRONT_TO_BACK;
  }
}
