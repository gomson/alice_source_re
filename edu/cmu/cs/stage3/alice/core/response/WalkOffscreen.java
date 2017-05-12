package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Style;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Vector3;
import javax.vecmath.Vector3d;

public class WalkOffscreen extends AbstractWalkAnimation
{
  public final DirectionProperty exitDirection = new DirectionProperty(this, "exit direction", Direction.RIGHT);
  
  double turnLength = 0.25D;
  
  public WalkOffscreen() {}
  
  protected void propertyChanged(Property property, Object value) { super.propertyChanged(property, value);
    if (property.equals(duration)) {
      if (!Double.isNaN(((Double)value).doubleValue()))
      {
        if (duration.doubleValue() > 2.0D) {
          turnLength = 0.5D;
        } else {
          turnLength = (duration.doubleValue() / 5.0D);
        }
      }
    } else if ((property.equals(stepSpeed)) && 
      (!Double.isNaN(((Double)value).doubleValue())))
    {
      turnLength = 0.5D; } }
  
  public class RuntimeWalkOffScreen extends AbstractWalkAnimation.RuntimeAbstractWalkAnimation implements edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener { private Matrix33 m_orient0;
    private Matrix33 m_orient1;
    
    public RuntimeWalkOffScreen() { super(); }
    



    private boolean firstOver1 = true;
    
    protected Camera camera = null;
    protected double cameraAngle = 0.0D;
    protected double distanceToCenter = 0.0D;
    
    protected double distanceToMove = 0.0D;
    double timePerStep = -1.0D;
    
    double stepLength = -1.0D;
    double numberOfSteps = -1.0D;
    double currentPos = 0.0D;
    
    double boundingBoxDepth = -1.0D;
    




    public void absoluteTransformationChanged(AbsoluteTransformationEvent absoluteTransformationEvent) {}
    



    public void prologue(double t)
    {
      super.prologue(t);
      findCamera();
      getActualStepLength();
      
      m_orient0 = subject.getOrientationAsAxes(camera);
      m_orient1 = new Matrix33();
      
      if (exitDirection.getDirectionValue().equals(Direction.LEFT)) {
        m_orient1.setForwardUpGuide(new Vector3d(-1.0D, 0.0D, 0.0D), m_orient0.getRow(1));
      } else {
        m_orient1.setForwardUpGuide(new Vector3d(1.0D, 0.0D, 0.0D), m_orient0.getRow(1));
      }
    }
    
    protected double getActualStepLength()
    {
      if (stepLength == -1.0D) {
        stepLength = getStepLength();
        if (stepLength == 0.0D) { stepLength = 1.0D;
        }
      }
      if (numberOfSteps == -1.0D) {
        numberOfSteps = Math.round(distanceToMove / stepLength);
      }
      
      return distanceToMove / numberOfSteps;
    }
    

    public double getTimeRemaining(double t)
    {
      double walkTime = duration.doubleValue() - turnLength;
      double totalTime = walkTime + turnLength;
      
      if (Double.isNaN(walkTime)) {
        walkTime = numberOfSteps / stepSpeed.doubleValue();
        totalTime = walkTime + turnLength;
      }
      
      return totalTime - getTimeElapsed(t);
    }
    
    protected double getPortion(double t)
    {
      double duration = getDuration();
      if (Double.isNaN(duration)) {
        duration = numberOfSteps / stepSpeed.doubleValue() + turnLength;
      }
      return m_style.getPortion(Math.min(getTimeElapsed(t), duration), duration);
    }
    

    public void update(double t)
    {
      if (getTimeElapsed(t) <= turnLength) {
        double portion = m_style.getPortion(Math.min(getTimeElapsed(t), turnLength), turnLength);
        
        Matrix33 q = Matrix33.interpolate(m_orient0, m_orient1, portion);
        subject.setOrientationRightNow(q, camera);
      } else {
        if (firstOver1) {
          Matrix33 q = Matrix33.interpolate(m_orient0, m_orient1, m_style.getPortion(Math.min(getTimeElapsed(turnLength), turnLength), turnLength));
          subject.setOrientationRightNow(q, camera);
          firstOver1 = false;
        }
        
        if (timePerStep == -1.0D) {
          if (!Double.isNaN(duration.doubleValue())) {
            timePerStep = ((duration.doubleValue() - turnLength) / numberOfSteps);
          } else {
            timePerStep = (1.0D / stepSpeed.doubleValue());
          }
        }
        
        int stepNumber = (int)Math.ceil((getTimeElapsed(t) - turnLength) * (1.0D / timePerStep)) - 1;
        
        if (stepNumber == -1) stepNumber = 0;
        if (stepNumber == numberOfSteps) {
          stepNumber--;
        }
        double portionOfStep = (getTimeElapsed(t) - turnLength - stepNumber * timePerStep) / timePerStep;
        
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
        
        double portion = (getTimeElapsed(t) - turnLength) / (getTimeElapsed(t) - turnLength + getTimeRemaining(t));
        double targetDistance = distanceToMove * portion;
        

        subject.getTransformableValue().moveRightNow(Direction.FORWARD, targetDistance - currentPos);
        currentPos += targetDistance - currentPos;
      }
      super.update(t);
    }
    

    protected void findCamera()
    {
      if (camera == null) {
        edu.cmu.cs.stage3.alice.core.Element camElement = subject.getWorld().getChildNamed("Camera");
        
        if (camElement != null) {
          camera = ((Camera)camElement);
        }
        camera.addAbsoluteTransformationListener(this);
      }
      
      if (boundingBoxDepth == -1.0D) {
        boundingBoxDepth = subject.getBoundingBox().getDepth();
      }
      
      cameraAngle = 0.0D;
      if ((camera instanceof SymmetricPerspectiveCamera)) {
        cameraAngle = camera).horizontalViewingAngle.doubleValue();
      }
      
      if (cameraAngle != 0.0D) {
        double hypot = subject.getPosition(camera).z;
        distanceToCenter = (hypot * Math.sin(cameraAngle / 2.0D));
      }
      
      if (exitDirection.getDirectionValue().equals(Direction.LEFT)) {
        distanceToMove = (distanceToCenter + 1.5D * boundingBoxDepth + subject.getPosition(camera).x);
      } else {
        distanceToMove = (distanceToCenter + 1.5D * boundingBoxDepth - subject.getPosition(camera).x);
      }
    }
  }
}
