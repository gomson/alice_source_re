package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import java.io.PrintStream;


















public class VehiclePropertyAnimation
  extends PropertyAnimation
{
  public VehiclePropertyAnimation()
  {
    propertyName.set("vehicle");
  }
  
  protected void propertyChanging(Property property, Object value) {
    if (property == propertyName)
    {
      if (!((String)value).equals("vehicle"))
      {

        System.err.println("propertyName: " + value);
        throw new RuntimeException("VehiclePropertyAnimation should be animating \"vehicle\" property.  Attempted to change to the \"" + value + "\" property.");
      }
    } else
      super.propertyChanging(property, value);
  }
  
  public class RuntimeVehiclePropertyAnimation extends PropertyAnimation.RuntimePropertyAnimation { public RuntimeVehiclePropertyAnimation() { super(); }
    
    protected void set(Object value) {
      VehicleProperty vehicleProperty = (VehicleProperty)getProperty();
      if (vehicleProperty != null) {
        vehicleProperty.set(value, true);
      } else {
        throw new RuntimeException();
      }
    }
  }
}
