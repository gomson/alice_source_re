package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.event.ScheduleEvent;
import edu.cmu.cs.stage3.alice.core.event.ScheduleListener;
import java.util.Vector;



















public class Scheduler
  implements Runnable
{
  public Scheduler() {}
  
  private static long s_startTime = ;
  private Vector m_scheduleListeners = new Vector();
  private ScheduleListener[] m_scheduleListenerArray = null;
  private double m_falseDT = NaN.0D;
  private double m_prevTime = NaN.0D;
  private double m_time = NaN.0D;
  
  public void addScheduleListener(ScheduleListener scheduleListener) {
    if (!m_scheduleListeners.contains(scheduleListener)) {
      m_scheduleListeners.addElement(scheduleListener);
      m_scheduleListenerArray = null;
    }
  }
  
  public void removeScheduleListener(ScheduleListener scheduleListener) { m_scheduleListeners.removeElement(scheduleListener);
    m_scheduleListenerArray = null;
  }
  
  public ScheduleListener[] getScheduleListeners() { if (m_scheduleListenerArray == null) {
      m_scheduleListenerArray = new ScheduleListener[m_scheduleListeners.size()];
      m_scheduleListeners.copyInto(m_scheduleListenerArray);
    }
    return m_scheduleListenerArray;
  }
  
  public double getFalseDT() { return m_falseDT; }
  
  public void setFalseDT(double falseDT) {
    m_falseDT = falseDT;
  }
  
  public double getTime() { return m_time; }
  
  public double getDT() {
    if ((Double.isNaN(m_time)) || (Double.isNaN(m_prevTime))) {
      return 0.0D;
    }
    return m_time - m_prevTime;
  }
  
  protected void schedule(ScheduleListener scheduleListener, ScheduleEvent scheduleEvent)
  {
    scheduleListener.scheduled(scheduleEvent);
  }
  
  private void updateTime() {
    m_time = ((System.currentTimeMillis() - s_startTime) * 0.001D);
    if ((!Double.isNaN(m_falseDT)) && 
      (!Double.isNaN(m_prevTime))) {
      m_time = (m_prevTime + m_falseDT);
    }
  }
  


  public void HACK_updateTime()
  {
    updateTime();
    m_prevTime = m_time;
  }
  
  private ScheduleEvent m_scheduleEvent = new ScheduleEvent(this, 0.0D);
  
  public synchronized void run() { updateTime();
    m_scheduleEvent.setTime(m_time);
    ScheduleListener[] sls = getScheduleListeners();
    for (int i = 0; i < sls.length; i++) {
      try {
        schedule(sls[i], m_scheduleEvent);
      } catch (RuntimeException re) {
        removeScheduleListener(sls[i]);
        throw re;
      }
    }
    m_prevTime = m_time;
  }
}
