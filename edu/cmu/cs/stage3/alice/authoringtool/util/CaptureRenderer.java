package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.Renderer;
import javax.media.format.AudioFormat;


















public class CaptureRenderer
  implements Renderer
{
  private Format[] m_inputFormats = {
  
    new AudioFormat(
    "LINEAR", 
    -1.0D, 
    16, 
    2, 
    0, 
    -1, 
    -1, 
    -1.0D, Format.byteArray) };
  

  private Format m_inputFormat;
  

  private Vector m_buffers = new Vector();
  
  public CaptureRenderer() {}
  
  public Format[] getSupportedInputFormats() { return m_inputFormats; }
  
  public Format setInputFormat(Format inputFormat)
  {
    m_inputFormat = inputFormat;
    return m_inputFormat;
  }
  
  public String getName() {
    return "CaptureRenderer";
  }
  

  public void open() {}
  
  public void close() {}
  
  public void reset()
  {
    m_buffers.clear();
  }
  

  public void start() {}
  
  public void stop() {}
  
  public Object[] getControls()
  {
    return new Object[0];
  }
  
  public Object getControl(String name) {
    return null;
  }
  
  public int process(Buffer buffer) {
    m_buffers.addElement(buffer.clone());
    return 0;
  }
  
  public int getDataLength() {
    int size = 0;
    Enumeration enum0 = m_buffers.elements();
    while (enum0.hasMoreElements()) {
      Buffer buffer = (Buffer)enum0.nextElement();
      size += buffer.getOffset();
      size += buffer.getLength();
    }
    return size;
  }
  
  public void getData(byte[] data, int offset, int length) {
    int location = offset;
    Enumeration enum0 = m_buffers.elements();
    while (enum0.hasMoreElements()) {
      Buffer buffer = (Buffer)enum0.nextElement();
      location += buffer.getOffset();
      if (location + buffer.getLength() <= data.length) {
        System.arraycopy(buffer.getData(), 0, data, location, buffer.getLength());
      } else {
        System.err.println("out of range " + buffer + " " + buffer.getOffset() + " " + buffer.getLength());
        System.err.println(data.length + " " + location);
      }
      location += buffer.getLength();
    }
  }
}
