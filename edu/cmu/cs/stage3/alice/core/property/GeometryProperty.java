package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Geometry;





















public class GeometryProperty
  extends ElementProperty
{
  public GeometryProperty(Element owner, String name, Geometry defaultValue)
  {
    super(owner, name, defaultValue, Geometry.class);
  }
  
  public Geometry getGeometryValue() { return (Geometry)getElementValue(); }
}
