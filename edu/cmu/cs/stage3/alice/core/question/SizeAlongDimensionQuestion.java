package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Dimension;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.lang.Messages;



















public abstract class SizeAlongDimensionQuestion
  extends Question
{
  public SizeAlongDimensionQuestion() {}
  
  public final TransformableProperty subject = new TransformableProperty(this, "subject", null);
  
  protected abstract Dimension getDimension();
  
  public Object getValue() { Transformable subjectValue = subject.getTransformableValue();
    if (subjectValue != null) {
      if ((this instanceof Width)) {
        edu.cmu.cs.stage3.alice.core.response.Print.outputtext = name.getStringValue() + Messages.getString("_s_width_is_");
      } else if ((this instanceof Height)) {
        edu.cmu.cs.stage3.alice.core.response.Print.outputtext = name.getStringValue() + Messages.getString("_s_height_is_");
      } else if ((this instanceof Depth)) {
        edu.cmu.cs.stage3.alice.core.response.Print.outputtext = name.getStringValue() + Messages.getString("_s_depth_is_");
      }
      return new Double(subjectValue.getSizeAlongDimension(getDimension()));
    }
    return null;
  }
  
  public Class getValueClass()
  {
    return Number.class;
  }
}
