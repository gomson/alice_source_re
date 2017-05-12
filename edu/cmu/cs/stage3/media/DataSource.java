package edu.cmu.cs.stage3.media;

import edu.cmu.cs.stage3.media.event.DataSourceListener;

public abstract interface DataSource
{
  public static final boolean USE_HINT_IF_NECESSARY = true;
  public static final boolean DO_NOT_USE_HINT = false;
  
  public abstract byte[] getData();
  
  public abstract String getExtension();
  
  public abstract double getDuration(boolean paramBoolean);
  
  public abstract double getDurationHint();
  
  public abstract void setDurationHint(double paramDouble);
  
  public abstract Player acquirePlayer();
  
  public abstract int waitForRealizedPlayerCount(int paramInt, long paramLong);
  
  public abstract boolean isCompressionWorthwhile();
  
  public abstract void addDataSourceListener(DataSourceListener paramDataSourceListener);
  
  public abstract void removeDataSourceListener(DataSourceListener paramDataSourceListener);
  
  public abstract DataSourceListener[] getDataSourceListeners();
}
