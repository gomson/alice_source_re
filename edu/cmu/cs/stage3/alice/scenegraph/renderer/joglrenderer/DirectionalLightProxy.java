package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;











class DirectionalLightProxy
  extends LightProxy
{
  DirectionalLightProxy() {}
  









  protected float[] getPosition(float[] rv)
  {
    double[] absolute = getAbsoluteTransformation();
    rv[0] = ((float)absolute[8]);
    rv[1] = ((float)absolute[9]);
    rv[2] = ((float)absolute[10]);
    rv[3] = 0.0F;
    return rv;
  }
}
