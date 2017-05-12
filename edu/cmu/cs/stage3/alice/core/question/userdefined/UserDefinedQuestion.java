package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import java.util.Vector;


















public class UserDefinedQuestion
  extends Question
{
  public UserDefinedQuestion() {}
  
  public final ClassProperty valueClass = new ClassProperty(this, "valueClass", null);
  public final ElementArrayProperty components = new ElementArrayProperty(this, "components", null, [Ledu.cmu.cs.stage3.alice.core.question.userdefined.Component.class);
  public final ElementArrayProperty requiredFormalParameters = new ElementArrayProperty(this, "requiredFormalParameters", null, [Ledu.cmu.cs.stage3.alice.core.Variable.class);
  public final ElementArrayProperty keywordFormalParameters = new ElementArrayProperty(this, "keywordFormalParameters", null, [Ledu.cmu.cs.stage3.alice.core.Variable.class);
  public final ElementArrayProperty localVariables = new ElementArrayProperty(this, "localVariables", null, [Ledu.cmu.cs.stage3.alice.core.Variable.class);
  
  protected void internalFindAccessibleExpressions(Class cls, Vector v) {
    for (int i = 0; i < requiredFormalParameters.size(); i++) {
      internalAddExpressionIfAssignableTo((Expression)requiredFormalParameters.get(i), cls, v);
    }
    for (int i = 0; i < keywordFormalParameters.size(); i++) {
      internalAddExpressionIfAssignableTo((Expression)keywordFormalParameters.get(i), cls, v);
    }
    for (int i = 0; i < localVariables.size(); i++) {
      internalAddExpressionIfAssignableTo((Expression)localVariables.get(i), cls, v);
    }
    super.internalFindAccessibleExpressions(cls, v);
  }
  
  public Object getValue() {
    for (int i = 0; i < components.size(); i++) {
      Component component = (Component)components.get(i);
      Object[] value = component.execute();
      if (value != null) {
        return value[0];
      }
    }
    return null;
  }
  
  public Class getValueClass() {
    return valueClass.getClassValue();
  }
}
