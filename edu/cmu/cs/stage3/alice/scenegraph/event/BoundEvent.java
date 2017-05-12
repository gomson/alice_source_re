package edu.cmu.cs.stage3.alice.scenegraph.event;

import edu.cmu.cs.stage3.alice.scenegraph.Geometry;
import java.util.EventObject;


















public class BoundEvent
  extends EventObject
{
  public BoundEvent(Geometry source)
  {
    super(source);
  }
}
