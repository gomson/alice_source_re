package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import javax.swing.JLabel;


















public class PropertyLabel
  extends JLabel
{
  protected Property property;
  
  public PropertyLabel(Property property)
  {
    this.property = property;
    setOpaque(false);
    property.addPropertyListener(
      new PropertyListener() {
        public void propertyChanging(PropertyEvent propertyEvent) {}
        
        public void propertyChanged(PropertyEvent propertyEvent) { update();
        }

      });
    update();
  }
  
  public void update() {
    setText(AuthoringToolResources.getReprForValue(property.get(), property));
  }
}
