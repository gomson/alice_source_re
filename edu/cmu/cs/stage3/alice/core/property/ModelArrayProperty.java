package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Model;

public class ModelArrayProperty extends ElementArrayProperty
{
  public ModelArrayProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, Model[] defaultValue) {
    super(owner, name, defaultValue, [Ledu.cmu.cs.stage3.alice.core.Model.class);
  }
  
  public Model[] getModelArrayValue() { return (Model[])getElementArrayValue(); }
}
