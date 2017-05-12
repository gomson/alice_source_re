package edu.cmu.cs.stage3.alice.scenegraph.event;

import edu.cmu.cs.stage3.alice.scenegraph.Element;
import java.util.EventObject;


















public class ReleaseEvent
  extends EventObject
{
  public ReleaseEvent(Element source)
  {
    super(source);
  }
}
