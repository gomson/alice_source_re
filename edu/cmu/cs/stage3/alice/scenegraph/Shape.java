package edu.cmu.cs.stage3.alice.scenegraph;

import javax.vecmath.Matrix4d;

public abstract class Shape
  extends Geometry
{
  public Shape() {}
  
  public void transform(Matrix4d trans) {}
}
