package edu.cmu.cs.stage3.alice.core;

public abstract interface ExecEvalScript
{
  public abstract String getExecScriptValue();
  
  public abstract void setExecScriptValue(String paramString);
  
  public abstract String getEvalScriptValue();
  
  public abstract void setEvalScriptValue(String paramString);
}
