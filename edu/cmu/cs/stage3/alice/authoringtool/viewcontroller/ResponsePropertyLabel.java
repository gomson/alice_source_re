package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.ResponseProperty;


















/**
 * @deprecated
 */
public class ResponsePropertyLabel
  extends PropertyLabel
{
  public ResponsePropertyLabel(ResponseProperty property)
  {
    super(property);
  }
  
  public void update() {
    setText(AuthoringToolResources.getReprForValue(property.get(), property));
  }
}
