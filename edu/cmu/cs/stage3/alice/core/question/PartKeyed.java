package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;


















public class PartKeyed
  extends Question
{
  public PartKeyed() {}
  
  public final TransformableProperty owner = new TransformableProperty(this, "owner", null);
  public final StringProperty key = new StringProperty(this, "key", "");
  public final BooleanProperty ignoreCase = new BooleanProperty(this, "ignoreCase", Boolean.TRUE);
  
  public Object getValue() {
    Transformable parentValue = owner.getTransformableValue();
    String keyValue = key.getStringValue();
    if (ignoreCase.booleanValue()) {
      return parentValue.getPartKeyedIgnoreCase(keyValue);
    }
    return parentValue.getPartKeyed(keyValue);
  }
  
  public Class getValueClass()
  {
    return Model.class;
  }
}
