package edu.cmu.cs.stage3.scheduler;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;


public class SchedulerThread
  extends Thread
{
  private Scheduler m_scheduler;
  private EventQueue m_eventQueue;
  private SchedulerEvent m_schedulerEvent;
  private boolean m_continue;
  private long m_sleepMillis = 15L;
  
  public SchedulerThread(Scheduler scheduler) { m_scheduler = scheduler;
    m_eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
    m_schedulerEvent = new SchedulerEvent(this);
  }
  
  public Scheduler getScheduler() { return m_scheduler; }
  
  public void markToStop() {
    m_continue = false;
  }
  
  public long getSleepMillis() { return m_sleepMillis; }
  
  public void setSleepMillis(long sleepMillis) {
    m_sleepMillis = sleepMillis;
  }
  
  public void run() {
    m_continue = true;
    do {
      try {
        AWTEvent e = m_eventQueue.peekEvent(m_schedulerEvent.getID());
        
        if (e == null) {
          m_eventQueue.postEvent(m_schedulerEvent);
          sleep(m_sleepMillis);
        } else {
          sleep(1L);
        }
      } catch (InterruptedException ie) {
        ie.printStackTrace();
        break;
      }
    } while (m_continue);
  }
}
