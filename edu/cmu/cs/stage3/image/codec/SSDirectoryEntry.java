package edu.cmu.cs.stage3.image.codec;









class SSDirectoryEntry
{
  int index;
  







  String name;
  







  long size;
  







  long startSector;
  







  long SIDLeftSibling;
  







  long SIDRightSibling;
  







  long SIDChild;
  








  public SSDirectoryEntry(int index, String name, long size, long startSector, long SIDLeftSibling, long SIDRightSibling, long SIDChild)
  {
    this.name = name;
    this.index = index;
    this.size = size;
    this.startSector = startSector;
    this.SIDLeftSibling = SIDLeftSibling;
    this.SIDRightSibling = SIDRightSibling;
    this.SIDChild = SIDChild;
  }
  



  public String getName()
  {
    return name;
  }
  
  public long getSize() {
    return size;
  }
  
  public long getStartSector() {
    return startSector;
  }
  
  public long getSIDLeftSibling() {
    return SIDLeftSibling;
  }
  
  public long getSIDRightSibling() {
    return SIDRightSibling;
  }
  
  public long getSIDChild() {
    return SIDChild;
  }
}
