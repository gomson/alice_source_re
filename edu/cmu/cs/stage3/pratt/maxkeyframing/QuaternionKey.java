package edu.cmu.cs.stage3.pratt.maxkeyframing;

import edu.cmu.cs.stage3.math.Quaternion;
import java.util.StringTokenizer;





















public class QuaternionKey
  extends Key
{
  protected Quaternion q;
  
  public QuaternionKey(double time, Quaternion q)
  {
    this.q = q;
    setTime(time);
    setValueComponents(new double[] { x, y, z, w });
  }
  
  private Quaternion qSample = new Quaternion();
  
  public Object createSample(double[] components) {
    qSample.x = components[0];
    qSample.y = components[1];
    qSample.z = components[2];
    qSample.w = components[3];
    return qSample;
  }
  
  public Quaternion getQuaternion()
  {
    return q;
  }
  
  public String toString()
  {
    String className = getClass().getName();
    double[] components = getValueComponents();
    int numComponents = components.length;
    
    StringBuffer repr = new StringBuffer();
    repr.append(className);
    repr.append("[");
    repr.append(getTime());
    repr.append(",");
    for (int i = 0; i < numComponents - 1; i++) {
      repr.append(components[i]);
      repr.append(",");
    }
    repr.append(components[(numComponents - 1)]);
    repr.append("]");
    
    return repr.toString();
  }
  
  public static QuaternionKey valueOf(String s) {
    StringTokenizer st = new StringTokenizer(s, " \t,[]");
    
    String className = st.nextToken();
    double time = Double.parseDouble(st.nextToken());
    Quaternion value = new Quaternion(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
    
    return new QuaternionKey(time, value);
  }
}
