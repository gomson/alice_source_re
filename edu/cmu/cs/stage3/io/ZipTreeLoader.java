package edu.cmu.cs.stage3.io;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;












public class ZipTreeLoader
  implements DirectoryTreeLoader
{
  protected ZipInputStream zipIn = null;
  protected String currentDirectory = null;
  protected boolean isLoading = false;
  protected Hashtable pathnamesToByteArrays = null;
  protected Vector directories = null;
  private Object pathnameBeingWaitedOn = null;
  protected static final Object WHOLE_FILE = new Object();
  protected ZipLoaderThread loaderThread = null;
  protected InputStream currentlyOpenStream = null;
  
  public ZipTreeLoader() {}
  
  public void open(Object pathname)
    throws IllegalArgumentException, IOException
  {
    if (zipIn != null) {
      close();
    }
    
    InputStream in = null;
    if ((pathname instanceof String)) {
      in = new FileInputStream((String)pathname);
    } else if ((pathname instanceof File)) {
      in = new FileInputStream((File)pathname);
    } else if ((pathname instanceof URL)) {
      in = ((URL)pathname).openStream();
    } else if ((pathname instanceof InputStream)) {
      in = (InputStream)pathname;
    } else { if (pathname == null) {
        throw new IllegalArgumentException(Messages.getString("pathname_is_null"));
      }
      throw new IllegalArgumentException(Messages.getString("pathname_must_be_an_instance_of_String__java_io_File__java_net_URL__or_java_io_InputStream"));
    }
    
    zipIn = new ZipInputStream(new BufferedInputStream(in));
    currentDirectory = "";
    pathnamesToByteArrays = new Hashtable();
    directories = new Vector();
    loaderThread = new ZipLoaderThread();
    loaderThread.start();
  }
  
  public void close() throws IOException {
    if (zipIn != null) {
      closeCurrentFile();
      if (isLoading) {
        loaderThread.stopEarly();
        waitFor(WHOLE_FILE);
      }
      zipIn.close();
      zipIn = null;
      pathnamesToByteArrays = null;
      directories = null;
      loaderThread = null;
    }
  }
  
  public void setCurrentDirectory(String pathname) throws IllegalArgumentException {
    if (pathname == null) {
      pathname = "";
    }
    else if (pathname.length() > 0) {
      if ((pathname.charAt(0) != '/') && (pathname.charAt(0) != '\\')) {
        pathname = currentDirectory + pathname;
      }
      
      pathname = getCanonicalPathname(pathname);
      
      if (!pathname.endsWith("/")) {
        pathname = pathname + "/";
      }
      if (!pathname.startsWith("/")) {
        pathname = "/" + pathname + "/";
      }
    }
    

    currentDirectory = pathname;
  }
  
  public String getCurrentDirectory() {
    return currentDirectory;
  }
  
  public InputStream readFile(String filename) throws IllegalArgumentException, IOException {
    closeCurrentFile();
    
    String pathname = getCanonicalPathname(currentDirectory + filename);
    waitFor(pathname);
    
    byte[] fileContents = (byte[])pathnamesToByteArrays.get(pathname);
    if (fileContents == null) {
      throw new FileNotFoundException(Messages.getString("Not_Found__") + pathname);
    }
    
    currentlyOpenStream = new ByteArrayInputStream(fileContents);
    return currentlyOpenStream;
  }
  
  public void closeCurrentFile() throws IOException {
    if (currentlyOpenStream != null) {
      currentlyOpenStream.close();
      currentlyOpenStream = null;
    }
  }
  


  public String[] getFilesInCurrentDirectory()
  {
    waitFor(WHOLE_FILE);
    
    Vector files = new Vector();
    for (Enumeration enum0 = pathnamesToByteArrays.keys(); enum0.hasMoreElements();) {
      String filename = (String)enum0.nextElement();
      int index = filename.indexOf(currentDirectory);
      if (index == 0) {
        String tail = filename.substring(currentDirectory.length());
        if (tail.indexOf('/') == -1) {
          files.addElement(tail);
        }
      }
    }
    

    String[] filenames = new String[files.size()];
    int i = 0;
    for (Enumeration enum0 = files.elements(); enum0.hasMoreElements();) {
      filenames[(i++)] = ((String)enum0.nextElement());
    }
    
    return filenames;
  }
  


  public String[] getDirectoriesInCurrentDirectory()
  {
    waitFor(WHOLE_FILE);
    
    Vector dirs = new Vector();
    for (Enumeration enum0 = directories.elements(); enum0.hasMoreElements();) {
      String dirname = (String)enum0.nextElement();
      int index = dirname.indexOf(currentDirectory);
      if (index == 0) {
        String tail = dirname.substring(currentDirectory.length());
        if ((tail.length() > 0) && 
          (tail.indexOf('/') == tail.lastIndexOf('/'))) {
          dirs.addElement(tail);
        }
      }
    }
    

    String[] dirnames = new String[dirs.size()];
    int i = 0;
    for (Enumeration enum0 = dirs.elements(); enum0.hasMoreElements();) {
      dirnames[(i++)] = ((String)enum0.nextElement());
    }
    
    return dirnames;
  }
  
  protected String getCanonicalPathname(String pathname) {
    pathname = pathname.replace('\\', '/');
    
    int index;
    
    while ((index = pathname.indexOf("//")) != -1) { int index;
      pathname = pathname.substring(0, index + 1) + pathname.substring(index + 2);
    }
    
    if (pathname.charAt(0) == '/') {
      pathname = pathname.substring(1);
    }
    
    return pathname;
  }
  
  protected synchronized void waitFor(Object pathname) {
    if (pathnamesToByteArrays.get(pathname) != null) {
      return;
    }
    if (directories.indexOf(pathname) != -1) {
      return;
    }
    if ((pathname == WHOLE_FILE) && (!isLoading)) {
      return;
    }
    
    pathnameBeingWaitedOn = pathname;
    try {
      wait();
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  protected synchronized void finishedLoading(Object pathname)
  {
    if ((pathname.equals(pathnameBeingWaitedOn)) || (pathname.equals(WHOLE_FILE))) {
      notifyAll();
    }
  }
  
  class ZipLoaderThread extends Thread {
    private boolean stoppedEarly = false;
    
    ZipLoaderThread() {}
    
    public void run() { setPriority(10);
      isLoading = true;
      

      try
      {
        while (!stoppedEarly)
        {


          ZipEntry entry = zipIn.getNextEntry();
          if (entry == null) {
            break;
          }
          
          String pathname = getCanonicalPathname(entry.getName());
          if (entry.isDirectory()) {
            directories.addElement(pathname);
            finishedLoading(pathname);

          }
          else
          {
            int size = 1024;
            int off = 0;
            byte[] entryContents = new byte[size];
            while (zipIn.available() == 1) {
              if (off == size) {
                size += 1024;
                byte[] temp = entryContents;
                entryContents = new byte[size];
                System.arraycopy(temp, 0, entryContents, 0, size - 1024);
              }
              int theByte = zipIn.read();
              if (theByte == -1) {
                break;
              }
              entryContents[(off++)] = ((byte)theByte);
            }
            if (off < size) {
              byte[] temp = entryContents;
              entryContents = new byte[off];
              System.arraycopy(temp, 0, entryContents, 0, off);
            }
            
            zipIn.closeEntry();
            
            pathnamesToByteArrays.put(pathname, entryContents);
            finishedLoading(pathname);
          }
        }
      } catch (IOException e) { e.printStackTrace();
      }
      

      stoppedEarly = false;
      isLoading = false;
      finishedLoading(ZipTreeLoader.WHOLE_FILE);
    }
    
    public void stopEarly() {
      stoppedEarly = true;
    }
  }
  
  public boolean isKeepFileSupported() {
    return false;
  }
  
  public Object getKeepKey(String filename) throws KeepFileNotSupportedException {
    throw new KeepFileNotSupportedException();
  }
}
