package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.ClassProperty;






















public class ScriptDefinedObject
  extends AbstractScriptDefinedObject
{
  public final ClassProperty valueClass = new ClassProperty(this, "valueClass", null);
  
  public ScriptDefinedObject() {}
  
  public Class getValueClass() { Class cls = (Class)valueClass.get();
    if (cls != null) {
      return cls;
    }
    return Object.class;
  }
}
