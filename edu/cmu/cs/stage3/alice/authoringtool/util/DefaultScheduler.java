package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.lang.Messages;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.python.core.Py;
import org.python.core.PyException;













public class DefaultScheduler
  implements Runnable
{
  private static final long minDelay = 20L;
  
  public DefaultScheduler() {}
  
  private double simFPS = 0.0D;
  private double renderFPS = 0.0D;
  private int simFrameCount = 0;
  private int renderFrameCount = 0;
  private long simDT = 0L;
  private long renderDT = 0L;
  private long simLastTime = 0L;
  private long renderLastTime = -1L;
  private long lastRenderLastTime = 0L;
  private long idleLastTime = 0L;
  
  private boolean defaultThreadEnabled = false;
  
  private Set doOnceRunnables = new HashSet();
  private Set eachFrameRunnables = new HashSet();
  private Set eachFrameRunnablesMarkedForRemoval = new HashSet();
  
  public boolean addDoOnceRunnable(Runnable doOnceRunnable) {
    synchronized (doOnceRunnables) {
      return doOnceRunnables.add(doOnceRunnable);
    }
  }
  
  public boolean addEachFrameRunnable(Runnable eachFrameRunnable) {
    synchronized (eachFrameRunnables) {
      return eachFrameRunnables.add(eachFrameRunnable);
    }
  }
  
  public boolean removeEachFrameRunnable(Runnable eachFrameRunnable) {
    synchronized (eachFrameRunnablesMarkedForRemoval) {
      return eachFrameRunnablesMarkedForRemoval.add(eachFrameRunnable);
    }
  }
  
  public Runnable[] getEachFrameRunnables() {
    synchronized (eachFrameRunnables) {
      Runnable[] runnables = new Runnable[eachFrameRunnables.size()];
      int i = 0;
      for (Iterator iter = eachFrameRunnables.iterator(); iter.hasNext();) {
        runnables[(i++)] = ((Runnable)iter.next());
      }
      return runnables;
    }
  }
  
  public void run() {
    simulateOnce();
  }
  








  private synchronized void simulateOnce()
  {
    for (Iterator<Runnable> iter = doOnceRunnables.iterator(); iter.hasNext();) {
      Runnable runnable = (Runnable)iter.next();
      try {
        runnable.run();
      }
      catch (PyException e) {
        if (!Py.matchException(e, Py.SystemExit))
        {

          Py.printException(e, null, AuthoringTool.getPyStdErr());
        }
      } catch (Throwable t) {
        System.err.println(Messages.getString("Error_during_simulation_"));
        t.printStackTrace();
      }
      iter.remove();
    }
    
    for (Iterator iter = eachFrameRunnablesMarkedForRemoval.iterator(); iter.hasNext();) {
      eachFrameRunnables.remove(iter.next());
    }
    eachFrameRunnablesMarkedForRemoval.clear();
    
    for (Iterator iter = eachFrameRunnables.iterator(); iter.hasNext();) {
      Runnable runnable = (Runnable)iter.next();
      try {
        runnable.run();
      }
      catch (PyException e) {
        if (!Py.matchException(e, Py.SystemExit))
        {

          Py.printException(e, null, AuthoringTool.getPyStdErr());
        }
      } catch (Throwable t) {
        System.err.println(Messages.getString("Error_during_simulation_"));
        t.printStackTrace();
      }
    }
    
    long time = System.currentTimeMillis();
    simDT += time - simLastTime;
    simLastTime = time;
    
    simFrameCount += 1;
    if ((simFrameCount == 5) || (simDT > 500L)) {
      simFPS = (simFrameCount / (simDT * 0.001D));
      simFrameCount = 0;
      simDT = 0L;
    }
  }
  
  public double getSimulationFPS() {
    return simFPS;
  }
}
