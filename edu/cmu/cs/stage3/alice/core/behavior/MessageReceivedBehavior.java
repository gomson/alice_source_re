package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.event.MessageEvent;
import edu.cmu.cs.stage3.alice.core.event.MessageListener;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;












public class MessageReceivedBehavior
  extends TriggerBehavior
  implements MessageListener
{
  public MessageReceivedBehavior() {}
  
  public final TransformableProperty fromWho = new TransformableProperty(this, "fromWho", null);
  public final TransformableProperty toWhom = new TransformableProperty(this, "toWhom", null);
  
  public void manufactureDetails() {
    super.manufactureDetails();
    Variable message = new Variable();
    name.set("message");
    message.setParent(this);
    valueClass.set(String.class);
    details.add(message);
    Variable fromWho = new Variable();
    name.set("fromWho");
    fromWho.setParent(this);
    valueClass.set(Transformable.class);
    details.add(fromWho);
    Variable toWhom = new Variable();
    name.set("message");
    toWhom.setParent(this);
    valueClass.set(Transformable.class);
    details.add(toWhom);
  }
  
  private void updateDetails(MessageEvent messageEvent) {
    for (int i = 0; i < details.size(); i++) {
      Variable detail = (Variable)details.get(i);
      if (name.getStringValue().equals("message")) {
        value.set(messageEvent.getMessage());
      } else if (name.getStringValue().equals("fromWho")) {
        value.set(messageEvent.getFromWho());
      } else if (name.getStringValue().equals("toWhom")) {
        value.set(messageEvent.getToWhom());
      }
    }
  }
  
  private boolean check(MessageEvent messageEvent) {
    Transformable fromWhoValue = fromWho.getTransformableValue();
    if ((fromWhoValue == null) || (messageEvent.getFromWho() == fromWhoValue)) {
      Transformable toWhomValue = toWhom.getTransformableValue();
      if ((toWhomValue == null) || (messageEvent.getToWhom() == toWhomValue)) {
        return true;
      }
    }
    return false;
  }
  
  public void messageSent(MessageEvent messageEvent) {
    if (check(messageEvent)) {
      updateDetails(messageEvent);
      trigger(messageEvent.getWhen() * 0.001D);
    }
  }
  
  protected void started(World world, double time) {
    super.started(world, time);
    world.addMessageListener(this);
  }
  
  protected void stopped(World world, double time) {
    super.stopped(world, time);
    world.removeMessageListener(this);
  }
}
