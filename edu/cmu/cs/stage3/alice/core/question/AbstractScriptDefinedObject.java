package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.ScriptProperty;
import edu.cmu.cs.stage3.alice.scripting.CompileType;


















public abstract class AbstractScriptDefinedObject
  extends Question
{
  public AbstractScriptDefinedObject() {}
  
  public final ScriptProperty evalScript = new ScriptProperty(this, "evalScript", null);
  
  public Object getValue() {
    return eval(evalScript.getCode(CompileType.EVAL));
  }
}
