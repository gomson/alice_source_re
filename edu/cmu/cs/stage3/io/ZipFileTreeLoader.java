package edu.cmu.cs.stage3.io;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.mozilla.universalchardet.UniversalDetector;













public class ZipFileTreeLoader
  implements DirectoryTreeLoader
{
  public ZipFileTreeLoader() {}
  
  private static String getCanonicalPathname(String pathname)
  {
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
  
  private Hashtable m_pathnameToZipEntryMap = new Hashtable();
  private File m_rootFile = null;
  private ZipFile m_zipFile = null;
  private String m_currentDirectory = null;
  private InputStream m_currentlyOpenStream = null;
  
  public void open(Object pathname) throws IllegalArgumentException, IOException {
    if (m_zipFile != null) {
      close();
    }
    
    if ((pathname instanceof String)) {
      m_rootFile = new File((String)pathname);
    } else if ((pathname instanceof File)) {
      m_rootFile = ((File)pathname);
    } else { if (pathname == null) {
        throw new IllegalArgumentException(Messages.getString("pathname_is_null"));
      }
      throw new IllegalArgumentException(Messages.getString("pathname_must_be_an_instance_of_String_or_java_io_File"));
    }
    m_zipFile = new ZipFile(m_rootFile);
    m_currentDirectory = "";
    m_currentlyOpenStream = null;
    m_pathnameToZipEntryMap.clear();
    Enumeration enum0 = m_zipFile.entries();
    while (enum0.hasMoreElements()) {
      ZipEntry zipEntry = (ZipEntry)enum0.nextElement();
      String encoding = new String(zipEntry.getName().getBytes("UTF-8"), "ISO-8859-1");
      String encoding1 = new String(zipEntry.getName().getBytes(), "UTF-8");
      String encoding2 = new String(zipEntry.getName().getBytes("UTF-8"), "UTF-8");
      String name = new String(zipEntry.getName().getBytes("UTF-8"), "ISO-8859-1");
      String path = new String(getCanonicalPathname(name));
      m_pathnameToZipEntryMap.put(path, zipEntry);
    }
  }
  
  public void close() throws IOException {
    if (m_zipFile != null) {
      closeCurrentFile();
      m_zipFile.close();
      m_zipFile = null;
    }
  }
  
  public void setCurrentDirectory(String pathname) throws IllegalArgumentException {
    if (pathname == null) {
      pathname = "";
    } else if (pathname.length() > 0) {
      if ((pathname.charAt(0) != '/') && (pathname.charAt(0) != '\\')) {
        pathname = m_currentDirectory + pathname;
      }
      
      pathname = getCanonicalPathname(pathname);
      
      if (!pathname.endsWith("/")) {
        pathname = pathname + "/";
      }
      
      if (!pathname.startsWith("/")) {
        pathname = "/" + pathname;
      }
    }
    try {
      String utf8 = new String(pathname.getBytes("UTF-8"), "UTF-8");
      
      m_currentDirectory = utf8;
    } catch (Exception localException) {}
  }
  
  public String getCurrentDirectory() {
    return m_currentDirectory;
  }
  
  public static String detectEncoding(byte[] bytes) {
    String DEFAULT_ENCODING = "UTF-8";
    UniversalDetector detector = 
      new UniversalDetector(null);
    detector.handleData(bytes, 0, bytes.length);
    detector.dataEnd();
    String encoding = detector.getDetectedCharset();
    detector.reset();
    if (encoding == null) {
      encoding = DEFAULT_ENCODING;
    }
    return encoding;
  }
  
  public InputStream readFile(String filename) throws IllegalArgumentException, IOException {
    closeCurrentFile();
    String pathname = getCanonicalPathname(m_currentDirectory + filename);
    ZipEntry zipEntry = (ZipEntry)m_pathnameToZipEntryMap.get(pathname);
    String enc = detectEncoding(pathname.getBytes());
    
    if (zipEntry == null) {
      String utf8 = new String(pathname.getBytes("UTF-8"), "ISO-8859-1");
      zipEntry = (ZipEntry)m_pathnameToZipEntryMap.get(utf8);
    }
    if (zipEntry == null) {
      String utf8 = new String(pathname.getBytes("UTF-8"), "UTF-8");
      zipEntry = (ZipEntry)m_pathnameToZipEntryMap.get(utf8);
    }
    if (zipEntry != null) {
      m_currentlyOpenStream = m_zipFile.getInputStream(zipEntry);
      return m_currentlyOpenStream;
    }
    throw new FileNotFoundException(Messages.getString("Not_Found__") + pathname);
  }
  
  public void closeCurrentFile() throws IOException
  {
    if (m_currentlyOpenStream != null) {
      m_currentlyOpenStream.close();
      m_currentlyOpenStream = null;
    }
  }
  
  public String[] getFilesInCurrentDirectory() {
    throw new RuntimeException(Messages.getString("not_implemented"));
  }
  
  public String[] getDirectoriesInCurrentDirectory() {
    throw new RuntimeException(Messages.getString("not_implemented"));
  }
  
  public boolean isKeepFileSupported() {
    return true;
  }
  
  static Object getKeepKey(File file, String currentDirectory, String filename) { return file.getAbsolutePath() + "____" + currentDirectory + filename; }
  
  public Object getKeepKey(String filename) {
    return getKeepKey(m_rootFile, m_currentDirectory, filename);
  }
}
