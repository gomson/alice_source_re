package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;




















public class SendMessage
  extends Response
{
  public final StringProperty message = new StringProperty(this, "message", null);
  public final TransformableProperty fromWho = new TransformableProperty(this, "fromWho", null);
  public final TransformableProperty toWhom = new TransformableProperty(this, "toWhom", null);
  public class RuntimeSendMessage extends Response.RuntimeResponse { public RuntimeSendMessage() { super(); }
    
    public void prologue(double t) {
      super.prologue(t);
      String messageValue = message.getStringValue();
      Transformable fromWhoValue = fromWho.getTransformableValue();
      Transformable toWhomValue = toWhom.getTransformableValue();
      World world = getWorld();
      if (world != null) {
        world.sendMessage(SendMessage.this, messageValue, fromWhoValue, toWhomValue, System.currentTimeMillis());
      }
    }
  }
  
  public SendMessage() {}
}
