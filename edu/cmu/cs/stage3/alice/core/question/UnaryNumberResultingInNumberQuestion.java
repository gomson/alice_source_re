package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.question.math.ACos;
import edu.cmu.cs.stage3.alice.core.question.math.ASin;
import edu.cmu.cs.stage3.alice.core.question.math.ATan;
import edu.cmu.cs.stage3.alice.core.question.math.Abs;
import edu.cmu.cs.stage3.alice.core.question.math.Ceil;
import edu.cmu.cs.stage3.alice.core.question.math.Cos;
import edu.cmu.cs.stage3.alice.core.question.math.Exp;
import edu.cmu.cs.stage3.alice.core.question.math.Floor;
import edu.cmu.cs.stage3.alice.core.question.math.Int;
import edu.cmu.cs.stage3.alice.core.question.math.Log;
import edu.cmu.cs.stage3.alice.core.question.math.Round;
import edu.cmu.cs.stage3.alice.core.question.math.Sin;
import edu.cmu.cs.stage3.alice.core.question.math.Sqrt;
import edu.cmu.cs.stage3.alice.core.question.math.Tan;
import edu.cmu.cs.stage3.alice.core.question.math.ToDegrees;
import edu.cmu.cs.stage3.alice.core.question.math.ToRadians;
import edu.cmu.cs.stage3.lang.Messages;




public abstract class UnaryNumberResultingInNumberQuestion
  extends NumberQuestion
{
  public UnaryNumberResultingInNumberQuestion() {}
  
  public final NumberProperty a = new NumberProperty(this, "a", new Double(0.0D));
  
  protected abstract double getValue(double paramDouble);
  
  public Object getValue() { double aValue = a.doubleValue(0.0D);
    if ((this instanceof Abs)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("absolute_value_of_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof Sqrt)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("square_root_of_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof Floor)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("floor_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof Ceil)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("ceiling_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof Sin)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("sin_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof Cos)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("cos_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof Tan)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("tan_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof ACos)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("arccos_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof ASin)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("arcsin_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof ATan)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("arctan_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof Log)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("natural_log_of_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof Exp)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("e_raised_to_the_") + aValue + " " + Messages.getString("power_is_");
    } else if ((this instanceof Round)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("round_") + aValue + " " + Messages.getString("is_");
    } else if ((this instanceof ToRadians)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = aValue + " " + Messages.getString("converted_from_radians_to_degrees_is_");
    } else if ((this instanceof ToDegrees)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = aValue + " " + Messages.getString("converted_from_degrees_to_radians_is_");
    }
    if ((this instanceof Int)) {
      return Integer.valueOf((int)aValue);
    }
    return new Double(getValue(aValue));
  }
}
