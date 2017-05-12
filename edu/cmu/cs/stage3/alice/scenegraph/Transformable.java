package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;
import java.io.PrintStream;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector3d;




















public class Transformable
  extends ReferenceFrame
{
  public static final Property LOCAL_TRANSFORMATION_PROPERTY = new Property(Transformable.class, "LOCAL_TRANSFORMATION");
  public static final Property IS_FIRST_CLASS_PROPERTY = new Property(Transformable.class, "IS_FIRST_CLASS");
  private Matrix4d m_localTransformation = null;
  private boolean m_isFirstClass = true;
  private static Transformable s_calculatePointAtHelperOffset = new Transformable();
  private static Transformable s_calculatePointAtHelperA = new Transformable();
  private static Transformable s_calculatePointAtHelperB = new Transformable();
  
  static { s_calculatePointAtHelperOffset.setName("s_calculatePointAtHelperOffset");
    s_calculatePointAtHelperA.setName("s_calculatePointAtHelperA");
    s_calculatePointAtHelperB.setName("s_calculatePointAtHelperB");
    s_calculatePointAtHelperOffset.setIsHelper(true);
    s_calculatePointAtHelperA.setIsHelper(true);
    s_calculatePointAtHelperB.setIsHelper(true); }
  
  private Matrix4d m_absoluteTransformation = null;
  private Matrix4d m_inverseAbsoluteTransformation = null;
  private Object m_absoluteTransformationLock = new Object();
  private boolean m_isHelper = false;
  
  public Transformable() {
    m_localTransformation = new Matrix4d();
    m_localTransformation.setIdentity();
  }
  
  public boolean isHelper() {
    return m_isHelper;
  }
  
  public void setIsHelper(boolean isHelper) { m_isHelper = isHelper; }
  


  public boolean getIsFirstClass() { return m_isFirstClass; }
  
  public void setIsFirstClass(boolean isFirstClass) {
    if (m_isFirstClass != isFirstClass) {
      m_isFirstClass = isFirstClass;
      onPropertyChange(IS_FIRST_CLASS_PROPERTY);
    }
  }
  
  public Matrix4d getLocalTransformation() { if (m_localTransformation == null) {
      throw new NullPointerException();
    }
    return new Matrix4d(m_localTransformation);
  }
  
  public void setLocalTransformation(Matrix4d localTransformation) { if (localTransformation == null) {
      throw new NullPointerException();
    }
    if (notequal(m_localTransformation, localTransformation))
    {



      m_localTransformation = localTransformation;
      onPropertyChange(LOCAL_TRANSFORMATION_PROPERTY);
      onAbsoluteTransformationChange();
    }
  }
  
  public Matrix4d getAbsoluteTransformation()
  {
    synchronized (m_absoluteTransformationLock) {
      if (m_absoluteTransformation == null) {
        Container parent = getParent();
        if (parent != null) {
          m_absoluteTransformation = MathUtilities.multiply(m_localTransformation, parent.getAbsoluteTransformation());
        } else {
          m_absoluteTransformation = new Matrix4d(m_localTransformation);
        }
        if (Math.abs(m_absoluteTransformation.m33 - 1.0D) > 0.01D) {
          System.err.println("JAVA SCENEGRAH LOCAL: holy corrupt matrix batman " + m_absoluteTransformation);
        }
      }
      return new Matrix4d(m_absoluteTransformation);
    }
  }
  
  public Matrix4d getInverseAbsoluteTransformation() {
    synchronized (m_absoluteTransformationLock) {
      if (m_inverseAbsoluteTransformation == null) {
        m_inverseAbsoluteTransformation = getAbsoluteTransformation();
        try {
          m_inverseAbsoluteTransformation.invert();
        } catch (SingularMatrixException sme) {
          System.err.println("cannot invert: " + m_inverseAbsoluteTransformation);
          throw sme;
        }
      }
      return new Matrix4d(m_inverseAbsoluteTransformation);
    }
  }
  
  protected void onAbsoluteTransformationChange()
  {
    super.onAbsoluteTransformationChange();
    synchronized (m_absoluteTransformationLock) {
      m_absoluteTransformation = null;
      m_inverseAbsoluteTransformation = null;
    }
  }
  
  public Matrix44 getTransformation(ReferenceFrame asSeenBy)
  {
    ReferenceFrame vehicle = (ReferenceFrame)getParent();
    if (asSeenBy == null) {
      asSeenBy = vehicle;
    }
    if (asSeenBy == vehicle) {
      return new Matrix44(getLocalTransformation());
    }
    if ((asSeenBy instanceof Scene)) {
      return new Matrix44(getAbsoluteTransformation());
    }
    return super.getTransformation(asSeenBy);
  }
  
  public Matrix44 calculateTransformation(Matrix4d m, ReferenceFrame asSeenBy) { ReferenceFrame vehicle = (ReferenceFrame)getParent();
    if (asSeenBy == null) {
      asSeenBy = vehicle;
    }
    if (asSeenBy == vehicle)
      return new Matrix44(m);
    Matrix4d vehicleInverse;
    Matrix4d vehicleInverse;
    if (vehicle != null) {
      vehicleInverse = vehicle.getInverseAbsoluteTransformation();
    } else {
      vehicleInverse = new Matrix4d();
      vehicleInverse.setIdentity();
    }
    return Matrix44.multiply(
      m, 
      Matrix44.multiply(
      asSeenBy.getAbsoluteTransformation(), 
      vehicleInverse));
  }
  
  public void setAbsoluteTransformation(Matrix4d m) {
    ReferenceFrame vehicle = (ReferenceFrame)getParent();
    setLocalTransformation(MathUtilities.multiply(m, vehicle.getInverseAbsoluteTransformation()));
  }
  
  public void setTransformation(Matrix4d m, ReferenceFrame asSeenBy) { setLocalTransformation(calculateTransformation(m, asSeenBy)); }
  
  public void setPosition(Vector3d position, ReferenceFrame asSeenBy) {
    Matrix33 axes = getAxes(null);
    Matrix44 m = new Matrix44();
    m.setPosition(position);
    m = calculateTransformation(m, asSeenBy);
    m.setAxes(axes);
    setLocalTransformation(m);
  }
  
  public void setAxes(Matrix3d axes, ReferenceFrame asSeenBy) { Vector3 translation = getPosition(null);
    Matrix44 m = new Matrix44();
    m.setAxes(axes);
    m = calculateTransformation(m, asSeenBy);
    m.setPosition(translation);
    setLocalTransformation(m);
  }
  
  public void setQuaternion(Quaternion quaternion, ReferenceFrame asSeenBy) {
    setAxes(quaternion.getMatrix33(), asSeenBy);
  }
  
  public Matrix33 calculatePointAt(ReferenceFrame target, Vector3d offset, Vector3d upGuide, ReferenceFrame asSeenBy, boolean onlyAffectYaw) {
    synchronized (s_calculatePointAtHelperOffset) {
      if (upGuide == null) {
        upGuide = MathUtilities.getYAxis();
      }
      if (asSeenBy == null) {
        asSeenBy = (ReferenceFrame)getParent();
      }
      Matrix44 transform = getTransformation(asSeenBy);
      Vector3 position = transform.getPosition();
      
      ReferenceFrame actualTarget;
      ReferenceFrame actualTarget;
      if (offset == null) {
        actualTarget = target;
      } else {
        s_calculatePointAtHelperOffset.setParent(target);
        Matrix44 m = new Matrix44();
        m30 = x;
        m31 = y;
        m32 = z;
        s_calculatePointAtHelperOffset.setLocalTransformation(m);
        actualTarget = s_calculatePointAtHelperOffset;
      }
      
      Matrix33 result;
      if (onlyAffectYaw)
      {
        s_calculatePointAtHelperA.setParent(asSeenBy);
        s_calculatePointAtHelperA.setLocalTransformation(new Matrix44());
        s_calculatePointAtHelperA.setPosition(Vector3.ZERO, this);
        

        Vector3 targetPosition = actualTarget.getPosition(s_calculatePointAtHelperA);
        double targetTheta = Math.atan2(x, z);
        

        s_calculatePointAtHelperB.setParent(this);
        s_calculatePointAtHelperB.setPosition(MathUtilities.getZAxis(), this);
        

        Vector3 forwardPosition = s_calculatePointAtHelperB.getPosition(s_calculatePointAtHelperA);
        double forwardTheta = Math.atan2(x, z);
        

        s_calculatePointAtHelperB.setLocalTransformation(new Matrix44());
        

        double deltaTheta = targetTheta - forwardTheta;
        

        s_calculatePointAtHelperB.rotate(MathUtilities.getYAxis(), deltaTheta, s_calculatePointAtHelperA);
        

        Matrix33 result = s_calculatePointAtHelperB.getAxes(asSeenBy);
        

        s_calculatePointAtHelperA.setParent(null);
        s_calculatePointAtHelperB.setParent(null);
      } else {
        Vector3d targetPosition = actualTarget.getPosition(asSeenBy);
        Vector3d zAxis = MathUtilities.normalizeV(MathUtilities.subtract(targetPosition, position));
        Vector3d xAxis = MathUtilities.normalizeV(MathUtilities.crossProduct(upGuide, zAxis));
        if (Double.isNaN(xAxis.lengthSquared())) {
          xAxis.set(0.0D, 0.0D, 0.0D);
          zAxis.set(0.0D, 0.0D, 0.0D);
        }
        
        Vector3d yAxis = MathUtilities.crossProduct(zAxis, xAxis);
        result = new Matrix33(xAxis, yAxis, zAxis);
      }
      
      if (offset == null) {
        s_calculatePointAtHelperOffset.setParent(null);
      }
      return result;
    }
  }
  
  public Matrix33 calculatePointAt(ReferenceFrame target, Vector3d offset, Vector3d upGuide, ReferenceFrame asSeenBy) {
    return calculatePointAt(target, offset, upGuide, asSeenBy, false);
  }
  
  public void pointAt(ReferenceFrame target, Vector3d offset, Vector3d upGuide, ReferenceFrame asSeenBy) { setAxes(calculatePointAt(target, offset, upGuide, asSeenBy), asSeenBy); }
  
  public static Matrix33 calculateOrientation(Vector3d forward, Vector3d upGuide) {
    if (upGuide == null) {
      upGuide = MathUtilities.getYAxis();
    }
    Vector3d zAxis = MathUtilities.normalizeV(forward);
    Vector3d xAxis = MathUtilities.normalizeV(MathUtilities.crossProduct(upGuide, zAxis));
    if (Double.isNaN(xAxis.lengthSquared())) {
      throw new RuntimeException("cannot calculate orientation: forward=" + forward + " upGuide=" + upGuide);
    }
    Vector3d yAxis = MathUtilities.crossProduct(zAxis, xAxis);
    return new Matrix33(xAxis, yAxis, zAxis);
  }
  
  public void setOrientation(Vector3d forward, Vector3d upGuide, ReferenceFrame asSeenBy) {
    setAxes(calculateOrientation(forward, upGuide), asSeenBy);
  }
  
  public Matrix33 calculateStandUp(ReferenceFrame asSeenBy) {
    Matrix33 axes = getAxes(asSeenBy);
    Vector3d yAxis = MathUtilities.getYAxis();
    Vector3d zAxis = MathUtilities.normalizeV(MathUtilities.crossProduct(axes.getRow(0), yAxis));
    Vector3d xAxis = MathUtilities.crossProduct(yAxis, zAxis);
    return new Matrix33(xAxis, yAxis, zAxis);
  }
  

  public void standUp(ReferenceFrame asSeenBy) { setAxes(calculateStandUp(asSeenBy), asSeenBy); }
  
  public void translate(Vector3d vector, ReferenceFrame asSeenBy) {
    if (asSeenBy == null) {
      asSeenBy = this;
    }
    Matrix44 m = getTransformation(asSeenBy);
    m.translate(vector);
    setTransformation(m, asSeenBy);
  }
  
  public void rotate(Vector3d axis, double amount, ReferenceFrame asSeenBy) { if (asSeenBy == null) {
      asSeenBy = this;
    }
    Matrix44 m = getTransformation(asSeenBy);
    m.rotate(axis, amount);
    setTransformation(m, asSeenBy);
  }
  
  public void scale(Vector3d axis, ReferenceFrame asSeenBy) { if (asSeenBy == null) {
      asSeenBy = this;
    }
    Matrix44 m = getTransformation(asSeenBy);
    m.scale(axis);
    setTransformation(m, asSeenBy);
  }
  
  public void transform(Matrix4d trans, ReferenceFrame asSeenBy) { if (asSeenBy == null) {
      asSeenBy = this;
    }
    Matrix44 m = getTransformation(asSeenBy);
    m.transform(trans);
    setTransformation(m, asSeenBy);
  }
  
































































  public void setPivot(ReferenceFrame pivot)
  {
    Matrix44 m = getTransformation(pivot);
    Matrix44 mInverse = Matrix44.invert(m);
    transform(mInverse, this);
    for (int i = 0; i < getChildCount(); i++) {
      Component child = getChildAt(i);
      if ((child instanceof Transformable)) {
        ((Transformable)child).transform(m, this);
      } else if ((child instanceof Visual)) {
        ((Visual)child).transform(m);
      }
    }
  }
}
