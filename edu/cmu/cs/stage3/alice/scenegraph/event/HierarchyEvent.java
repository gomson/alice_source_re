package edu.cmu.cs.stage3.alice.scenegraph.event;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import java.util.EventObject;


















public class HierarchyEvent
  extends EventObject
{
  public HierarchyEvent(Component source)
  {
    super(source);
  }
}
