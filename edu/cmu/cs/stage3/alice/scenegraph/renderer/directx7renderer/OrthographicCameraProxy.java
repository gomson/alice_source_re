package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.BackgroundProxy;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy;
import javax.vecmath.Matrix4d;

class OrthographicCameraProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OrthographicCameraProxy
{
  OrthographicCameraProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onAbsoluteTransformationChange(Matrix4d paramMatrix4d);
  
  protected native void addToScene(SceneProxy paramSceneProxy);
  
  protected native void removeFromScene(SceneProxy paramSceneProxy);
  
  protected native void onNearClippingPlaneDistanceChange(double paramDouble);
  
  protected native void onFarClippingPlaneDistanceChange(double paramDouble);
  
  protected native void onBackgroundChange(BackgroundProxy paramBackgroundProxy);
  
  protected native void onPlaneChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
}
