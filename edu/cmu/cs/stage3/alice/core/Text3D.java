package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.FontProperty;
import edu.cmu.cs.stage3.alice.core.property.GeometryProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;




























public class Text3D
  extends Model
{
  public Text3D() {}
  
  public final StringProperty text = new StringProperty(this, "text", null);
  public final FontProperty font = new FontProperty(this, "font", null);
  public final NumberProperty extrusion = new NumberProperty(this, "extrusion", new Double(0.25D));
  public final NumberProperty curvature = new NumberProperty(this, "curvature", new Integer(2));
  
  public void create3DTextGeometry() {
    edu.cmu.cs.stage3.alice.core.geometry.Text3D geom = new edu.cmu.cs.stage3.alice.core.geometry.Text3D();
    curvature.set(curvature.get());
    font.set(font.get());
    text.set(text.get());
    addChild(geom);
    geometry.set(geom);
  }
  
  protected void propertyChanged(Property property, Object value)
  {
    if ((geometry.getGeometryValue() != null) && ((geometry.getGeometryValue() instanceof edu.cmu.cs.stage3.alice.core.geometry.Text3D))) {
      if (property == text) {
        geometry.getGeometryValue()).text.set(value);
      } else if (property == font) {
        geometry.getGeometryValue()).font.set(value);
      } else if (property == extrusion) {
        geometry.getGeometryValue()).extrusion.set(value);
      } else if (property == curvature) {
        geometry.getGeometryValue()).curvature.set(value);
      } else
        super.propertyChanged(property, value);
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  protected void propertyChanging(Property property, Object value) {
    if ((property == geometry) && (value != null) && (!(value instanceof edu.cmu.cs.stage3.alice.core.geometry.Text3D))) {
      throw new ClassCastException(Messages.getString("A_3D_text_model_s_geometry_must_be_a_3D_text_geometry"));
    }
    super.propertyChanging(property, value);
  }
}
