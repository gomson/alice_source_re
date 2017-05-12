package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.util.Enumerable;
























public class FillingStyle
  extends Enumerable
{
  public FillingStyle() {}
  
  public static final FillingStyle SOLID = new FillingStyle();
  public static final FillingStyle WIREFRAME = new FillingStyle();
  public static final FillingStyle POINTS = new FillingStyle();
  
  public static FillingStyle valueOf(String s) { return (FillingStyle)Enumerable.valueOf(s, FillingStyle.class); }
}
