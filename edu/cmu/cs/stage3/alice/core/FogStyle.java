package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.util.Enumerable;


















public class FogStyle
  extends Enumerable
{
  public FogStyle() {}
  
  public static final FogStyle NONE = new FogStyle();
  public static final FogStyle LINEAR = new FogStyle();
  public static final FogStyle EXPONENTIAL = new FogStyle();
  
  public static FogStyle valueOf(String s) { return (FogStyle)Enumerable.valueOf(s, FogStyle.class); }
}
