package edu.cmu.cs.stage3.image.codec;

















class Property
{
  private int type;
  















  private int offset;
  
















  public Property(int type, int offset)
  {
    this.type = type;
    this.offset = offset;
  }
  
  public int getType() {
    return type;
  }
  
  public int getOffset() {
    return offset;
  }
}
