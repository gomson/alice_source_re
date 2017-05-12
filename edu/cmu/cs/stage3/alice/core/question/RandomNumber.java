package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;




















public class RandomNumber
  extends Question
{
  public RandomNumber() {}
  
  public final NumberProperty minimum = new NumberProperty(this, "minimum", new Double(0.0D));
  public final NumberProperty maximum = new NumberProperty(this, "maximum", new Double(1.0D));
  public final BooleanProperty integerOnly = new BooleanProperty(this, "integerOnly", Boolean.FALSE);
  
  public Object getValue() {
    double minimumValue = minimum.doubleValue();
    double maximumValue = maximum.doubleValue();
    if (minimumValue > maximumValue) {
      double temp = minimumValue;
      minimumValue = maximumValue;
      maximumValue = temp;
    }
    double r = Math.random();
    double value = r * (maximumValue - minimumValue) + minimumValue;
    if (integerOnly.booleanValue())
    {
      return new Double((int)value);
    }
    return new Double(value);
  }
  
  public Class getValueClass()
  {
    if (integerOnly.booleanValue()) {
      return Integer.class;
    }
    return Number.class;
  }
}
