package edu.cmu.cs.stage3.image.codec;















class PNGChunk
{
  int length;
  













  int type;
  













  byte[] data;
  













  int crc;
  













  String typeString;
  













  public PNGChunk(int length, int type, byte[] data, int crc)
  {
    this.length = length;
    this.type = type;
    this.data = data;
    this.crc = crc;
    
    typeString = new String();
    typeString += (char)(type >> 24);
    typeString += (char)(type >> 16 & 0xFF);
    typeString += (char)(type >> 8 & 0xFF);
    typeString += (char)(type & 0xFF);
  }
  
  public int getLength() {
    return length;
  }
  
  public int getType() {
    return type;
  }
  
  public String getTypeString() {
    return typeString;
  }
  
  public byte[] getData() {
    return data;
  }
  
  public byte getByte(int offset) {
    return data[offset];
  }
  
  public int getInt1(int offset) {
    return data[offset] & 0xFF;
  }
  
  public int getInt2(int offset) {
    return (data[offset] & 0xFF) << 8 | 
      data[(offset + 1)] & 0xFF;
  }
  
  public int getInt4(int offset) {
    return (data[offset] & 0xFF) << 24 | 
      (data[(offset + 1)] & 0xFF) << 16 | 
      (data[(offset + 2)] & 0xFF) << 8 | 
      data[(offset + 3)] & 0xFF;
  }
  
  public String getString4(int offset) {
    String s = new String();
    s = s + (char)data[offset];
    s = s + (char)data[(offset + 1)];
    s = s + (char)data[(offset + 2)];
    s = s + (char)data[(offset + 3)];
    return s;
  }
  
  public boolean isType(String typeName) {
    return typeString.equals(typeName);
  }
}
