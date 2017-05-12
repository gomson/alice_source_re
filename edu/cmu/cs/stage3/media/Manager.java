package edu.cmu.cs.stage3.media;

import java.io.BufferedInputStream;

public class Manager { private static boolean s_isJMFAvailable;
  
  static { try { Class.forName("javax.media.Manager");
      s_isJMFAvailable = true;
    } catch (ClassNotFoundException cnfe) {
      s_isJMFAvailable = false; } }
  
  public Manager() {}
  
  public static DataSource createDataSource(byte[] data, String extension) { if (s_isJMFAvailable) {
      return new edu.cmu.cs.stage3.media.jmfmedia.DataSource(data, extension);
    }
    return new edu.cmu.cs.stage3.media.nullmedia.DataSource(data, extension);
  }
  
  public static DataSource createDataSource(java.io.InputStream is, String extension) throws java.io.IOException { BufferedInputStream bis;
    BufferedInputStream bis;
    if ((is instanceof BufferedInputStream)) {
      bis = (BufferedInputStream)is;
    } else {
      bis = new BufferedInputStream(is);
    }
    int byteCount = bis.available();
    byte[] data = new byte[byteCount];
    bis.read(data);
    return createDataSource(data, extension);
  }
  
  public static DataSource createDataSource(java.io.File file) throws java.io.IOException { java.io.FileInputStream fis = new java.io.FileInputStream(file);
    return createDataSource(fis, edu.cmu.cs.stage3.io.FileUtilities.getExtension(file));
  }
}
