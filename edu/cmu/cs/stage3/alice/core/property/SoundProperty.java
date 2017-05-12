package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Sound;





















public class SoundProperty
  extends ElementProperty
{
  public SoundProperty(Element owner, String name, Sound defaultValue)
  {
    super(owner, name, defaultValue, Sound.class);
  }
  
  public Sound getSoundValue() { return (Sound)getElementValue(); }
}
