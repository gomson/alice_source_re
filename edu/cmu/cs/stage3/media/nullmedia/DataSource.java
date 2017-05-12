package edu.cmu.cs.stage3.media.nullmedia;

import edu.cmu.cs.stage3.media.AbstractDataSource;

public class DataSource extends AbstractDataSource {
  public DataSource(byte[] data, String extension) { super(extension);
    m_data = data;
  }
  
  private byte[] m_data;
  public byte[] getData() { return m_data; }
  
  protected edu.cmu.cs.stage3.media.Player createPlayer()
  {
    return new Player(this);
  }
  
  public double waitForDuration(long timeout) { return getDurationHint(); }
}
