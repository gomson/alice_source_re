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
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.util.HowMuch;
import javax.vecmath.Vector3d;

public class SitAnimation extends AbstractBodyPositionAnimation
{
  public final TransformableProperty target = new TransformableProperty(this, "target", null);
  public final DirectionProperty side = new DirectionProperty(this, "side", Direction.FORWARD);
  
  public class RuntimeSitAnimation extends AbstractBodyPositionAnimation.RuntimeAbstractBodyPositionAnimation { public RuntimeSitAnimation() { super(); }
    

    Transformable m_target;
    private Vector3d m_positionBegin;
    private Vector3d m_positionEnd;
    public void prologue(double t)
    {
      super.prologue(t);
      
      m_target = target.getTransformableValue();
      m_positionBegin = m_subject.getPosition(m_subject.getWorld());
      m_positionEnd = null;
      
      if (m_target == null) {
        throw new SimulationPropertyException(m_subject.name.getStringValue() + " " + Messages.getString("needs_something_or_someone_to_sit_on_"), null, target);
      }
      if (m_target == m_subject) {
        throw new SimulationPropertyException(m_subject.name.getStringValue() + " " + Messages.getString("can_t_sit_on_") + m_target.name.getStringValue() + ".", getCurrentStack(), target);
      }
      
      if (m_subject.isAncestorOf(m_target)) {
        throw new SimulationPropertyException(m_subject.name.getStringValue() + " " + Messages.getString("can_t_sit_on_a_part_of_itself"), getCurrentStack(), target);
      }
      
      findLegs();
      getInitialOrientations();
      setFinalOrientations();
    }
    
    public void update(double t)
    {
      super.update(t);
      if (m_positionEnd == null) {
        m_positionEnd = getPositionEnd();
      }
      m_subject.setPositionRightNow(MathUtilities.interpolate(m_positionBegin, m_positionEnd, getPortion(t)), edu.cmu.cs.stage3.alice.core.ReferenceFrame.ABSOLUTE);
      
      setOrientation(rightUpper, rightUpperInitialOrient, rightUpperFinalOrient, getPortion(t));
      if (!m_target.name.getStringValue().equals("ground")) {
        setOrientation(rightLower, rightLowerInitialOrient, rightLowerFinalOrient, getPortion(t));
      }
      setOrientation(leftUpper, leftUpperInitialOrient, leftUpperFinalOrient, getPortion(t));
      if (!m_target.name.getStringValue().equals("ground")) {
        setOrientation(leftLower, leftLowerInitialOrient, leftLowerFinalOrient, getPortion(t));
      }
      adjustHeight();
    }
    

    protected void adjustHeight()
    {
      if ((m_target == null) || (m_target.name.getStringValue().equals("ground")))
      {

        super.adjustHeight();
      }
    }
    

    protected edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion()
    {
      Matrix33 orient = m_target.getOrientationAsAxes(m_target.getWorld());
      
      if (side.getDirectionValue().equals(Direction.BACKWARD)) {
        orient.rotateY(3.141592653589793D);
      } else if (side.getDirectionValue().equals(Direction.LEFT)) {
        orient.rotateY(-1.5707963267948966D);
      } else if (side.getDirectionValue().equals(Direction.RIGHT)) {
        orient.rotateY(1.5707963267948966D);
      }
      
      if (m_target.name.getStringValue().equals("ground")) {
        edu.cmu.cs.stage3.math.Matrix44 currentTrans = m_subject.getTransformation(m_subject.getWorld());
        m_subject.standUpRightNow();
        orient = m_subject.getOrientationAsAxes(m_subject.getWorld());
        m_subject.setTransformationRightNow(currentTrans, m_subject.getWorld());
      }
      return orient.getQuaternion();
    }
    
    protected Vector3d getPositionEnd() {
      if (m_target != null) {
        Vector3d centerTopFace = m_target.getBoundingBox(m_target).getCenterOfTopFace();
        Vector3d endPos = m_target.getBoundingBox(m_target.getWorld()).getCenterOfTopFace();
        Vector3d[] forwardAndUp = m_target.getOrientationAsForwardAndUpGuide(m_target.getWorld());
        
        if (((leftUpper != null) && (leftLower == null)) || (m_target.name.getStringValue().equals("ground"))) {
          double xOffset = m_subject.getBoundingBox().getCenter().x;
          double yOffset = leftUpper.getPosition(m_subject).y;
          double zStart = 0.0D;
          
          double depthSeat = leftUpper.getBoundingBox(leftUpper).getHeight() * 2.0D / 3.0D;
          
          if ((side.getDirectionValue().equals(Direction.BACKWARD)) || (side.getDirectionValue().equals(Direction.FORWARD)))
          {
            if (depthSeat > m_target.getBoundingBox(m_target).getDepth()) {
              depthSeat = m_target.getBoundingBox(m_target).getDepth();
            }
            if (side.getDirectionValue().equals(Direction.BACKWARD)) {
              zStart = m_target.getBoundingBox(m_target).getCenterOfBackFace().z;
              depthSeat *= -1.0D;
            } else {
              zStart = m_target.getBoundingBox(m_target).getCenterOfFrontFace().z;
            }
            
            endPos = new Vector3d(x - xOffset, y - yOffset, zStart - depthSeat);
            endPos = m_target.getPosition(endPos, m_target.getWorld());
          } else if ((side.getDirectionValue().equals(Direction.LEFT)) || (side.getDirectionValue().equals(Direction.RIGHT)))
          {
            if (depthSeat > m_target.getBoundingBox(m_target).getWidth()) {
              depthSeat = m_target.getBoundingBox(m_target).getWidth();
            }
            if (side.getDirectionValue().equals(Direction.RIGHT)) {
              zStart = m_target.getBoundingBox(m_target).getCenterOfRightFace().x;
            } else {
              zStart = m_target.getBoundingBox(m_target).getCenterOfLeftFace().x;
              depthSeat *= -1.0D;
            }
            
            endPos = new Vector3d(zStart - depthSeat, y - yOffset, z - xOffset);
            endPos = m_target.getPosition(endPos, m_target.getWorld());
          }
          
          if (m_target.name.getStringValue().equals("ground")) {
            endPos = m_subject.getPosition(m_subject.getWorld());
            y -= depthSeat;
          }
          return endPos; }
        if ((leftUpper != null) && (leftLower != null)) {
          double depthSeat = leftUpper.getBoundingBox(leftUpper, HowMuch.INSTANCE).getHeight() - leftLower.getBoundingBox(leftLower, HowMuch.INSTANCE).getDepth() / 2.0D;
          depthSeat *= 0.6666666666666666D;
          
          double xOffset = m_subject.getBoundingBox().getCenter().x;
          double yOffset = leftUpper.getPosition(m_subject).y;
          double zStart = 0.0D;
          
          if ((side.getDirectionValue().equals(Direction.BACKWARD)) || (side.getDirectionValue().equals(Direction.FORWARD)))
          {
            if (depthSeat > m_target.getBoundingBox(m_target).getDepth()) {
              depthSeat = m_target.getBoundingBox(m_target).getDepth();
            }
            if (side.getDirectionValue().equals(Direction.BACKWARD)) {
              zStart = m_target.getBoundingBox(m_target).getCenterOfBackFace().z;
              depthSeat *= -1.0D;
            } else {
              zStart = m_target.getBoundingBox(m_target).getCenterOfFrontFace().z;
            }
            
            endPos = new Vector3d(x - xOffset, y - yOffset, zStart - depthSeat);
            endPos = m_target.getPosition(endPos, m_target.getWorld());
          } else if ((side.getDirectionValue().equals(Direction.LEFT)) || (side.getDirectionValue().equals(Direction.RIGHT)))
          {
            if (depthSeat > m_target.getBoundingBox(m_target).getWidth()) {
              depthSeat = m_target.getBoundingBox(m_target).getWidth();
            }
            if (side.getDirectionValue().equals(Direction.RIGHT)) {
              zStart = m_target.getBoundingBox(m_target).getCenterOfRightFace().x;
            } else {
              zStart = m_target.getBoundingBox(m_target).getCenterOfLeftFace().x;
              depthSeat *= -1.0D;
            }
            
            endPos = new Vector3d(zStart - depthSeat, y - yOffset, z - xOffset);
            endPos = m_target.getPosition(endPos, m_target.getWorld());
          }
          
          return endPos;
        }
        
        return endPos;
      }
      return m_positionBegin;
    }
    
    public void setFinalOrientations()
    {
      rightUpperFinalOrient = new Matrix33();
      leftUpperFinalOrient = new Matrix33();
      rightLowerFinalOrient = new Matrix33();
      leftLowerFinalOrient = new Matrix33();
      
      rightUpperFinalOrient.rotateX(-1.5707963267948966D);
      leftUpperFinalOrient.rotateX(-1.5707963267948966D);
      
      rightLowerFinalOrient.rotateX(1.5707963267948966D);
      leftLowerFinalOrient.rotateX(1.5707963267948966D);
    }
  }
  
  public SitAnimation() {}
}
