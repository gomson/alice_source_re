package edu.cmu.cs.stage3.media.jmfmedia;
import javax.media.protocol.ContentDescriptor;

class ByteArraySeekablePullSourceStream implements javax.media.protocol.PullSourceStream, javax.media.protocol.Seekable { private static final ContentDescriptor RAW_CONTENT_DISCRIPTOR = new ContentDescriptor("raw");
  private byte[] m_data;
  private long m_location;
  private long m_size;
  
  public ByteArraySeekablePullSourceStream(byte[] data) { m_data = data;
    m_location = 0L;
    m_size = data.length;
  }
  
  public int read(byte[] buffer, int offset, int length) throws java.io.IOException { long bytesLeft = m_size - m_location;
    if (bytesLeft == 0L) {
      return -1;
    }
    int intBytesLeft = (int)bytesLeft;
    int toRead = length;
    if (intBytesLeft < length)
      toRead = intBytesLeft;
    System.arraycopy(m_data, (int)m_location, buffer, offset, toRead);
    m_location += toRead;
    return toRead;
  }
  
  public Object getControl(String controlType) { return null; }
  
  public Object[] getControls() {
    return null;
  }
  
  public ContentDescriptor getContentDescriptor() { return RAW_CONTENT_DISCRIPTOR; }
  
  public boolean endOfStream() {
    return m_location == m_size;
  }
  
  public long getContentLength() { return m_size; }
  
  public boolean willReadBlock() {
    return endOfStream();
  }
  
  public boolean isRandomAccess() { return true; }
  
  public long seek(long where) {
    if (where > m_size) {
      m_location = m_size;
    } else {
      m_location = where;
    }
    return m_location;
  }
  
  public long tell() { return m_location; }
}
