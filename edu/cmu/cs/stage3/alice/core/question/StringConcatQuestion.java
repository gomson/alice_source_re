package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
































public class StringConcatQuestion
  extends Question
{
  public final StringProperty a = new StringProperty(this, "a", new String(""));
  public final StringProperty b = new StringProperty(this, "b", new String(""));
  

  public StringConcatQuestion() {}
  
  public Class getValueClass()
  {
    return String.class;
  }
  

  public Object getValue()
  {
    return a.getStringValue() + b.getStringValue();
  }
}
