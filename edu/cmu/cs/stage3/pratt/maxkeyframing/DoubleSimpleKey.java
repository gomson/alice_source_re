package edu.cmu.cs.stage3.pratt.maxkeyframing;

import java.util.StringTokenizer;






















public class DoubleSimpleKey
  extends SimpleKey
{
  public DoubleSimpleKey(double time, double value)
  {
    super(time, new double[] { value });
  }
  
  public Object createSample(double[] components)
  {
    return new Double(components[0]);
  }
  
  public static DoubleSimpleKey valueOf(String s) {
    StringTokenizer st = new StringTokenizer(s, " \t,[]");
    
    String className = st.nextToken();
    double time = Double.parseDouble(st.nextToken());
    double value = Double.parseDouble(st.nextToken());
    
    return new DoubleSimpleKey(time, value);
  }
}
