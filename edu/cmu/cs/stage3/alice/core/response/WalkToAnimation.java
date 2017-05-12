package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.SpatialRelation;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.SpatialRelationProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.HermiteCubic;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class WalkToAnimation extends AbstractWalkAnimation
{
  public final SpatialRelationProperty spatialRelation = new SpatialRelationProperty(this, "spatialRelation", SpatialRelation.IN_FRONT_OF);
  public final NumberProperty amount = new NumberProperty(this, "amount", new Double(1.0D));
  public final ReferenceFrameProperty asSeenBy = new ReferenceFrameProperty(this, "target", null);
  
  public class RuntimeWalkToAnimation extends AbstractWalkAnimation.RuntimeAbstractWalkAnimation { public RuntimeWalkToAnimation() { super(); }
    

    private Box m_subjectBoundingBox;
    
    private Box m_asSeenByBoundingBox;
    
    protected ReferenceFrame m_asSeenBy;
    private Matrix44 m_transformationBegin;
    private Matrix44 m_transformationEnd;
    private HermiteCubic m_xHermite;
    private HermiteCubic m_yHermite;
    private HermiteCubic m_zHermite;
    private boolean m_affectPosition;
    private boolean m_affectQuaternion;
    private boolean beginEqualsEnd = false;
    
    double stepLength = -1.0D;
    double numberOfSteps = -1.0D;
    double currentPos = 0.0D;
    double timePerStep = -1.0D;
    
    private boolean done = false;
    
    protected Vector3d getPositionEnd() {
      if (m_subjectBoundingBox == null) {
        m_subjectBoundingBox = subject.getBoundingBox();
        
        if (m_subjectBoundingBox.getMaximum() == null) {
          m_subjectBoundingBox = new Box(subject.getPosition(subject), subject.getPosition(subject));
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
    
    protected double getValueAtTime(double t) {
      double ft = m_xHermite.evaluateDerivative(t);
      double ht = m_zHermite.evaluateDerivative(t);
      
      return Math.sqrt(ft * ft + ht * ht);
    }
    
    protected double getActualStepLength()
    {
      double distanceToMove = getPathLength();
      
      if (stepLength == -1.0D) {
        stepLength = getStepLength();
        if (stepLength == 0.0D) {
          stepLength = 1.0D;
        }
      }
      
      if (numberOfSteps == -1.0D) {
        numberOfSteps = Math.round(distanceToMove / stepLength);
      }
      
      return distanceToMove / numberOfSteps;
    }
    

    public double getTimeRemaining(double t)
    {
      double walkTime = duration.doubleValue();
      if (Double.isNaN(walkTime)) {
        walkTime = numberOfSteps / stepSpeed.doubleValue();
      }
      return walkTime - getTimeElapsed(t);
    }
    
    protected double getPathLength() {
      double x1s = getValueAtTime(0.0D) + getValueAtTime(1.0D);
      
      double h = 0.1D;
      
      double startT = h;
      double x4s = 0.0D;
      
      while (startT < 1.0D) {
        x4s += getValueAtTime(startT);
        startT += 2.0D * h;
      }
      
      startT = 2.0D * h;
      double x2s = 0.0D;
      
      while (startT < 1.0D) {
        x2s += getValueAtTime(startT);
        startT += 2.0D * h;
      }
      

      return (x1s + 4.0D * x4s + 2.0D * x2s) * (h / 3.0D);
    }
    

    public void prologue(double t)
    {
      beginEqualsEnd = false;
      done = false;
      
      subject = subject.getTransformableValue();
      m_asSeenBy = asSeenBy.getReferenceFrameValue();
      
      Matrix44 asSeenByTrans = m_asSeenBy.getTransformation(subject.getWorld());
      ((Transformable)m_asSeenBy).standUpRightNow(subject.getWorld());
      
      m_transformationBegin = subject.getTransformation(m_asSeenBy);
      
      if (m_asSeenBy == null) {
        throw new SimulationPropertyException(subject.name.getStringValue() + " " + edu.cmu.cs.stage3.lang.Messages.getString("needs_something_or_someone_to_walk_to_"), null, asSeenBy);
      }
      if (subject == m_asSeenBy) {
        throw new SimulationPropertyException(subject.name.getStringValue() + " " + edu.cmu.cs.stage3.lang.Messages.getString("can_t_walk_to_") + subject.name.getStringValue() + ".", getCurrentStack(), asSeenBy);
      }
      
      if (subject.isAncestorOf(m_asSeenBy)) {
        throw new SimulationPropertyException(subject.name.getStringValue() + " " + edu.cmu.cs.stage3.lang.Messages.getString("can_t_walk_to_a_part_of_itself"), getCurrentStack(), asSeenBy);
      }
      

      Vector3d posAbs = getPositionEnd();
      Vector3d curPos = subject.getPosition();
      subject.setPositionRightNow(posAbs, m_asSeenBy);
      

      javax.vecmath.Matrix3d paMatrix = subject.calculatePointAt(m_asSeenBy, null, new Vector3d(0.0D, 1.0D, 0.0D), m_asSeenBy, true);
      subject.setPositionRightNow(curPos);
      
      Matrix4d pov = asSeenBy.getReferenceFrameValue().getPointOfView();
      pov.set(paMatrix);
      pov.setRow(3, x, y, z, 1.0D);
      
      m_transformationEnd = new Matrix44(pov);
      
      double dx = m_transformationBegin.m30 - m_transformationEnd.m30;
      double dy = m_transformationBegin.m31 - m_transformationEnd.m31;
      double dz = m_transformationBegin.m32 - m_transformationEnd.m32;
      double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
      double s = distance;
      
      m_xHermite = new HermiteCubic(m_transformationBegin.m30, m_transformationEnd.m30, m_transformationBegin.m20 * s, m_transformationEnd.m20 * s);
      m_yHermite = new HermiteCubic(m_transformationBegin.m31, m_transformationEnd.m31, m_transformationBegin.m21 * s, m_transformationEnd.m21 * s);
      m_zHermite = new HermiteCubic(m_transformationBegin.m32, m_transformationEnd.m32, m_transformationBegin.m22 * s, m_transformationEnd.m22 * s);
      
      super.prologue(t);
      getActualStepLength();
      
      ((Transformable)m_asSeenBy).setTransformationRightNow(asSeenByTrans, subject.getWorld());
    }
    

    public void update(double t)
    {
      if (getTimeRemaining(t) > 0.0D)
      {
        Matrix44 asSeenByTrans = m_asSeenBy.getTransformation(subject.getWorld());
        ((Transformable)m_asSeenBy).standUpRightNow(subject.getWorld());
        
        double portion = getTimeElapsed(t) / (getTimeElapsed(t) + getTimeRemaining(t));
        
        if (portion <= 1.0D)
        {







          double x = m_xHermite.evaluate(portion);
          double y = m_yHermite.evaluate(portion);
          double z = m_zHermite.evaluate(portion);
          
          subject.setPositionRightNow(x, y, z, m_asSeenBy);
          

          double dx = m_xHermite.evaluateDerivative(portion);
          
          double dy = 0.0D;
          double dz = m_zHermite.evaluateDerivative(portion);
          

          if ((dx != 0.0D) || (dy != 0.0D) || (dz != 0.0D)) {
            Matrix33 orient = new Matrix33();
            orient.setForwardUpGuide(new Vector3d(dx, dy, dz), new Vector3d(0.0D, 1.0D, 0.0D));
            
            subject.setOrientationRightNow(orient, m_asSeenBy);
          }
          



          if (timePerStep == -1.0D) {
            if (!Double.isNaN(duration.doubleValue())) {
              timePerStep = (duration.doubleValue() / numberOfSteps);
            } else {
              timePerStep = (1.0D / stepSpeed.doubleValue());
            }
          }
          

          int stepNumber = (int)Math.ceil(getTimeElapsed(t) * (1.0D / timePerStep)) - 1;
          if (stepNumber == -1) stepNumber = 0;
          if (stepNumber == numberOfSteps) {
            stepNumber--;
          }
          double portionOfStep = (getTimeElapsed(t) - stepNumber * timePerStep) / timePerStep;
          
          if (portionOfStep > 1.0D) { portionOfStep = 1.0D;
          }
          boolean lastStep = false;
          if (stepNumber == numberOfSteps - 1.0D) { lastStep = true;
          }
          if (stepNumber % 2 == 0) {
            stepRight(portionOfStep, lastStep);
          } else {
            stepLeft(portionOfStep, lastStep);
          }
          

          super.update(t);
        }
        ((Transformable)m_asSeenBy).setTransformationRightNow(asSeenByTrans, subject.getWorld());
      }
    }
  }
  
  public WalkToAnimation() {}
}
