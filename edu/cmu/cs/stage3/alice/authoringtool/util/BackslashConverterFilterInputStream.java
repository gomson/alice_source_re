package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


















public class BackslashConverterFilterInputStream
  extends FilterInputStream
{
  int newChar;
  
  public BackslashConverterFilterInputStream(InputStream is)
  {
    super(is);
    newChar = 47;
  }
  
  public BackslashConverterFilterInputStream(InputStream is, int newChar)
  {
    super(is);
    this.newChar = newChar;
  }
  
  public int read() throws IOException {
    int c = super.read();
    if (c == 92) {
      c = newChar;
    }
    return c;
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    int result = super.read(b, off, len);
    for (int i = 0; i < result; i++) {
      if (b[(off + i)] == 92) {
        b[(off + i)] = ((byte)newChar);
      }
    }
    return result;
  }
}
