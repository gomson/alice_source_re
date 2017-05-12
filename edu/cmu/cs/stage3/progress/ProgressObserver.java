package edu.cmu.cs.stage3.progress;

public abstract interface ProgressObserver
{
  public static final int UNKNOWN_TOTAL = -1;
  
  public abstract void progressBegin(int paramInt);
  
  public abstract void progressUpdateTotal(int paramInt);
  
  public abstract void progressUpdate(int paramInt, String paramString)
    throws ProgressCancelException;
  
  public abstract void progressEnd();
}
