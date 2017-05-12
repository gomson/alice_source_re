package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;
import java.util.Vector;
















public class LoopN
  extends Composite
{
  public LoopN() {}
  
  public final NumberProperty count = new NumberProperty(this, "count", new Double(Double.POSITIVE_INFINITY));
  
  public final VariableProperty index = new VariableProperty(this, "index", null);
  
  public final NumberProperty start = new NumberProperty(this, "start", new Double(0.0D));
  public final NumberProperty end = new NumberProperty(this, "end", new Double(Double.POSITIVE_INFINITY));
  public final NumberProperty increment = new NumberProperty(this, "increment", new Double(1.0D));
  
  protected void internalFindAccessibleExpressions(Class cls, Vector v) {
    internalAddExpressionIfAssignableTo((Expression)index.get(), cls, v);
    super.internalFindAccessibleExpressions(cls, v);
  }
  
  public Object[] execute() {
    if (start.get() == null) {
      throw new NullPointerException();
    }
    if (end.get() == null) {
      throw new NullPointerException();
    }
    if (index.get() == null) {
      throw new NullPointerException();
    }
    Variable indexVariable = index.getVariableValue();
    double lcv = start.doubleValue();
    while (lcv < end.doubleValue()) {
      value.set(new Double(lcv));
      Object[] value = super.execute();
      lcv += increment.doubleValue(1.0D);
      if (value != null) {
        return value;
      }
    }
    return null;
  }
}
