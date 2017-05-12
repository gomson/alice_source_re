package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.decorator.ViewVolumeDecorator;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.RenderTargetProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory;
import java.awt.Image;
import java.awt.Point;













public abstract class Camera
  extends Model
{
  public final BooleanProperty isLetterboxedAsOpposedToDistorted = new BooleanProperty(this, "isLetterboxedAsOpposedToDistorted", Boolean.TRUE);
  public final NumberProperty nearClippingPlaneDistance = new NumberProperty(this, "nearClippingPlaneDistance", new Double(0.1D));
  public final NumberProperty farClippingPlaneDistance = new NumberProperty(this, "farClippingPlaneDistance", new Double(100.0D));
  public final RenderTargetProperty renderTarget = new RenderTargetProperty(this, "renderTarget", null);
  public final BooleanProperty isViewVolumeShowing = new BooleanProperty(this, "isViewVolumeShowing", Boolean.FALSE);
  private edu.cmu.cs.stage3.alice.scenegraph.Camera m_sgCamera;
  private ViewVolumeDecorator m_viewVolumeDecorator;
  
  protected Camera(edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera) {
    m_sgCamera = sgCamera;
    m_sgCamera.setParent(getSceneGraphTransformable());
    m_sgCamera.setBonus(this);
    nearClippingPlaneDistance.set(new Double(m_sgCamera.getNearClippingPlaneDistance()));
    farClippingPlaneDistance.set(new Double(m_sgCamera.getFarClippingPlaneDistance()));
    m_viewVolumeDecorator = createViewVolumeDecorator();
  }
  
  protected void internalRelease(int pass) {
    switch (pass) {
    case 1: 
      m_sgCamera.setParent(null);
      m_viewVolumeDecorator.internalRelease(1);
      break;
    case 2: 
      m_sgCamera.release();
      m_sgCamera = null;
      m_viewVolumeDecorator.internalRelease(2);
      m_viewVolumeDecorator = null;
    }
    
    super.internalRelease(pass);
  }
  

  public edu.cmu.cs.stage3.alice.scenegraph.Camera getSceneGraphCamera() { return m_sgCamera; }
  
  protected abstract ViewVolumeDecorator createViewVolumeDecorator();
  
  protected void nameValueChanged(String value) {
    super.nameValueChanged(value);
    String s = null;
    if (value != null) {
      s = value + ".m_sgCamera";
    }
    m_sgCamera.setName(s);
  }
  
  private void nearClippingPlaneDistanceValueChanged(Number value) { double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    m_sgCamera.setNearClippingPlaneDistance(d);
  }
  
  private void farClippingPlaneDistanceValueChanged(Number value) { double d = NaN.0D;
    if (value != null) {
      d = value.doubleValue();
    }
    m_sgCamera.setFarClippingPlaneDistance(d);
  }
  
  private void renderTargetValueChanging(RenderTarget renderTargetValueToBe) { RenderTarget renderTargetValue = (RenderTarget)renderTarget.getValue();
    if (renderTargetValue != null)
      renderTargetValue.removeCamera(this);
  }
  
  private void renderTargetValueChanged(RenderTarget renderTargetValue) {
    if (renderTargetValue != null)
      renderTargetValue.addCamera(this);
  }
  
  private void isViewVolumeShowingValueChanged(Boolean value) {
    if (m_viewVolumeDecorator != null) {
      m_viewVolumeDecorator.setIsShowing(value);
    }
  }
  
  protected void propertyChanging(Property property, Object value) {
    if (property != nearClippingPlaneDistance)
    {
      if (property != farClippingPlaneDistance)
      {
        if (property == renderTarget) {
          renderTargetValueChanging((RenderTarget)value);
        } else if (property != isViewVolumeShowing)
        {

          super.propertyChanging(property, value); } }
    }
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == nearClippingPlaneDistance) {
      nearClippingPlaneDistanceValueChanged((Number)value);
    } else if (property == farClippingPlaneDistance) {
      farClippingPlaneDistanceValueChanged((Number)value);
    } else if (property == renderTarget) {
      renderTargetValueChanged((RenderTarget)value);
    } else if (property == isViewVolumeShowing) {
      isViewVolumeShowingValueChanged((Boolean)value);
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  public PickInfo pick(int x, int y) {
    RenderTarget renderTargetValue = (RenderTarget)renderTarget.getValue();
    if (renderTargetValue != null) {
      return renderTargetValue.getInternal().pick(x, y, false, true);
    }
    return null;
  }
  
  public PickInfo pick(Point p) {
    return pick(x, y);
  }
  
  public Image takePicture(int width, int height) {
    Image image = null;
    World world = getWorld();
    if (world != null) {
      RenderTargetFactory renderTargetFactory = world.getRenderTargetFactory();
      if (renderTargetFactory != null) {
        OffscreenRenderTarget offscreenRenderTarget = renderTargetFactory.createOffscreenRenderTarget();
        offscreenRenderTarget.setSize(width, height);
        offscreenRenderTarget.addCamera(getSceneGraphCamera());
        offscreenRenderTarget.clearAndRenderOffscreen();
        image = offscreenRenderTarget.getOffscreenImage();
        offscreenRenderTarget.removeCamera(getSceneGraphCamera());
        offscreenRenderTarget.release();
      }
    }
    return image;
  }
  


  public boolean canSee(Model model, boolean checkForOcclusion)
  {
    if (model.getSceneGraphVisual().isInProjectionVolumeOf(getSceneGraphCamera())) {
      return true;
    }
    
    for (int i = 0; i < parts.size(); i++) {
      Object v = parts.get(i);
      if (((v instanceof Model)) && 
        (canSee((Model)v, checkForOcclusion))) {
        return true;
      }
    }
    
    return false;
  }
}
