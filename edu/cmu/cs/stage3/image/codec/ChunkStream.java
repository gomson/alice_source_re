package edu.cmu.cs.stage3.image.codec;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;





















































































class ChunkStream
  extends OutputStream
  implements DataOutput
{
  private String type;
  private ByteArrayOutputStream baos;
  private DataOutputStream dos;
  
  public ChunkStream(String type)
    throws IOException
  {
    this.type = type;
    
    baos = new ByteArrayOutputStream();
    dos = new DataOutputStream(baos);
  }
  
  public void write(byte[] b) throws IOException
  {
    dos.write(b);
  }
  
  public void write(byte[] b, int off, int len) throws IOException
  {
    dos.write(b, off, len);
  }
  
  public void write(int b) throws IOException
  {
    dos.write(b);
  }
  
  public void writeBoolean(boolean v) throws IOException {
    dos.writeBoolean(v);
  }
  
  public void writeByte(int v) throws IOException {
    dos.writeByte(v);
  }
  
  public void writeBytes(String s) throws IOException {
    dos.writeBytes(s);
  }
  
  public void writeChar(int v) throws IOException {
    dos.writeChar(v);
  }
  
  public void writeChars(String s) throws IOException {
    dos.writeChars(s);
  }
  
  public void writeDouble(double v) throws IOException {
    dos.writeDouble(v);
  }
  
  public void writeFloat(float v) throws IOException {
    dos.writeFloat(v);
  }
  
  public void writeInt(int v) throws IOException {
    dos.writeInt(v);
  }
  
  public void writeLong(long v) throws IOException {
    dos.writeLong(v);
  }
  
  public void writeShort(int v) throws IOException {
    dos.writeShort(v);
  }
  
  public void writeUTF(String str) throws IOException {
    dos.writeUTF(str);
  }
  
  public void writeToStream(DataOutputStream output) throws IOException {
    byte[] typeSignature = new byte[4];
    typeSignature[0] = ((byte)type.charAt(0));
    typeSignature[1] = ((byte)type.charAt(1));
    typeSignature[2] = ((byte)type.charAt(2));
    typeSignature[3] = ((byte)type.charAt(3));
    
    dos.flush();
    baos.flush();
    
    byte[] data = baos.toByteArray();
    int len = data.length;
    
    output.writeInt(len);
    output.write(typeSignature);
    output.write(data, 0, len);
    
    int crc = -1;
    crc = CRC.updateCRC(crc, typeSignature, 0, 4);
    crc = CRC.updateCRC(crc, data, 0, len);
    output.writeInt(crc ^ 0xFFFFFFFF);
  }
}
