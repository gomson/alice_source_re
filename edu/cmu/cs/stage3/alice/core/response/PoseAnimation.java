package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Pose;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;
import java.io.PrintStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;









public class PoseAnimation
  extends Animation
{
  public final TransformableProperty subject = new TransformableProperty(this, "subject", null);
  public final PoseProperty pose = new PoseProperty(this, "pose", null);
  
  public PoseAnimation() {}
  
  public class PoseProperty
    extends ElementProperty
  {
    public PoseProperty(Element owner, String name, Pose defaultValue)
    {
      super(name, defaultValue, Pose.class);
    }
    
    public Pose getPoseValue() { return (Pose)getElementValue(); }
  }
  
  public class RuntimePoseAnimation extends Animation.RuntimeAnimation { protected Transformable subject;
    protected Pose pose;
    protected Dictionary poseStringMap;
    
    public RuntimePoseAnimation() { super(); }
    


    protected Vector transformableKeys = new Vector();
    protected Dictionary poseTransformableMap = new Hashtable();
    protected Dictionary sourcePositionMap = new Hashtable();
    protected Dictionary targetPositionMap = new Hashtable();
    protected Dictionary sourceQuaternionMap = new Hashtable();
    protected Dictionary targetQuaternionMap = new Hashtable();
    protected Dictionary sourceScaleMap = new Hashtable();
    protected Dictionary targetScaleMap = new Hashtable();
    
    public void prologue(double t)
    {
      transformableKeys = new Vector();
      super.prologue(t);
      subject = subject.getTransformableValue();
      pose = pose.getPoseValue();
      Dictionary poseStringMap = pose.poseMap.getDictionaryValue();
      for (Enumeration enum0 = poseStringMap.keys(); enum0.hasMoreElements();) {
        String stringKey = (String)enum0.nextElement();
        Transformable key = (Transformable)subject.getDescendantKeyed(stringKey);
        if (key != null) {
          transformableKeys.add(key);
          Matrix44 m = (Matrix44)poseStringMap.get(stringKey);
          sourcePositionMap.put(key, key.getPosition());
          sourceQuaternionMap.put(key, key.getOrientationAsQuaternion());
          targetPositionMap.put(key, m.getPosition());
          targetQuaternionMap.put(key, m.getAxes().getQuaternion());
        } else {
          System.err.println("Can't find " + stringKey + " in " + subject);
        }
      }
    }
    
    public void update(double t)
    {
      super.update(t);
      double portion = getPortion(t);
      for (Enumeration enum0 = transformableKeys.elements(); enum0.hasMoreElements();) {
        Transformable key = (Transformable)enum0.nextElement();
        
        Vector3 sourcePosition = (Vector3)sourcePositionMap.get(key);
        Vector3 targetPosition = (Vector3)targetPositionMap.get(key);
        Quaternion sourceQuaternion = (Quaternion)sourceQuaternionMap.get(key);
        Quaternion targetQuaternion = (Quaternion)targetQuaternionMap.get(key);
        
        Vector3 currentPosition = Vector3.interpolate(sourcePosition, targetPosition, portion);
        Quaternion currentQuaternion = Quaternion.interpolate(sourceQuaternion, targetQuaternion, portion);
        
        key.setPositionRightNow(currentPosition);
        key.setOrientationRightNow(currentQuaternion);
      }
    }
  }
}
