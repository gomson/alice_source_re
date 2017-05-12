package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Transformable;





















public class TransformableProperty
  extends ReferenceFrameProperty
{
  protected TransformableProperty(Element owner, String name, Transformable defaultValue, Class valueClass)
  {
    super(owner, name, defaultValue, valueClass);
  }
  
  public TransformableProperty(Element owner, String name, Transformable defaultValue) { this(owner, name, defaultValue, Transformable.class); }
  
  public Transformable getTransformableValue() {
    return (Transformable)getReferenceFrameValue();
  }
}
