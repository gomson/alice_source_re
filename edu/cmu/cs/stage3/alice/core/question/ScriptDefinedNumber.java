package edu.cmu.cs.stage3.alice.core.question;











public class ScriptDefinedNumber
  extends AbstractScriptDefinedObject
{
  public ScriptDefinedNumber() {}
  









  public Class getValueClass()
  {
    return Number.class;
  }
  
  public Number getNumberValue() { return (Number)getValue(); }
}
