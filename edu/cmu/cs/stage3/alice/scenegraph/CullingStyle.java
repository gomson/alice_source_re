package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.util.Enumerable;
























public class CullingStyle
  extends Enumerable
{
  public CullingStyle() {}
  
  public static final CullingStyle NONE = new CullingStyle();
  public static final CullingStyle FRONT = new CullingStyle();
  public static final CullingStyle BACK = new CullingStyle();
  
  public static CullingStyle valueOf(String s) {
    return (CullingStyle)Enumerable.valueOf(s, CullingStyle.class);
  }
}
