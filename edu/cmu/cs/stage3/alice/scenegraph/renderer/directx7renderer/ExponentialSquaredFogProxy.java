package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy;
import javax.vecmath.Matrix4d;

class ExponentialSquaredFogProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.ExponentialSquaredFogProxy
{
  ExponentialSquaredFogProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onAbsoluteTransformationChange(Matrix4d paramMatrix4d);
  
  protected native void addToScene(SceneProxy paramSceneProxy);
  
  protected native void removeFromScene(SceneProxy paramSceneProxy);
  
  protected native void onColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected native void onDensityChange(double paramDouble);
}
