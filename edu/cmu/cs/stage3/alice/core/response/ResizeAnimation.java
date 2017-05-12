package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Dimension;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.DimensionProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.util.HowMuch;
import javax.vecmath.Vector3d;



















public class ResizeAnimation
  extends TransformAnimation
{
  public final DimensionProperty dimension = new DimensionProperty(this, "dimension", Dimension.ALL);
  public final NumberProperty amount = new NumberProperty(this, "amount", new Double(2.0D));
  public final BooleanProperty likeRubber = new BooleanProperty(this, "likeRubber", Boolean.FALSE);
  public final ObjectProperty howMuch = new ObjectProperty(this, "howMuch", HowMuch.INSTANCE_AND_PARTS, HowMuch.class);
  private static final Vector3d VECTOR_111 = new Vector3d(1.0D, 1.0D, 1.0D);
  public class RuntimeResizeAnimation extends TransformAnimation.RuntimeTransformAnimation { public RuntimeResizeAnimation() { super(); }
    
    private Vector3d m_vector;
    private Vector3d m_vectorPrev;
    private HowMuch m_howMuch;
    public void prologue(double t) {
      super.prologue(t);
      m_vectorPrev = new Vector3d(1.0D, 1.0D, 1.0D);
      Dimension dimensionValue = dimension.getDimensionValue();
      double amountValue = amount.doubleValue();
      m_vector = Transformable.calculateResizeScale(dimensionValue, amountValue, likeRubber.booleanValue());
      m_howMuch = ((HowMuch)howMuch.getValue());
    }
    
    public void update(double t) {
      super.update(t);
      Vector3d vectorCurrent = MathUtilities.interpolate(ResizeAnimation.VECTOR_111, m_vector, getPortion(t));
      m_subject.resizeRightNow(MathUtilities.divide(vectorCurrent, m_vectorPrev), m_asSeenBy, m_howMuch);
      m_vectorPrev = vectorCurrent;
    }
  }
  
  public ResizeAnimation() {}
}
