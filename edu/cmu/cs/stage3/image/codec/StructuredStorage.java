package edu.cmu.cs.stage3.image.codec;

import java.io.IOException;
import java.util.StringTokenizer;
























































































































public class StructuredStorage
{
  SeekableStream file;
  private int sectorShift;
  private int miniSectorShift;
  private long csectFat;
  private long sectDirStart;
  private long miniSectorCutoff;
  private long sectMiniFatStart;
  private long csectMiniFat;
  private long sectDifStart;
  private long csectDif;
  private long[] sectFat;
  private long[] MINIFAT;
  private SSDirectoryEntry[] DIR;
  private SeekableStream miniStream;
  private SeekableStream FATStream;
  long cwdIndex = -1L;
  
  public StructuredStorage(SeekableStream file) throws IOException {
    this.file = file;
    

    getHeader();
    

    getFat();
    

    getMiniFat();
    

    getDirectory();
    

    getMiniStream();
  }
  
  private void getHeader() throws IOException {
    file.seek(30L);
    sectorShift = file.readUnsignedShortLE();
    
    file.seek(32L);
    miniSectorShift = file.readUnsignedShortLE();
    
    file.seek(44L);
    csectFat = file.readUnsignedIntLE();
    
    file.seek(48L);
    sectDirStart = file.readUnsignedIntLE();
    
    file.seek(56L);
    miniSectorCutoff = file.readUnsignedIntLE();
    
    file.seek(60L);
    sectMiniFatStart = file.readUnsignedIntLE();
    
    file.seek(64L);
    csectMiniFat = file.readUnsignedIntLE();
    
    file.seek(68L);
    sectDifStart = file.readUnsignedIntLE();
    
    file.seek(72L);
    csectDif = file.readUnsignedIntLE();
    
    sectFat = new long[109];
    file.seek(76L);
    for (int i = 0; i < 109; i++) {
      sectFat[i] = file.readUnsignedIntLE();
    }
  }
  
  private void getFat() throws IOException {
    int size = getSectorSize();
    int sectsPerFat = size / 4;
    int fatsPerDif = size / 4 - 1;
    
    int numFATSectors = (int)(csectFat + csectDif * fatsPerDif);
    long[] FATSectors = new long[numFATSectors];
    int count = 0;
    
    for (int i = 0; i < 109; i++) {
      long sector = sectFat[i];
      if (sector == 4294967295L) {
        break;
      }
      
      FATSectors[(count++)] = getOffsetOfSector(sectFat[i]);
    }
    
    if (csectDif > 0L) {
      long dif = sectDifStart;
      byte[] difBuf = new byte[size];
      
      for (int i = 0; i < csectDif; i++) {
        readSector(dif, difBuf, 0);
        for (int j = 0; j < fatsPerDif; j++) {
          int sec = FPXUtils.getIntLE(difBuf, 4 * j);
          FATSectors[(count++)] = getOffsetOfSector(sec);
        }
        
        dif = FPXUtils.getIntLE(difBuf, size - 4);
      }
    }
    
    FATStream = new SegmentedSeekableStream(file, 
      FATSectors, 
      size, 
      numFATSectors * size, 
      true);
  }
  
  private void getMiniFat() throws IOException {
    int size = getSectorSize();
    int sectsPerFat = size / 4;
    int index = 0;
    
    MINIFAT = new long[(int)(csectMiniFat * sectsPerFat)];
    
    long sector = sectMiniFatStart;
    byte[] buf = new byte[size];
    while (sector != 4294967294L) {
      readSector(sector, buf, 0);
      for (int j = 0; j < sectsPerFat; j++) {
        MINIFAT[(index++)] = FPXUtils.getIntLE(buf, 4 * j);
      }
      sector = getFATSector(sector);
    }
  }
  
  private void getDirectory() throws IOException {
    int size = getSectorSize();
    long sector = sectDirStart;
    

    int numDirectorySectors = 0;
    while (sector != 4294967294L) {
      sector = getFATSector(sector);
      numDirectorySectors++;
    }
    
    int directoryEntries = 4 * numDirectorySectors;
    DIR = new SSDirectoryEntry[directoryEntries];
    
    sector = sectDirStart;
    byte[] buf = new byte[size];
    int index = 0;
    while (sector != 4294967294L) {
      readSector(sector, buf, 0);
      
      int offset = 0;
      for (int i = 0; i < 4; i++)
      {

        int length = FPXUtils.getShortLE(buf, offset + 64);
        
        String name = FPXUtils.getString(buf, offset + 0, length);
        long SIDLeftSibling = 
          FPXUtils.getUnsignedIntLE(buf, offset + 68);
        long SIDRightSibling = 
          FPXUtils.getUnsignedIntLE(buf, offset + 72);
        long SIDChild = FPXUtils.getUnsignedIntLE(buf, offset + 76);
        long startSector = 
          FPXUtils.getUnsignedIntLE(buf, offset + 116);
        long streamSize = FPXUtils.getUnsignedIntLE(buf, offset + 120);
        
        DIR[index] = new SSDirectoryEntry(index, 
          name, 
          streamSize, 
          startSector, 
          SIDLeftSibling, 
          SIDRightSibling, 
          SIDChild);
        index++;
        offset += 128;
      }
      
      sector = getFATSector(sector);
    }
  }
  
  private void getMiniStream() throws IOException {
    int length = getLength(0L);
    int sectorSize = getSectorSize();
    int sectors = (length + sectorSize - 1) / sectorSize;
    
    long[] segmentPositions = new long[sectors];
    
    long sector = getStartSector(0L);
    
    for (int i = 0; i < sectors - 1; i++) {
      segmentPositions[i] = getOffsetOfSector(sector);
      sector = getFATSector(sector);
    }
    segmentPositions[(sectors - 1)] = getOffsetOfSector(sector);
    
    miniStream = new SegmentedSeekableStream(file, 
      segmentPositions, 
      sectorSize, 
      length, 
      true);
  }
  
  private int getSectorSize() {
    return 1 << sectorShift;
  }
  
  private long getOffsetOfSector(long sector) {
    return sector * getSectorSize() + 512L;
  }
  
  private int getMiniSectorSize() {
    return 1 << miniSectorShift;
  }
  
  private long getOffsetOfMiniSector(long sector) {
    return sector * getMiniSectorSize();
  }
  
  private void readMiniSector(long sector, byte[] buf, int offset, int length)
    throws IOException
  {
    miniStream.seek(getOffsetOfMiniSector(sector));
    miniStream.read(buf, offset, length);
  }
  
  private void readMiniSector(long sector, byte[] buf, int offset) throws IOException
  {
    readMiniSector(sector, buf, offset, getMiniSectorSize());
  }
  
  private void readSector(long sector, byte[] buf, int offset, int length) throws IOException
  {
    file.seek(getOffsetOfSector(sector));
    file.read(buf, offset, length);
  }
  
  private void readSector(long sector, byte[] buf, int offset) throws IOException
  {
    readSector(sector, buf, offset, getSectorSize());
  }
  
  private SSDirectoryEntry getDirectoryEntry(long index)
  {
    return DIR[((int)index)];
  }
  
  private long getStartSector(long index)
  {
    return DIR[((int)index)].getStartSector();
  }
  

  private int getLength(long index)
  {
    return (int)DIR[((int)index)].getSize();
  }
  
  private long getFATSector(long sector) throws IOException {
    FATStream.seek(4L * sector);
    return FATStream.readUnsignedIntLE();
  }
  
  private long getMiniFATSector(long sector) {
    return MINIFAT[((int)sector)];
  }
  
  private int getCurrentIndex() {
    return -1;
  }
  
  private int getIndex(String name, int index) {
    return -1;
  }
  
  private long searchDirectory(String name, long index) {
    if (index == 4294967295L) {
      return -1L;
    }
    
    SSDirectoryEntry dirent = getDirectoryEntry(index);
    
    if (name.equals(dirent.getName())) {
      return index;
    }
    long lindex = 
      searchDirectory(name, dirent.getSIDLeftSibling());
    if (lindex != -1L) {
      return lindex;
    }
    
    long rindex = 
      searchDirectory(name, dirent.getSIDRightSibling());
    if (rindex != -1L) {
      return rindex;
    }
    

    return -1L;
  }
  

  public void changeDirectoryToRoot()
  {
    cwdIndex = getDirectoryEntry(0L).getSIDChild();
  }
  
  public boolean changeDirectory(String name) {
    long index = searchDirectory(name, cwdIndex);
    if (index != -1L) {
      cwdIndex = getDirectoryEntry(index).getSIDChild();
      return true;
    }
    return false;
  }
  

  private long getStreamIndex(String name)
  {
    long index = cwdIndex;
    
    StringTokenizer st = new StringTokenizer(name, "/");
    boolean firstTime = true;
    while (st.hasMoreTokens()) {
      String tok = st.nextToken();
      
      if (!firstTime) {
        index = getDirectoryEntry(index).getSIDChild();
      } else {
        firstTime = false;
      }
      index = searchDirectory(tok, index);
    }
    
    return index;
  }
  
  public byte[] getStreamAsBytes(String name) throws IOException {
    long index = getStreamIndex(name);
    if (index == -1L) {
      return null;
    }
    


    int length = getLength(index);
    byte[] buf = new byte[length];
    
    if (length > miniSectorCutoff) {
      int sectorSize = getSectorSize();
      int sectors = (length + sectorSize - 1) / sectorSize;
      
      long sector = getStartSector(index);
      int offset = 0;
      for (int i = 0; i < sectors - 1; i++) {
        readSector(sector, buf, offset, sectorSize);
        offset += sectorSize;
        sector = getFATSector(sector);
      }
      
      readSector(sector, buf, offset, length - offset);
    } else {
      int sectorSize = getMiniSectorSize();
      int sectors = (length + sectorSize - 1) / sectorSize;
      
      long sector = getStartSector(index);
      

      int offset = 0;
      for (int i = 0; i < sectors - 1; i++) {
        long miniSectorOffset = getOffsetOfMiniSector(sector);
        readMiniSector(sector, buf, offset, sectorSize);
        offset += sectorSize;
        sector = getMiniFATSector(sector);
      }
      readMiniSector(sector, buf, offset, length - offset);
    }
    
    return buf;
  }
  
  public SeekableStream getStream(String name) throws IOException {
    long index = getStreamIndex(name);
    if (index == -1L) {
      return null;
    }
    


    int length = getLength(index);
    



    if (length > miniSectorCutoff) {
      int sectorSize = getSectorSize();
      int sectors = (length + sectorSize - 1) / sectorSize;
      long[] segmentPositions = new long[sectors];
      
      long sector = getStartSector(index);
      for (int i = 0; i < sectors - 1; i++) {
        segmentPositions[i] = getOffsetOfSector(sector);
        sector = getFATSector(sector);
      }
      segmentPositions[(sectors - 1)] = getOffsetOfSector(sector);
      
      return new SegmentedSeekableStream(file, 
        segmentPositions, 
        sectorSize, 
        length, 
        true);
    }
    int sectorSize = getMiniSectorSize();
    int sectors = (length + sectorSize - 1) / sectorSize;
    long[] segmentPositions = new long[sectors];
    
    long sector = getStartSector(index);
    for (int i = 0; i < sectors - 1; i++) {
      segmentPositions[i] = getOffsetOfMiniSector(sector);
      sector = getMiniFATSector(sector);
    }
    segmentPositions[(sectors - 1)] = getOffsetOfMiniSector(sector);
    
    return new SegmentedSeekableStream(miniStream, 
      segmentPositions, 
      sectorSize, 
      length, 
      true);
  }
}
