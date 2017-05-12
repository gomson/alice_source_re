package edu.cmu.cs.stage3.scheduler;

import java.awt.event.InvocationEvent;

class SchedulerEvent extends InvocationEvent {
  public SchedulerEvent(SchedulerThread schedulerThread) { super(schedulerThread, 13234, schedulerThread.getScheduler(), null, false); }
  
  public static final int ID = 13234;
}
