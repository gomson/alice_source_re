package edu.cmu.cs.stage3.scheduler;

public abstract interface Scheduler
  extends Runnable
{
  public abstract void addEachFrameRunnable(Runnable paramRunnable);
  
  public abstract void markEachFrameRunnableForRemoval(Runnable paramRunnable);
}
