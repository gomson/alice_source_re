package edu.cmu.cs.stage3.image.codec;

import java.io.IOException;































































public class ByteArraySeekableStream
  extends SeekableStream
{
  private byte[] src;
  private int offset;
  private int length;
  private int pointer;
  
  public ByteArraySeekableStream(byte[] src, int offset, int length)
    throws IOException
  {
    this.src = src;
    this.offset = offset;
    this.length = length;
  }
  


  public ByteArraySeekableStream(byte[] src)
    throws IOException
  {
    this(src, 0, src.length);
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
  {
    pointer = ((int)pos);
  }
  






  public int read()
  {
    if (pointer < length + offset) {
      return src[(pointer++ + offset)] & 0xFF;
    }
    return -1;
  }
  











































  public int read(byte[] b, int off, int len)
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
    
    int oldPointer = pointer;
    pointer = Math.min(pointer + len, length + offset);
    
    if (pointer == oldPointer) {
      return -1;
    }
    System.arraycopy(src, oldPointer, b, off, pointer - oldPointer);
    return pointer - oldPointer;
  }
  
















  public int skipBytes(int n)
  {
    int oldPointer = pointer;
    pointer = Math.min(pointer + n, length + offset);
    return pointer - oldPointer;
  }
  


  public void close() {}
  

  public long length()
  {
    return length;
  }
}
