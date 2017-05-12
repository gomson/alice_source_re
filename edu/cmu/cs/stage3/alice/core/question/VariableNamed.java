package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;

public class VariableNamed extends edu.cmu.cs.stage3.alice.core.Question
{
  public VariableNamed() {}
  
  public final edu.cmu.cs.stage3.alice.core.property.TransformableProperty owner = new edu.cmu.cs.stage3.alice.core.property.TransformableProperty(this, "owner", null);
  public final StringProperty variableName = new StringProperty(this, "variableName", "");
  public final ClassProperty valueClass = new ClassProperty(this, "valueClass", null);
  public final BooleanProperty ignoreCase = new BooleanProperty(this, "ignoreCase", Boolean.TRUE);
  
  public Object getValue() {
    edu.cmu.cs.stage3.alice.core.Transformable ownerValue = owner.getTransformableValue();
    String nameValue = variableName.getStringValue();
    if (ownerValue != null) {
      for (int i = 0; i < variables.size(); i++) {
        Variable variableI = (Variable)variables.get(i);
        if (variableI != null) {
          String nameI = name.getStringValue();
          if (nameI != null) {
            if (ignoreCase.booleanValue()) {
              if (nameI.equalsIgnoreCase(nameValue)) {
                return variableI.getValue();
              }
            }
            else if (nameI.equals(nameValue)) {
              return variableI.getValue();
            }
          }
        }
      }
    }
    
    return null;
  }
  
  public Class getValueClass() {
    Class cls = valueClass.getClassValue();
    if (cls != null) {
      return cls;
    }
    return Object.class;
  }
}
