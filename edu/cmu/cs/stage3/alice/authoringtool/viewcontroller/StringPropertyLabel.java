package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.OverridableElementProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.response.PropertyAnimation;

















public class StringPropertyLabel
  extends PropertyLabel
{
  public StringPropertyLabel(StringProperty property)
  {
    super(property);
  }
  
  public void update() {
    String propertyName = ((StringProperty)property).getStringValue();
    if (((property.getOwner() instanceof PropertyAnimation)) && (property.getName().equals("propertyName"))) {
      Element element = property.getOwner()).element.getElementValue();
      if (element != null) {
        Class elementClass = element.getClass();
        while (Element.class.isAssignableFrom(elementClass)) {
          String propertyKey = elementClass.getName() + "." + propertyName;
          if (AuthoringToolResources.getName(propertyKey) != null) {
            propertyName = AuthoringToolResources.getName(propertyKey);
            break;
          }
          elementClass = elementClass.getSuperclass();
        }
      }
    }
    setText(propertyName);
  }
}
