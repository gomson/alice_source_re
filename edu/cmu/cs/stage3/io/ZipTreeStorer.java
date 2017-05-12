package edu.cmu.cs.stage3.io;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
















public class ZipTreeStorer
  implements DirectoryTreeStorer
{
  public ZipTreeStorer() {}
  
  protected ZipOutputStream zipOut = null;
  protected String currentDirectory = null;
  protected ZipEntry currentEntry = null;
  
  protected boolean isCompressed() {
    return true;
  }
  

  public void open(Object pathname)
    throws IllegalArgumentException, IOException
  {
    if (zipOut != null) {
      close();
    }
    
    OutputStream out = null;
    if ((pathname instanceof String)) {
      out = new FileOutputStream((String)pathname);
    }
    else if ((pathname instanceof File)) {
      out = new FileOutputStream((File)pathname);
    }
    else if ((pathname instanceof OutputStream)) {
      out = (OutputStream)pathname;
    }
    else {
      throw new IllegalArgumentException(Messages.getString("pathname_must_be_an_instance_of_String__java_io_File__or_java_io_OutputStream"));
    }
    
    zipOut = new ZipOutputStream(new BufferedOutputStream(out));
    if (!isCompressed())
    {

      zipOut.setMethod(0);
    }
    currentDirectory = "";
  }
  
  public void close() throws IOException {
    if (zipOut != null) {
      closeCurrentFile();
      zipOut.flush();
      zipOut.finish();
      zipOut.close();
      zipOut = null;
    }
  }
  
  public void createDirectory(String pathname) throws IllegalArgumentException, IOException {
    if ((pathname.indexOf('/') != -1) || (pathname.indexOf('\\') != -1)) {
      throw new IllegalArgumentException(Messages.getString("pathname_cannot_contain_path_separators"));
    }
    if (pathname.length() <= 0) {
      throw new IllegalArgumentException(Messages.getString("pathname_has_no_length"));
    }
    
    String path = new String((currentDirectory + pathname + "/").getBytes(), "ISO-8859-1");
    ZipEntry newEntry = new ZipEntry(path);
    if (zipOut != null) {
      zipOut.putNextEntry(newEntry);
      zipOut.closeEntry();
    }
    else {
      throw new IOException(Messages.getString("No_zip_file_currently_open"));
    }
  }
  
  public void setCurrentDirectory(String pathname) throws IllegalArgumentException {
    if (pathname == null) {
      pathname = "";
    }
    else if (pathname.length() > 0) {
      pathname = pathname.replace('\\', '/');
      
      int index;
      
      while ((index = pathname.indexOf("//")) != -1) { int index;
        pathname = pathname.substring(0, index + 1) + pathname.substring(index + 2);
      }
      
      if (pathname.charAt(0) == '/') {
        pathname = pathname.substring(1);
      }
      else {
        pathname = currentDirectory + pathname;
      }
      
      if (!pathname.endsWith("/")) {
        pathname = pathname + "/";
      }
      if (!pathname.startsWith("/")) {
        pathname = "/" + pathname;
      }
    }
    
    currentDirectory = pathname;
  }
  
  public String getCurrentDirectory() {
    return currentDirectory;
  }
  
  public OutputStream createFile(String filename, boolean compressItIfYouGotIt) throws IllegalArgumentException, IOException
  {
    if (zipOut != null) {
      currentEntry = new ZipEntry(currentDirectory + filename);
      if (!isCompressed())
      {

        currentEntry.setMethod(0);
      }
      zipOut.putNextEntry(currentEntry);
    }
    else {
      throw new IOException(Messages.getString("No_zip_file_currently_open"));
    }
    
    return zipOut;
  }
  
  public void closeCurrentFile() throws IOException {
    if (currentEntry != null) {
      zipOut.flush();
      zipOut.closeEntry();
      currentEntry = null;
    }
  }
  
  public Object getKeepKey(String filename) {
    return null;
  }
  
  public boolean isKeepFileSupported() {
    return false;
  }
  
  public void keepFile(String filename) throws KeepFileNotSupportedException, KeepFileDoesNotExistException {
    throw new KeepFileNotSupportedException();
  }
}
