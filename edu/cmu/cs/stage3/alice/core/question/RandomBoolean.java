package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.lang.Messages;




















public class RandomBoolean
  extends Question
{
  public RandomBoolean() {}
  
  public final NumberProperty probabilityOfTrue = new NumberProperty(this, "probabilityOfTrue", new Double(0.5D));
  
  public Object getValue() {
    edu.cmu.cs.stage3.alice.core.response.Print.outputtext = Messages.getString("choose_true_") + (int)(probabilityOfTrue.doubleValue() * 100.0D) + Messages.getString("__of_the_time_is_");
    if (Math.random() < probabilityOfTrue.doubleValue()) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
  
  public Class getValueClass()
  {
    return Boolean.class;
  }
}
