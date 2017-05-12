package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy;
import javax.vecmath.Matrix4d;

class PointLightProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.PointLightProxy
{
  PointLightProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onAbsoluteTransformationChange(Matrix4d paramMatrix4d);
  
  protected native void addToScene(SceneProxy paramSceneProxy);
  
  protected native void removeFromScene(SceneProxy paramSceneProxy);
  
  protected native void onColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected native void onBrightnessChange(double paramDouble);
  
  protected native void onRangeChange(double paramDouble);
  
  protected native void onConstantAttenuationChange(double paramDouble);
  
  protected native void onLinearAttenuationChange(double paramDouble);
  
  protected native void onQuadraticAttenuationChange(double paramDouble);
}
