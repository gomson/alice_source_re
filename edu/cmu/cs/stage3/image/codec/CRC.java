package edu.cmu.cs.stage3.image.codec;































































class CRC
{
  private static int[] crcTable = new int['Ā'];
  
  static
  {
    for (int n = 0; n < 256; n++) {
      int c = n;
      for (int k = 0; k < 8; k++) {
        if ((c & 0x1) == 1) {
          c = 0xEDB88320 ^ c >>> 1;
        } else {
          c >>>= 1;
        }
        
        crcTable[n] = c;
      }
    } }
  
  CRC() {}
  
  public static int updateCRC(int crc, byte[] data, int off, int len) { int c = crc;
    
    for (int n = 0; n < len; n++) {
      c = crcTable[((c ^ data[(off + n)]) & 0xFF)] ^ c >>> 8;
    }
    
    return c;
  }
}
