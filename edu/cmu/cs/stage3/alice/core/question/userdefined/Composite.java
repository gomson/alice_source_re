package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;




















public abstract class Composite
  extends Component
{
  public Composite() {}
  
  public final ElementArrayProperty components = new ElementArrayProperty(this, "components", null, [Ledu.cmu.cs.stage3.alice.core.question.userdefined.Component.class);
  
  protected Object[] execute(ElementArrayProperty eap) {
    for (int i = 0; i < eap.size(); i++) {
      Component component = (Component)eap.get(i);
      Object[] value = component.execute();
      if (value != null) {
        return value;
      }
    }
    return null;
  }
  
  public Object[] execute()
  {
    return execute(components);
  }
}
