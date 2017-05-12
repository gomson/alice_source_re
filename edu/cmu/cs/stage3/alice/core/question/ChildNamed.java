package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;




















public class ChildNamed
  extends Question
{
  public ChildNamed() {}
  
  public final ElementProperty parent = new ElementProperty(this, "parent", null);
  public final StringProperty name = new StringProperty(this, "name", "");
  public final BooleanProperty ignoreCase = new BooleanProperty(this, "ignoreCase", Boolean.TRUE);
  
  public Object getValue() {
    Element parentValue = parent.getElementValue();
    String nameValue = name.getStringValue();
    if (ignoreCase.booleanValue()) {
      return parentValue.getChildNamed(nameValue);
    }
    return parentValue.getChildNamedIgnoreCase(nameValue);
  }
  
  public Class getValueClass()
  {
    return Element.class;
  }
}
