package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.SpatialRelation;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.Matrix44Property;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class BetterPlaceAnimation extends PointOfViewAnimation
{
  public final SpatialRelationProperty spatialRelation = new SpatialRelationProperty(this, "spatialRelation", SpatialRelation.IN_FRONT_OF);
  public final NumberProperty amount = new NumberProperty(this, "amount", new Double(0.0D));
  
  public BetterPlaceAnimation() {}
  
  public class RuntimeBetterPlaceAnimation extends PointOfViewAnimation.RuntimePointOfViewAnimation { public RuntimeBetterPlaceAnimation() { super(); }
    

    private Box m_subjectBoundingBox;
    private Box m_asSeenByBoundingBox;
    private boolean beginEqualsEnd = false;
    
    protected Vector3d getPositionEnd() {
      if (m_subjectBoundingBox == null) {
        m_subjectBoundingBox = m_subject.getBoundingBox();
        
        if (m_subjectBoundingBox.getMaximum() == null) {
          m_subjectBoundingBox = new Box(m_subject.getPosition(m_subject), m_subject.getPosition(m_subject));
        }
      }
      if (m_asSeenByBoundingBox == null) {
        m_asSeenByBoundingBox = m_asSeenBy.getBoundingBox();
        
        if (m_asSeenByBoundingBox.getMaximum() == null) {
          m_asSeenByBoundingBox = new Box(m_asSeenBy.getPosition(m_asSeenBy), m_asSeenBy.getPosition(m_asSeenBy));
        }
      }
      SpatialRelation sv = spatialRelation.getSpatialRelationValue();
      Vector3d v = sv.getPlaceVector(amount.doubleValue(), m_subjectBoundingBox, m_asSeenByBoundingBox);
      return v;
    }
    

    public void prologue(double t)
    {
      beginEqualsEnd = false;
      
      m_subject = subject.getTransformableValue();
      m_asSeenBy = asSeenBy.getReferenceFrameValue();
      
      if (m_asSeenBy == null) {
        throw new SimulationPropertyException(m_subject.name.getStringValue() + " " + Messages.getString("needs_something_or_someone_to_move_to_"), null, asSeenBy);
      }
      if (m_subject == m_asSeenBy) {
        throw new SimulationPropertyException(m_subject.name.getStringValue() + " " + Messages.getString("can_t_move_to_") + m_subject.name.getStringValue() + ".", getCurrentStack(), asSeenBy);
      }
      
      if (m_subject.isAncestorOf(m_asSeenBy)) {
        throw new SimulationPropertyException(m_subject.name.getStringValue() + " " + Messages.getString("can_t_move_to_a_part_of_itself"), getCurrentStack(), asSeenBy);
      }
      
      Matrix4d pov = asSeenBy.getReferenceFrameValue().getPointOfView();
      
      Vector3d posAbs = getPositionEnd();
      Vector3d curPos = m_subject.getPosition();
      m_subject.setPositionRightNow(posAbs, m_asSeenBy);
      javax.vecmath.Matrix3d paMatrix = m_subject.calculatePointAt(m_asSeenBy, null, new Vector3d(0.0D, 1.0D, 0.0D), null, true);
      m_subject.setPositionRightNow(curPos);
      
      pov.set(paMatrix);
      pov.setRow(3, x, y, z, 1.0D);
      
      pointOfView.set(pov);
      
      if (curPos.equals(posAbs)) {
        beginEqualsEnd = true;
      }
      
      super.prologue(t);
    }
    
    public void update(double t)
    {
      if (!beginEqualsEnd) { super.update(t);
      }
    }
    
    protected boolean affectQuaternion()
    {
      return false;
    }
    
    protected boolean followHermiteCubic() {
      return true;
    }
    
    protected boolean followHermiteCubicOrientation() {
      return true;
    }
  }
}
