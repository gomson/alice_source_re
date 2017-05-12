package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Dimension;
import edu.cmu.cs.stage3.alice.core.Element;





















public class DimensionProperty
  extends EnumerableProperty
{
  public DimensionProperty(Element owner, String name, Dimension defaultValue)
  {
    super(owner, name, defaultValue, Dimension.class);
  }
  
  public Dimension getDimensionValue() { return (Dimension)getEnumerableValue(); }
}
