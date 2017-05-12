package edu.cmu.cs.stage3.alice.core.question;











public class ScriptDefinedBoolean
  extends AbstractScriptDefinedObject
{
  public ScriptDefinedBoolean() {}
  









  public Class getValueClass()
  {
    return Boolean.class;
  }
  
  public Boolean getBooleanValue() { return (Boolean)getValue(); }
}
