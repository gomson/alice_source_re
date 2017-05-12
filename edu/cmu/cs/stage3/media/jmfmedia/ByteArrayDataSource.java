package edu.cmu.cs.stage3.media.jmfmedia;

import java.io.IOException;
import java.util.Dictionary;
import javax.media.Duration;
import javax.media.Time;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;















































class ByteArrayDataSource
  extends PullDataSource
{
  private static Dictionary s_extensionToContentTypeMap;
  private byte[] m_data;
  private String m_contentType;
  
  public ByteArrayDataSource(byte[] data, String contentType)
  {
    m_data = data;
    m_contentType = contentType;
  }
  
  public byte[] getData() { return m_data; }
  
  public String getContentType()
  {
    return m_contentType;
  }
  
  public Time getDuration() {
    return Duration.DURATION_UNKNOWN;
  }
  
  public void connect() throws IOException
  {}
  
  public void start() throws IOException
  {}
  
  public void disconnect() {}
  
  public Object getControl(String parm1)
  {
    return null;
  }
  
  public PullSourceStream[] getStreams() {
    return new PullSourceStream[] {
      new ByteArraySeekablePullSourceStream(m_data) };
  }
  
  public void stop() throws IOException
  {}
  
  public Object[] getControls()
  {
    return null;
  }
}
