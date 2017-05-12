package edu.cmu.cs.stage3.pratt.maxkeyframing;

import java.util.StringTokenizer;






















public class DoubleTCBKey
  extends TCBKey
{
  public DoubleTCBKey(double time, double value, double tension, double continuity, double bias)
  {
    super(time, new double[] { value }, tension, continuity, bias);
  }
  
  public Object createSample(double[] components)
  {
    return new Double(components[0]);
  }
  
  public static DoubleTCBKey valueOf(String s) {
    StringTokenizer st = new StringTokenizer(s, " \t,[]");
    
    String className = st.nextToken();
    double time = Double.parseDouble(st.nextToken());
    double value = Double.parseDouble(st.nextToken());
    double tension = Double.parseDouble(st.nextToken());
    double continuity = Double.parseDouble(st.nextToken());
    double bias = Double.parseDouble(st.nextToken());
    
    return new DoubleTCBKey(time, value, tension, continuity, bias);
  }
}
