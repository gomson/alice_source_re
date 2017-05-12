package edu.cmu.cs.stage3.alice.player;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ExceptionWrapper;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.clock.DefaultClock;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.ZipFileTreeLoader;
import edu.cmu.cs.stage3.io.ZipTreeLoader;
import edu.cmu.cs.stage3.progress.ProgressCancelException;
import edu.cmu.cs.stage3.progress.ProgressObserver;
import edu.cmu.cs.stage3.scheduler.AbstractScheduler;
import edu.cmu.cs.stage3.scheduler.Scheduler;
import edu.cmu.cs.stage3.scheduler.SchedulerThread;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;

public abstract class AbstractPlayer
{
  private DefaultRenderTargetFactory m_drtf;
  private DefaultClock m_clock = new DefaultClock();
  private long m_when0 = System.currentTimeMillis();
  private World m_world = null;
  private boolean m_isGoodToSchedule = false;
  
  public AbstractPlayer(Class rendererClass) {
    m_drtf = new DefaultRenderTargetFactory(rendererClass);
    Scheduler scheduler = new AbstractScheduler()
    {
      protected void handleCaughtThowable(Runnable source, Throwable t) {
        markEachFrameRunnableForRemoval(source);
        t.printStackTrace();
      }
    };
    scheduler.addEachFrameRunnable(new Runnable() {
      public void run() {
        schedule();
      }
    });
    SchedulerThread schedulerThread = new SchedulerThread(scheduler);
    

    schedulerThread.start();
  }
  
  public AbstractPlayer() { this(null); }
  


  protected edu.cmu.cs.stage3.alice.core.Clock newClock() { return new DefaultClock(); }
  
  protected abstract void handleRenderTarget(RenderTarget paramRenderTarget);
  
  protected abstract boolean isPreserveAndRestoreRequired();
  
  public void loadWorld(DirectoryTreeLoader loader, ProgressObserver progressObserver) throws IOException {
    try {
      m_world = ((World)Element.load(loader, null, progressObserver));
      m_world.setRenderTargetFactory(m_drtf);
      m_world.setClock(m_clock);
      m_clock.setWorld(m_world);
      RenderTarget[] renderTargets = (RenderTarget[])m_world.getDescendants(RenderTarget.class);
      for (int i = 0; i < renderTargets.length; i++) {
        handleRenderTarget(renderTargets[i]);
      }
    } catch (ProgressCancelException pce) {
      throw new ExceptionWrapper(pce, loader.toString());
    } catch (UnresolvablePropertyReferencesException upre) {
      PropertyReference[] propertyReferences = upre.getPropertyReferences();
      System.err.println("could not load: " + loader + ".  was unable to resolve the following references.");
      for (int i = 0; i < propertyReferences.length; i++) {
        System.err.println("\t" + propertyReferences[i]);
      }
    }
  }
  
  public void loadWorld(InputStream ios, ProgressObserver progressObserver) throws IOException
  {
    DirectoryTreeLoader loader = new ZipTreeLoader();
    loader.open(ios);
    loadWorld(loader, progressObserver);
  }
  
  public void loadWorld(URL url, ProgressObserver progressObserver) throws IOException {
    DirectoryTreeLoader loader = new ZipTreeLoader();
    loader.open(url);
    loadWorld(loader, progressObserver);
  }
  
  public void loadWorld(File file, ProgressObserver progressObserver) throws IOException {
    DirectoryTreeLoader loader = new ZipFileTreeLoader();
    loader.open(file);
    loadWorld(loader, progressObserver);
  }
  
  public void unloadWorld() {
    if (m_world != null) {
      stopWorldIfNecessary();
      m_world.release();
      m_world = null;
    }
  }
  
  public void startWorld() { if (isPreserveAndRestoreRequired()) {
      m_world.preserve();
    }
    m_clock.start();
    m_isGoodToSchedule = true;
  }
  
  public void pauseWorld() {
    m_clock.pause();
  }
  
  public void resumeWorld() { m_clock.resume(); }
  
  public void stopWorld()
  {
    m_isGoodToSchedule = false;
    m_clock.stop();
    if (isPreserveAndRestoreRequired()) {
      m_world.restore();
    }
  }
  
  public void stopWorldIfNecessary() {
    if ((m_world != null) && (m_world.isRunning())) {
      stopWorld();
    }
  }
  
  private double getTime() {
    return (System.currentTimeMillis() - m_when0) * 0.001D;
  }
  
  public void schedule() {
    if (m_isGoodToSchedule) {
      m_clock.schedule();
    }
  }
  
  public void setSpeed(double speed) {
    m_clock.setSpeed(speed);
  }
}
