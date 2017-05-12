package edu.cmu.cs.stage3.media;

import edu.cmu.cs.stage3.media.event.PlayerListener;

public abstract interface Player
{
  public static final int STATE_UNREALIZED = 100;
  public static final int STATE_REALIZING = 200;
  public static final int STATE_REALIZED = 300;
  public static final int STATE_PREFETCHING = 400;
  public static final int STATE_PREFETCHED = 500;
  public static final int STATE_STARTED = 600;
  
  public abstract DataSource getDataSource();
  
  public abstract boolean isAvailable();
  
  public abstract void setIsAvailable(boolean paramBoolean);
  
  public abstract double getBeginTime();
  
  public abstract void setBeginTime(double paramDouble);
  
  public abstract double getEndTime();
  
  public abstract void setEndTime(double paramDouble);
  
  public abstract int getState();
  
  public abstract void setVolumeLevel(float paramFloat);
  
  public abstract void setRate(float paramFloat);
  
  public abstract void prefetch();
  
  public abstract void realize();
  
  public abstract void start();
  
  public abstract void stop();
  
  public abstract void startFromBeginning();
  
  public abstract double getCurrentTime();
  
  public abstract void setCurrentTime(double paramDouble);
  
  public abstract double getDuration();
  
  public abstract void addPlayerListener(PlayerListener paramPlayerListener);
  
  public abstract void removePlayerListener(PlayerListener paramPlayerListener);
  
  public abstract PlayerListener[] getPlayerListeners();
}
