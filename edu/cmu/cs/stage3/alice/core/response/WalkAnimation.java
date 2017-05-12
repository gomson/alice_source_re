package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StyleProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle;








public class WalkAnimation
  extends AbstractWalkAnimation
{
  public final NumberProperty distance = new NumberProperty(this, "distance", new Double(1.0D));
  
  public WalkAnimation() {
    style.set(TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY);
  }
  
  protected void propertyChanging(Property property, Object value)
  {
    if (property == distance) {
      if ((value instanceof Number)) {
        double d = ((Number)value).doubleValue();
      }
      
    }
    else
      super.propertyChanging(property, value);
  }
  
  public class RuntimeWalkAnimation extends AbstractWalkAnimation.RuntimeAbstractWalkAnimation {
    public RuntimeWalkAnimation() { super(); }
    double currentPos = 0.0D;
    double stepLength = -1.0D;
    
    double numberOfSteps = -1.0D;
    double timePerStep = -1.0D;
    
    private boolean done = false;
    
    protected double getActualStepLength() {
      if (stepLength == -1.0D) {
        stepLength = getStepLength();
        if (stepLength == 0.0D) { stepLength = 1.0D;
        }
      }
      if (numberOfSteps == -1.0D) {
        numberOfSteps = Math.round(distance.doubleValue() / stepLength);
      }
      
      return distance.doubleValue() / numberOfSteps;
    }
    
    protected double getTotalTime()
    {
      getActualStepLength();
      if (Double.isNaN(duration.doubleValue())) {
        return numberOfSteps / stepSpeed.doubleValue();
      }
      return duration.doubleValue();
    }
    

    public double getTimeRemaining(double t)
    {
      return getTotalTime() - getTimeElapsed(t);
    }
    
    public void prologue(double t)
    {
      super.prologue(t);
      currentPos = 0.0D;
    }
    
    public void update(double t)
    {
      if (getTimeRemaining(t) > 0.0D) {
        super.update(t);
        
        done = false;
        
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
        
        double portion = getTimeElapsed(t) / getTotalTime();
        double targetDistance = distance.doubleValue() * portion;
        
        if (targetDistance - currentPos > 0.0D)
        {
          subject.getTransformableValue().moveRightNow(Direction.FORWARD, targetDistance - currentPos);
          currentPos += targetDistance - currentPos;
          
          adjustHeight();
        }
      }
    }
  }
}
