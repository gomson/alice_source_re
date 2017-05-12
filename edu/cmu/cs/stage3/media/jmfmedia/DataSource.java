package edu.cmu.cs.stage3.media.jmfmedia;

import edu.cmu.cs.stage3.media.AbstractDataSource;
import java.util.Dictionary;
import java.util.Hashtable;



































































































public class DataSource
  extends AbstractDataSource
{
  private static Dictionary s_extensionToContentTypeMap = new Hashtable();
  
  static { s_extensionToContentTypeMap = new Hashtable();
    s_extensionToContentTypeMap.put("mp3", "audio.mpeg");
    s_extensionToContentTypeMap.put("wav", "audio.x_wav");
  }
  
  private ByteArrayDataSource m_jmfDataSource;
  public DataSource(byte[] data, String extension) {
    super(extension);
    String contentType = (String)s_extensionToContentTypeMap.get(extension.toLowerCase());
    try {
      m_jmfDataSource = new ByteArrayDataSource(data, contentType);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
  
  public byte[] getData() { return m_jmfDataSource.getData(); }
  
  public javax.media.protocol.DataSource getJMFDataSource() {
    return m_jmfDataSource;
  }
  
  protected edu.cmu.cs.stage3.media.Player createPlayer() {
    return new Player(this);
  }
}
