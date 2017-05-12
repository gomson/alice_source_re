package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.lang.Messages;




















public abstract class TransformResponse
  extends Response
{
  public final TransformableProperty subject = new TransformableProperty(this, "subject", null);
  public final ReferenceFrameProperty asSeenBy = new ReferenceFrameProperty(this, "asSeenBy", null);
  public abstract class RuntimeTransformResponse extends Response.RuntimeResponse { public RuntimeTransformResponse() { super(); }
    
    protected Transformable m_subject;
    protected ReferenceFrame m_asSeenBy;
    public void prologue(double t) {
      super.prologue(t);
      m_subject = subject.getTransformableValue();
      m_asSeenBy = asSeenBy.getReferenceFrameValue();
      if (m_subject == null) {
        throw new SimulationPropertyException(Messages.getString("subject_must_not_be_null_"), getCurrentStack(), subject);
      }
    }
  }
  
  public TransformResponse() {}
}
