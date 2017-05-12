package edu.cmu.cs.stage3.pratt.maxkeyframing;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.response.TransformResponse;













public abstract class KeyframeResponse
  extends TransformResponse
{
  public final ObjectProperty spline = new ObjectProperty(this, "spline", null, Spline.class);
  
  public KeyframeResponse() {}
  
  public abstract class RuntimeKeyframeResponse extends KeyframeResponse.Hack_RuntimeTransformResponse {
    public RuntimeKeyframeResponse() {
      super(); }
    protected Spline runtimeSpline = null;
    protected double splineDuration = 0.0D;
    protected double timeFactor = 1.0D;
    
    public void prologue(double t)
    {
      super.prologue(t);
      runtimeSpline = ((Spline)spline.getValue());
      if (runtimeSpline != null) {
        splineDuration = runtimeSpline.getDuration();
      } else {
        splineDuration = 0.0D;
      }
      timeFactor = 1.0D;
      if (!Double.isNaN(getDuration())) {
        timeFactor = (splineDuration / getDuration());
      } else {
        setDuration(splineDuration);
      }
    }
    
    public void epilogue(double t)
    {
      super.epilogue(t);
      runtimeSpline = null; } }
  
  abstract class Hack_RuntimeTransformResponse extends Response.RuntimeResponse { protected Transformable m_transformable;
    
    Hack_RuntimeTransformResponse() { super(); }
    
    protected ReferenceFrame m_asSeenBy;
    public void prologue(double t)
    {
      super.prologue(t);
      m_transformable = ((Transformable)subject.getValue());
      m_asSeenBy = ((ReferenceFrame)asSeenBy.getValue());
    }
  }
}
