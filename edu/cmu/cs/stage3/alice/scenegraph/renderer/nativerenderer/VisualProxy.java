package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Affector;
import edu.cmu.cs.stage3.alice.scenegraph.Appearance;
import edu.cmu.cs.stage3.alice.scenegraph.Geometry;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import java.util.Vector;
import javax.vecmath.Matrix3d;









public abstract class VisualProxy
  extends ComponentProxy
{
  protected abstract void onFrontFacingAppearanceChange(AppearanceProxy paramAppearanceProxy);
  
  protected abstract void onBackFacingAppearanceChange(AppearanceProxy paramAppearanceProxy);
  
  protected abstract void onGeometryChange(GeometryProxy paramGeometryProxy);
  
  protected abstract void onScaleChange(Matrix3d paramMatrix3d);
  
  protected abstract void onIsShowingChange(boolean paramBoolean);
  
  protected abstract void onDisabledAffectorsChange(AffectorProxy[] paramArrayOfAffectorProxy);
  
  static Vector m_instances = new Vector();
  
  public VisualProxy() {
    m_instances.addElement(this);
  }
  
  public void release() {
    m_instances.removeElement(this);
    super.release();
  }
  
  static VisualProxy map(int nativeInstance) {
    for (int i = 0; i < m_instances.size(); i++) {
      VisualProxy visualProxy = (VisualProxy)m_instances.elementAt(i);
      if (visualProxy.getNativeInstance() == nativeInstance) {
        return visualProxy;
      }
    }
    return null;
  }
  
  protected void changed(Property property, Object value)
  {
    if (property == Visual.FRONT_FACING_APPEARANCE_PROPERTY) {
      onFrontFacingAppearanceChange((AppearanceProxy)getProxyFor((Appearance)value));
    } else if (property == Visual.BACK_FACING_APPEARANCE_PROPERTY) {
      onBackFacingAppearanceChange((AppearanceProxy)getProxyFor((Appearance)value));
    } else if (property == Visual.GEOMETRY_PROPERTY) {
      onGeometryChange((GeometryProxy)getProxyFor((Geometry)value));
    } else if (property == Visual.SCALE_PROPERTY) {
      onScaleChange((Matrix3d)value);
    } else if (property == Visual.IS_SHOWING_PROPERTY) {
      onIsShowingChange(((Boolean)value).booleanValue());
    } else if (property == Visual.DISABLED_AFFECTORS_PROPERTY) {
      onDisabledAffectorsChange((AffectorProxy[])getProxiesFor((Affector[])value, AffectorProxy.class));
    } else {
      super.changed(property, value);
    }
  }
}
