package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Dimension;




















public class Width
  extends SizeAlongDimensionQuestion
{
  public Width() {}
  
  private static Class[] s_supportedCoercionClasses = { Height.class, Depth.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected Dimension getDimension() {
    return Dimension.LEFT_TO_RIGHT;
  }
}
