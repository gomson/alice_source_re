package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.util.OneShot;


















public class OneShotSimpleBehavior
  extends OneShot
{
  protected Property[] affectedProperties;
  
  public OneShotSimpleBehavior() {}
  
  public Property[] getAffectedProperties()
  {
    return affectedProperties;
  }
  
  public void setAffectedProperties(Property[] affectedProperties) { this.affectedProperties = affectedProperties; }
}
