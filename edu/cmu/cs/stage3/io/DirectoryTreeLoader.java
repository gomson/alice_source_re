package edu.cmu.cs.stage3.io;

import java.io.IOException;
import java.io.InputStream;

public abstract interface DirectoryTreeLoader
{
  public abstract void open(Object paramObject)
    throws IllegalArgumentException, IOException;
  
  public abstract void close()
    throws IOException;
  
  public abstract void setCurrentDirectory(String paramString)
    throws IllegalArgumentException;
  
  public abstract String getCurrentDirectory();
  
  public abstract String[] getFilesInCurrentDirectory();
  
  public abstract String[] getDirectoriesInCurrentDirectory();
  
  public abstract InputStream readFile(String paramString)
    throws IllegalArgumentException, IOException;
  
  public abstract void closeCurrentFile()
    throws IOException;
  
  public abstract boolean isKeepFileSupported();
  
  public abstract Object getKeepKey(String paramString)
    throws KeepFileNotSupportedException;
}
