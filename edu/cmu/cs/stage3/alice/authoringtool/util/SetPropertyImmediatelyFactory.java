package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;



















public class SetPropertyImmediatelyFactory
  implements PopupItemFactory
{
  protected Property property;
  
  public SetPropertyImmediatelyFactory(Property property)
  {
    this.property = property;
  }
  
  public Object createItem(final Object value) {
    new Runnable() {
      public void run() {
        run(value);
      }
    };
  }
  
  protected void run(Object value) {
    if ((value instanceof ElementPrototype)) {
      value = ((ElementPrototype)value).createNewElement();
    }
    
    if ((value instanceof Element)) {
      Element element = (Element)value;
      if ((element.getParent() == null) && (!(element instanceof World))) {
        property.getOwner().addChild(element);
        data.put("associatedProperty", property.getName());
      }
    }
    
    property.set(value);
  }
}
