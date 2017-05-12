package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Amount;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.criterion.ElementNameContainsCriterion;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.AmountProperty;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.util.HowMuch;
import javax.vecmath.Vector3d;

public class AbstractWalkAnimation extends Animation implements PropertyListener
{
  public final TransformableProperty subject = new TransformableProperty(this, "subject", null);
  public final AmountProperty stepAmount = new AmountProperty(this, "step size", Amount.NORMAL);
  public final AmountProperty bounceAmount = new AmountProperty(this, "bounce size", Amount.NORMAL);
  public final AmountProperty armAmount = new AmountProperty(this, "arm swing size", Amount.NORMAL);
  public final BooleanProperty swingArms = new BooleanProperty(this, "swing arms", Boolean.TRUE);
  
  public final NumberProperty stepSpeed = new NumberProperty(this, "stepsPerSecond", new Double(1.5D));
  
  public AbstractWalkAnimation() {
    duration.set(new Double(NaN.0D));
    
    duration.addPropertyListener(this);
    stepSpeed.addPropertyListener(this);
  }
  


  protected void propertyChanged(Property property, Object value)
  {
    super.propertyChanged(property, value);
    if (property.equals(duration)) {
      if (!Double.isNaN(((Double)value).doubleValue()))
      {
        stepSpeed.set(new Double(NaN.0D));
      }
    }
    else if ((property.equals(stepSpeed)) && 
      (!Double.isNaN(((Double)value).doubleValue())))
    {
      duration.set(new Double(NaN.0D)); }
  }
  
  public class RuntimeAbstractWalkAnimation extends Animation.RuntimeAnimation { protected Transformable subject;
    protected static final double normalContactAngle = 0.46D;
    
    public RuntimeAbstractWalkAnimation() { super(); }
    

    protected static final double normalBackRecoilAngle = 1.3D;
    
    protected static final double normalFrontRecoilAngle = 0.5D;
    
    protected double upperArmAngle = 0.3D;
    protected double lowerArmAngle = 0.1D;
    
    protected double portionContact = 0.3333333333333333D;
    protected double portionRecoil = 0.16666666666666666D;
    protected double portionPassing = 0.3333333333333333D;
    protected double portionHighPoint = 0.16666666666666666D;
    
    protected double contactAngle = 0.245D;
    
    protected double recoilBackLowerAngle = 0.6D;
    protected double recoilFrontUpperAngle = 0.3D;
    
    protected double passingFrontUpperAngle = 0.0D;
    protected double passingFrontLowerAngle = 0.0D;
    protected double passingFrontFootAngle = 0.0D;
    
    protected double passingBackLowerAngle = 0.2D;
    
    protected double highPointFrontUpperAngle = 0.2D;
    protected double highPointBackUpperAngle = 0.7D;
    protected double highPointBackLowerAngle = 0.0D;
    
    protected double heightFromGround = 0.0D;
    protected double initialBoundingBoxHeight = 0.0D;
    
    protected boolean firstTimeContact = true;
    protected boolean firstTimeRecoil = true;
    protected boolean firstTimePassing = true;
    protected boolean firstTimeHighPoint = true;
    

    protected Transformable rightUpper = null;
    protected Transformable rightLower = null;
    protected Transformable rightFoot = null;
    
    protected Transformable leftUpper = null;
    protected Transformable leftLower = null;
    protected Transformable leftFoot = null;
    

    protected Transformable rightUpperArm = null;
    protected Transformable rightLowerArm = null;
    
    protected Transformable leftUpperArm = null;
    protected Transformable leftLowerArm = null;
    

    protected double totalLength = 0.0D;
    protected double upperLength = 0.0D;
    protected double lowerLength = 0.0D;
    protected double footLength = 0.0D;
    protected double footHorizLength = 0.0D;
    

    protected Matrix33 rightUpperInitialOrient = null;
    protected Matrix33 rightLowerInitialOrient = null;
    protected Matrix33 rightFootInitialOrient = null;
    
    protected Matrix33 leftUpperInitialOrient = null;
    protected Matrix33 leftLowerInitialOrient = null;
    protected Matrix33 leftFootInitialOrient = null;
    

    protected Matrix33 rightUpperArmInitialOrient = null;
    protected Matrix33 rightLowerArmInitialOrient = null;
    
    protected Matrix33 leftUpperArmInitialOrient = null;
    protected Matrix33 leftLowerArmInitialOrient = null;
    
    protected Vector3 initialPos = null;
    
    protected Matrix33 defaultOrient = new Matrix33();
    

    protected Matrix33 frontUpperContactOrient = new Matrix33();
    protected Matrix33 frontLowerContactOrient = new Matrix33();
    protected Matrix33 frontFootContactOrient = new Matrix33();
    
    protected Matrix33 backUpperContactOrient = new Matrix33();
    protected Matrix33 backLowerContactOrient = new Matrix33();
    protected Matrix33 backFootContactOrient = new Matrix33();
    
    protected Vector3 contactPos = null;
    protected double distanceToMoveContact = 0.0D;
    

    protected Matrix33 frontUpperRecoilOrient = new Matrix33();
    protected Matrix33 frontLowerRecoilOrient = new Matrix33();
    protected Matrix33 frontFootRecoilOrient = new Matrix33();
    
    protected Matrix33 backUpperRecoilOrient = new Matrix33();
    protected Matrix33 backLowerRecoilOrient = new Matrix33();
    protected Matrix33 backFootRecoilOrient = new Matrix33();
    
    protected Vector3 recoilPos = null;
    protected double distanceToMoveRecoil = 0.0D;
    

    protected Matrix33 frontUpperPassingOrient = new Matrix33();
    protected Matrix33 frontLowerPassingOrient = new Matrix33();
    protected Matrix33 frontFootPassingOrient = new Matrix33();
    
    protected Matrix33 backUpperPassingOrient = new Matrix33();
    protected Matrix33 backLowerPassingOrient = new Matrix33();
    protected Matrix33 backFootPassingOrient = new Matrix33();
    
    protected Vector3 passingPos = null;
    protected double distanceToMovePassing = 0.0D;
    

    protected Matrix33 frontUpperHighPointOrient = new Matrix33();
    protected Matrix33 frontLowerHighPointOrient = new Matrix33();
    protected Matrix33 frontFootHighPointOrient = new Matrix33();
    
    protected Matrix33 backUpperHighPointOrient = new Matrix33();
    protected Matrix33 backLowerHighPointOrient = new Matrix33();
    protected Matrix33 backFootHighPointOrient = new Matrix33();
    
    protected Vector3 highPointPos = null;
    protected double distanceToMoveHighPoint = 0.0D;
    

    protected Matrix33 frontUpperArmOrient = new Matrix33();
    protected Matrix33 frontLowerArmOrient = new Matrix33();
    
    protected Matrix33 backUpperArmOrient = new Matrix33();
    protected Matrix33 backLowerArmOrient = new Matrix33();
    

    protected Matrix33 rightUpperCurrentOrient = new Matrix33();
    protected Matrix33 rightLowerCurrentOrient = new Matrix33();
    protected Matrix33 rightFootCurrentOrient = new Matrix33();
    
    protected Matrix33 leftUpperCurrentOrient = new Matrix33();
    protected Matrix33 leftLowerCurrentOrient = new Matrix33();
    protected Matrix33 leftFootCurrentOrient = new Matrix33();
    
    protected Matrix33 rightUpperArmCurrentOrient = new Matrix33();
    protected Matrix33 rightLowerArmCurrentOrient = new Matrix33();
    
    protected Matrix33 leftUpperArmCurrentOrient = new Matrix33();
    protected Matrix33 leftLowerArmCurrentOrient = new Matrix33();
    



    public void prologue(double t)
    {
      super.prologue(t);
      
      resetData();
      
      subject = subject.getTransformableValue();
      recoilFrontUpperAngle = 0.5D;
      
      if (armAmount.getValue().equals(Amount.HUGE)) {
        upperArmAngle = 0.8D;
        lowerArmAngle = 1.2D;
      } else if (armAmount.getValue().equals(Amount.BIG)) {
        upperArmAngle = 0.675D;
        lowerArmAngle = 0.925D;
      } else if (armAmount.getValue().equals(Amount.NORMAL)) {
        upperArmAngle = 0.55D;
        lowerArmAngle = 0.65D;
      } else if (armAmount.getValue().equals(Amount.LITTLE)) {
        upperArmAngle = 0.425D;
        lowerArmAngle = 0.375D;
      } else if (armAmount.getValue().equals(Amount.TINY)) {
        upperArmAngle = 0.3D;
        lowerArmAngle = 0.1D;
      }
      
      if (bounceAmount.getValue().equals(Amount.HUGE)) {
        recoilFrontUpperAngle = 0.5D;
        recoilBackLowerAngle = 2.0D;
      } else if (bounceAmount.getValue().equals(Amount.BIG)) {
        recoilFrontUpperAngle = 0.37D;
        recoilBackLowerAngle = 1.625D;
      } else if (bounceAmount.getValue().equals(Amount.NORMAL)) {
        recoilFrontUpperAngle = 0.25D;
        recoilBackLowerAngle = 1.25D;
      } else if (bounceAmount.getValue().equals(Amount.LITTLE)) {
        recoilFrontUpperAngle = 0.12D;
        recoilBackLowerAngle = 0.875D;
      } else if (bounceAmount.getValue().equals(Amount.TINY)) {
        recoilFrontUpperAngle = 0.0D;
        recoilBackLowerAngle = 0.5D;
      }
      
      if (stepAmount.getValue().equals(Amount.HUGE)) {
        contactAngle = 0.6900000000000001D;
      } else if (stepAmount.getValue().equals(Amount.BIG)) {
        contactAngle = 0.5750000000000001D;
      } else if (stepAmount.getValue().equals(Amount.NORMAL)) {
        contactAngle = 0.46D;
      } else if (stepAmount.getValue().equals(Amount.LITTLE)) {
        contactAngle = 0.34500000000000003D;
      } else if (stepAmount.getValue().equals(Amount.TINY)) {
        contactAngle = 0.23D;
      }
      
      findLegs();
      findArms();
      setLegLengths();
      setInitialOrientations();
      setContactData();
      setRecoilData();
      setPassingData();
      setHighPointData();
      setArmData();
    }
    


    public void stepRight(double portion, boolean lastStep)
    {
      step(rightUpper, portion, lastStep);
    }
    
    public void stepLeft(double portion, boolean lastStep) {
      step(leftUpper, portion, lastStep);
    }
    

    protected void step(Transformable leg, double portion, boolean lastStep)
    {
      adjustHeight();
      
      if (swingArms.getValue().equals(Boolean.TRUE)) {
        updateArms(leg, portion, lastStep);
      }
      
      if (portion < portionContact) {
        if (firstTimeContact) {
          firstTimeContact = false;
          firstTimeHighPoint = true;
          getCurrentOrientations();
        }
        portion /= portionContact;
        updateContact(leg, portion);
      } else if (portion < portionContact + portionRecoil) {
        if (firstTimeRecoil) {
          firstTimeRecoil = false;
          firstTimeContact = true;
          getCurrentOrientations();
        }
        portion = (portion - portionContact) / portionRecoil;
        if (leftLower != null) {
          updateRecoil(leg, portion);
        }
      } else if (portion < portionContact + portionRecoil + portionPassing) {
        if (firstTimePassing) {
          firstTimePassing = false;
          firstTimeRecoil = true;
          getCurrentOrientations();
        }
        portion = (portion - portionContact - portionRecoil) / portionPassing;
        updatePassing(leg, portion);
      } else {
        if (firstTimeHighPoint) {
          firstTimeHighPoint = false;
          firstTimePassing = true;
          getCurrentOrientations();
        }
        portion = (portion - portionContact - portionRecoil - portionPassing) / portionHighPoint;
        updateHighPoint(leg, portion, lastStep);
      }
      
      adjustHeight();
    }
    
    protected void adjustHeight()
    {
      double distanceAboveGround = 0.0D;
      if ((rightFoot != null) && (leftFoot != null)) {
        double rightHeight = rightFoot.getBoundingBox(subject.getWorld()).getCenterOfBottomFace().y;
        double leftHeight = leftFoot.getBoundingBox(subject.getWorld()).getCenterOfBottomFace().y;
        
        distanceAboveGround = Math.min(rightHeight, leftHeight);
      } else {
        distanceAboveGround = subject.getBoundingBox(subject.getWorld()).getCenterOfBottomFace().y;
      }
      double roundHeight = Math.round(subject.getBoundingBox(subject.getWorld()).getCenterOfBottomFace().y);
      int level = (int)Math.round(roundHeight / 256.0D);
      subject.moveRightNow(Direction.DOWN, distanceAboveGround - 256.0D * level, subject.getWorld());
    }
    
    public void getCurrentOrientations()
    {
      if (rightUpper != null) rightUpperCurrentOrient = rightUpper.getOrientationAsAxes();
      if (rightLower != null) rightLowerCurrentOrient = rightLower.getOrientationAsAxes();
      if (rightFoot != null) { rightFootCurrentOrient = rightFoot.getOrientationAsAxes();
      }
      if (leftUpper != null) leftUpperCurrentOrient = leftUpper.getOrientationAsAxes();
      if (leftLower != null) leftLowerCurrentOrient = leftLower.getOrientationAsAxes();
      if (leftFoot != null) { leftFootCurrentOrient = leftFoot.getOrientationAsAxes();
      }
      if (rightUpperArm != null) rightUpperArmCurrentOrient = rightUpperArm.getOrientationAsAxes();
      if (rightLowerArm != null) { rightLowerArmCurrentOrient = rightLowerArm.getOrientationAsAxes();
      }
      if (leftUpperArm != null) leftUpperArmCurrentOrient = leftUpperArm.getOrientationAsAxes();
      if (leftLowerArm != null) { leftLowerArmCurrentOrient = leftLowerArm.getOrientationAsAxes();
      }
    }
    

    public void findLegs()
    {
      Element[] legs = subject.search(new ElementNameContainsCriterion("UpperLeg"));
      for (int i = 0; i < legs.length; i++) {
        if ((legs[i].getKey().indexOf("leftU") != -1) && ((legs[i] instanceof Transformable))) {
          leftUpper = ((Transformable)legs[i]);
          leftLower = getTransformableChild(leftUpper);
          if (leftLower.name.getStringValue().indexOf("Foot") != -1) {
            leftFoot = leftLower;
            leftLower = null;
          }
          if (leftLower != null) {
            leftFoot = getTransformableChild(leftLower);
          } else {
            leftFoot = getTransformableChild(leftUpper);
          }
        } else if ((legs[i].getKey().indexOf("rightU") != -1) && ((legs[i] instanceof Transformable))) {
          rightUpper = ((Transformable)legs[i]);
          
          rightLower = getTransformableChild(rightUpper);
          if (rightLower.name.getStringValue().indexOf("Foot") != -1) {
            rightFoot = rightLower;
            rightLower = null;
          }
          if (rightLower != null) {
            rightFoot = getTransformableChild(rightLower);
          } else {
            rightFoot = getTransformableChild(rightUpper);
          }
        }
      }
    }
    
    public void findArms() {
      Element[] arms = subject.search(new ElementNameContainsCriterion("UpperArm"));
      for (int i = 0; i < arms.length; i++) {
        if ((arms[i].getKey().indexOf("left") != -1) && ((arms[i] instanceof Transformable))) {
          leftUpperArm = ((Transformable)arms[i]);
          leftLowerArm = getTransformableChild(leftUpperArm);
          if ((leftLowerArm != null) && (leftLowerArm.name.getStringValue().indexOf("Hand") != -1)) {
            leftLowerArm = null;
          }
        } else if ((arms[i].getKey().indexOf("right") != -1) && ((arms[i] instanceof Transformable))) {
          rightUpperArm = ((Transformable)arms[i]);
          rightLowerArm = getTransformableChild(rightUpperArm);
          if ((rightLowerArm != null) && (rightLowerArm.name.getStringValue().indexOf("Hand") != -1)) {
            rightLowerArm = null;
          }
        }
      }
    }
    
    public void resetData() {
      contactAngle = 0.245D;
      
      recoilBackLowerAngle = 0.2D;
      recoilFrontUpperAngle = 0.4D;
      
      passingFrontUpperAngle = 0.0D;
      passingFrontLowerAngle = 0.0D;
      passingFrontFootAngle = 0.0D;
      
      passingBackLowerAngle = 0.2D;
      
      highPointFrontUpperAngle = 0.2D;
      highPointBackUpperAngle = 0.7D;
      highPointBackLowerAngle = 0.0D;
      
      frontUpperContactOrient = new Matrix33();
      frontLowerContactOrient = new Matrix33();
      frontFootContactOrient = new Matrix33();
      
      backUpperContactOrient = new Matrix33();
      backLowerContactOrient = new Matrix33();
      backFootContactOrient = new Matrix33();
      
      contactPos = null;
      distanceToMoveContact = 0.0D;
      

      frontUpperRecoilOrient = new Matrix33();
      frontLowerRecoilOrient = new Matrix33();
      frontFootRecoilOrient = new Matrix33();
      
      backUpperRecoilOrient = new Matrix33();
      backLowerRecoilOrient = new Matrix33();
      backFootRecoilOrient = new Matrix33();
      
      recoilPos = null;
      distanceToMoveRecoil = 0.0D;
      

      frontUpperPassingOrient = new Matrix33();
      frontLowerPassingOrient = new Matrix33();
      frontFootPassingOrient = new Matrix33();
      
      backUpperPassingOrient = new Matrix33();
      backLowerPassingOrient = new Matrix33();
      backFootPassingOrient = new Matrix33();
      
      passingPos = null;
      distanceToMovePassing = 0.0D;
      

      frontUpperHighPointOrient = new Matrix33();
      frontLowerHighPointOrient = new Matrix33();
      frontFootHighPointOrient = new Matrix33();
      
      backUpperHighPointOrient = new Matrix33();
      backLowerHighPointOrient = new Matrix33();
      backFootHighPointOrient = new Matrix33();
      
      highPointPos = null;
      distanceToMoveHighPoint = 0.0D;
    }
    
    public void setLegLengths()
    {
      Vector3 top = new Vector3();
      Vector3d bottom = new Vector3d();
      
      footLength = 0.0D;
      footHorizLength = 0.0D;
      
      if (rightFoot != null) {
        top = rightFoot.getPosition(rightFoot);
        bottom = rightFoot.getBoundingBox(rightFoot).getCenterOfBottomFace();
        
        footLength = (y - y);
        footHorizLength = (z - z);
      }
      
      lowerLength = 0.0D;
      
      if (rightLower != null)
      {
        top = rightLower.getPosition(rightLower);
        if (rightFoot != null) bottom = rightFoot.getPosition(rightLower); else
          bottom = rightLower.getBoundingBox(rightLower).getCenterOfBottomFace();
        lowerLength = (y - y);
      }
      
      upperLength = 0.0D;
      
      if (rightUpper != null) {
        top = rightUpper.getPosition(rightUpper);
        
        if (rightLower != null) { bottom = rightLower.getPosition(rightUpper);
        } else if (rightFoot != null) bottom = rightFoot.getPosition(rightLower); else {
          bottom = rightUpper.getBoundingBox(rightUpper).getCenterOfBottomFace();
        }
        upperLength = (y - y);
      }
      
      totalLength = (footLength + lowerLength + upperLength);
    }
    
    public double getStepLength() {
      double stepLength = totalLength * Math.sin(contactAngle) * 1.5D;
      if (stepLength == 0.0D) stepLength = 1.0D;
      return stepLength;
    }
    
    public void setInitialOrientations() {
      if (rightUpper != null) { rightUpperInitialOrient = rightUpper.getOrientationAsAxes(rightUpper);
      }
      if (rightLower != null) { rightLowerInitialOrient = rightLower.getOrientationAsAxes(rightLower);
      }
      if (rightFoot != null) { rightFootInitialOrient = rightFoot.getOrientationAsAxes(rightFoot);
      }
      
      if (leftUpper != null) { leftUpperInitialOrient = leftUpper.getOrientationAsAxes(leftUpper);
      }
      if (leftLower != null) { leftLowerInitialOrient = leftLower.getOrientationAsAxes(leftLower);
      }
      if (leftFoot != null) { leftFootInitialOrient = leftFoot.getOrientationAsAxes(leftFoot);
      }
      
      if (rightUpperArm != null) { rightUpperArmInitialOrient = rightUpperArm.getOrientationAsAxes(rightUpperArm);
      }
      if (rightLowerArm != null) { rightLowerArmInitialOrient = rightLowerArm.getOrientationAsAxes(rightLowerArm);
      }
      
      if (leftUpperArm != null) { leftUpperArmInitialOrient = leftUpperArm.getOrientationAsAxes(leftUpperArm);
      }
      if (leftLowerArm != null) { leftLowerArmInitialOrient = leftLowerArm.getOrientationAsAxes(leftLowerArm);
      }
      
      if ((rightUpper != null) && (leftUpper != null))
      {
        Vector3 top = rightUpper.getPosition(rightUpper);
        Vector3d bottom = rightUpper.getBoundingBox(rightUpper).getCenterOfBottomFace();
        double offset = y - y - totalLength;
        
        top = leftUpper.getPosition(leftUpper);
        bottom = leftUpper.getBoundingBox(leftUpper).getCenterOfBottomFace();
        double offset2 = y - y - totalLength;
        
        if (offset2 > offset) { offset = offset2;
        }
        
        initialPos = subject.getPosition(new Vector3d(0.0D, offset, 0.0D), subject.getWorld());
        heightFromGround = initialPos.y;
        initialBoundingBoxHeight = getCurrentLegHeight();
      }
    }
    
    public double getCurrentLegHeight() {
      if (rightUpper != null) {
        rightUpper.getBoundingBox(rightUpper.getWorld(), HowMuch.INSTANCE);
        
        double boundingBoxHeight = rightUpper.getBoundingBox(rightUpper.getWorld()).getHeight();
        double boundingBoxHeight2 = leftUpper.getBoundingBox(leftUpper.getWorld()).getHeight();
        if (boundingBoxHeight2 > boundingBoxHeight) { boundingBoxHeight = boundingBoxHeight2;
        }
        return boundingBoxHeight; }
      return 0.0D;
    }
    
    public void setContactData() {
      double rotationLower = 0.0D;
      double rotationUpper = 0.0D;
      

      if ((leftLower == null) || (rightLower == null))
      {
        rotationUpper = contactAngle;
      }
      else {
        double lowerLegEffectiveLength = Math.sqrt(footHorizLength * footHorizLength + (lowerLength + footLength) * (lowerLength + footLength));
        double kneeAngle = (totalLength * totalLength - upperLength * upperLength - lowerLegEffectiveLength * lowerLegEffectiveLength) / (-2.0D * upperLength * lowerLegEffectiveLength);
        


        kneeAngle = Math.acos(kneeAngle);
        

        rotationLower = 3.141592653589793D - kneeAngle + Math.atan(footHorizLength / (footLength + lowerLength));
        rotationUpper = contactAngle - Math.asin(lowerLegEffectiveLength * Math.sin(kneeAngle) / totalLength);
        
        recoilBackLowerAngle += rotationLower;
        recoilFrontUpperAngle += contactAngle;
        
        passingFrontUpperAngle = recoilFrontUpperAngle;
        passingFrontLowerAngle = (recoilFrontUpperAngle + 0.2D);
        passingFrontFootAngle = 0.2D;
        passingBackLowerAngle += recoilBackLowerAngle;
        
        highPointBackLowerAngle = (passingBackLowerAngle / 2.0D);
      }
      
      frontUpperContactOrient.rotateX(-1.0D * contactAngle);
      backUpperContactOrient.rotateX(rotationUpper);
      backLowerContactOrient.rotateX(rotationLower);
      
      distanceToMoveContact = (totalLength - totalLength * Math.cos(contactAngle));
      contactPos = subject.getPosition(new Vector3d(0.0D, -1.0D * distanceToMoveContact, 0.0D), subject.getWorld());
    }
    
    public void setRecoilData() {
      frontUpperRecoilOrient.rotateX(-1.0D * recoilFrontUpperAngle);
      frontLowerRecoilOrient.rotateX(recoilFrontUpperAngle);
      backLowerRecoilOrient.rotateX(recoilBackLowerAngle);
      
      double distance = upperLength - upperLength * Math.cos(passingFrontUpperAngle) + lowerLength - lowerLength * Math.cos(passingFrontLowerAngle - passingFrontUpperAngle);
      recoilPos = subject.getPosition(new Vector3d(0.0D, -1.0D * distance, 0.0D), subject.getWorld());
    }
    
    public void setPassingData() {
      frontUpperPassingOrient.rotateX(-1.0D * passingFrontUpperAngle);
      frontLowerPassingOrient.rotateX(passingFrontLowerAngle);
      frontFootPassingOrient.rotateX(-1.0D * passingFrontFootAngle);
      
      backUpperPassingOrient.rotateX(-1.0D * passingFrontUpperAngle);
      backLowerPassingOrient.rotateX(passingBackLowerAngle);
      
      double distance = upperLength - upperLength * Math.cos(recoilFrontUpperAngle);
      passingPos = subject.getPosition(new Vector3d(0.0D, -1.0D * distance, 0.0D), subject.getWorld());
    }
    
    public void setHighPointData()
    {
      frontUpperHighPointOrient.rotateX(highPointFrontUpperAngle);
      
      backUpperHighPointOrient.rotateX(-1.0D * highPointBackUpperAngle);
      backLowerHighPointOrient.rotateX(highPointBackLowerAngle);
      
      double distance = totalLength - totalLength * Math.cos(highPointFrontUpperAngle);
      highPointPos = subject.getPosition(new Vector3d(0.0D, -1.0D * distance, 0.0D), subject.getWorld());
    }
    
    public void setArmData() {
      frontUpperArmOrient.rotateX(-1.0D * upperArmAngle);
      frontLowerArmOrient.rotateX(-1.0D * lowerArmAngle);
      
      backUpperArmOrient.rotateX(2.0D * upperArmAngle);
    }
    
    public Transformable getTransformableChild(Transformable parent) {
      if (parent == null) return null;
      Element[] legBits = parent.getChildren(Transformable.class);
      

      if (legBits.length == 1)
        return (Transformable)legBits[0];
      return null;
    }
    
    public void updateContact(Transformable leg, double portion)
    {
      if ((portion <= 1.0D) && 
        (leg != null)) {
        if (leg.equals(rightUpper)) {
          setQuaternion(rightUpper, rightUpperCurrentOrient, frontUpperContactOrient, portion);
          setQuaternion(rightLower, rightLowerCurrentOrient, frontLowerContactOrient, portion);
          
          setQuaternion(leftUpper, leftUpperCurrentOrient, backUpperContactOrient, portion);
          setQuaternion(leftLower, leftLowerCurrentOrient, backLowerContactOrient, portion);
        } else {
          setQuaternion(leftUpper, leftUpperCurrentOrient, frontUpperContactOrient, portion);
          setQuaternion(leftLower, leftLowerCurrentOrient, frontLowerContactOrient, portion);
          
          setQuaternion(rightUpper, rightUpperCurrentOrient, backUpperContactOrient, portion);
          setQuaternion(rightLower, rightLowerCurrentOrient, backLowerContactOrient, portion);
        }
      }
    }
    
    public void updateRecoil(Transformable leg, double portion) {
      if ((leg != null) && 
        (portion <= 1.0D)) {
        if (leg.equals(rightUpper)) {
          setQuaternion(rightUpper, rightUpperCurrentOrient, frontUpperRecoilOrient, portion);
          setQuaternion(rightLower, rightLowerCurrentOrient, frontLowerRecoilOrient, portion);
          
          setQuaternion(leftUpper, leftUpperCurrentOrient, backUpperRecoilOrient, portion);
          setQuaternion(leftLower, leftLowerCurrentOrient, backLowerRecoilOrient, portion);
        } else {
          setQuaternion(leftUpper, leftUpperCurrentOrient, frontUpperRecoilOrient, portion);
          setQuaternion(leftLower, leftLowerCurrentOrient, frontLowerRecoilOrient, portion);
          
          setQuaternion(rightUpper, rightUpperCurrentOrient, backUpperRecoilOrient, portion);
          setQuaternion(rightLower, rightLowerCurrentOrient, backLowerRecoilOrient, portion);
        }
      }
    }
    
    public void updatePassing(Transformable leg, double portion)
    {
      if ((leg != null) && 
        (portion <= 1.0D)) {
        if (leg.equals(rightUpper)) {
          setQuaternion(rightUpper, rightUpperCurrentOrient, frontUpperPassingOrient, portion);
          setQuaternion(rightLower, rightLowerCurrentOrient, frontLowerPassingOrient, portion);
          setQuaternion(rightFoot, rightFootCurrentOrient, frontFootPassingOrient, portion);
          
          setQuaternion(leftUpper, leftUpperCurrentOrient, backUpperPassingOrient, portion);
          setQuaternion(leftLower, leftLowerCurrentOrient, backLowerPassingOrient, portion);
          setQuaternion(leftFoot, leftFootCurrentOrient, backFootPassingOrient, portion);
        } else {
          setQuaternion(leftUpper, leftUpperCurrentOrient, frontUpperPassingOrient, portion);
          setQuaternion(leftLower, leftLowerCurrentOrient, frontLowerPassingOrient, portion);
          setQuaternion(leftFoot, leftFootCurrentOrient, frontFootPassingOrient, portion);
          
          setQuaternion(rightUpper, rightUpperCurrentOrient, backUpperPassingOrient, portion);
          setQuaternion(rightLower, rightLowerCurrentOrient, backLowerPassingOrient, portion);
          setQuaternion(rightFoot, rightFootCurrentOrient, backFootPassingOrient, portion);
        }
      }
    }
    
    public void updateHighPoint(Transformable leg, double portion, boolean lastStep) {
      if ((leg != null) && 
        (portion <= 1.0D)) {
        if (lastStep) {
          if (leg.equals(rightUpper)) {
            setQuaternion(rightUpper, rightUpperCurrentOrient, defaultOrient, portion);
            setQuaternion(rightLower, rightLowerCurrentOrient, defaultOrient, portion);
            setQuaternion(rightFoot, rightFootCurrentOrient, defaultOrient, portion);
            
            setQuaternion(leftUpper, leftUpperCurrentOrient, defaultOrient, portion);
            setQuaternion(leftLower, leftLowerCurrentOrient, defaultOrient, portion);
          } else {
            setQuaternion(leftUpper, leftUpperCurrentOrient, defaultOrient, portion);
            setQuaternion(leftLower, leftLowerCurrentOrient, defaultOrient, portion);
            setQuaternion(leftFoot, leftFootCurrentOrient, defaultOrient, portion);
            
            setQuaternion(rightUpper, rightUpperCurrentOrient, defaultOrient, portion);
            setQuaternion(rightLower, rightLowerCurrentOrient, defaultOrient, portion);
          }
          
        }
        else if (leg.equals(rightUpper)) {
          setQuaternion(rightUpper, rightUpperCurrentOrient, frontUpperHighPointOrient, portion);
          setQuaternion(rightLower, rightLowerCurrentOrient, frontLowerHighPointOrient, portion);
          setQuaternion(rightFoot, rightFootCurrentOrient, frontFootHighPointOrient, portion);
          
          setQuaternion(leftUpper, leftUpperCurrentOrient, backUpperHighPointOrient, portion);
          setQuaternion(leftLower, leftLowerCurrentOrient, backLowerHighPointOrient, portion);
        } else {
          setQuaternion(leftUpper, leftUpperCurrentOrient, frontUpperHighPointOrient, portion);
          setQuaternion(leftLower, leftLowerCurrentOrient, frontLowerHighPointOrient, portion);
          setQuaternion(leftFoot, leftFootCurrentOrient, frontFootHighPointOrient, portion);
          
          setQuaternion(rightUpper, rightUpperCurrentOrient, backUpperHighPointOrient, portion);
          setQuaternion(rightLower, rightLowerCurrentOrient, backLowerHighPointOrient, portion);
        }
      }
    }
    

    public void updateArms(Transformable leg, double portion, boolean lastStep)
    {
      if ((lastStep) && (leg != null)) {
        setQuaternion(leftUpperArm, leftUpperArmCurrentOrient, defaultOrient, portion);
        setQuaternion(leftLowerArm, leftLowerArmCurrentOrient, defaultOrient, portion);
        
        setQuaternion(rightUpperArm, rightUpperArmCurrentOrient, defaultOrient, portion);
        setQuaternion(rightLowerArm, rightLowerArmCurrentOrient, defaultOrient, portion);
      }
      else if (leg != null) {
        if (leg.equals(leftUpper)) {
          setQuaternion(rightUpperArm, rightUpperArmCurrentOrient, frontUpperArmOrient, portion);
          setQuaternion(rightLowerArm, rightLowerArmCurrentOrient, frontLowerArmOrient, portion);
          
          setQuaternion(leftUpperArm, leftUpperArmCurrentOrient, backUpperArmOrient, portion);
          setQuaternion(leftLowerArm, leftLowerArmCurrentOrient, backLowerArmOrient, portion);
        } else {
          setQuaternion(leftUpperArm, leftUpperArmCurrentOrient, frontUpperArmOrient, portion);
          setQuaternion(leftLowerArm, leftLowerArmCurrentOrient, frontLowerArmOrient, portion);
          
          setQuaternion(rightUpperArm, rightUpperArmCurrentOrient, backUpperArmOrient, portion);
          setQuaternion(rightLowerArm, rightLowerArmCurrentOrient, backLowerArmOrient, portion);
        }
      }
    }
    

    public void epilogue(double t)
    {
      super.epilogue(t);
      
      if (leftUpper != null) {
        if (swingArms.getValue().equals(Boolean.TRUE)) {
          setQuaternion(leftUpperArm, leftUpperArmCurrentOrient, defaultOrient, 1.0D);
          setQuaternion(leftLowerArm, leftLowerArmCurrentOrient, defaultOrient, 1.0D);
          
          setQuaternion(rightUpperArm, rightUpperArmCurrentOrient, defaultOrient, 1.0D);
          setQuaternion(rightLowerArm, rightLowerArmCurrentOrient, defaultOrient, 1.0D);
        }
        
        setQuaternion(rightUpper, rightUpperCurrentOrient, defaultOrient, 1.0D);
        setQuaternion(rightLower, rightLowerCurrentOrient, defaultOrient, 1.0D);
        setQuaternion(rightFoot, rightFootCurrentOrient, defaultOrient, 1.0D);
        
        setQuaternion(leftUpper, leftUpperCurrentOrient, defaultOrient, 1.0D);
        setQuaternion(leftLower, leftLowerCurrentOrient, defaultOrient, 1.0D);
        setQuaternion(rightFoot, rightFootCurrentOrient, defaultOrient, 1.0D);
      }
      
      adjustHeight();
    }
    

    private void setQuaternion(Transformable part, Matrix33 initialOrient, Matrix33 finalOrient, double portion)
    {
      double positionPortion = m_style.getPortion(portion, 1.0D);
      Matrix33 currentOrient = Matrix33.interpolate(initialOrient, finalOrient, positionPortion);
      
      if (part != null)
      {
        part.setOrientationRightNow(currentOrient);
      }
    }
  }
}
