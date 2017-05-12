package edu.cmu.cs.stage3.image.codec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;




























































public final class MemoryCacheSeekableStream
  extends SeekableStream
{
  private InputStream src;
  private long pointer = 0L;
  

  private static final int SECTOR_SHIFT = 9;
  

  private static final int SECTOR_SIZE = 512;
  

  private static final int SECTOR_MASK = 511;
  

  private Vector data = new Vector();
  

  int sectors = 0;
  

  int length = 0;
  

  boolean foundEOS = false;
  




  public MemoryCacheSeekableStream(InputStream src)
  {
    this.src = src;
  }
  





  private long readUntil(long pos)
    throws IOException
  {
    if (pos < length) {
      return pos;
    }
    
    if (foundEOS) {
      return length;
    }
    
    int sector = (int)(pos >> 9);
    

    int startSector = length >> 9;
    

    for (int i = startSector; i <= sector; i++) {
      byte[] buf = new byte['Ȁ'];
      data.addElement(buf);
      

      int len = 512;
      int off = 0;
      while (len > 0) {
        int nbytes = src.read(buf, off, len);
        
        if (nbytes == -1) {
          foundEOS = true;
          return length;
        }
        off += nbytes;
        len -= nbytes;
        

        length += nbytes;
      }
    }
    
    return length;
  }
  





  public boolean canSeekBackwards()
  {
    return true;
  }
  






  public long getFilePointer()
  {
    return pointer;
  }
  









  public void seek(long pos)
    throws IOException
  {
    if (pos < 0L) {
      throw new IOException(JaiI18N.getString("pos___0_"));
    }
    pointer = pos;
  }
  










  public int read()
    throws IOException
  {
    long next = pointer + 1L;
    long pos = readUntil(next);
    if (pos >= next) {
      byte[] buf = 
        (byte[])data.elementAt((int)(pointer >> 9));
      return buf[((int)(pointer++ & 0x1FF))] & 0xFF;
    }
    return -1;
  }
  















































  public int read(byte[] b, int off, int len)
    throws IOException
  {
    if (b == null) {
      throw new NullPointerException();
    }
    if ((off < 0) || (len < 0) || (off + len > b.length)) {
      throw new IndexOutOfBoundsException();
    }
    if (len == 0) {
      return 0;
    }
    
    long pos = readUntil(pointer + len);
    
    if (pos <= pointer) {
      return -1;
    }
    
    byte[] buf = (byte[])data.elementAt((int)(pointer >> 9));
    int nbytes = Math.min(len, 512 - (int)(pointer & 0x1FF));
    System.arraycopy(buf, (int)(pointer & 0x1FF), 
      b, off, nbytes);
    pointer += nbytes;
    return nbytes;
  }
}
