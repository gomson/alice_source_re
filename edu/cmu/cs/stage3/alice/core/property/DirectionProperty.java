package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Element;





















public class DirectionProperty
  extends EnumerableProperty
{
  public DirectionProperty(Element owner, String name, Direction defaultValue)
  {
    super(owner, name, defaultValue, Direction.class);
  }
  
  public Direction getDirectionValue() { return (Direction)getEnumerableValue(); }
}
