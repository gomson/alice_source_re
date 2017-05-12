package edu.cmu.cs.stage3.pratt.maxkeyframing;

import edu.cmu.cs.stage3.math.HermiteCubic;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Quaternion;
import java.util.HashMap;




















public class TCBSpline
  extends Spline
{
  protected TCBKey[] keys;
  protected HermiteCubic[][] curves;
  protected HashMap curveMap = new HashMap();
  
  public TCBSpline() {}
  
  public boolean addKey(TCBKey key) { boolean result = super.addKey(key);
    updateKeys();
    return result;
  }
  
  protected int numComponents;
  public boolean removeKey(TCBKey key) { boolean result = super.removeKey(key);
    updateKeys();
    return result;
  }
  
  public void updateKeys() {
    keys = ((TCBKey[])getKeyArray(new TCBKey[0]));
    curveMap.clear();
    
    if (keys != null) {
      numComponents = keys[0].getValueComponents().length;
      curves = new HermiteCubic[keys.length - 1][numComponents];
      
      for (int i = 0; i < curves.length; i++) {
        TCBKey keyLast = keys[Math.max(i - 1, 0)];
        TCBKey keyThis = keys[i];
        TCBKey keyNext = keys[(i + 1)];
        TCBKey keyNextNext = keys[Math.min(i + 2, keys.length - 1)];
        curveMap.put(keyThis, new Integer(i));
        for (int j = 0; j < numComponents; j++) {
          double pLast = keyLast.getValueComponents()[j];
          double pThis = keyThis.getValueComponents()[j];
          double pNext = keyNext.getValueComponents()[j];
          double pNextNext = keyNextNext.getValueComponents()[j];
          
          double dThis = getTangentAtKey(keyThis, pLast, pThis, pNext);
          double dNext = getTangentAtKey(keyNext, pThis, pNext, pNextNext);
          
          curves[i][j] = new HermiteCubic(pThis, pNext, dThis, dNext);
        }
      }
    } else {
      curves = null;
    }
  }
  
  private double getTangentAtKey(TCBKey key, double pLast, double pThis, double pNext) {
    double t = key.getTension();
    double c = key.getContinuity();
    double b = key.getBias();
    
    double ds = (1.0D - t) * (1.0D - c) * (1.0D + b) / 2.0D * (pThis - pLast) + (1.0D - t) * (1.0D + c) * (1.0D - b) / 2.0D * (pNext - pThis);
    double dd = (1.0D - t) * (1.0D + c) * (1.0D + b) / 2.0D * (pThis - pLast) + (1.0D - t) * (1.0D - c) * (1.0D - b) / 2.0D * (pNext - pThis);
    return (ds + dd) / 2.0D;
  }
  
  public void correctForMAXRelativeKeys() {
    Matrix33 lastRot = null;
    TCBKey[] keys = (TCBKey[])getKeyArray(new TCBKey[0]);
    for (int i = 0; i < keys.length; i++)
    {
      Quaternion thisQ = (Quaternion)keys[i].createSample(keys[i].getValueComponents());
      if (i > 0) {
        Quaternion realQ = Matrix33.multiply(lastRot, thisQ.getMatrix33()).getQuaternion();
        QuaternionTCBKey realKey = new QuaternionTCBKey(keys[i].getTime(), realQ, keys[i].getTension(), keys[i].getContinuity(), keys[i].getBias());
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
        
        Object o = curveMap.get(boundingKeys[0]);
        if ((o instanceof Integer)) {
          int i = ((Integer)o).intValue();
          
          double[] components = new double[numComponents];
          for (int j = 0; j < numComponents; j++) {
            components[j] = curves[i][j].evaluate(portion);
          }
          
          return boundingKeys[0].createSample(components);
        }
      }
    }
    return null;
  }
}
