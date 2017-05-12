package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.SpatialRelation;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Matrix33;
import javax.vecmath.Vector3d;






public class TwoShotAnimation
  extends AbstractPositionAnimation
{
  public final SpatialRelationProperty spatialRelation = new SpatialRelationProperty(this, "spatialRelation", SpatialRelation.RIGHT_OF);
  public final ReferenceFrameProperty asSeenBy2 = new ReferenceFrameProperty(this, "asSeenBy2", null);
  public final NumberProperty amount = new NumberProperty(this, "amount", new Double(1.0D));
  
  private double angleToRotate = 0.5235987755982988D;
  protected ReferenceFrame m_asSeenBy2;
  
  public class RuntimeTwoShotAnimation extends AbstractPositionAnimation.RuntimeAbstractPositionAnimation {
    public RuntimeTwoShotAnimation() { super(); }
    












    public void prologue(double t)
    {
      m_asSeenBy = asSeenBy.getReferenceFrameValue();
      if (m_asSeenBy == null) {
        throw new SimulationPropertyException(Messages.getString("first_character_value_must_not_be_null_"), null, asSeenBy);
      }
      
      super.prologue(t);
      m_asSeenBy2 = asSeenBy2.getReferenceFrameValue();
      m_amount = amount.getNumberValue().doubleValue();
      

      if (m_asSeenBy2 == null) {
        throw new SimulationPropertyException(Messages.getString("second_character_value_must_not_be_null_"), getCurrentStack(), asSeenBy2);
      }
      if (m_subject == null) {
        throw new SimulationPropertyException(Messages.getString("subject_value_must_not_be_null_"), getCurrentStack(), subject);
      }
      if (m_asSeenBy2 == m_asSeenBy) {
        throw new SimulationPropertyException(Messages.getString("first_and_second_characters_must_be_different"), getCurrentStack(), asSeenBy2);
      }
      if ((m_subject == m_asSeenBy) || (m_subject == m_asSeenBy2)) {
        throw new SimulationPropertyException(Messages.getString("subject_value_cannot_be_the_same_as_either_character"), getCurrentStack(), subject);
      }
      if (m_amount < 0.0D) {
        throw new SimulationPropertyException(Messages.getString("amount_must_be_greater_than_0"), getCurrentStack(), amount);
      }
      

      m_asSeenBy2.addAbsoluteTransformationListener(this);
    }
    
    protected Box getBoundingBox(ReferenceFrame ref, ReferenceFrame asSeenBy) {
      Box bbox = ref.getBoundingBox(asSeenBy);
      if (bbox.getMaximum() == null) {
        bbox = new Box(ref.getPosition(asSeenBy), ref.getPosition(asSeenBy));
      }
      return bbox;
    }
    
    protected double calculateB(double x0, double z0, double dx, double dz)
    {
      return z0 - dz / dx * x0;
    }
    

    protected double calculateXIntersect(double dxLeft, double dzLeft, double bLeft, double bForward)
    {
      return (bForward - bLeft) / (dzLeft / dxLeft + dxLeft / dzLeft);
    }
    

    protected double calculateZIntersect(double dxLeft, double dzLeft, double bLeft, double bForward)
    {
      return (dxLeft / dzLeft * bLeft + dzLeft / dxLeft * bForward) / (dxLeft / dzLeft + dzLeft / dxLeft);
    }
    
    protected double calculateDistance(double x1, double z1, double x2, double z2) {
      return Math.sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));
    }
    
    protected Vector3d getPositionBegin()
    {
      m_cameraBeginOrientation = m_subject.getOrientationAsAxes(m_asSeenBy);
      return m_subject.getPosition(ReferenceFrame.ABSOLUTE);
    }
    


    protected Vector3d getPositionEnd()
    {
      if (m_subjectBoundingBox == null) m_subjectBoundingBox = getBoundingBox(m_subject, m_subject);
      if (m_asSeenByBoundingBox == null) m_asSeenByBoundingBox = getBoundingBox(m_asSeenBy, m_asSeenBy);
      if (m_asSeenBy2BoundingBox == null) m_asSeenBy2BoundingBox = getBoundingBox(m_asSeenBy2, m_asSeenBy);
      m_asSeenByBoundingBox.union(m_asSeenBy2BoundingBox);
      
      SpatialRelation sv = spatialRelation.getSpatialRelationValue();
      Matrix33 cameraEndOrientation = m_asSeenBy.getOrientationAsAxes(m_asSeenBy);
      Vector3d cameraEndPos = new Vector3d();
      

      if (sv.equals(SpatialRelation.LEFT_OF)) {
        cameraEndPos = sv.getPlaceVector(1.0D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        cameraEndOrientation.setForwardUpGuide(new Vector3d(1.0D, 0.0D, 0.0D), cameraEndOrientation.getRow(1));
      } else if (sv.equals(SpatialRelation.RIGHT_OF)) {
        cameraEndPos = sv.getPlaceVector(1.0D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        cameraEndOrientation.setForwardUpGuide(new Vector3d(-1.0D, 0.0D, 0.0D), cameraEndOrientation.getRow(1));
      } else if (sv.equals(SpatialRelation.BEHIND_RIGHT_OF)) {
        cameraEndPos = SpatialRelation.BEHIND.getPlaceVector(1.0D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, 0.0D, 1.0D), cameraEndOrientation.getRow(1));
        cameraEndOrientation.rotateY(-1.0D * angleToRotate);
      } else if (sv.equals(SpatialRelation.BEHIND_LEFT_OF)) {
        cameraEndPos = SpatialRelation.BEHIND.getPlaceVector(1.0D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, 0.0D, 1.0D), cameraEndOrientation.getRow(1));
        cameraEndOrientation.rotateY(angleToRotate);
      } else if (sv.equals(SpatialRelation.FRONT_RIGHT_OF)) {
        cameraEndPos = SpatialRelation.IN_FRONT_OF.getPlaceVector(1.0D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, 0.0D, -1.0D), cameraEndOrientation.getRow(1));
        cameraEndOrientation.rotateY(angleToRotate);
      } else if (sv.equals(SpatialRelation.FRONT_LEFT_OF)) {
        cameraEndPos = SpatialRelation.IN_FRONT_OF.getPlaceVector(1.0D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, 0.0D, -1.0D), cameraEndOrientation.getRow(1));
        cameraEndOrientation.rotateY(-1.0D * angleToRotate);

      }
      else if (sv.equals(SpatialRelation.IN_FRONT_OF)) {
        cameraEndPos = sv.getPlaceVector(1.0D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, 0.0D, -1.0D), cameraEndOrientation.getRow(1));
      } else if (sv.equals(SpatialRelation.BEHIND)) {
        cameraEndPos = sv.getPlaceVector(1.0D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, 0.0D, 1.0D), cameraEndOrientation.getRow(1));
      } else if (sv.equals(SpatialRelation.ABOVE)) {
        cameraEndPos = sv.getPlaceVector(1.0D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, -1.0D, 0.0D), new Vector3d(-1.0D, 0.0D, 0.0D));
      } else if (sv.equals(SpatialRelation.BELOW)) {
        cameraEndPos = sv.getPlaceVector(1.0D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        cameraEndOrientation.setForwardUpGuide(new Vector3d(0.0D, 1.0D, 0.0D), new Vector3d(-1.0D, 0.0D, 0.0D));
      }
      







      if ((sv.equals(SpatialRelation.BEHIND_LEFT_OF)) || (sv.equals(SpatialRelation.BEHIND_RIGHT_OF)) || (sv.equals(SpatialRelation.FRONT_LEFT_OF)) || (sv.equals(SpatialRelation.FRONT_RIGHT_OF)))
      {
        Vector3d leftVector = cameraEndOrientation.getRow(0);
        Vector3d forwardVector = cameraEndOrientation.getRow(2);
        
        double bLeft = calculateB(x, z, x, z);
        

        Vector3d boxMin = m_asSeenByBoundingBox.getMinimum();
        double bForward = calculateB(x, z, x, z);
        double minX = calculateXIntersect(x, z, bLeft, bForward);
        double minZ = calculateZIntersect(x, z, bLeft, bForward);
        

        Vector3d boxMax = m_asSeenByBoundingBox.getMaximum();
        bForward = calculateB(x, z, x, z);
        double maxX = calculateXIntersect(x, z, bLeft, bForward);
        double maxZ = calculateZIntersect(x, z, bLeft, bForward);
        


        x = (minX + (maxX - minX) / 2.0D);
        z = (minZ + (maxZ - minZ) / 2.0D);
        

        double distance = calculateDistance(minX, minZ, maxX, maxZ);
        double largestDimSize = 0.0D;
        largestDimSize = amount.doubleValue() * m_asSeenByBoundingBox.getHeight();
        if (distance > largestDimSize) { largestDimSize = distance;
        }
        x += -2.5D * largestDimSize * x;
        z += -2.5D * largestDimSize * z;
        

        double halfViewable = m_asSeenByBoundingBox.getHeight() * amount.doubleValue() * 1.5D / 2.0D;
        double startingHeight = (1.0D - amount.doubleValue()) * m_asSeenByBoundingBox.getHeight();
        m_cameraHeight = (startingHeight + halfViewable + m_asSeenByBoundingBox.getMinimum().y);
        y = m_cameraHeight;
      }
      else if ((sv.equals(SpatialRelation.LEFT_OF)) || (sv.equals(SpatialRelation.RIGHT_OF)))
      {
        double largestDimSize = m_asSeenByBoundingBox.getDepth();
        if (amount.doubleValue() * m_asSeenByBoundingBox.getHeight() > largestDimSize) {
          largestDimSize = amount.doubleValue() * m_asSeenByBoundingBox.getHeight();
        }
        cameraEndPos = sv.getPlaceVector(largestDimSize * 2.5D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        
        double halfViewable = m_asSeenByBoundingBox.getHeight() * amount.doubleValue() * 1.5D / 2.0D;
        double startingHeight = (1.0D - amount.doubleValue()) * m_asSeenByBoundingBox.getHeight();
        m_cameraHeight = (startingHeight + halfViewable + m_asSeenByBoundingBox.getMinimum().y);
        
        y = m_cameraHeight;
        z = m_asSeenByBoundingBox.getCenter().z;
      } else if ((sv.equals(SpatialRelation.IN_FRONT_OF)) || (sv.equals(SpatialRelation.BEHIND)))
      {
        double largestDimSize = m_asSeenByBoundingBox.getWidth();
        if (amount.doubleValue() * m_asSeenByBoundingBox.getHeight() > largestDimSize) {
          largestDimSize = amount.doubleValue() * m_asSeenByBoundingBox.getHeight();
        }
        cameraEndPos = sv.getPlaceVector(largestDimSize * 3.0D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        
        double halfViewable = m_asSeenByBoundingBox.getHeight() * 1.5D * amount.doubleValue() / 2.0D;
        double startingHeight = (1.0D - amount.doubleValue()) * m_asSeenByBoundingBox.getHeight();
        m_cameraHeight = (startingHeight + halfViewable + m_asSeenByBoundingBox.getMinimum().y);
        
        y = m_cameraHeight;
        x = m_asSeenByBoundingBox.getCenter().x;
      } else if ((sv.equals(SpatialRelation.ABOVE)) || (sv.equals(SpatialRelation.BELOW)))
      {
        double largestDimSize = m_asSeenByBoundingBox.getDepth();
        if (m_asSeenByBoundingBox.getWidth() > largestDimSize) {
          largestDimSize = m_asSeenByBoundingBox.getWidth();
        }
        cameraEndPos = sv.getPlaceVector(largestDimSize * 2.2D, m_subjectBoundingBox, m_asSeenByBoundingBox);
        
        double halfViewable = m_asSeenByBoundingBox.getDepth() / 2.0D;
        m_cameraHeight = (halfViewable + m_asSeenByBoundingBox.getMinimum().y);
        
        z = m_cameraHeight;
        x = m_asSeenByBoundingBox.getCenter().x;
      }
      

      m_cameraEndOrientation = cameraEndOrientation;
      return m_asSeenBy.getPosition(cameraEndPos, ReferenceFrame.ABSOLUTE);
    }
    




    protected ReferenceFrame m_asSeenBy2;
    



    private Box m_subjectBoundingBox;
    



    private Box m_asSeenByBoundingBox;
    



    private Box m_asSeenBy2BoundingBox;
    



    private double m_amount;
    



    private Matrix33 m_cameraEndOrientation;
    



    private Matrix33 m_cameraBeginOrientation;
    


    private double m_cameraHeight;
    


    public void update(double t)
    {
      super.update(t);
      
      Matrix33 nextOrient = Matrix33.interpolate(m_cameraBeginOrientation, m_cameraEndOrientation, getPortion(t));
      m_subject.setOrientationRightNow(nextOrient, m_asSeenBy);
    }
    



    public void epilogue(double t)
    {
      super.epilogue(t);
      if (m_asSeenBy2 != null) m_asSeenBy2.removeAbsoluteTransformationListener(this);
    }
  }
  
  public TwoShotAnimation() {}
}
