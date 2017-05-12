package edu.cmu.cs.stage3.io;

import java.io.File;

public class FileUtilities
{
  private static boolean s_successfullyLoadedLibrary;
  public static final int DIRECTORY_DOES_NOT_EXIST = -1;
  public static final int DIRECTORY_IS_NOT_WRITABLE = -2;
  public static final int DIRECTORY_IS_WRITABLE = 1;
  public static final int BAD_DIRECTORY_INPUT = -3;
  
  static {
    try {
      System.loadLibrary("jni_fileutilities");
      s_successfullyLoadedLibrary = true;
    }
    catch (Throwable t) {
      s_successfullyLoadedLibrary = false; } }
  
  public FileUtilities() {}
  
  public static boolean isFileCopySupported() { return s_successfullyLoadedLibrary; }
  
  private static native boolean copy(String paramString1, String paramString2, boolean paramBoolean, edu.cmu.cs.stage3.progress.ProgressObserver paramProgressObserver) throws edu.cmu.cs.stage3.progress.ProgressCancelException;
  
  public static boolean copy(File src, File dst, boolean overwriteIfNecessary, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
    if (isFileCopySupported()) {
      dst.getParentFile().mkdirs();
      if (progressObserver != null) {
        progressObserver.progressBegin(-1);
      }
      try {
        return copy(src.getAbsolutePath(), dst.getAbsolutePath(), overwriteIfNecessary, progressObserver);
      } finally {
        if (progressObserver != null) {
          progressObserver.progressEnd();
        }
      }
    }
    throw new RuntimeException(edu.cmu.cs.stage3.lang.Messages.getString("file_copy_not_supported"));
  }
  
  public static void copy(File src, File dst, boolean overwriteIfNecessary) {
    try {
      copy(src, dst, overwriteIfNecessary, null);
    } catch (edu.cmu.cs.stage3.progress.ProgressCancelException pce) {
      throw new Error(edu.cmu.cs.stage3.lang.Messages.getString("caught_ProgressCancelException_without_ProgressObserver"));
    }
  }
  
  public static String getExtension(String filename) {
    String extension = null;
    if (filename != null) {
      int index = filename.lastIndexOf('.');
      if (index != -1) {
        extension = filename.substring(index + 1);
      }
    }
    return extension;
  }
  
  public static String getExtension(File file) { if (file != null) {
      return getExtension(file.getName());
    }
    return null;
  }
  
  public static String getBaseName(String filename)
  {
    String basename = null;
    if (filename != null) {
      int index = filename.lastIndexOf('.');
      if (index != -1) {
        basename = filename.substring(0, index);
      } else {
        basename = filename;
      }
    }
    return basename;
  }
  
  public static String getBaseName(File file) { if (file != null) {
      return getBaseName(file.getName());
    }
    return null;
  }
  
  public static int isWritableDirectory(File directory)
  {
    if ((directory == null) || (!directory.isDirectory())) {
      return -3;
    }
    File testFile = new File(directory, "test.test");
    boolean writable;
    boolean writable; if (testFile.exists()) {
      writable = testFile.canWrite();
    } else {
      try {
        boolean success = testFile.createNewFile();
        writable = success;
      } catch (Throwable t) { boolean writable;
        writable = false;
      } finally { boolean writable;
        testFile.delete();
      }
    }
    if (!writable) {
      return -2;
    }
    if ((!directory.exists()) || (!directory.canRead())) {
      return -1;
    }
    return 1;
  }
}
