package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;





















public class ReferenceFrameProperty
  extends ElementProperty
{
  protected ReferenceFrameProperty(Element owner, String name, ReferenceFrame defaultValue, Class valueClass)
  {
    super(owner, name, defaultValue, valueClass);
  }
  
  public ReferenceFrameProperty(Element element, String name, ReferenceFrame defaultValue) { this(element, name, defaultValue, ReferenceFrame.class); }
  
  public ReferenceFrame getReferenceFrameValue() {
    return (ReferenceFrame)getElementValue();
  }
}
