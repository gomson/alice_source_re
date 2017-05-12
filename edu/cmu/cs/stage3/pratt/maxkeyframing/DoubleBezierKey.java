package edu.cmu.cs.stage3.pratt.maxkeyframing;

import java.util.StringTokenizer;






















public class DoubleBezierKey
  extends BezierKey
{
  public DoubleBezierKey(double time, double value, double incomingControl, double outgoingControl)
  {
    super(time, new double[] { value }, new double[] { incomingControl }, new double[] { outgoingControl });
  }
  
  public Object createSample(double[] components)
  {
    return new Double(components[0]);
  }
  
  public static DoubleBezierKey valueOf(String s) {
    StringTokenizer st = new StringTokenizer(s, " \t,[]");
    
    String className = st.nextToken();
    double time = Double.parseDouble(st.nextToken());
    double value = Double.parseDouble(st.nextToken());
    double incomingControl = Double.parseDouble(st.nextToken());
    double outgoingControl = Double.parseDouble(st.nextToken());
    
    return new DoubleBezierKey(time, value, incomingControl, outgoingControl);
  }
}
