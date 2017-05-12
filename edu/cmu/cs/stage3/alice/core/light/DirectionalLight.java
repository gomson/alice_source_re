package edu.cmu.cs.stage3.alice.core.light;

import edu.cmu.cs.stage3.alice.core.Light;



















public class DirectionalLight
  extends Light
{
  public DirectionalLight()
  {
    super(new edu.cmu.cs.stage3.alice.scenegraph.DirectionalLight());
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.DirectionalLight getSceneGraphDirectionalLight() { return (edu.cmu.cs.stage3.alice.scenegraph.DirectionalLight)getSceneGraphLight(); }
}
