package edu.cmu.cs.stage3.caitlin.stencilhelp.application;

public abstract interface StateCapsule
{
  public abstract void parse(String paramString);
  
  public abstract String getStorableRepr();
}
