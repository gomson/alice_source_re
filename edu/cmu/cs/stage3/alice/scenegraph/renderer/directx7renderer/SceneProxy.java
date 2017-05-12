package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.BackgroundProxy;
import javax.vecmath.Matrix4d;

class SceneProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy
{
  SceneProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onAbsoluteTransformationChange(Matrix4d paramMatrix4d);
  
  protected native void addToScene(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy paramSceneProxy);
  
  protected native void removeFromScene(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy paramSceneProxy);
  
  protected native void onBackgroundChange(BackgroundProxy paramBackgroundProxy);
}
