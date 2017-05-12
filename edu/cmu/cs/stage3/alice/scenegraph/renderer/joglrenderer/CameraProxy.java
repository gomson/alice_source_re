package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Background;
import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import java.awt.Rectangle;
import javax.media.opengl.GL;
















abstract class CameraProxy
  extends ComponentProxy
{
  CameraProxy() {}
  
  private BackgroundProxy m_backgroundProxy = null;
  private float m_near;
  private float m_far;
  
  private Rectangle m_viewport = null;
  private boolean m_isLetterboxedAsOpposedToDistorted = true;
  
  protected abstract Rectangle getActualLetterboxedViewport(int paramInt1, int paramInt2);
  
  public Rectangle getActualViewport(int width, int height) { if (m_viewport != null) {
      return m_viewport;
    }
    if (m_isLetterboxedAsOpposedToDistorted) {
      return getActualLetterboxedViewport(width, height);
    }
    return new Rectangle(0, 0, width, height);
  }
  
  public Rectangle getViewport()
  {
    return m_viewport;
  }
  
  public void setViewport(Rectangle viewport) { m_viewport = new Rectangle(viewport); }
  
  public boolean isLetterboxedAsOpposedToDistorted() {
    return m_isLetterboxedAsOpposedToDistorted;
  }
  
  public void setIsLetterboxedAsOpposedToDistorted(boolean isLetterboxedAsOpposedToDistorted) { m_isLetterboxedAsOpposedToDistorted = isLetterboxedAsOpposedToDistorted; }
  
  protected abstract double[] getActualNearPlane(double[] paramArrayOfDouble, int paramInt1, int paramInt2, double paramDouble);
  
  public double[] getActualNearPlane(double[] ret, int width, int height) {
    return getActualNearPlane(ret, width, height, m_near);
  }
  
  public void setup(RenderContext context) {}
  
  protected abstract void projection(Context paramContext, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2);
  
  public void performClearAndRenderOffscreen(RenderContext context) {
    SceneProxy sceneProxy = getSceneProxy();
    if (sceneProxy != null) {
      Rectangle actualViewport = getActualViewport(context.getWidth(), context.getHeight());
      BackgroundProxy backgroundProxy;
      BackgroundProxy backgroundProxy; if (m_backgroundProxy != null) {
        backgroundProxy = m_backgroundProxy;
      } else {
        backgroundProxy = sceneProxy.getBackgroundProxy();
      }
      context.clear(backgroundProxy, actualViewport);
      
      gl.glMatrixMode(5889);
      gl.glLoadIdentity();
      projection(context, width, height, m_near, m_far);
      gl.glMatrixMode(5888);
      gl.glLoadIdentity();
      gl.glLoadMatrixd(getInverseAbsoluteTransformationAsBuffer());
      
      sceneProxy.setup(context);
      

      context.setRenderOpaque();
      sceneProxy.render(context);
      
      gl.glBlendFunc(770, 771);
      gl.glEnable(3042);
      gl.glEnable(3008);
      gl.glAlphaFunc(516, 0.0F);
      


      context.setRenderTransparent();
      sceneProxy.render(context);
      
      gl.glDisable(3042);
    }
  }
  
  public void performPick(PickContext context, PickParameters pickParameters) { SceneProxy sceneProxy = getSceneProxy();
    if (sceneProxy != null) {
      int width = context.getWidth();
      int height = context.getHeight();
      projection(context, width, height, m_near, m_far);
      gl.glMatrixMode(5888);
      gl.glLoadIdentity();
      gl.glLoadMatrixd(getInverseAbsoluteTransformationAsBuffer());
      sceneProxy.pick(context, pickParameters);
    }
  }
  

  public void render(RenderContext context) {}
  
  public void pick(PickContext context, PickParameters pickParameters) {}
  
  protected void changed(Property property, Object value)
  {
    if (property == Camera.NEAR_CLIPPING_PLANE_DISTANCE_PROPERTY) {
      m_near = ((Number)value).floatValue();
    } else if (property == Camera.FAR_CLIPPING_PLANE_DISTANCE_PROPERTY) {
      m_far = ((Number)value).floatValue();
    } else if (property == Camera.BACKGROUND_PROPERTY) {
      m_backgroundProxy = ((BackgroundProxy)getProxyFor((Background)value));
    } else {
      super.changed(property, value);
    }
  }
}
