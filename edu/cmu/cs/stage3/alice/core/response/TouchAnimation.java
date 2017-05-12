package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Limb;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.alice.core.property.LimbProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.util.HowMuch;
import javax.vecmath.Vector3d;



public class TouchAnimation
  extends AbstractBodyPositionAnimation
{
  public final TransformableProperty target = new TransformableProperty(this, "target", null);
  public final LimbProperty limb = new LimbProperty(this, "limb", Limb.rightArm);
  public final DirectionProperty sideToTouch = new DirectionProperty(this, "side", Direction.FORWARD);
  public final NumberProperty offset = new NumberProperty(this, "offset", new Double(0.1D));
  public final DirectionProperty offsetDirection = new DirectionProperty(this, "offsetDirection", null);
  
  public class RuntimeTouchAnimation extends AbstractBodyPositionAnimation.RuntimeAbstractBodyPositionAnimation { public RuntimeTouchAnimation() { super(); }
    
    Transformable m_target;
    Quaternion initialQuat = null;
    Quaternion initialLowerQuat = null;
    Quaternion upperTargetQuat = null;
    Quaternion lowerTargetQuat = null;
    
    double upperLimbLength = -1.0D;
    double lowerLimbLength = -1.0D;
    double limbAngle = 0.0D;
    
    Transformable upperLimb = null;
    Transformable lowerLimb = null;
    
    Vector3 initialVector = null;
    Vector3 targetVector = null;
    
    public void prologue(double t)
    {
      super.prologue(t);
      
      if ((limb.getLimbValue().equals(Limb.leftArm)) || (limb.getLimbValue().equals(Limb.rightArm))) {
        findArms();
        if (limb.getLimbValue().equals(Limb.leftArm)) {
          upperLimb = leftUpperArm;
          lowerLimb = leftLowerArm;
        } else {
          upperLimb = rightUpperArm;
          lowerLimb = rightLowerArm;
        }
      } else {
        findLegs();
        if (limb.getLimbValue().equals(Limb.leftLeg)) {
          upperLimb = leftUpper;
          lowerLimb = leftLower;
        } else {
          upperLimb = rightUpper;
          lowerLimb = rightLower;
        }
      }
      
      m_target = target.getTransformableValue();
      m_asSeenBy = asSeenBy.getReferenceFrameValue();
      

      initialQuat = upperLimb.getOrientationAsQuaternion((ReferenceFrame)upperLimb.getParent());
      initialLowerQuat = null;
      if (lowerLimb != null) initialLowerQuat = lowerLimb.getOrientationAsQuaternion((ReferenceFrame)lowerLimb.getParent());
      upperTargetQuat = null;
      lowerTargetQuat = null;
      
      limbAngle = 0.0D;
      upperLimbLength = -1.0D;
      lowerLimbLength = -1.0D;
      
      initialVector = null;
      targetVector = null;
    }
    


    public void update(double t)
    {
      if (getPortion(t) <= 1.0D) {
        if (lowerTargetQuat == null) {
          lowerTargetQuat = getLowerTargetQuaternion();
        }
        if (upperTargetQuat == null) {
          upperTargetQuat = getTargetQuaternion();
        }
        
        Quaternion q = Quaternion.interpolate(initialQuat, upperTargetQuat, getPortion(t));
        q.normalize();
        
        upperLimb.setOrientationRightNow(q, (ReferenceFrame)upperLimb.getParent());
        
        if (lowerLimb != null) {
          Quaternion r = Quaternion.interpolate(initialLowerQuat, lowerTargetQuat, getPortion(t));
          r.normalize();
          
          lowerLimb.setOrientationRightNow(r, (ReferenceFrame)lowerLimb.getParent());
        }
      }
    }
    













    double getLength(Vector3 vec)
    {
      return Math.sqrt(x * x + y * y + z * z);
    }
    
    double getAngle(Vector3 a, Vector3 b)
    {
      Vector3 c = Vector3.subtract(a, b);
      double cLength = getLength(c);
      double aLength = getLength(a);
      double bLength = getLength(b);
      double cosC = (cLength * cLength - aLength * aLength - bLength * bLength) / (-2.0D * aLength * bLength);
      
      return Math.acos(cosC);
    }
    
    protected void setArmLengths() {
      if ((upperLimb != null) && (lowerLimb != null)) {
        Vector3 tmp = Vector3.subtract(lowerLimb.getPosition(lowerLimb), lowerLimb.getBoundingBox(lowerLimb).getCenterOfBottomFace());
        lowerLimbLength = tmp.getLength();
        
        upperLimbLength = (upperLimb.getPosition(upperLimb).y - lowerLimb.getPosition(upperLimb).y);
      } else {
        upperLimbLength = (upperLimb.getPosition(upperLimb).y - upperLimb.getBoundingBox(upperLimb).getCenterOfBottomFace().y);
        lowerLimbLength = 0.0D;
      }
    }
    
    Vector3 getTargetPosition()
    {
      ReferenceFrame asb = m_asSeenBy;
      if (asb == null) { asb = upperLimb;
      }
      Vector3d targPos = m_target.getBoundingBox(asb).getCenter();
      Vector3d offsetDir = null;
      
      HowMuch howMuch = HowMuch.INSTANCE_AND_PARTS;
      
      if (!m_target.getParent().equals(m_target.getWorld())) {
        howMuch = HowMuch.INSTANCE;
      }
      

      if (sideToTouch.getDirectionValue().equals(Direction.BACKWARD)) {
        targPos = m_target.getBoundingBox(m_target, howMuch).getCenterOfBackFace();
        offsetDir = m_target.getOrientationAsAxes(asb).getRow(2);
        offsetDir.negate();
      } else if (sideToTouch.getDirectionValue().equals(Direction.UP)) {
        targPos = m_target.getBoundingBox(m_target, howMuch).getCenterOfTopFace();
        offsetDir = m_target.getOrientationAsAxes(asb).getRow(1);
      } else if (sideToTouch.getDirectionValue().equals(Direction.DOWN)) {
        targPos = m_target.getBoundingBox(m_target, howMuch).getCenterOfBottomFace();
        offsetDir = m_target.getOrientationAsAxes(asb).getRow(1);
        offsetDir.negate();
      } else if (sideToTouch.getDirectionValue().equals(Direction.LEFT)) {
        targPos = m_target.getBoundingBox(m_target, howMuch).getCenterOfLeftFace();
        offsetDir = m_target.getOrientationAsAxes(asb).getRow(0);
      } else if (sideToTouch.getDirectionValue().equals(Direction.RIGHT)) {
        targPos = m_target.getBoundingBox(m_target, howMuch).getCenterOfRightFace();
        offsetDir = m_target.getOrientationAsAxes(asb).getRow(0);
        offsetDir.negate();
      } else {
        targPos = m_target.getBoundingBox(m_target, howMuch).getCenterOfFrontFace();
        offsetDir = m_target.getOrientationAsAxes(asb).getRow(2);
      }
      
      targPos = m_target.getPosition(targPos, upperLimb);
      
      if (offset.doubleValue() != 0.0D) {
        offsetDir.scale(offset.doubleValue());
        targPos.add(offsetDir);
      }
      
      Vector3 targPos2 = new Vector3(x, y, z);
      

      targPos.normalize();
      

      return targPos2;
    }
    



    protected void setVectors()
    {
      Vector3 rightUpperPos = upperLimb.getPosition(upperLimb);
      Vector3 targetPos = getTargetPosition();
      targetVector = Vector3.subtract(targetPos, rightUpperPos);
      

      Vector3d currentDir = null;
      if (lowerLimb == null) {
        currentDir = upperLimb.getBoundingBox(upperLimb).getCenterOfBottomFace();
      } else {
        currentDir = lowerLimb.getBoundingBox(lowerLimb).getCenterOfBottomFace();
        currentDir = lowerLimb.getPosition(currentDir, upperLimb);
      }
      
      initialVector = new Vector3(x, y, z);
    }
    

    protected void setTargetQuaternions()
    {
      Matrix44 initialTrans = null;
      if (lowerLimb != null) initialTrans = lowerLimb.getTransformation((ReferenceFrame)lowerLimb.getParent());
      Matrix44 initialUpperTrans = upperLimb.getTransformation((ReferenceFrame)upperLimb.getParent());
      

      if (lowerLimb != null) lowerLimb.setOrientationRightNow(new Matrix33(), (ReferenceFrame)lowerLimb.getParent());
      upperLimb.setOrientationRightNow(new Matrix33(), (ReferenceFrame)upperLimb.getParent());
      

      setArmLengths();
      setVectors();
      

      double targetDistance = targetVector.length();
      limbAngle = 0.0D;
      

      if (targetDistance < upperLimbLength + lowerLimbLength)
      {

        double cosAngle = (targetDistance * targetDistance - upperLimbLength * upperLimbLength - lowerLimbLength * lowerLimbLength) / (-2.0D * upperLimbLength * lowerLimbLength);
        
        if (cosAngle > 1.0D) cosAngle = 1.0D;
        limbAngle = ((3.141592653589793D - Math.acos(cosAngle)) / 6.283185307179586D);
        



        if ((limb.getLimbValue().equals(Limb.leftArm)) || (limb.getLimbValue().equals(Limb.rightArm))) {
          if (lowerLimb != null) lowerLimb.turnRightNow(Direction.BACKWARD, limbAngle);
          upperLimb.turnRightNow(Direction.FORWARD, limbAngle / 2.0D);
        } else {
          if (lowerLimb != null) lowerLimb.turnRightNow(Direction.FORWARD, limbAngle);
          upperLimb.turnRightNow(Direction.BACKWARD, limbAngle / 2.0D);
        }
      }
      



      double angle = getAngle(initialVector, targetVector);
      
      Vector3 cross = Vector3.crossProduct(initialVector, targetVector);
      cross.normalize();
      upperLimb.rotateRightNow(cross, angle / 6.283185307179586D, (ReferenceFrame)upperLimb.getParent());
      

      targetVector.normalize();
      double turnAmt = 0.0D;
      
      if (limb.getLimbValue().equals(Limb.rightArm)) {
        if (targetVector.x < 0.0D) turnAmt = 0.25D + -0.1D * targetVector.x;
        if (targetVector.z < 0.3D) turnAmt += 1.0D * (0.3D - targetVector.z);
        if (turnAmt > 0.0D) upperLimb.rotateRightNow(targetVector, turnAmt, (ReferenceFrame)upperLimb.getParent());
      } else if (limb.getLimbValue().equals(Limb.leftArm)) {
        if (targetVector.x > 0.0D) turnAmt = 0.25D + -0.1D * targetVector.x;
        if (targetVector.z < 0.5D) turnAmt += 1.0D * (0.3D - targetVector.z);
        if (Math.abs(turnAmt) > 0.0D) { upperLimb.rotateRightNow(targetVector, -1.0D * turnAmt, (ReferenceFrame)upperLimb.getParent());
        }
      }
      
      lowerTargetQuat = null;
      if (lowerLimb != null) lowerTargetQuat = lowerLimb.getOrientationAsQuaternion((ReferenceFrame)lowerLimb.getParent());
      upperTargetQuat = upperLimb.getOrientationAsQuaternion((ReferenceFrame)upperLimb.getParent());
      

      if (lowerLimb != null) lowerLimb.setTransformationRightNow(initialTrans, (ReferenceFrame)lowerLimb.getParent());
      upperLimb.setTransformationRightNow(initialUpperTrans, (ReferenceFrame)upperLimb.getParent());
    }
    
    public Quaternion getLowerTargetQuaternion()
    {
      if ((lowerLimb != null) && (lowerTargetQuat == null)) {
        setTargetQuaternions();
      }
      return lowerTargetQuat;
    }
    
    public Quaternion getTargetQuaternion()
    {
      if (upperTargetQuat == null) {
        setTargetQuaternions();
      }
      return upperTargetQuat;
    }
  }
  
  public TouchAnimation() {}
}
