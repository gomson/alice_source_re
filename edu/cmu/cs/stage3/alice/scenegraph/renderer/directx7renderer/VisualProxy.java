package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AffectorProxy;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AppearanceProxy;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.GeometryProxy;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SceneProxy;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;

class VisualProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.VisualProxy
{
  VisualProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onAbsoluteTransformationChange(Matrix4d paramMatrix4d);
  
  protected native void addToScene(SceneProxy paramSceneProxy);
  
  protected native void removeFromScene(SceneProxy paramSceneProxy);
  
  protected native void onFrontFacingAppearanceChange(AppearanceProxy paramAppearanceProxy);
  
  protected native void onBackFacingAppearanceChange(AppearanceProxy paramAppearanceProxy);
  
  protected native void onGeometryChange(GeometryProxy paramGeometryProxy);
  
  protected native void onScaleChange(Matrix3d paramMatrix3d);
  
  protected native void onIsShowingChange(boolean paramBoolean);
  
  protected native void onDisabledAffectorsChange(AffectorProxy[] paramArrayOfAffectorProxy);
}
