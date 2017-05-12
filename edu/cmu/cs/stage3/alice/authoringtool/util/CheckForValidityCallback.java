package edu.cmu.cs.stage3.alice.authoringtool.util;

public abstract interface CheckForValidityCallback
{
  public abstract void setValidity(Object paramObject, boolean paramBoolean);
  
  public abstract void doAction();
}
