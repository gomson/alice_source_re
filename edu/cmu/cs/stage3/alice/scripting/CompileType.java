package edu.cmu.cs.stage3.alice.scripting;


























public abstract interface CompileType
{
  public static final CompileType EVAL = new CompileType() {};
  public static final CompileType EXEC_SINGLE = new CompileType() {};
  public static final CompileType EXEC_MULTIPLE = new CompileType() {};
}
