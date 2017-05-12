package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.io.File;


















public class CDUtil
{
  private static boolean s_successfullyLoadedLibrary;
  
  static
  {
    try
    {
      System.loadLibrary("jni_cdutil");
      s_successfullyLoadedLibrary = true;
    } catch (UnsatisfiedLinkError ule) {
      s_successfullyLoadedLibrary = false; } }
  
  public CDUtil() {}
  
  private static native String[] getCDRootPaths();
  public static File[] getCDRoots() { if (s_successfullyLoadedLibrary) {
      String[] cdRootPaths = getCDRootPaths();
      if (cdRootPaths != null) {
        File[] files = new File[cdRootPaths.length];
        for (int i = 0; i < cdRootPaths.length; i++) {
          files[i] = new File(cdRootPaths[i]);
        }
        return files;
      }
    }
    return new File[0];
  }
}
