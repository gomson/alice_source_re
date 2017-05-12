package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.property.StringProperty;




















public class Comment
  extends Component
{
  public Comment() {}
  
  public final StringProperty text = new StringProperty(this, "text", "");
  
  public Object[] execute() {
    return null;
  }
}
