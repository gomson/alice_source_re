package edu.cmu.cs.stage3.pratt.maxkeyframing;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.PrintStream;
import javax.vecmath.Vector3d;















public class ScaleKeyframeResponse
  extends KeyframeResponse
{
  public ScaleKeyframeResponse() {}
  
  public class RuntimeScaleKeyframeResponse
    extends KeyframeResponse.RuntimeKeyframeResponse
  {
    public RuntimeScaleKeyframeResponse() { super(); }
    
    public void update(double t) {
      double timeElapsed = getTimeElapsed(t);
      double splineTimeElapsed = timeElapsed * timeFactor;
      try
      {
        Vector3d localVector3d = (Vector3d)runtimeSpline.getSample(splineTimeElapsed);
      }
      catch (ClassCastException e)
      {
        System.err.println(Messages.getString("Incorrect_sample_type_from_spline_") + runtimeSpline + Messages.getString("___Vector3_expected_"));
      }
    }
  }
}
