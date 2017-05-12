package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.question.math.ATan2;
import edu.cmu.cs.stage3.alice.core.question.math.IEEERemainder;
import edu.cmu.cs.stage3.alice.core.question.math.Max;
import edu.cmu.cs.stage3.alice.core.question.math.Min;
import edu.cmu.cs.stage3.alice.core.question.math.Pow;
import edu.cmu.cs.stage3.lang.Messages;















public abstract class BinaryNumberResultingInNumberQuestion
  extends NumberQuestion
{
  public BinaryNumberResultingInNumberQuestion() {}
  
  public final NumberProperty a = new NumberProperty(this, "a", new Double(0.0D));
  public final NumberProperty b = new NumberProperty(this, "b", new Double(0.0D));
  
  protected abstract double getValue(double paramDouble1, double paramDouble2);
  
  public Object getValue() { double aValue = a.doubleValue();
    double bValue = b.doubleValue();
    if ((this instanceof Min)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("minimum_of_") + aValue + " " + Messages.getString("and_") + bValue + " " + Messages.getString("is_");
    } else if ((this instanceof Max)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("maximum_of_") + aValue + " " + Messages.getString("and_") + bValue + " " + Messages.getString("is_");
    } else if ((this instanceof ATan2)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "arctan2 " + aValue + " " + bValue + " " + Messages.getString("is_");
    } else if ((this instanceof Pow)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = aValue + Messages.getString("_raised_to_the_") + bValue + " " + Messages.getString("power_is_");
    } else if ((this instanceof IEEERemainder)) {
      edu.cmu.cs.stage3.alice.core.response.Print.outputtext = "IEEERemainder of " + aValue + "/" + bValue + " " + Messages.getString("is_");
    }
    return new Double(getValue(aValue, bValue));
  }
}
