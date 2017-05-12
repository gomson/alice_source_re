package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Vector3;
import java.io.PrintStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;













public class Pose
  extends Element
{
  public Pose() {}
  
  public final DictionaryProperty poseMap = new DictionaryProperty(this, "poseMap", null);
  
  public static Pose manufacturePose(Transformable modelRoot, Transformable poseRoot) {
    Pose pose = new Pose();
    Hashtable map = new Hashtable();
    Transformable[] descendants = (Transformable[])poseRoot.getDescendants(Transformable.class);
    for (int i = 0; i < descendants.length; i++) {
      if (descendants[i] != poseRoot) {
        map.put(descendants[i].getKey(modelRoot), descendants[i].getLocalTransformation());
      }
    }
    poseMap.set(map);
    return pose;
  }
  
  private Hashtable HACK_hardMap = new Hashtable();
  
  public void HACK_harden() {
    Element parent = getParent();
    HACK_hardMap.clear();
    Enumeration enum0 = poseMap.keys();
    while (enum0.hasMoreElements()) {
      String key = (String)enum0.nextElement();
      Object value = poseMap.get(key);
      Element hardKey = parent.getDescendantKeyedIgnoreCase(key);
      if (hardKey != null) {
        HACK_hardMap.put(hardKey, value);
      } else {
        System.err.println("COULD NOT HARDEN KEY: " + key);
      }
    }
  }
  
  public void HACK_soften() {
    Element parent = getParent();
    Dictionary softMap = new Hashtable();
    Enumeration enum0 = HACK_hardMap.keys();
    while (enum0.hasMoreElements()) {
      Element hardKey = (Element)enum0.nextElement();
      Object value = HACK_hardMap.get(hardKey);
      String softKey = hardKey.getKey(parent);
      if (softKey != null) {
        softMap.put(softKey, value);
      } else {
        System.err.println("COULD NOT SOFTEN KEY: " + hardKey);
      }
    }
    poseMap.set(softMap);
  }
  
  public void scalePositionRightNow(Element part, Element modelRoot, Vector3d scale, ReferenceFrame asSeenBy) {
    String key = part.getKey(modelRoot);
    Matrix4d prev = (Matrix4d)poseMap.get(key);
    if (prev != null) {
      Matrix4d curr = new Matrix44();
      curr.set(prev);
      m30 *= x;
      m31 *= y;
      m32 *= z;
      poseMap.put(key, curr);
    }
  }
  







  public void resize(Element part, Element modelRoot, double ratio)
  {
    Enumeration keys = poseMap.keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      if (((String)key).indexOf(part.getKey(modelRoot)) != -1) {
        Matrix44 transform = (Matrix44)poseMap.get(key);
        Vector3 pos = transform.getPosition();
        pos.scale(ratio);
        transform.setPosition(pos);
        poseMap.put(key, transform);
      }
    }
  }
}
