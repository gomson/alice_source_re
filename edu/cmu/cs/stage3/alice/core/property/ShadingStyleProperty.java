package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle;





















public class ShadingStyleProperty
  extends EnumerableProperty
{
  public ShadingStyleProperty(Element owner, String name, ShadingStyle defaultValue)
  {
    super(owner, name, defaultValue, ShadingStyle.class);
  }
  
  public ShadingStyle getShadingStyleValue() { return (ShadingStyle)getEnumerableValue(); }
}
