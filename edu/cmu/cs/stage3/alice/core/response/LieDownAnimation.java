package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;
import javax.vecmath.Vector3d;

public class LieDownAnimation extends BetterStandUpAnimation
{
  public final TransformableProperty target = new TransformableProperty(this, "target", null);
  public final DirectionProperty feetFaceDirection = new DirectionProperty(this, "feetFacingDirection", Direction.FORWARD);
  
  public class RuntimeLieDownAnimation extends BetterStandUpAnimation.RuntimeBetterStandUpAnimation { public RuntimeLieDownAnimation() { super(); }
    Transformable target = null;
    
    private Vector3d m_positionBegin;
    private Vector3d m_positionEnd;
    private Direction m_direction;
    
    public void prologue(double t)
    {
      super.prologue(t);
      target = target.getTransformableValue();
      
      if (target == null) {
        throw new SimulationPropertyException(m_subject.name.getStringValue() + " " + Messages.getString("needs_something_or_someone_to_lie_down_on_"), null, target);
      }
      if (m_subject == target) {
        throw new SimulationPropertyException(m_subject.name.getStringValue() + " " + Messages.getString("can_t_lie_down_on_") + target.name.getStringValue() + ".", getCurrentStack(), target);
      }
      
      if (m_subject.isAncestorOf(target)) {
        throw new SimulationPropertyException(m_subject.name.getStringValue() + Messages.getString("can_t_lie_down_on_a_part_of_itself"), getCurrentStack(), target);
      }
      
      m_positionBegin = m_subject.getPosition(m_subject.getWorld());
      m_direction = feetFaceDirection.getDirectionValue();
    }
    
    public void update(double t)
    {
      super.update(t);
      if (m_positionEnd == null) {
        m_positionEnd = getPositionEnd();
      }
      m_subject.setPositionRightNow(MathUtilities.interpolate(m_positionBegin, m_positionEnd, getPortion(t)), edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);
    }
    
    public void epilogue(double t)
    {
      super.epilogue(t);
      m_positionEnd = null;
    }
    


    protected Vector3d getPositionEnd()
    {
      if (target != null)
      {
        Vector3d endPos = null;
        
        if (target.name.getStringValue().equals("ground")) {
          endPos = m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace();
        } else {
          endPos = target.getBoundingBox(target.getWorld()).getCenterOfTopFace();
        }
        Vector3d[] forwardAndUp = target.getOrientationAsForwardAndUpGuide(target.getWorld());
        Vector3d left = new Vector3d();
        left.cross(forwardAndUp[0], forwardAndUp[1]);
        

        double subjectHeight = m_subject.getHeight();
        
        double zOffset = Math.abs(m_subject.getBoundingBox(m_subject).getCenterOfBackFace().z) - m_subject.getBoundingBox(m_subject).getDepth() * 0.2D;
        
        if (m_direction.equals(Direction.BACKWARD)) {
          endPos = new Vector3d(x - 0x * subjectHeight / 2.0D, y + zOffset + 0y * subjectHeight / 2.0D, z - 0z * subjectHeight / 2.0D);
        } else if (m_direction.equals(Direction.FORWARD)) {
          endPos = new Vector3d(x + 0x * subjectHeight / 2.0D, y + zOffset + 0y * subjectHeight / 2.0D, z + 0z * subjectHeight / 2.0D);
        } else if (m_direction.equals(Direction.LEFT)) {
          endPos = new Vector3d(x - x * subjectHeight / 2.0D, y + zOffset + y * subjectHeight / 2.0D, z - z * subjectHeight / 2.0D);
        } else if (m_direction.equals(Direction.RIGHT)) {
          endPos = new Vector3d(x + x * subjectHeight / 2.0D, y + zOffset + y * subjectHeight / 2.0D, z + z * subjectHeight / 2.0D);
        }
        return endPos;
      }
      return m_positionBegin;
    }
    
    protected Matrix33 getGoalOrientation(Matrix33 targetsOrient, Vector3 goalForward)
    {
      Matrix33 orient = new Matrix33();
      
      Vector3 goalUp = null;
      
      if ((m_direction.equals(Direction.FORWARD)) || (m_direction.equals(Direction.BACKWARD))) {
        goalUp = targetsOrient.getRow(2);
        if (m_direction.equals(Direction.FORWARD)) {
          goalUp.negate();
        }
      } else {
        goalUp = targetsOrient.getRow(0);
        if (m_direction.equals(Direction.LEFT)) {
          goalUp.negate();
        }
      }
      
      orient.setForwardUpGuide(goalForward, goalUp);
      return orient;
    }
    

    protected Quaternion getTargetQuaternion()
    {
      Quaternion quat = m_subject.calculateStandUp(m_subject.getWorld()).getQuaternion();
      

      if ((target == null) || (target.name.getStringValue().equals("ground"))) {
        Matrix33 orient = quat.getMatrix33();
        quat.setMatrix33(getGoalOrientation(orient, quat.getMatrix33().getRow(1)));
      } else {
        Matrix33 orientSubject = quat.getMatrix33();
        Matrix33 orientTarget = target.getOrientationAsAxes(target.getWorld());
        
        quat.setMatrix33(getGoalOrientation(orientTarget, quat.getMatrix33().getRow(1)));
      }
      

      return quat;
    }
    
    protected void adjustHeight()
    {
      if (target == null) {
        super.adjustHeight();
      } else {
        double distanceAboveTarget = 0.0D;
        if (m_subject != null) {
          distanceAboveTarget = m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace().y;
          distanceAboveTarget -= target.getBoundingBox(m_subject.getWorld()).getCenterOfTopFace().y;
          
          m_subject.moveRightNow(Direction.DOWN, distanceAboveTarget, m_subject.getWorld());
        }
      }
    }
  }
  
  public LieDownAnimation() {}
}
