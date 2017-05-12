package edu.cmu.cs.stage3.pratt.maxkeyframing;

import java.util.StringTokenizer;
import javax.vecmath.Vector3d;





















public class Vector3TCBKey
  extends TCBKey
{
  public Vector3TCBKey(double time, Vector3d value, double tension, double continuity, double bias)
  {
    super(time, new double[] { x, y, z }, tension, continuity, bias);
  }
  
  private Vector3d vSample = new Vector3d();
  
  public Object createSample(double[] components) {
    vSample.x = components[0];
    vSample.y = components[1];
    vSample.z = components[2];
    return vSample;
  }
  
  public static Vector3TCBKey valueOf(String s)
  {
    StringTokenizer st = new StringTokenizer(s, " \t,[]");
    
    String className = st.nextToken();
    double time = Double.parseDouble(st.nextToken());
    Vector3d value = new Vector3d(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
    double tension = Double.parseDouble(st.nextToken());
    double continuity = Double.parseDouble(st.nextToken());
    double bias = Double.parseDouble(st.nextToken());
    
    return new Vector3TCBKey(time, value, tension, continuity, bias);
  }
}
