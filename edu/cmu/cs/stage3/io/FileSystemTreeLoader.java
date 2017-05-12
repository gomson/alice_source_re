package edu.cmu.cs.stage3.io;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;



















public class FileSystemTreeLoader
  implements DirectoryTreeLoader
{
  protected File root = null;
  protected File currentDirectory = null;
  protected InputStream currentlyOpenStream = null;
  
  public FileSystemTreeLoader() {}
  
  public void open(Object pathname)
    throws IllegalArgumentException, FileNotFoundException, IOException
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
      if (!root.canRead()) {
        throw new IOException(Messages.getString("cannot_read_") + root);
      }
      
    }
    else {
      throw new FileNotFoundException(root + " " + Messages.getString("does_not_exist"));
    }
    
    currentDirectory = root;
  }
  
  public void close() throws IOException {
    closeCurrentFile();
    root = null;
    currentDirectory = null;
  }
  
  public void setCurrentDirectory(String pathname) throws IllegalArgumentException { File newCurrentDirectory;
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
    return getRelativePathname(currentDirectory);
  }
  
  public InputStream readFile(String filename) throws FileNotFoundException, IOException
  {
    closeCurrentFile();
    
    File file = new File(currentDirectory, filename);
    if (!file.exists())
    {
      throw new FileNotFoundException(file + " " + Messages.getString("does_not_exist"));
    }
    if (!file.canRead()) {
      throw new IOException(Messages.getString("cannot_read_") + file);
    }
    
    currentlyOpenStream = new FileInputStream(file);
    return currentlyOpenStream;
  }
  
  public void closeCurrentFile() throws IOException {
    if (currentlyOpenStream != null) {
      currentlyOpenStream.close();
      currentlyOpenStream = null;
    }
  }
  
  public String[] getFilesInCurrentDirectory() {
    File[] files = currentDirectory.listFiles(
      new FileFilter() {
        public boolean accept(File f) {
          return f.isFile();
        }
        

      });
    String[] filenames = new String[files.length];
    for (int i = 0; i < files.length; i++) {
      filenames[i] = getRelativePathname(files[i]);
    }
    
    return filenames;
  }
  
  public String[] getDirectoriesInCurrentDirectory() {
    File[] files = currentDirectory.listFiles(
      new FileFilter() {
        public boolean accept(File f) {
          return f.isDirectory();
        }
        

      });
    String[] filenames = new String[files.length];
    for (int i = 0; i < files.length; i++) {
      filenames[i] = getRelativePathname(files[i]);
    }
    
    return filenames;
  }
  
  protected String getRelativePathname(File file) {
    StringBuffer dir = new StringBuffer(file.getAbsolutePath());
    dir.delete(0, root.getAbsolutePath().length());
    return dir.toString();
  }
  
  public boolean isKeepFileSupported() {
    return true;
  }
  
  static Object getKeepKey(File currentDirectory, String filename) {
    return new File(currentDirectory, filename).getAbsolutePath();
  }
  
  public Object getKeepKey(String filename) throws KeepFileNotSupportedException {
    return getKeepKey(currentDirectory, filename);
  }
}
