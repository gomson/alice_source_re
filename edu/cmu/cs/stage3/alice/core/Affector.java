package edu.cmu.cs.stage3.alice.core;

public abstract class Affector
  extends Model
{
  public Affector() {}
  
  public abstract edu.cmu.cs.stage3.alice.scenegraph.Affector getSceneGraphAffector();
}
