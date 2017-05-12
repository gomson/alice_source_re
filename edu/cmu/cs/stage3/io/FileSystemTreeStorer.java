package edu.cmu.cs.stage3.io;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;





















public class FileSystemTreeStorer
  implements DirectoryTreeStorer
{
  protected File root = null;
  protected File currentDirectory = null;
  protected OutputStream currentlyOpenStream = null;
  
  public FileSystemTreeStorer() {}
  
  public void open(Object pathname) throws IllegalArgumentException, IOException
  {
    if (root != null) {
      close();
    }
    
    if ((pathname instanceof String)) {
      root = new File((String)pathname);
    }
    else if ((pathname instanceof File)) {
      root = ((File)pathname);
    }
    else {
      throw new IllegalArgumentException(Messages.getString("pathname_must_be_an_instance_of_String_or_java_io_File"));
    }
    
    if (root.exists()) {
      if (!root.canWrite()) {
        throw new IOException(Messages.getString("cannot_write_to_") + root);
      }
    }
    else {
      if (!root.mkdir()) {
        throw new IOException(Messages.getString("cannot_create_") + root);
      }
      if (!root.canWrite()) {
        throw new IOException(Messages.getString("cannot_write_to_") + root);
      }
    }
    
    currentDirectory = root;
  }
  
  public void close() throws IOException {
    closeCurrentFile();
    root = null;
    currentDirectory = null;
  }
  
  public void createDirectory(String pathname)
    throws IllegalArgumentException, IOException
  {
    if (pathname.indexOf('/') != -1) {
      throw new IllegalArgumentException(Messages.getString("pathname_cannot_contain_path_separators"));
    }
    if (pathname.length() <= 0) {
      throw new IllegalArgumentException(Messages.getString("pathname_has_no_length"));
    }
    File newDir = new File(currentDirectory, pathname);
    if ((!newDir.exists()) && 
      (!newDir.mkdir())) {
      throw new IOException(Messages.getString("cannot_create_") + newDir);
    }
  }
  
  public void setCurrentDirectory(String pathname) throws IllegalArgumentException {
    File newCurrentDirectory;
    File newCurrentDirectory;
    if ((pathname.length() == 0) || (pathname.charAt(0) == '/') || (pathname.charAt(0) == '\\')) {
      newCurrentDirectory = new File(root.getAbsolutePath() + pathname);
    }
    else {
      newCurrentDirectory = new File(currentDirectory.getAbsolutePath() + "/" + pathname);
    }
    
    if (!newCurrentDirectory.exists()) {
      throw new IllegalArgumentException(newCurrentDirectory + " " + Messages.getString("doesn_t_exist"));
    }
    if (!newCurrentDirectory.isDirectory()) {
      throw new IllegalArgumentException(newCurrentDirectory + " " + Messages.getString("isn_t_a_directory"));
    }
    
    currentDirectory = newCurrentDirectory;
  }
  
  public String getCurrentDirectory() {
    StringBuffer dir = new StringBuffer(currentDirectory.getAbsolutePath());
    dir.delete(0, root.getAbsolutePath().length());
    return dir.toString();
  }
  
  public OutputStream createFile(String filename, boolean compressItIfYouGotIt) throws IllegalArgumentException, IOException {
    File newFile = new File(currentDirectory, filename);
    if ((!newFile.exists()) && 
      (!newFile.createNewFile())) {
      throw new IOException(Messages.getString("cannot_create_") + newFile);
    }
    
    if (!newFile.canWrite()) {
      throw new IOException(Messages.getString("cannot_write_to_") + newFile);
    }
    
    currentlyOpenStream = new FileOutputStream(newFile);
    return currentlyOpenStream;
  }
  
  public void closeCurrentFile() throws IOException {
    if (currentlyOpenStream != null) {
      currentlyOpenStream.flush();
      currentlyOpenStream.close();
      currentlyOpenStream = null;
    }
  }
  
  public Object getKeepKey(String filename) throws KeepFileNotSupportedException {
    return FileSystemTreeLoader.getKeepKey(currentDirectory, filename);
  }
  
  public boolean isKeepFileSupported() {
    return true;
  }
  
  public void keepFile(String filename) throws KeepFileNotSupportedException, KeepFileDoesNotExistException {
    File file = new File(currentDirectory, filename);
    if (!file.exists()) {
      throw new KeepFileDoesNotExistException(currentDirectory.getAbsolutePath(), filename);
    }
  }
}
