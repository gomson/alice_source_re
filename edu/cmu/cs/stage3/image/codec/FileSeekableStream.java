package edu.cmu.cs.stage3.image.codec;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;





















































public class FileSeekableStream
  extends SeekableStream
{
  private RandomAccessFile file;
  private long markPos = -1L;
  

  private static final int PAGE_SHIFT = 9;
  

  private static final int PAGE_SIZE = 512;
  

  private static final int PAGE_MASK = 511;
  

  private static final int NUM_PAGES = 32;
  

  private static final int READ_CACHE_LIMIT = 512;
  

  private byte[][] pageBuf = new byte['Ȁ'][32];
  


  private int[] currentPage = new int[32];
  
  private long length = 0L;
  
  private long pointer = 0L;
  


  public FileSeekableStream(RandomAccessFile file)
    throws IOException
  {
    this.file = file;
    file.seek(0L);
    length = file.length();
    

    for (int i = 0; i < 32; i++) {
      pageBuf[i] = new byte['Ȁ'];
      currentPage[i] = -1;
    }
  }
  


  public FileSeekableStream(File file)
    throws IOException
  {
    this(new RandomAccessFile(file, "r"));
  }
  


  public FileSeekableStream(String name)
    throws IOException
  {
    this(new RandomAccessFile(name, "r"));
  }
  

  public final boolean canSeekBackwards()
  {
    return true;
  }
  






  public final long getFilePointer()
    throws IOException
  {
    return pointer;
  }
  
  public final void seek(long pos) throws IOException
  {
    if (pos < 0L) {
      throw new IOException(JaiI18N.getString("pos___0_"));
    }
    pointer = pos;
  }
  
  public final int skip(int n) throws IOException {
    pointer += n;
    return n;
  }
  
  private byte[] readPage(long pointer) throws IOException {
    int page = (int)(pointer >> 9);
    
    for (int i = 0; i < 32; i++) {
      if (currentPage[i] == page) {
        return pageBuf[i];
      }
    }
    

    int index = (int)(Math.random() * 32.0D);
    currentPage[index] = page;
    
    long pos = page << 9;
    long remaining = length - pos;
    int len = 512L < remaining ? 512 : (int)remaining;
    file.seek(pos);
    file.readFully(pageBuf[index], 0, len);
    
    return pageBuf[index];
  }
  
  public final int read()
    throws IOException
  {
    if (pointer >= length) {
      return -1;
    }
    
    byte[] buf = readPage(pointer);
    return buf[((int)(pointer++ & 0x1FF))] & 0xFF;
  }
  
  public final int read(byte[] b, int off, int len)
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
    
    len = (int)Math.min(len, length - pointer);
    if (len <= 0) {
      return -1;
    }
    

    if (len > 512) {
      file.seek(pointer);
      int nbytes = file.read(b, off, len);
      pointer += nbytes;
      return nbytes;
    }
    byte[] buf = readPage(pointer);
    

    int remaining = 512 - (int)(pointer & 0x1FF);
    int newLen = len < remaining ? len : remaining;
    System.arraycopy(buf, (int)(pointer & 0x1FF), b, off, newLen);
    
    pointer += newLen;
    return newLen;
  }
  

  public final void close()
    throws IOException
  {
    file.close();
  }
  




  public final synchronized void mark(int readLimit)
  {
    markPos = pointer;
  }
  




  public final synchronized void reset()
    throws IOException
  {
    if (markPos != -1L) {
      pointer = markPos;
    }
  }
  

  public boolean markSupported()
  {
    return true;
  }
}
