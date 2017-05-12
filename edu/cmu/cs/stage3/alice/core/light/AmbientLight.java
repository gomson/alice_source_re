package edu.cmu.cs.stage3.alice.core.light;

import edu.cmu.cs.stage3.alice.core.Light;



















public class AmbientLight
  extends Light
{
  public AmbientLight()
  {
    super(new edu.cmu.cs.stage3.alice.scenegraph.AmbientLight());
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.AmbientLight getSceneGraphAmbientLight() { return (edu.cmu.cs.stage3.alice.scenegraph.AmbientLight)getSceneGraphLight(); }
}
