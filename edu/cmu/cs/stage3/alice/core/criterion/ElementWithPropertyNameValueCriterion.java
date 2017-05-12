package edu.cmu.cs.stage3.alice.core.criterion;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.util.Criterion;







public class ElementWithPropertyNameValueCriterion
  implements Criterion
{
  private String propertyName = null;
  private Object propertyValue = null;
  private boolean returnEqual = true;
  
  public ElementWithPropertyNameValueCriterion(String propertyName, Object value) {
    this(propertyName, value, true);
  }
  
  public ElementWithPropertyNameValueCriterion(String propertyName, Object value, boolean returnEqual) {
    this.propertyName = propertyName;
    propertyValue = value;
    this.returnEqual = returnEqual;
  }
  
  public boolean accept(Object o) {
    if ((o instanceof Element)) {
      Element element = (Element)o;
      
      Property property = element.getPropertyNamed(propertyName);
      if (property != null) {
        if (property.getValue().equals(propertyValue)) {
          if (returnEqual) return true;
          return false;
        }
        if (returnEqual) return false;
        return true;
      }
      return false; }
    return false;
  }
}
