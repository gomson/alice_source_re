package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.SpatialRelation;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Matrix33;
import javax.vecmath.Vector3d;



public class CloseUpAnimation
  extends AbstractPositionAnimation
{
  public final Vector3Property position = new Vector3Property(this, "position", new Vector3d(0.0D, 0.0D, 0.0D));
  public final SpatialRelationProperty spatialRelation = new SpatialRelationProperty(this, "spatialRelation", SpatialRelation.IN_FRONT_OF);
  public final NumberProperty amount = new NumberProperty(this, "amount", new Double(1.0D));
  private Matrix33 m_cameraEndOrientation;
  private Matrix33 m_cameraBeginOrientation;
  
  public CloseUpAnimation() {}
  
  public static CloseUpAnimation createCloseUpAnimation(Object subject, Object spatialRelation, Object amount, Object asSeenBy) { CloseUpAnimation closeUpAnimation = new CloseUpAnimation();
    subject.set(subject);
    spatialRelation.set(spatialRelation);
    amount.set(amount);
    asSeenBy.set(asSeenBy);
    return closeUpAnimation; }
  
  public class RuntimeCloseUpAnimation extends AbstractPositionAnimation.RuntimeAbstractPositionAnimation { private Box m_subjectBoundingBox;
    
    public RuntimeCloseUpAnimation() { super(); }
    
    private Box m_asSeenByBoundingBox;
    private double m_amount;
    private double m_cameraHeight;
    protected Vector3d getPositionBegin()
    {
      m_cameraBeginOrientation = m_subject.getOrientationAsAxes(m_asSeenBy);
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
      Matrix33 cameraEndOrientation = m_asSeenBy.getOrientationAsAxes(m_asSeenBy);
      

      if (sv.equals(SpatialRelation.LEFT_OF)) {
        cameraEndOrientation.setForwardUpGuide(new Vector3d(1.0D, 0.0D, 0.0D), cameraEndOrientation.getRow(1));
      } else if (sv.equals(SpatialRelation.RIGHT_OF)) {
        cameraEndOrientation.setForwardUpGuide(new Vector3d(-1.0D, 0.0D, 0.0D), cameraEndOrientation.getRow(1));
      } else if (sv.equals(SpatialRelation.BEHIND_RIGHT_OF)) {
        cameraEndOrientation.setForwardUpGuide(new Vector3d(-0.7071068D, 0.0D, 0.7071068D), cameraEndOrientation.getRow(1));
      } else if (sv.equals(SpatialRelation.BEHIND_LEFT_OF)) {
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.7071068D, 0.0D, 0.7071068D), cameraEndOrientation.getRow(1));
      } else if (sv.equals(SpatialRelation.FRONT_RIGHT_OF)) {
        cameraEndOrientation.setForwardUpGuide(new Vector3d(-0.7071068D, 0.0D, -0.7071068D), cameraEndOrientation.getRow(1));
      } else if (sv.equals(SpatialRelation.FRONT_LEFT_OF)) {
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.7071068D, 0.0D, -0.7071068D), cameraEndOrientation.getRow(1));

      }
      else if (sv.equals(SpatialRelation.IN_FRONT_OF)) {
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, 0.0D, -1.0D), cameraEndOrientation.getRow(1));
      } else if (sv.equals(SpatialRelation.BEHIND)) {
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, 0.0D, 1.0D), cameraEndOrientation.getRow(1));
      } else if (sv.equals(SpatialRelation.ABOVE)) {
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, -1.0D, 0.0D), new Vector3d(-1.0D, 0.0D, 0.0D));
      } else if (sv.equals(SpatialRelation.BELOW)) {
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, 1.0D, 0.0D), new Vector3d(-1.0D, 0.0D, 0.0D));
      }
      

      m_cameraEndOrientation = cameraEndOrientation;
      m_amount = (amount.doubleValue() * m_asSeenBy.getHeight() * 3.0D);
      Vector3d v = sv.getPlaceVector(m_amount, m_subjectBoundingBox, m_asSeenByBoundingBox);
      
      double halfViewable = m_asSeenBy.getHeight() * 1.5D * amount.doubleValue() / 2.0D;
      double startingHeight = (1.0D - amount.doubleValue()) * m_asSeenByBoundingBox.getHeight();
      m_cameraHeight = (startingHeight + halfViewable + m_asSeenByBoundingBox.getMinimum().y);
      y = m_cameraHeight;
      return m_asSeenBy.getPosition(v, ReferenceFrame.ABSOLUTE);
    }
    
    public void prologue(double t)
    {
      m_asSeenBy = asSeenBy.getReferenceFrameValue();
      m_amount = amount.getNumberValue().doubleValue();
      if (m_asSeenBy == null) {
        throw new SimulationPropertyException(Messages.getString("character_value_must_not_be_null_"), getCurrentStack(), asSeenBy);
      }
      
      super.prologue(t);
      
      if (m_subject == null) {
        throw new SimulationPropertyException(Messages.getString("subject_value_must_not_be_null_"), getCurrentStack(), subject);
      }
      if (m_subject == m_asSeenBy) {
        throw new SimulationPropertyException(Messages.getString("subject_and_character_values_must_not_be_the_same_"), getCurrentStack(), subject);
      }
      if (m_amount < 0.0D) {
        throw new SimulationPropertyException(Messages.getString("amount_must_be_greater_than_0"), getCurrentStack(), amount);
      }
    }
    
    public void update(double t)
    {
      super.update(t);
      
      Matrix33 nextOrient = Matrix33.interpolate(m_cameraBeginOrientation, m_cameraEndOrientation, getPortion(t));
      m_subject.setOrientationRightNow(nextOrient, m_asSeenBy);
    }
  }
}
