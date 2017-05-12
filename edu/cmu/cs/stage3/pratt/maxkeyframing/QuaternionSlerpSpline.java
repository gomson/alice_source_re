package edu.cmu.cs.stage3.pratt.maxkeyframing;

import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Quaternion;



















public class QuaternionSlerpSpline
  extends Spline
{
  public QuaternionSlerpSpline() {}
  
  public boolean addKey(QuaternionKey key)
  {
    return super.addKey(key);
  }
  
  public boolean removeKey(QuaternionKey key) {
    return super.removeKey(key);
  }
  
  public void correctForMAXRelativeKeys() {
    Matrix33 lastRot = null;
    QuaternionKey[] keys = (QuaternionKey[])getKeyArray(new QuaternionKey[0]);
    for (int i = 0; i < keys.length; i++) {
      Quaternion thisQ = (Quaternion)keys[i].createSample(keys[i].getValueComponents());
      if (i > 0) {
        Quaternion realQ = Matrix33.multiply(lastRot, thisQ.getMatrix33()).getQuaternion();
        QuaternionKey realKey = new QuaternionKey(keys[i].getTime(), realQ);
        removeKey(keys[i]);
        addKey(realKey);
        lastRot = realQ.getMatrix33();
      } else {
        lastRot = thisQ.getMatrix33();
      }
    }
  }
  
  public Object getSample(double t)
  {
    if (t <= 0.0D) {
      Key key = getFirstKey();
      if (key != null) {
        return key.createSample(key.getValueComponents());
      }
    } else if (t >= getDuration()) {
      Key key = getLastKey();
      if (key != null) {
        return key.createSample(key.getValueComponents());
      }
    } else {
      Key[] boundingKeys = getBoundingKeys(t);
      if (boundingKeys != null) {
        double timeSpan = boundingKeys[1].getTime() - boundingKeys[0].getTime();
        double portion = (t - boundingKeys[0].getTime()) / timeSpan;
        
        return Quaternion.interpolate(((QuaternionKey)boundingKeys[0]).getQuaternion(), ((QuaternionKey)boundingKeys[1]).getQuaternion(), portion);
      }
    }
    return null;
  }
}
