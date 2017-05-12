package edu.cmu.cs.stage3.pratt.maxkeyframing;

import edu.cmu.cs.stage3.math.Quaternion;
import java.util.StringTokenizer;





















public class QuaternionTCBKey
  extends TCBKey
{
  public QuaternionTCBKey(double time, Quaternion value, double tension, double continuity, double bias)
  {
    super(time, new double[] { x, y, z, w }, tension, continuity, bias);
  }
  
  private Quaternion qSample = new Quaternion();
  
  public Object createSample(double[] components) {
    double lengthSquared = components[0] * components[0] + components[1] * components[1] + components[2] * components[2] + components[3] * components[3];
    if (lengthSquared == 1.0D) {
      qSample.x = components[0];
      qSample.y = components[1];
      qSample.z = components[2];
      qSample.w = components[3];
      return qSample;
    }
    
    double length = Math.sqrt(lengthSquared);
    qSample.x = (components[0] / length);
    qSample.y = (components[1] / length);
    qSample.z = (components[2] / length);
    qSample.w = (components[3] / length);
    return qSample;
  }
  

  public static QuaternionTCBKey valueOf(String s)
  {
    StringTokenizer st = new StringTokenizer(s, " \t,[]");
    
    String className = st.nextToken();
    double time = Double.parseDouble(st.nextToken());
    Quaternion value = new Quaternion(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
    double tension = Double.parseDouble(st.nextToken());
    double continuity = Double.parseDouble(st.nextToken());
    double bias = Double.parseDouble(st.nextToken());
    
    return new QuaternionTCBKey(time, value, tension, continuity, bias);
  }
}
