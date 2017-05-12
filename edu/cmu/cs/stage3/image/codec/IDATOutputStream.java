package edu.cmu.cs.stage3.image.codec;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;




























































































































































































class IDATOutputStream
  extends FilterOutputStream
{
  private static final byte[] typeSignature = { 73, 68, 65, 84 };
  
  private int bytesWritten = 0;
  private int segmentLength;
  byte[] buffer;
  
  public IDATOutputStream(OutputStream output, int segmentLength)
  {
    super(output);
    this.segmentLength = segmentLength;
    buffer = new byte[segmentLength];
  }
  
  public void close() throws IOException
  {
    flush();
  }
  
  private void writeInt(int x) throws IOException {
    out.write(x >> 24);
    out.write(x >> 16 & 0xFF);
    out.write(x >> 8 & 0xFF);
    out.write(x & 0xFF);
  }
  
  public void flush()
    throws IOException
  {
    writeInt(bytesWritten);
    
    out.write(typeSignature);
    
    out.write(buffer, 0, bytesWritten);
    
    int crc = -1;
    crc = CRC.updateCRC(crc, typeSignature, 0, 4);
    crc = CRC.updateCRC(crc, buffer, 0, bytesWritten);
    

    writeInt(crc ^ 0xFFFFFFFF);
    

    bytesWritten = 0;
  }
  
  public void write(byte[] b) throws IOException
  {
    write(b, 0, b.length);
  }
  
  public void write(byte[] b, int off, int len) throws IOException
  {
    while (len > 0) {
      int bytes = Math.min(segmentLength - bytesWritten, len);
      System.arraycopy(b, off, buffer, bytesWritten, bytes);
      off += bytes;
      len -= bytes;
      bytesWritten += bytes;
      
      if (bytesWritten == segmentLength) {
        flush();
      }
    }
  }
  
  public void write(int b) throws IOException
  {
    buffer[(bytesWritten++)] = ((byte)b);
    if (bytesWritten == segmentLength) {
      flush();
    }
  }
}
