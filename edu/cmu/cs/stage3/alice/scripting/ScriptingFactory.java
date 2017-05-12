package edu.cmu.cs.stage3.alice.scripting;

import java.io.OutputStream;

public abstract interface ScriptingFactory
{
  public abstract Interpreter manufactureInterpreter();
  
  public abstract Interpreter[] getInterpreters();
  
  public abstract OutputStream getStdOut();
  
  public abstract void setStdOut(OutputStream paramOutputStream);
  
  public abstract OutputStream getStdErr();
  
  public abstract void setStdErr(OutputStream paramOutputStream);
}
