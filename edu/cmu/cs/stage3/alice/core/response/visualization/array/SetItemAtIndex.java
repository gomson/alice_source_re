package edu.cmu.cs.stage3.alice.core.response.visualization.array;

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.math.HermiteCubic;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;

public class SetItemAtIndex extends ArrayVisualizationWithItemAnimation
{
  public final NumberProperty index = new NumberProperty(this, "index", new Integer(-1));
  public class RuntimeSetValue extends ArrayVisualizationWithItemAnimation.RuntimeArrayVisualizationWithItemAnimation { public RuntimeSetValue() { super(); }
    
    private Quaternion m_quaternion0;
    private Quaternion m_quaternion1;
    private HermiteCubic m_xHermite;
    private HermiteCubic m_yHermite;
    private HermiteCubic m_zHermite;
    private edu.cmu.cs.stage3.alice.core.visualization.ArrayOfModelsVisualization m_subject;
    private Model m_item;
    public void prologue(double t)
    {
      super.prologue(t);
      m_subject = subject.getArrayOfModelsVisualizationValue();
      m_item = item.getModelValue();
      if (m_item != null)
      {

        m_item.visualization.set(null);
        
        Matrix44 transformation0 = m_item.getTransformation(m_subject);
        Matrix44 transformation1 = new Matrix44(m_subject.getTransformationFor(m_item, index.intValue()));
        m_quaternion0 = transformation0.getAxes().getQuaternion();
        m_quaternion1 = transformation1.getAxes().getQuaternion();
        double dx = m30 - m30;
        double dy = m31 - m31;
        double dz = m32 - m32;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double s = distance / 2.0D;
        m_xHermite = new HermiteCubic(m30, m30, m20 * s, m20 * s);
        m_yHermite = new HermiteCubic(m31, m31, m21 * s, m21 * s);
        m_zHermite = new HermiteCubic(m32, m32, m22 * s, m22 * s);
      }
    }
    
    public void update(double t) {
      super.update(t);
      if (m_item != null) {
        double portion = getPortion(t);
        double x = m_xHermite.evaluate(portion);
        double y = m_yHermite.evaluate(portion);
        double z = m_zHermite.evaluate(portion);
        m_item.setPositionRightNow(x, y, z, m_subject);
        Quaternion q = Quaternion.interpolate(m_quaternion0, m_quaternion1, getPortion(t));
        m_item.setOrientationRightNow(q, m_subject);
      }
    }
    
    public void epilogue(double t) {
      super.epilogue(t);
      
      int indexValue = index.intValue();
      Model[] array = (Model[])m_subject.getItems().clone();
      for (int i = 0; i < array.length; i++) {
        if (array[i] == m_item) {
          array[i] = null;
        }
      }
      array[indexValue] = m_item;
      m_subject.setItems(array);
    }
  }
  
  public SetItemAtIndex() {}
}
