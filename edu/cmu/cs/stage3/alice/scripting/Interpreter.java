package edu.cmu.cs.stage3.alice.scripting;

import edu.cmu.cs.stage3.alice.core.World;

public abstract interface Interpreter
{
  public abstract void setWorld(World paramWorld);
  
  public abstract void release();
  
  public abstract void start();
  
  public abstract void stop();
  
  public abstract Code compile(String paramString, Object paramObject, CompileType paramCompileType);
  
  public abstract Object eval(Code paramCode);
  
  public abstract void exec(Code paramCode);
}
