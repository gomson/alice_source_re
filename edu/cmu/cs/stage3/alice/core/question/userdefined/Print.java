package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import java.io.PrintStream;




















public class Print
  extends Component
{
  public final StringProperty text = new StringProperty(this, "text", "");
  public final ObjectProperty object = new ObjectProperty(this, "object", null, Object.class)
  {

    protected boolean getValueOfExpression() { return true; } };
  
  public Print() {}
  public final BooleanProperty addNewLine = new BooleanProperty(this, "addNewLine", Boolean.TRUE);
  
  public Object[] execute() {
    String output = text.getStringValue();
    Object o = object.get();
    if (o != null) {
      o = object.getValue();
      output = output + o;
    }
    if (addNewLine.booleanValue()) {
      System.out.println(output);
    } else {
      System.out.print(output);
    }
    return null;
  }
}
