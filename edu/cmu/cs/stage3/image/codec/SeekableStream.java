package edu.cmu.cs.stage3.image.codec;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;










































































































































public abstract class SeekableStream
  extends InputStream
  implements DataInput
{
  public SeekableStream() {}
  
  public static SeekableStream wrapInputStream(InputStream is, boolean canSeekBackwards)
  {
    SeekableStream stream = null;
    
    if (canSeekBackwards) {
      try {
        stream = new FileCacheSeekableStream(is);
      } catch (Exception e) {
        stream = new MemoryCacheSeekableStream(is);
      }
    } else {
      stream = new ForwardSeekableStream(is);
    }
    return stream;
  }
  
















































































  protected long markPos = -1L;
  
  public abstract int read() throws IOException;
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  public synchronized void mark(int readLimit)
  {
    try {
      markPos = getFilePointer();
    } catch (IOException e) {
      markPos = -1L;
    }
  }
  




  public synchronized void reset()
    throws IOException
  {
    if (markPos != -1L) {
      seek(markPos);
    }
  }
  







  public boolean markSupported()
  {
    return canSeekBackwards();
  }
  




  public boolean canSeekBackwards()
  {
    return false;
  }
  











  public abstract long getFilePointer()
    throws IOException;
  











  public abstract void seek(long paramLong)
    throws IOException;
  










  public final void readFully(byte[] b)
    throws IOException
  {
    readFully(b, 0, b.length);
  }
  













  public final void readFully(byte[] b, int off, int len)
    throws IOException
  {
    int n = 0;
    do {
      int count = read(b, off + n, len - n);
      if (count < 0)
        throw new EOFException();
      n += count;
    } while (
    



      n < len);
  }
  
















  public int skipBytes(int n)
    throws IOException
  {
    if (n <= 0) {
      return 0;
    }
    return (int)skip(n);
  }
  










  public final boolean readBoolean()
    throws IOException
  {
    int ch = read();
    if (ch < 0)
      throw new EOFException();
    return ch != 0;
  }
  
















  public final byte readByte()
    throws IOException
  {
    int ch = read();
    if (ch < 0)
      throw new EOFException();
    return (byte)ch;
  }
  











  public final int readUnsignedByte()
    throws IOException
  {
    int ch = read();
    if (ch < 0)
      throw new EOFException();
    return ch;
  }
  



















  public final short readShort()
    throws IOException
  {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException();
    return (short)((ch1 << 8) + (ch2 << 0));
  }
  



















  public final short readShortLE()
    throws IOException
  {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException();
    return (short)((ch2 << 8) + (ch1 << 0));
  }
  


















  public final int readUnsignedShort()
    throws IOException
  {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException();
    return (ch1 << 8) + (ch2 << 0);
  }
  



















  public final int readUnsignedShortLE()
    throws IOException
  {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException();
    return (ch2 << 8) + (ch1 << 0);
  }
  

















  public final char readChar()
    throws IOException
  {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException();
    return (char)((ch1 << 8) + (ch2 << 0));
  }
  


















  public final char readCharLE()
    throws IOException
  {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException();
    return (char)((ch2 << 8) + (ch1 << 0));
  }
  


















  public final int readInt()
    throws IOException
  {
    int ch1 = read();
    int ch2 = read();
    int ch3 = read();
    int ch4 = read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      throw new EOFException();
    return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
  }
  



















  public final int readIntLE()
    throws IOException
  {
    int ch1 = read();
    int ch2 = read();
    int ch3 = read();
    int ch4 = read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      throw new EOFException();
    return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
  }
  


















  public final long readUnsignedInt()
    throws IOException
  {
    long ch1 = read();
    long ch2 = read();
    long ch3 = read();
    long ch4 = read();
    if ((ch1 | ch2 | ch3 | ch4) < 0L)
      throw new EOFException();
    return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
  }
  
  private byte[] ruileBuf = new byte[4];
  



















  public final long readUnsignedIntLE()
    throws IOException
  {
    readFully(ruileBuf);
    long ch1 = ruileBuf[0] & 0xFF;
    long ch2 = ruileBuf[1] & 0xFF;
    long ch3 = ruileBuf[2] & 0xFF;
    long ch4 = ruileBuf[3] & 0xFF;
    
    return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
  }
  


























  public final long readLong()
    throws IOException
  {
    return (readInt() << 32) + (readInt() & 0xFFFFFFFF);
  }
  



























  public final long readLongLE()
    throws IOException
  {
    int i1 = readIntLE();
    int i2 = readIntLE();
    return (i2 << 32) + (i1 & 0xFFFFFFFF);
  }
  















  public final float readFloat()
    throws IOException
  {
    return Float.intBitsToFloat(readInt());
  }
  
















  public final float readFloatLE()
    throws IOException
  {
    return Float.intBitsToFloat(readIntLE());
  }
  















  public final double readDouble()
    throws IOException
  {
    return Double.longBitsToDouble(readLong());
  }
  
















  public final double readDoubleLE()
    throws IOException
  {
    return Double.longBitsToDouble(readLongLE());
  }
  





















  public final String readLine()
    throws IOException
  {
    StringBuffer input = new StringBuffer();
    int c = -1;
    boolean eol = false;
    
    while (!eol) {
      switch (c = read()) {
      case -1: 
      case 10: 
        eol = true;
        break;
      case 13: 
        eol = true;
        long cur = getFilePointer();
        if (read() != 10) {
          seek(cur);
        }
        break;
      default: 
        input.append((char)c);
      }
      
    }
    
    if ((c == -1) && (input.length() == 0)) {
      return null;
    }
    return input.toString();
  }
  




















  public final String readUTF()
    throws IOException
  {
    return DataInputStream.readUTF(this);
  }
  



  protected void finalize()
    throws Throwable
  {
    super.finalize();
    close();
  }
}
