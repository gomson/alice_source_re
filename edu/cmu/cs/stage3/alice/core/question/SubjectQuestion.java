package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;




















public abstract class SubjectQuestion
  extends Question
{
  public SubjectQuestion() {}
  
  public final TransformableProperty subject = new TransformableProperty(this, "subject", null);
  
  protected abstract Object getValue(Transformable paramTransformable);
  
  public Object getValue() { Transformable subjectValue = subject.getTransformableValue();
    return getValue(subjectValue);
  }
}
