package edu.cmu.cs.stage3.pratt.maxkeyframing;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.lang.Messages;
import java.io.PrintStream;
import javax.vecmath.Vector3d;














public class PositionKeyframeResponse
  extends KeyframeResponse
{
  public PositionKeyframeResponse() {}
  
  public class RuntimePositionKeyframeResponse
    extends KeyframeResponse.RuntimeKeyframeResponse
  {
    public RuntimePositionKeyframeResponse() { super(); }
    
    public void update(double t) {
      double timeElapsed = getTimeElapsed(t);
      double splineTimeElapsed = timeElapsed * timeFactor;
      try
      {
        Vector3d pos = (Vector3d)runtimeSpline.getSample(splineTimeElapsed);
        







        if (pos != null) {
          m_transformable.setPositionRightNow(pos);
        }
      }
      catch (ClassCastException e) {
        System.err.println(Messages.getString("Incorrect_sample_type_from_spline_") + runtimeSpline + Messages.getString("___Vector3_expected___Instead__got__") + runtimeSpline.getSample(splineTimeElapsed));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
