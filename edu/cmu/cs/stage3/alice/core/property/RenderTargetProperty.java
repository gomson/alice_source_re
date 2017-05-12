package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.RenderTarget;





















public class RenderTargetProperty
  extends ElementProperty
{
  public RenderTargetProperty(Element owner, String name, RenderTarget defaultValue)
  {
    super(owner, name, defaultValue, RenderTarget.class);
  }
  
  public RenderTarget getRenderTargetValue() { return (RenderTarget)getElementValue(); }
}
