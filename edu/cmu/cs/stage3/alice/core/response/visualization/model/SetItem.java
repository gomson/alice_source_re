package edu.cmu.cs.stage3.alice.core.response.visualization.model;

import edu.cmu.cs.stage3.math.Matrix44;

public class SetItem extends ModelVisualizationWithItemAnimation {
  public SetItem() {}
  
  public class RuntimeSetItem extends ModelVisualizationWithItemAnimation.RuntimeModelVisualizationWithItemAnimation { private edu.cmu.cs.stage3.math.Quaternion m_quaternion0;
    
    public RuntimeSetItem() { super(); }
    
    private edu.cmu.cs.stage3.math.Quaternion m_quaternion1;
    private edu.cmu.cs.stage3.math.HermiteCubic m_xHermite;
    private edu.cmu.cs.stage3.math.HermiteCubic m_yHermite;
    private edu.cmu.cs.stage3.math.HermiteCubic m_zHermite;
    private edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization m_subject;
    private edu.cmu.cs.stage3.alice.core.Model m_value;
    public void prologue(double t)
    {
      m_subject = subject.getModelVisualizationValue();
      m_value = item.getModelValue();
      edu.cmu.cs.stage3.alice.core.Model prev = m_subject.getItem();
      if ((prev != null) && (prev != m_value)) {
        visualization.set(null);
      }
      if (m_value != null)
      {

        m_value.visualization.set(null);
        
        Matrix44 transformation0 = m_value.getTransformation(m_subject);
        Matrix44 transformation1 = new Matrix44(m_subject.getTransformationFor(m_value));
        m_quaternion0 = transformation0.getAxes().getQuaternion();
        m_quaternion1 = transformation1.getAxes().getQuaternion();
        double dx = m30 - m30;
        double dy = m31 - m31;
        double dz = m32 - m32;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double s = distance / 2.0D;
        m_xHermite = new edu.cmu.cs.stage3.math.HermiteCubic(m30, m30, m20 * s, m20 * s);
        m_yHermite = new edu.cmu.cs.stage3.math.HermiteCubic(m31, m31, m21 * s, m21 * s);
        m_zHermite = new edu.cmu.cs.stage3.math.HermiteCubic(m32, m32, m22 * s, m22 * s);
      }
      super.prologue(t);
    }
    
    public void update(double t) {
      super.update(t);
      if (m_value != null) {
        double portion = getPortion(t);
        double x = m_xHermite.evaluate(portion);
        double y = m_yHermite.evaluate(portion);
        double z = m_zHermite.evaluate(portion);
        m_value.setPositionRightNow(x, y, z, m_subject);
        edu.cmu.cs.stage3.math.Quaternion q = edu.cmu.cs.stage3.math.Quaternion.interpolate(m_quaternion0, m_quaternion1, getPortion(t));
        m_value.setOrientationRightNow(q, m_subject);
      }
    }
    
    public void epilogue(double t) {
      super.epilogue(t);
      m_subject.setItem(m_value);
    }
  }
}
