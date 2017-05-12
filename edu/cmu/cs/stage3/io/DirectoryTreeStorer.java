package edu.cmu.cs.stage3.io;

import java.io.IOException;
import java.io.OutputStream;

public abstract interface DirectoryTreeStorer
{
  public abstract void open(Object paramObject)
    throws IllegalArgumentException, IOException;
  
  public abstract void close()
    throws IOException;
  
  public abstract void createDirectory(String paramString)
    throws IllegalArgumentException, IOException;
  
  public abstract void setCurrentDirectory(String paramString)
    throws IllegalArgumentException;
  
  public abstract String getCurrentDirectory();
  
  public abstract OutputStream createFile(String paramString, boolean paramBoolean)
    throws IllegalArgumentException, IOException;
  
  public abstract void closeCurrentFile()
    throws IOException;
  
  public abstract Object getKeepKey(String paramString)
    throws KeepFileNotSupportedException;
  
  public abstract boolean isKeepFileSupported();
  
  public abstract void keepFile(String paramString)
    throws KeepFileNotSupportedException, KeepFileDoesNotExistException;
}
