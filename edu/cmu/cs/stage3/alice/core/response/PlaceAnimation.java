package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.SpatialRelation;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import javax.vecmath.Vector3d;
















public class PlaceAnimation
  extends AbstractPositionAnimation
{
  public final SpatialRelationProperty spatialRelation = new SpatialRelationProperty(this, "spatialRelation", SpatialRelation.IN_FRONT_OF);
  public final NumberProperty amount = new NumberProperty(this, "amount", new Double(0.0D));
  public class RuntimePlaceAnimation extends AbstractPositionAnimation.RuntimeAbstractPositionAnimation { public RuntimePlaceAnimation() { super(); }
    
    private Box m_subjectBoundingBox;
    private Box m_asSeenByBoundingBox;
    protected Vector3d getPositionBegin() {
      return m_subject.getPosition(ReferenceFrame.ABSOLUTE);
    }
    
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
      return m_asSeenBy.getPosition(v, ReferenceFrame.ABSOLUTE);
    }
    
    public void prologue(double t) {
      super.prologue(t);
      if (spatialRelation.getSpatialRelationValue() == null) {
        throw new SimulationPropertyException(Messages.getString("spatial_relation_value_must_not_be_null_"), null, spatialRelation);
      }
      if (amount.getValue() == null) {
        throw new SimulationPropertyException(Messages.getString("amount_value_must_not_be_null_"), null, amount);
      }
    }
  }
  
  public PlaceAnimation() {}
}
