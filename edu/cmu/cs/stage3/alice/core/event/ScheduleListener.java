package edu.cmu.cs.stage3.alice.core.event;

public abstract interface ScheduleListener
{
  public abstract void scheduled(ScheduleEvent paramScheduleEvent);
}
