package edu.cmu.cs.stage3.alice.core.style;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Style;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.math.BezierCubic;
import edu.cmu.cs.stage3.math.Cubic;















public class CubicStyle
  extends Element
  implements Style
{
  public CubicStyle() {}
  
  public final ObjectProperty controls = new ObjectProperty(this, "controls", null, [D.class);
  
  public double getPortion(double current, double total) { double[] controlsValue = (double[])controls.getValue();
    if (controlsValue != null) {
      Cubic cubic = new BezierCubic(controlsValue[0], controlsValue[1], controlsValue[2], controlsValue[3]);
      double t = current / total;
      return cubic.evaluate(t);
    }
    return 0.0D;
  }
}
