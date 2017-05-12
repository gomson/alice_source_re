package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ListProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;
import java.util.Vector;












public class ForEach
  extends Composite
{
  public final VariableProperty each = new VariableProperty(this, "each", null);
  

  public final ListProperty list = new ListProperty(this, "list", null);
  
  public ForEach() {}
  
  protected void internalFindAccessibleExpressions(Class cls, Vector v) { internalAddExpressionIfAssignableTo((Expression)each.get(), cls, v);
    super.internalFindAccessibleExpressions(cls, v);
  }
  
  public Object[] execute()
  {
    World world = getWorld();
    if (world != null) {
      Sandbox sandbox = world.getCurrentSandbox();
      if (sandbox != null) {
        Behavior currentBehavior = sandbox.getCurrentBehavior();
        if (currentBehavior != null) {
          List listValue = list.getListValue();
          if (listValue.size() > 0) {
            Variable eachVariable = each.getVariableValue();
            Variable eachRuntimeVariable = new Variable();
            valueClass.set(valueClass.get());
            for (int i = 0; i < listValue.size(); i++) {
              value.set(values.get(i));
              currentBehavior.pushEach(eachVariable, eachRuntimeVariable);
              Object[] returnValue = super.execute();
              currentBehavior.popStack();
              if (returnValue != null) {
                return returnValue;
              }
            }
          }
        }
      }
    }
    return null;
  }
}
