package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Appearance;
import edu.cmu.cs.stage3.alice.scenegraph.Geometry;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import java.nio.DoubleBuffer;
import javax.media.opengl.GL;
import javax.vecmath.Matrix3d;














class VisualProxy
  extends ComponentProxy
{
  VisualProxy() {}
  
  private AppearanceProxy m_frontFacingAppearanceProxy = null;
  private AppearanceProxy m_backFacingAppearanceProxy = null;
  private GeometryProxy m_geometryProxy = null;
  private boolean m_isShowing = false;
  private double[] m_scale = new double[16];
  
  private DoubleBuffer m_scaleBuffer = DoubleBuffer.wrap(m_scale);
  
  public Visual getSceneGraphVisual() { return (Visual)getSceneGraphElement(); }
  
  protected void changed(Property property, Object value)
  {
    if (property == Visual.FRONT_FACING_APPEARANCE_PROPERTY) {
      m_frontFacingAppearanceProxy = ((AppearanceProxy)getProxyFor((Appearance)value));
    } else if (property == Visual.BACK_FACING_APPEARANCE_PROPERTY) {
      m_backFacingAppearanceProxy = ((AppearanceProxy)getProxyFor((Appearance)value));
    } else if (property == Visual.GEOMETRY_PROPERTY) {
      m_geometryProxy = ((GeometryProxy)getProxyFor((Geometry)value));
    } else if (property == Visual.SCALE_PROPERTY) {
      copy(m_scale, (Matrix3d)value);
    } else if (property == Visual.IS_SHOWING_PROPERTY) {
      m_isShowing = ((value != null) && (((Boolean)value).booleanValue()));
    } else if (property != Visual.DISABLED_AFFECTORS_PROPERTY)
    {

      super.changed(property, value);
    }
  }
  

  public void setup(RenderContext context) {}
  
  private double opacity()
  {
    if ((m_isShowing) && (m_geometryProxy != null)) {
      if (m_frontFacingAppearanceProxy != null) {
        return m_frontFacingAppearanceProxy.Showing();
      }
      if (m_backFacingAppearanceProxy != null) {
        return m_backFacingAppearanceProxy.Showing();
      }
    }
    return 0.0D;
  }
  
  public void render(RenderContext context) {
    if (opacity() > 0.0D) {
      if ((opacity() < 1.0D) && (context.renderOpaque()))
        return;
      if ((opacity() == 1.0D) && (!context.renderOpaque()))
        return;
      if (m_frontFacingAppearanceProxy != null) {
        if (m_backFacingAppearanceProxy != null) {
          gl.glDisable(2884);
        } else {
          gl.glEnable(2884);
          gl.glCullFace(1029);
        }
      }
      else if (m_backFacingAppearanceProxy != null) {
        gl.glEnable(2884);
        gl.glCullFace(1028);
      }
      



      if (m_frontFacingAppearanceProxy == m_backFacingAppearanceProxy) {
        if (m_frontFacingAppearanceProxy != null) {
          m_frontFacingAppearanceProxy.setPipelineState(context, 1032);
        }
      }
      else
      {
        if (m_frontFacingAppearanceProxy != null) {
          m_frontFacingAppearanceProxy.setPipelineState(context, 1028);
        }
        if (m_backFacingAppearanceProxy != null) {
          m_backFacingAppearanceProxy.setPipelineState(context, 1029);
        }
      }
      
      gl.glPushMatrix();
      gl.glMultMatrixd(m_scaleBuffer);
      m_geometryProxy.render(context);
      gl.glPopMatrix();
      
      gl.glDepthMask(true);
    }
  }
  
  public void pick(PickContext context, PickParameters pickParameters) {
    if (opacity() > 0.0D) {
      gl.glPushMatrix();
      gl.glMultMatrixd(m_scaleBuffer);
      
      gl.glPushName(context.getPickNameForVisualProxy(this));
      gl.glEnable(2884);
      if (m_backFacingAppearanceProxy != null) {
        gl.glCullFace(1028);
        gl.glPushName(0);
        m_geometryProxy.pick(context, pickParameters.isSubElementRequired());
        gl.glPopName();
      }
      if (m_frontFacingAppearanceProxy != null) {
        gl.glCullFace(1029);
        gl.glPushName(1);
        m_geometryProxy.pick(context, pickParameters.isSubElementRequired());
        gl.glPopName();
      }
      gl.glPopName();
      gl.glPopMatrix();
    }
  }
}
