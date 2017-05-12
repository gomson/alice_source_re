package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Sphere;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;


















public class Visual
  extends Component
{
  public static final Property FRONT_FACING_APPEARANCE_PROPERTY = new Property(Visual.class, "FRONT_FACING_APPEARANCE");
  public static final Property BACK_FACING_APPEARANCE_PROPERTY = new Property(Visual.class, "BACK_FACING_APPEARANCE");
  public static final Property GEOMETRY_PROPERTY = new Property(Visual.class, "GEOMETRY");
  public static final Property SCALE_PROPERTY = new Property(Visual.class, "SCALE");
  public static final Property IS_SHOWING_PROPERTY = new Property(Visual.class, "IS_SHOWING");
  public static final Property DISABLED_AFFECTORS_PROPERTY = new Property(Visual.class, "DISABLED_AFFECTORS");
  
  private Appearance m_frontFacingAppearance = null;
  private Appearance m_backFacingAppearance = null;
  private Geometry m_geometry = null;
  private Matrix3d m_scale = null;
  private boolean m_isShowing = true;
  private Affector[] m_disabledAffectors = null;
  
  public Visual() {
    m_scale = new Matrix3d();
    m_scale.setIdentity();
  }
  
  protected void releasePass1() {
    if (m_frontFacingAppearance != null) {
      warnln(Messages.getString("WARNING__released_visual_") + this + " " + Messages.getString("still_has_front_facing_appearance_") + m_frontFacingAppearance + ".");
      setFrontFacingAppearance(null);
    }
    if (m_backFacingAppearance != null) {
      warnln(Messages.getString("WARNING__released_visual_") + this + " " + Messages.getString("still_has_back_facing_appearance_") + m_frontFacingAppearance + ".");
      setBackFacingAppearance(null);
    }
    if (m_geometry != null) {
      warnln(Messages.getString("WARNING__released_visual_") + this + " " + Messages.getString("still_has_geometry_") + m_geometry + ".");
      setGeometry(null);
    }
    if ((m_disabledAffectors != null) && (m_disabledAffectors.length > 0)) {
      warnln(Messages.getString("WARNING__released_visual_") + this + " " + Messages.getString("still_has_disabled_affectors__"));
      for (int i = 0; i < m_disabledAffectors.length; i++) {
        warnln("\t" + m_disabledAffectors[i]);
      }
      setDisabledAffectors(null);
    }
    super.releasePass1();
  }
  
  public Geometry getGeometry() { return m_geometry; }
  
  public void setGeometry(Geometry geometry) {
    if (notequal(m_geometry, geometry)) {
      m_geometry = geometry;
      onPropertyChange(GEOMETRY_PROPERTY);
    }
  }
  

  public Appearance getFrontFacingAppearance() { return m_frontFacingAppearance; }
  
  public void setFrontFacingAppearance(Appearance frontFacingAppearance) {
    if (notequal(m_frontFacingAppearance, frontFacingAppearance)) {
      m_frontFacingAppearance = frontFacingAppearance;
      onPropertyChange(FRONT_FACING_APPEARANCE_PROPERTY);
    }
  }
  

  public Appearance getBackFacingAppearance() { return m_backFacingAppearance; }
  
  public void setBackFacingAppearance(Appearance backFacingAppearance) {
    if (notequal(m_backFacingAppearance, backFacingAppearance)) {
      m_backFacingAppearance = backFacingAppearance;
      onPropertyChange(BACK_FACING_APPEARANCE_PROPERTY);
    }
  }
  

  public Matrix3d getScale() { return m_scale; }
  
  public void setScale(Matrix3d scale) {
    if (notequal(m_scale, scale)) {
      m_scale = scale;
      onPropertyChange(SCALE_PROPERTY);
    }
  }
  

  public boolean getIsShowing() { return m_isShowing; }
  
  public void setIsShowing(boolean isShowing) {
    if (m_isShowing != isShowing) {
      m_isShowing = isShowing;
      onPropertyChange(IS_SHOWING_PROPERTY);
    }
  }
  
  public Affector[] getDisabledAffectors() { return m_disabledAffectors; }
  
  public void setDisabledAffectors(Affector[] disabledAffectors) {
    if (notequal(m_disabledAffectors, disabledAffectors)) {
      m_disabledAffectors = disabledAffectors;
      onPropertyChange(DISABLED_AFFECTORS_PROPERTY);
    }
  }
  
  public Box getBoundingBox() {
    if (m_geometry != null) {
      Box box = m_geometry.getBoundingBox();
      if (box != null) {
        box.scale(m_scale);
      }
      return box;
    }
    return null;
  }
  
  public Sphere getBoundingSphere() {
    if (m_geometry != null) {
      Sphere sphere = m_geometry.getBoundingSphere();
      if (sphere != null) {
        sphere.scale(m_scale);
      }
      return sphere;
    }
    return null;
  }
  
  public void transform(Matrix4d trans)
  {
    Geometry geometry = getGeometry();
    if (geometry != null) {
      geometry.transform(trans);
    }
  }
  
  public boolean isInProjectionVolumeOf(Camera camera) {
    Sphere boundingSphere = getBoundingSphere();
    if (boundingSphere != null) {
      Matrix4d cameraProjection = camera.getProjection();
      Matrix4d cameraInverse = camera.getInverseAbsoluteTransformation();
      Matrix4d absolute = getAbsoluteTransformation();
      
      Matrix4d m = MathUtilities.multiply(absolute, 
        MathUtilities.multiply(cameraInverse, cameraProjection));
      
      Vector4d centerV4 = MathUtilities.multiply(boundingSphere.getCenter(), 1.0D, m);
      Vector3d centerV3 = MathUtilities.createVector3d(centerV4);
      
      if ((x <= 1.0D) && (x >= -1.0D) && 
        (y <= 1.0D) && (y >= -1.0D))
      {
        if ((z <= 1.0D) && (z >= 0.0D)) {
          return true;
        }
      }
    }
    
    return false;
  }
}
