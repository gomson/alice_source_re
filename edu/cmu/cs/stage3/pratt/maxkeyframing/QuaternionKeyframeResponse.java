package edu.cmu.cs.stage3.pratt.maxkeyframing;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Quaternion;
import java.io.PrintStream;














public class QuaternionKeyframeResponse
  extends KeyframeResponse
{
  public QuaternionKeyframeResponse() {}
  
  public class RuntimeQuaternionKeyframeResponse
    extends KeyframeResponse.RuntimeKeyframeResponse
  {
    public RuntimeQuaternionKeyframeResponse() { super(); }
    
    public void update(double t) {
      double timeElapsed = getTimeElapsed(t);
      double splineTimeElapsed = timeElapsed * timeFactor;
      try
      {
        Quaternion q = (Quaternion)runtimeSpline.getSample(splineTimeElapsed);
        if (q != null) {
          m_transformable.setOrientationRightNow(q);
        }
      } catch (ClassCastException e) {
        System.err.println(Messages.getString("Incorrect_sample_type_from_spline_") + runtimeSpline + Messages.getString("___Quaternion_expected___Found_") + runtimeSpline.getSample(splineTimeElapsed));
      }
    }
  }
}
