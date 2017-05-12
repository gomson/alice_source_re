package edu.cmu.cs.stage3.alice.scenegraph.event;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import java.util.EventObject;


















public class AbsoluteTransformationEvent
  extends EventObject
{
  public AbsoluteTransformationEvent(Component source)
  {
    super(source);
  }
}
