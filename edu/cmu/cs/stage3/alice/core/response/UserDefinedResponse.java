package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import java.util.Vector;



















public class UserDefinedResponse
  extends DoInOrder
{
  public UserDefinedResponse() {}
  
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
    super.internalFindAccessibleExpressions(cls, v); }
  
  private static Class[] s_supportedCoercionClasses = new Class[0];
  

  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  
  public class RuntimeUserDefinedResponse extends DoInOrder.RuntimeDoInOrder { public RuntimeUserDefinedResponse() { super(); }
  }
}
