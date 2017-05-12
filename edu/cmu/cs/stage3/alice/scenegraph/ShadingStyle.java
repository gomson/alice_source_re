package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.util.Enumerable;
























public class ShadingStyle
  extends Enumerable
{
  public ShadingStyle() {}
  
  public static final ShadingStyle NONE = new ShadingStyle();
  public static final ShadingStyle FLAT = new ShadingStyle();
  public static final ShadingStyle SMOOTH = new ShadingStyle();
  
  public static ShadingStyle valueOf(String s) {
    return (ShadingStyle)Enumerable.valueOf(s, ShadingStyle.class);
  }
}
