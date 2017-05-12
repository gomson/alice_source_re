package edu.cmu.cs.stage3.alice.core;

public abstract interface Clock
{
  public abstract void start();
  
  public abstract void stop();
  
  public abstract void pause();
  
  public abstract void resume();
  
  public abstract double getTime();
  
  public abstract double getTimeElapsed();
  
  public abstract World getWorld();
  
  public abstract void setWorld(World paramWorld);
  
  public abstract void schedule();
}
