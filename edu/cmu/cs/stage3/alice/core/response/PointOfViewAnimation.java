package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.Matrix44Property;
import edu.cmu.cs.stage3.math.HermiteCubic;
import edu.cmu.cs.stage3.math.Interpolator;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;




















public class PointOfViewAnimation
  extends OrientationAnimation
{
  public final Matrix44Property pointOfView = new Matrix44Property(this, "pointOfView", new Matrix44());
  public final BooleanProperty affectPosition = new BooleanProperty(this, "affectPosition", Boolean.TRUE);
  
  public final BooleanProperty affectQuaternion = new BooleanProperty(this, "affectQuaternion", Boolean.TRUE);
  public final BooleanProperty followHermiteCubic = new BooleanProperty(this, "followHermiteCubic", Boolean.FALSE);
  
  public class RuntimePointOfViewAnimation extends OrientationAnimation.RuntimeOrientationAnimation { public RuntimePointOfViewAnimation() { super(); }
    
    private Matrix44 m_transformationBegin;
    private Matrix44 m_transformationEnd;
    private boolean m_affectPosition;
    private boolean m_affectQuaternion;
    private boolean m_followHermiteCubic;
    private HermiteCubic m_xHermite;
    private HermiteCubic m_yHermite;
    private HermiteCubic m_zHermite;
    public void prologue(double t) {
      m_affectPosition = affectPosition.booleanValue();
      m_affectQuaternion = affectQuaternion.booleanValue();
      super.prologue(t);
      if (m_affectPosition) {
        m_followHermiteCubic = followHermiteCubic.booleanValue();
        m_transformationBegin = m_subject.getTransformation(m_asSeenBy);
        m_transformationEnd = pointOfView.getMatrix44Value();
        if (m_followHermiteCubic) {
          double dx = m_transformationBegin.m30 - m_transformationEnd.m30;
          double dy = m_transformationBegin.m31 - m_transformationEnd.m31;
          double dz = m_transformationBegin.m32 - m_transformationEnd.m32;
          double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
          double s = distance / 2.0D;
          m_xHermite = new HermiteCubic(m_transformationBegin.m30, m_transformationEnd.m30, m_transformationBegin.m20 * s, m_transformationEnd.m20 * s);
          m_yHermite = new HermiteCubic(m_transformationBegin.m31, m_transformationEnd.m31, m_transformationBegin.m21 * s, m_transformationEnd.m21 * s);
          m_zHermite = new HermiteCubic(m_transformationBegin.m32, m_transformationEnd.m32, m_transformationBegin.m22 * s, m_transformationEnd.m22 * s);
        }
      }
    }
    
    protected boolean affectQuaternion() {
      return m_affectQuaternion;
    }
    
    protected Quaternion getTargetQuaternion() {
      return m_transformationEnd.getAxes().getQuaternion();
    }
    
    public void update(double t) {
      if (m_affectPosition) {
        double portion = getPortion(t);
        double z;
        double x;
        double y;
        double z; if (m_followHermiteCubic) {
          double x = m_xHermite.evaluate(portion);
          double y = m_yHermite.evaluate(portion);
          z = m_zHermite.evaluate(portion);
        } else {
          x = Interpolator.interpolate(m_transformationBegin.m30, m_transformationEnd.m30, portion);
          y = Interpolator.interpolate(m_transformationBegin.m31, m_transformationEnd.m31, portion);
          z = Interpolator.interpolate(m_transformationBegin.m32, m_transformationEnd.m32, portion);
        }
        m_subject.setPositionRightNow(x, y, z, m_asSeenBy);
      }
      super.update(t);
    }
    
    public void epilogue(double t) {
      if (m_affectPosition) {
        m_subject.setPositionRightNow(m_transformationEnd.m30, m_transformationEnd.m31, m_transformationEnd.m32, m_asSeenBy);
      }
      super.epilogue(t);
    }
  }
  
  public PointOfViewAnimation() {}
}
