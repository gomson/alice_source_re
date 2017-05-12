package edu.cmu.cs.stage3.alice.core.event;

import java.util.EventObject;


















public class ScheduleEvent
  extends EventObject
{
  protected double m_time;
  
  public ScheduleEvent(Object source, double time)
  {
    super(source);
    m_time = time;
  }
  
  public double getTime() { return m_time; }
  
  public void setTime(double time) {
    m_time = time;
  }
}
