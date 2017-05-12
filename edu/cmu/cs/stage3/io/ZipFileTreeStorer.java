package edu.cmu.cs.stage3.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.Deflater;














public class ZipFileTreeStorer
  implements DirectoryTreeStorer
{
  private static final int ENDSIG = 101010256;
  private static final int HEADSIG = 33639248;
  private static final int LOCHEADSIG = 67324752;
  private static final int DATADESCSIG = 134695760;
  private static final int SIG = 1;
  private static final int MADEBY = 2;
  private static final int VERNEEDED = 3;
  private static final int BITFLAG = 4;
  private static final int COMPMETH = 5;
  private static final int MODTIME = 6;
  private static final int MODDATE = 7;
  private static final int CRC32 = 8;
  private static final int COMPSIZE = 9;
  private static final int UNCOMPSIZE = 10;
  private static final int NAMELENGTH = 11;
  private static final int EXTRALENGTH = 12;
  private static final int COMLENGTH = 13;
  private static final int DISKSTART = 14;
  private static final int INTATTRIB = 15;
  private static final int EXTATTRIB = 16;
  private static final int OFFSET = 17;
  private static final int FILENAME = 18;
  private static final int EXTRAFIELD = 19;
  private static final int COMMENT = 20;
  private static final int compressionBitFlag = 0;
  private static final int compressionMethodValue = 8;
  private static final int BITFLAG_DEFAULT = 0;
  private byte[] endHeader;
  private Vector centralDirectory;
  private int centralDirectoryLocation;
  private String currentFile = null;
  private File rootFile;
  private int currentHeader = 0;
  private byte[] currentLocalFileData;
  private CRC32 crc = new CRC32();
  
  private Vector holes;
  private Vector toWrite = new Vector();
  private ByteArrayOutputStream m_currentlyOpenStream = null;
  private boolean outputStreamOpen = false;
  private RandomAccessFile m_randomAccessFile = null;
  private HashMap filenameToHeaderMap;
  private boolean newZip = false;
  private boolean allOpen = false;
  private boolean shouldCompressCurrent = true;
  private int lastEntry = -1;
  private int timeStamp = -1;
  private String currentDirectory;
  public ZipFileTreeStorer() {}
  
  private class CentralDirectoryHeader
  {
    public int position;
    public int size;
    boolean shouldDelete = false;
    public ZipFileTreeStorer.LocalHeader localHeaderReference;
    public boolean newEntry = false;
    public boolean shouldCompress = true;
    public byte[] data = null;
    
    public int sig = 33639248;
    public int versionMadeBy = 20;
    public int versionNeededToExtract = 20;
    public int bitFlag = 0;
    public int compressionMethod = 8;
    public int lastModTime = 0;
    
    public int crc32 = 0;
    public int compressedSize = -1;
    public int uncompressedSize = -1;
    public int fileNameLength = 0;
    public int extraFieldLength = 0;
    public int fileCommentLength = 0;
    public int diskNumberStart = 0;
    public int internalFileAttributes = 0;
    public int externalFileAttributes = 0;
    public int relativeOffset = 0;
    public String fileName = "";
    public String extraField = "";
    public String comment = "";
    
    public CentralDirectoryHeader() {}
    
    public CentralDirectoryHeader(int offset, int pos, byte[] buf) throws IllegalArgumentException
    {
      position = (pos + offset);
      if ((buf != null) && (buf.length > 42 + offset)) {
        sig = ((int)ZipFileTreeStorer.this.getValue(buf, offset, 4));
        if (sig != 33639248) {
          throw new IllegalArgumentException();
        }
        versionMadeBy = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 4, 2));
        versionNeededToExtract = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 6, 2));
        bitFlag = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 8, 2));
        compressionMethod = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 10, 2));
        lastModTime = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 12, 4));
        
        crc32 = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 16, 4));
        compressedSize = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 20, 4));
        uncompressedSize = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 24, 4));
        fileNameLength = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 28, 2));
        extraFieldLength = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 30, 2));
        fileCommentLength = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 32, 2));
        diskNumberStart = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 34, 2));
        internalFileAttributes = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 36, 2));
        externalFileAttributes = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 38, 4));
        relativeOffset = ((int)ZipFileTreeStorer.this.getValue(buf, offset + 42, 4));
        fileName = ZipFileTreeStorer.this.getString(buf, offset + 46, fileNameLength);
        extraField = ZipFileTreeStorer.this.getString(buf, offset + 46 + fileNameLength, extraFieldLength);
        comment = ZipFileTreeStorer.this.getString(buf, offset + 46 + fileNameLength + extraFieldLength, fileCommentLength);
        
        size = (fileNameLength + extraFieldLength + fileCommentLength + 46);
      }
      else {
        throw new IllegalArgumentException();
      }
      if (compressionMethod == 0) {
        shouldCompress = false;
      }
      else {
        shouldCompress = true;
      }
    }
    
    public void setCompression(boolean compressBool) {
      shouldCompress = compressBool;
      if (shouldCompress) {
        bitFlag = 0;
        compressionMethod = 8;
        if (localHeaderReference != null) {
          localHeaderReference.bitFlag = bitFlag;
          localHeaderReference.compressionMethod = compressionMethod;
        }
      }
      else {
        bitFlag = 0;
        compressionMethod = 0;
        if (localHeaderReference != null) {
          localHeaderReference.bitFlag = 0;
          localHeaderReference.compressionMethod = 0;
        }
      }
    }
    
    public int getLocalHeaderSpace() {
      if (((bitFlag & 0x8) > 0) && (localHeaderReference != null) && (localHeaderReference.hasDataDescriptor))
      {
        return 30 + fileNameLength + extraFieldLength + compressedSize + 16;
      }
      

      return 30 + fileNameLength + extraFieldLength + compressedSize;
    }
    
    public int getThisHeaderSpace()
    {
      return 46 + fileNameLength + extraFieldLength + fileCommentLength;
    }
    
    public void writeData(int toWrite) throws IOException {
      switch (toWrite) {
      case 1:  ZipFileTreeStorer.this.writeInt(position + 0, sig, 4); break;
      case 2:  ZipFileTreeStorer.this.writeInt(position + 4, versionMadeBy, 2); break;
      case 3:  ZipFileTreeStorer.this.writeInt(position + 6, versionNeededToExtract, 2); break;
      case 4:  ZipFileTreeStorer.this.writeInt(position + 8, bitFlag, 2); break;
      case 5:  ZipFileTreeStorer.this.writeInt(position + 10, compressionMethod, 2); break;
      case 6:  ZipFileTreeStorer.this.writeInt(position + 12, lastModTime, 4); break;
      case 8: 
        ZipFileTreeStorer.this.writeInt(position + 16, crc32, 4); break;
      case 9:  ZipFileTreeStorer.this.writeInt(position + 20, compressedSize, 4); break;
      case 10:  ZipFileTreeStorer.this.writeInt(position + 24, uncompressedSize, 4); break;
      case 11:  ZipFileTreeStorer.this.writeInt(position + 28, fileNameLength, 2); break;
      case 12:  ZipFileTreeStorer.this.writeInt(position + 30, extraFieldLength, 2); break;
      case 13:  ZipFileTreeStorer.this.writeInt(position + 32, fileCommentLength, 2); break;
      case 14:  ZipFileTreeStorer.this.writeInt(position + 34, diskNumberStart, 2); break;
      case 15:  ZipFileTreeStorer.this.writeInt(position + 36, internalFileAttributes, 2); break;
      case 16:  ZipFileTreeStorer.this.writeInt(position + 38, externalFileAttributes, 4); break;
      case 17:  ZipFileTreeStorer.this.writeInt(position + 42, relativeOffset, 4); break;
      case 18:  ZipFileTreeStorer.this.writeString(position + 46, fileName); break;
      case 19:  ZipFileTreeStorer.this.writeString(position + 46 + fileName.length(), extraField); break;
      case 20:  ZipFileTreeStorer.this.writeString(position + 46 + fileName.length() + extraField.length(), comment); break;
      }
    }
    
    public void writeAll() throws IOException
    {
      for (int i = 1; i <= 20; i++) {
        writeData(i);
      }
    }
    
    public void setShouldDelete(boolean toSetTo) {
      shouldDelete = toSetTo;
    }
    
    public void setFileName(String name)
    {
      fileName = new String(name);
      fileNameLength = fileName.length();
      if (localHeaderReference != null) {
        localHeaderReference.fileName = new String(fileName);
        localHeaderReference.fileNameLength = fileName.length();
      }
    }
    
    public String toString() {
      String toReturn = "";
      toReturn = toReturn + "position in file: " + String.valueOf(position) + "\n";
      toReturn = toReturn + "size: " + String.valueOf(size) + "\n";
      toReturn = toReturn + "sig: " + String.valueOf(sig) + "\n";
      toReturn = toReturn + "version made: " + String.valueOf(versionMadeBy) + "\n";
      toReturn = toReturn + "version needed: " + String.valueOf(versionNeededToExtract) + "\n";
      toReturn = toReturn + "bit flag: " + String.valueOf(bitFlag) + "\n";
      toReturn = toReturn + "compression method: " + String.valueOf(compressionMethod) + "\n";
      toReturn = toReturn + "las mod time: " + String.valueOf(lastModTime) + "\n";
      
      toReturn = toReturn + " crc-32: " + String.valueOf(crc32) + "\n";
      toReturn = toReturn + "compressed size: " + String.valueOf(compressedSize) + "\n";
      toReturn = toReturn + "uncompressed size: " + String.valueOf(uncompressedSize) + "\n";
      toReturn = toReturn + "file name length: " + String.valueOf(fileNameLength) + "\n";
      toReturn = toReturn + "extra field length: " + String.valueOf(extraFieldLength) + "\n";
      toReturn = toReturn + "comment length: " + String.valueOf(fileCommentLength) + "\n";
      toReturn = toReturn + "disk number start: " + String.valueOf(diskNumberStart) + "\n";
      toReturn = toReturn + "internal attrib: " + String.valueOf(internalFileAttributes) + "\n";
      toReturn = toReturn + "external attrib: " + String.valueOf(externalFileAttributes) + "\n";
      toReturn = toReturn + "relative offset for local header: " + String.valueOf(relativeOffset) + "\n";
      toReturn = toReturn + "file name: " + fileName + ", spans from " + String.valueOf(position) + " to " + String.valueOf(position + getThisHeaderSpace()) + "\n";
      toReturn = toReturn + "extra field: " + extraField + "\n";
      toReturn = toReturn + "comment: " + comment;
      
      return toReturn;
    }
  }
  

  private class LocalHeader
  {
    public int position;
    
    public int size;
    public ZipFileTreeStorer.CentralDirectoryHeader centralDirectoryReference;
    public int sig = 67324752;
    public int versionNeededToExtract = 20;
    public int bitFlag = 0;
    public int compressionMethod = 8;
    public int lastModTime = 0;
    
    public int crc32 = 0;
    public int compressedSize = -1;
    public int uncompressedSize = -1;
    public int fileNameLength = 0;
    public int extraFieldLength = 0;
    public String fileName = "";
    public String extraField = "";
    public boolean hasDataDescriptor = false;
    
    public LocalHeader() {}
    
    public LocalHeader(int pos, ZipFileTreeStorer.CentralDirectoryHeader cd, byte[] buf) throws IllegalArgumentException
    {
      centralDirectoryReference = cd;
      position = pos;
      if ((buf != null) && (buf.length > 30)) {
        sig = ((int)ZipFileTreeStorer.this.getValue(buf, 0, 4));
        if (sig != 67324752)
        {
          throw new IllegalArgumentException();
        }
        versionNeededToExtract = ((int)ZipFileTreeStorer.this.getValue(buf, 4, 2));
        bitFlag = ((int)ZipFileTreeStorer.this.getValue(buf, 6, 2));
        compressionMethod = ((int)ZipFileTreeStorer.this.getValue(buf, 8, 2));
        lastModTime = ((int)ZipFileTreeStorer.this.getValue(buf, 10, 4));
        
        crc32 = ((int)ZipFileTreeStorer.this.getValue(buf, 14, 4));
        compressedSize = ((int)ZipFileTreeStorer.this.getValue(buf, 18, 4));
        uncompressedSize = ((int)ZipFileTreeStorer.this.getValue(buf, 22, 4));
        fileNameLength = ((int)ZipFileTreeStorer.this.getValue(buf, 26, 2));
        extraFieldLength = ((int)ZipFileTreeStorer.this.getValue(buf, 28, 2));
        fileName = ZipFileTreeStorer.this.getString(buf, 30, fileNameLength);
        extraField = ZipFileTreeStorer.this.getString(buf, 30 + fileNameLength, extraFieldLength);
        
        size = (fileNameLength + extraFieldLength + 30);
        
        hasDataDescriptor = false;


      }
      else
      {


        throw new IllegalArgumentException();
      }
    }
    
    public int getThisHeaderSpace() {
      return 30 + fileNameLength + extraFieldLength;
    }
    
    public boolean needsDataDescriptor() {
      return (bitFlag & 0x8) > 0;
    }
    
    public void writeData(int toWrite) throws IOException {
      switch (toWrite) {
      case 1:  ZipFileTreeStorer.this.writeInt(position + 0, sig, 4); break;
      case 3:  ZipFileTreeStorer.this.writeInt(position + 4, versionNeededToExtract, 2); break;
      case 4:  ZipFileTreeStorer.this.writeInt(position + 6, bitFlag, 2); break;
      case 5:  ZipFileTreeStorer.this.writeInt(position + 8, compressionMethod, 2); break;
      case 6:  ZipFileTreeStorer.this.writeInt(position + 10, lastModTime, 4); break;
      case 8: 
        ZipFileTreeStorer.this.writeInt(position + 14, crc32, 4); break;
      case 9:  ZipFileTreeStorer.this.writeInt(position + 18, compressedSize, 4); break;
      case 10:  ZipFileTreeStorer.this.writeInt(position + 22, uncompressedSize, 4); break;
      case 11:  ZipFileTreeStorer.this.writeInt(position + 26, fileNameLength, 2); break;
      case 12:  ZipFileTreeStorer.this.writeInt(position + 28, extraFieldLength, 2); break;
      case 18:  ZipFileTreeStorer.this.writeString(position + 30, fileName); break;
      case 19:  ZipFileTreeStorer.this.writeString(position + 30 + fileName.length(), extraField); break;
      }
    }
    
    public void writeAll() throws IOException
    {
      for (int i = 1; i <= 20; i++) {
        writeData(i);
      }
    }
    
    public void writeDataDescriptor() throws IOException {
      int startPosition = position + getThisHeaderSpace() + compressedSize;
      
      ZipFileTreeStorer.this.writeInt(startPosition, 134695760, 4);
      ZipFileTreeStorer.this.writeInt(startPosition + 4, crc32, 4);
      ZipFileTreeStorer.this.writeInt(startPosition + 8, compressedSize, 4);
      ZipFileTreeStorer.this.writeInt(startPosition + 12, uncompressedSize, 4);
    }
    
    public String toString() {
      String toReturn = "";
      toReturn = toReturn + "position in file: " + String.valueOf(position) + "\n";
      toReturn = toReturn + "size: " + String.valueOf(size) + "\n";
      toReturn = toReturn + "sig: " + String.valueOf(sig) + "\n";
      toReturn = toReturn + "version made: " + String.valueOf(versionNeededToExtract) + "\n";
      toReturn = toReturn + "bit flag: " + String.valueOf(bitFlag) + "\n";
      toReturn = toReturn + "compression method: " + String.valueOf(compressionMethod) + "\n";
      toReturn = toReturn + "las mod time: " + String.valueOf(lastModTime) + "\n";
      
      toReturn = toReturn + "crc-32: " + String.valueOf(crc32) + "\n";
      toReturn = toReturn + "compressed size: " + String.valueOf(compressedSize) + "\n";
      toReturn = toReturn + "uncompressed size: " + String.valueOf(uncompressedSize) + "\n";
      toReturn = toReturn + "file name length: " + String.valueOf(fileNameLength) + "\n";
      toReturn = toReturn + "extra field length: " + String.valueOf(extraFieldLength) + "\n";
      toReturn = toReturn + "local header file name: " + fileName + " spans from " + String.valueOf(position) + " to " + String.valueOf(position + getThisHeaderSpace() + compressedSize) + "\n";
      toReturn = toReturn + "extra field: " + extraField + "\n";
      if (hasDataDescriptor) {
        toReturn = toReturn + "There IS a data descriptor\n";
      }
      else {
        toReturn = toReturn + "There ISN'T a data descriptor\n";
      }
      

      return toReturn;
    }
  }
  
  private class LocalizedVacuity {
    public int pos;
    public int size;
    
    public LocalizedVacuity(int newpos, int newsize) {
      pos = newpos;
      size = newsize;
    }
    

    public String toString() { return "position: " + String.valueOf(pos) + ", size: " + String.valueOf(size); }
  }
  
  private class HoleComparator implements Comparator {
    private HoleComparator() {}
    
    public int compare(Object a, Object b) { ZipFileTreeStorer.LocalizedVacuity holeA = (ZipFileTreeStorer.LocalizedVacuity)a;
      ZipFileTreeStorer.LocalizedVacuity holeB = (ZipFileTreeStorer.LocalizedVacuity)b;
      return size - size;
    }
  }
  
  private class HeaderSizeComparator implements Comparator { private HeaderSizeComparator() {}
    
    public int compare(Object a, Object b) { ZipFileTreeStorer.CentralDirectoryHeader headerA = (ZipFileTreeStorer.CentralDirectoryHeader)a;
      ZipFileTreeStorer.CentralDirectoryHeader headerB = (ZipFileTreeStorer.CentralDirectoryHeader)b;
      int headerASize = headerA.getLocalHeaderSpace();
      int headerBSize = headerB.getLocalHeaderSpace();
      return headerASize - headerBSize;
    }
  }
  
  private class HeaderLocationComparator implements Comparator { private HeaderLocationComparator() {}
    
    public int compare(Object a, Object b) { ZipFileTreeStorer.CentralDirectoryHeader headerA = (ZipFileTreeStorer.CentralDirectoryHeader)a;
      ZipFileTreeStorer.CentralDirectoryHeader headerB = (ZipFileTreeStorer.CentralDirectoryHeader)b;
      return relativeOffset - relativeOffset;
    }
  }
  
  private HoleComparator holeComparator = new HoleComparator(null);
  private HeaderSizeComparator headerSizeComparator = new HeaderSizeComparator(null);
  private HeaderLocationComparator headerLocationComparator = new HeaderLocationComparator(null);
  
  private static long getCurrentDosTime() {
    Calendar d = Calendar.getInstance();
    int year = d.get(1);
    if (year < 1980) {
      return 2162688L;
    }
    return year - 1980 << 25 | d.get(2) + 1 << 21 | 
      d.get(5) << 16 | d.get(11) << 11 | d.get(12) << 5 | 
      d.get(13) >> 1;
  }
  
  private void writeInt(int pos, int value, int size) throws IOException {
    int mask = 255;
    try {
      m_randomAccessFile.seek(pos);
      for (int i = 0; i < size; i++) {
        m_randomAccessFile.writeByte(mask & value);
        value >>= 8;
      }
    }
    catch (IOException e) {
      throw e;
    }
  }
  
  private void writeString(int pos, String toWrite) throws IOException {
    try {
      m_randomAccessFile.seek(pos);
      m_randomAccessFile.write(toWrite.getBytes());
    }
    catch (IOException e) {
      throw e;
    }
  }
  
  private static String getCanonicalPathname(String pathname) {
    pathname = pathname.replace('\\', '/');
    
    int index;
    while ((index = pathname.indexOf("//")) != -1) { int index;
      pathname = pathname.substring(0, index + 1) + pathname.substring(index + 2);
    }
    
    if (pathname.charAt(0) == '/') {
      pathname = pathname.substring(1);
    }
    if (pathname.endsWith("/")) {
      pathname = pathname.substring(0, pathname.length() - 2);
    }
    return pathname;
  }
  
  public void setCurrentDirectory(String pathname) throws IllegalArgumentException {
    if (pathname == null) {
      pathname = "";
    }
    else if (pathname.length() > 0) {
      pathname = pathname.replace('\\', '/');
      
      int index;
      
      while ((index = pathname.indexOf("//")) != -1) { int index;
        pathname = pathname.substring(0, index + 1) + pathname.substring(index + 2);
      }
      
      if (pathname.charAt(0) == '/') {
        pathname = pathname.substring(1);
      }
      else {
        pathname = currentDirectory + pathname;
      }
      
      if (!pathname.endsWith("/")) {
        pathname = pathname + "/";
      }
      if (!pathname.startsWith("/")) {
        pathname = "/" + pathname;
      }
    }
    currentDirectory = pathname;
  }
  
  public String getCurrentDirectory() {
    return currentDirectory;
  }
  
  protected boolean isCompressed() {
    return true;
  }
  





  public void openForUpdate(Object pathname)
    throws IllegalArgumentException, IOException
  {
    open(pathname);
    for (int i = 0; i < centralDirectory.size(); i++) {
      centralDirectory.get(i)).shouldDelete = false;
    }
  }
  
  public void open(Object pathname) throws IllegalArgumentException, IOException {
    if (allOpen) {
      close();
    }
    if ((pathname instanceof String)) {
      m_randomAccessFile = new RandomAccessFile((String)pathname, "rw");
      rootFile = new File((String)pathname);
    } else if ((pathname instanceof File)) {
      m_randomAccessFile = new RandomAccessFile((File)pathname, "rw");
      rootFile = ((File)pathname);
    } else { if ((pathname instanceof OutputStream))
        throw new IllegalArgumentException("pathname must be an instance of String or java.io.File");
      if (pathname == null) {
        throw new IllegalArgumentException("pathname is null");
      }
      throw new IllegalArgumentException("pathname must be an instance of String or java.io.File");
    }
    if (m_randomAccessFile.length() <= 0L) {
      newZip = true;
    }
    currentDirectory = "";
    m_currentlyOpenStream = null;
    if (!newZip) {
      try {
        setAtEndSig();
      }
      catch (IOException e) {
        newZip = true;
      }
    }
    timeStamp = ((int)getCurrentDosTime());
    initCentralDirectory();
    
    setEnd();
    allOpen = true;
  }
  



  private void findHoles()
  {
    lastEntry = -1;
    if (centralDirectory != null) {
      holes = new Vector();
      for (int i = 0; i < centralDirectory.size() - 1; i++) {
        CentralDirectoryHeader current = (CentralDirectoryHeader)centralDirectory.get(i);
        CentralDirectoryHeader next = (CentralDirectoryHeader)centralDirectory.get(i + 1);
        int currentSize = 30 + fileNameLength + extraFieldLength + compressedSize;
        int currentEnd = relativeOffset + currentSize;
        if (currentEnd < relativeOffset) {
          LocalizedVacuity hole = new LocalizedVacuity(currentEnd, relativeOffset - currentEnd);
          holes.add(hole);
        }
      }
      if (centralDirectory.size() == 0) {
        lastEntry = 0;
      }
      else {
        CentralDirectoryHeader last = (CentralDirectoryHeader)centralDirectory.get(centralDirectory.size() - 1);
        int lastSize = 30 + fileNameLength + extraFieldLength + compressedSize;
        int currentEnd = relativeOffset + lastSize;
        lastEntry = currentEnd;
        Collections.sort(holes, holeComparator);
      }
    }
  }
  
  private void fillHoles() {
    findHoles();
    for (int i = 0; i < holes.size(); i++) {
      LocalizedVacuity currentHole = (LocalizedVacuity)holes.get(i);
      byte[] bufOfZeroes = new byte[size];
      try {
        writeValue(bufOfZeroes, 0, size, pos);
      }
      catch (Exception localException) {}
    }
  }
  
  private void setEnd() {
    lastEntry = 0;
    if ((centralDirectory != null) && (centralDirectory.size() > 0)) {
      CentralDirectoryHeader last = (CentralDirectoryHeader)centralDirectory.get(centralDirectory.size() - 1);
      int lastSize = last.getLocalHeaderSpace();
      int currentEnd = relativeOffset + lastSize;
      lastEntry = currentEnd;
    }
  }
  
  private void investigateHole(LocalizedVacuity h) throws IOException
  {
    try {
      m_randomAccessFile.seek(pos);
      byte[] buf = new byte[size];
      m_randomAccessFile.readFully(buf);
      for (int i = 0; i < buf.length; i++) {
        System.out.print((char)buf[i]);
      }
    }
    catch (IOException e) {
      throw e;
    }
  }
  




  private void updateHeader(CentralDirectoryHeader header, byte[] data, boolean writeImmediately)
    throws IOException
  {
    try
    {
      int newSize = 0;
      int oldSize = compressedSize;
      int inflatedSize = 0;
      if (data != null) {
        inflatedSize = data.length;
        crc.reset();
        crc.update(data);
        if (shouldCompress) {
          data = compressData(data);
        }
        newSize = data.length;
      }
      crc32 = ((int)crc.getValue());
      compressedSize = newSize;
      uncompressedSize = inflatedSize;
      if (localHeaderReference != null) {
        localHeaderReference.crc32 = ((int)crc.getValue());
        localHeaderReference.compressedSize = newSize;
        localHeaderReference.uncompressedSize = inflatedSize;
      }
      if ((newSize > oldSize) && (!writeImmediately))
      {
        header.setShouldDelete(true);
        data = data;
        toWrite.add(header);
        centralDirectory.remove(header);
      }
      else {
        data = null;
        if (writeImmediately)
        {
          localHeaderReference.writeAll();
        }
        else
        {
          localHeaderReference.writeData(8);
          localHeaderReference.writeData(9);
          localHeaderReference.writeData(10);
          localHeaderReference.writeData(6);
        }
        writeValue(data, 0, newSize, relativeOffset + localHeaderReference.getThisHeaderSpace());
        if ((localHeaderReference.needsDataDescriptor()) && (localHeaderReference.hasDataDescriptor)) {
          localHeaderReference.writeDataDescriptor();
        }
      }
    }
    catch (IOException e)
    {
      throw e;
    }
  }
  
  private byte[] compressData(byte[] data)
  {
    Deflater deflater = new Deflater(-1, true);
    deflater.setInput(data);
    int totalCompressed = 0;
    ByteArrayOutputStream compressedData = new ByteArrayOutputStream();
    byte[] buf = new byte['Ȁ'];
    while (!deflater.needsInput()) {
      int justCompressed = deflater.deflate(buf, 0, buf.length);
      if (justCompressed > 0) {
        compressedData.write(buf, 0, justCompressed);
      }
      totalCompressed += justCompressed;
    }
    deflater.finish();
    while (!deflater.finished()) {
      int justCompressed = deflater.deflate(buf, 0, buf.length);
      if (justCompressed > 0) {
        compressedData.write(buf, 0, justCompressed);
      }
      totalCompressed += justCompressed;
    }
    return compressedData.toByteArray();
  }
  
  private int getUnsigned(byte toConvert) {
    if (toConvert < 0) {
      return toConvert + 256;
    }
    return toConvert;
  }
  
  private long getValue(byte[] buf, int offset, int size) {
    long toReturn = 0L;
    for (int i = size - 1; i >= 0; i--) {
      int toAdd = getUnsigned(buf[(offset + i)]);
      toReturn += toAdd;
      if (i != 0) {
        toReturn <<= 8;
      }
    }
    return toReturn;
  }
  
  private void setValue(byte[] buf, int offset, int size, long value) {
    long mask = 255L;
    long temp = value;
    for (int i = 0; i < size; i++) {
      buf[(offset + i)] = ((byte)(int)(mask & temp));
      temp >>= 8;
    }
  }
  
  private void writeValue(byte[] buf, int offset, int size, int pos) throws IOException {
    if (buf != null) {
      try {
        m_randomAccessFile.seek(pos);
        m_randomAccessFile.write(buf, offset, size);
      }
      catch (Exception e) {
        throw new IOException("An error occurred while writing to the zip file. The file may not have been saved properly.");
      }
    }
  }
  










  private String getString(byte[] buf, int offset, int size)
  {
    StringBuffer sb = new StringBuffer(size);
    for (int i = 0; i < size; i++) {
      sb.append((char)getUnsigned(buf[(offset + i)]));
    }
    return sb.toString();
  }
  
  private void printFileHeaders() {
    for (int i = 0; i < centralDirectory.size(); i++) {
      System.out.println(centralDirectory.get(i));
    }
  }
  
  private void printEndHeader() {
    System.out.println("end header");
    System.out.println("Sig: " + getValue(endHeader, 0, 4));
    System.out.println("number of this disk: " + getValue(endHeader, 4, 2));
    System.out.println("number of the disk with the start of the cd: " + getValue(endHeader, 6, 2));
    System.out.println("total entries on this disk: " + getValue(endHeader, 8, 2));
    System.out.println("total entries: " + getValue(endHeader, 10, 2));
    System.out.println("size of cd: " + getValue(endHeader, 12, 4));
    System.out.println("offset of start of cd: " + getValue(endHeader, 16, 4));
    long commentLength = getValue(endHeader, 20, 2);
    System.out.println("comment length: " + commentLength);
  }
  
  private void initCentralDirectory() throws IOException {
    centralDirectory = new Vector();
    filenameToHeaderMap = new HashMap();
    if (newZip) {
      return;
    }
    int size = (int)getValue(endHeader, 12, 4);
    long offset = getValue(endHeader, 16, 4);
    int currentHeaderOffset = 0;
    centralDirectoryLocation = ((int)offset);
    byte[] centralArray = new byte[size];
    try {
      m_randomAccessFile.seek(offset);
      m_randomAccessFile.readFully(centralArray, 0, size);
    }
    catch (IOException e) {
      newZip = true;
      return;
    }
    currentHeaderOffset = 0;
    int currentSize = 46;
    while (currentHeaderOffset < centralArray.length) {
      if (currentHeaderOffset + 46 >= centralArray.length) {
        currentHeaderOffset = centralArray.length;
        break;
      }
      CentralDirectoryHeader header = null;
      try {
        header = new CentralDirectoryHeader(currentHeaderOffset, centralDirectoryLocation, centralArray);
      }
      catch (IllegalArgumentException e) {
        centralDirectory = new Vector();
        filenameToHeaderMap = new HashMap();
        newZip = true;
        return;
      }
      currentHeaderOffset += size;
      header.setShouldDelete(true);
      centralDirectory.add(header);
      filenameToHeaderMap.put(fileName, header);
      try
      {
        localHeaderReference = initLocalHeader(header);
      }
      catch (IOException e) {
        centralDirectory = new Vector();
        filenameToHeaderMap = new HashMap();
        newZip = true;
        return;
      }
      if (currentHeaderOffset + 46 >= centralArray.length) {
        currentHeaderOffset = centralArray.length;
        break;
      }
      if (getValue(centralArray, currentHeaderOffset, 4) != 33639248L) {
        currentHeaderOffset -= currentSize + 1;
        while ((currentHeaderOffset + 3 < centralArray.length) && (getValue(centralArray, currentHeaderOffset, 4) != 33639248L)) {
          currentHeaderOffset++;
        }
      }
    }
    Collections.sort(centralDirectory, headerLocationComparator);
  }
  
  private LocalHeader getCurrentLocalHeader() {
    if ((centralDirectory != null) && (currentHeader < centralDirectory.size())) {
      return centralDirectory.get(currentHeader)).localHeaderReference;
    }
    
    return null;
  }
  
  private CentralDirectoryHeader getCurrentCentralDirectoryHeader()
  {
    if ((centralDirectory != null) && (currentHeader < centralDirectory.size())) {
      return (CentralDirectoryHeader)centralDirectory.get(currentHeader);
    }
    
    return null;
  }
  
  private LocalHeader initLocalHeader(CentralDirectoryHeader headerReference) throws IOException
  {
    int pos = relativeOffset;
    int headerSize = fileNameLength + extraFieldLength + 30;
    byte[] currentLocalHeaderBytes = new byte[headerSize];
    LocalHeader headerToReturn = null;
    try
    {
      m_randomAccessFile.seek(pos);
      m_randomAccessFile.readFully(currentLocalHeaderBytes, 0, headerSize);
      headerToReturn = new LocalHeader(pos, headerReference, currentLocalHeaderBytes);
      m_randomAccessFile.seek(headerToReturn.getThisHeaderSpace() + compressedSize);
      byte[] dataDescriptor = new byte[4];
      m_randomAccessFile.readFully(dataDescriptor, 0, 4);
      if (getValue(dataDescriptor, 0, 4) == 134695760L) {
        hasDataDescriptor = true;
      }
    }
    catch (IOException e) {
      throw e;
    }
    return headerToReturn;
  }
  
  private void printHex(byte[] buf, int offset, int length)
  {
    for (int i = offset; i < length + offset; i++) {
      byte myByte = buf[i];
      for (int j = 0; j < 2; j++) {
        byte current = (byte)(myByte >> 4 * (1 - j));
        current = (byte)(current & 0xF);
        switch (current) {
        case 0:  System.out.print("0"); break;
        case 1:  System.out.print("1"); break;
        case 2:  System.out.print("2"); break;
        case 3:  System.out.print("3"); break;
        case 4:  System.out.print("4"); break;
        case 5:  System.out.print("5"); break;
        case 6:  System.out.print("6"); break;
        case 7:  System.out.print("7"); break;
        case 8:  System.out.print("8"); break;
        case 9:  System.out.print("9"); break;
        case 10:  System.out.print("a"); break;
        case 11:  System.out.print("b"); break;
        case 12:  System.out.print("c"); break;
        case 13:  System.out.print("d"); break;
        case 14:  System.out.print("e"); break;
        case 15:  System.out.print("f");
        }
      }
      System.out.print(" ");
    }
    System.out.println();
  }
  
  private void setAtEndSig() throws IOException
  {
    try {
      long pos;
      long len = pos = m_randomAccessFile.length();
      int endHeaderLength = 22;
      int maxSize = 65535;
      
      byte[] buf = new byte[endHeaderLength * 2];
      Arrays.fill(buf, (byte)0);
      int i; for (; len - pos < maxSize; 
          





          i < endHeaderLength * 2 - 3)
      {
        for (int i = 0; i < endHeaderLength; i++) {
          buf[(i + endHeaderLength)] = buf[i];
        }
        pos -= endHeaderLength;
        m_randomAccessFile.seek(pos);
        m_randomAccessFile.readFully(buf, 0, endHeaderLength);
        i = 0; continue;
        if (getValue(buf, i, 4) == 101010256L) {
          long endpos = pos + i;
          int commentLength = (int)getValue(buf, i + 20, 2);
          if (endpos + commentLength + endHeaderLength == len) {
            endHeader = new byte[commentLength + endHeaderLength];
            m_randomAccessFile.seek(endpos);
            m_randomAccessFile.readFully(endHeader, i, commentLength + endHeaderLength);
            return;
          }
        }
        i++;
      }
      










      throw new IOException("End header not found");
    }
    catch (IOException e) {
      throw e;
    }
  }
  
  private CentralDirectoryHeader getHeader(String filename)
  {
    if (newZip) {
      return null;
    }
    









    return (CentralDirectoryHeader)filenameToHeaderMap.get(filename);
  }
  
  private void writeHeader(CentralDirectoryHeader header, int pos) throws IOException {
    relativeOffset = pos;
    localHeaderReference.position = pos;
    
    if (data != null) {
      crc.reset();
      crc.update(data);
    }
    localHeaderReference.writeAll();
    if (data != null) {
      writeValue(data, 0, data.length, localHeaderReference.position + localHeaderReference.getThisHeaderSpace());
    }
    if (localHeaderReference.needsDataDescriptor()) {
      localHeaderReference.writeDataDescriptor();
    }
  }
  
  private boolean placeHeader(CentralDirectoryHeader header) throws IOException {
    for (int i = 0; i < holes.size(); i++) {
      LocalizedVacuity currentHole = (LocalizedVacuity)holes.get(i);
      if (size >= header.getLocalHeaderSpace())
      {
        writeHeader(header, pos);
        holes.remove(currentHole);
        return true;
      }
    }
    return false;
  }
  
  private void placeHeaders()
    throws IOException
  {
    Collections.sort(toWrite, headerSizeComparator);
    findHoles();
    






    boolean stillPlacing = true;
    for (int i = 0; i < toWrite.size(); i++) {
      CentralDirectoryHeader currentHeader = (CentralDirectoryHeader)toWrite.get(i);
      
      if (stillPlacing) {
        stillPlacing = placeHeader(currentHeader);
      }
      if (!stillPlacing) {
        int amountWritten = currentHeader.getLocalHeaderSpace();
        
        writeHeader(currentHeader, lastEntry);
        lastEntry += amountWritten;
      }
      
      if (!centralDirectory.contains(currentHeader)) {
        centralDirectory.add(currentHeader);
      }
    }
    



    if (toWrite.size() > 0) {
      Collections.sort(centralDirectory, headerLocationComparator);
    }
  }
  
  private void writeCentralDirectoryAndEndHeader() throws IOException {
    newZip = false;
    setEnd();
    
    int startLocation = lastEntry;
    int pos = startLocation;
    int size = 0;
    int totalEntries = centralDirectory.size();
    
    for (int i = 0; i < totalEntries; i++) {
      CentralDirectoryHeader currentHeader = (CentralDirectoryHeader)centralDirectory.get(i);
      position = pos;
      
      currentHeader.writeAll();
      pos += currentHeader.getThisHeaderSpace();
      size += currentHeader.getThisHeaderSpace();
    }
    writeInt(pos, 101010256, 4);
    writeInt(pos + 4, 0, 2);
    writeInt(pos + 6, 0, 2);
    writeInt(pos + 8, totalEntries, 2);
    writeInt(pos + 10, totalEntries, 2);
    writeInt(pos + 12, size, 4);
    writeInt(pos + 16, startLocation, 4);
    writeInt(pos + 20, 0, 2);
    try {
      m_randomAccessFile.setLength(pos + 22);
    }
    catch (IOException e) {
      throw e;
    }
  }
  
  private void deleteDirectories()
  {
    Iterator i = centralDirectory.iterator();
    while (i.hasNext()) {
      CentralDirectoryHeader currentHeader = (CentralDirectoryHeader)i.next();
      if (shouldDelete)
      {
        i.remove();
      }
    }
  }
  
  private void printLocalAndCDHeaders(Vector v) {
    System.out.println("*********************************LOCAL HEADERS:");
    for (int i = 0; i < v.size(); i++) {
      System.out.println(getlocalHeaderReference);
    }
    System.out.println("\n*******************************CENTRAL DIRECTORY HEADERS:\n");
    for (int i = 0; i < v.size(); i++) {
      System.out.println(v.get(i) + "\n");
    }
  }
  
  public void close() throws IOException {
    if (outputStreamOpen) {
      closeCurrentFile();
    }
    








    deleteDirectories();
    placeHeaders();
    writeCentralDirectoryAndEndHeader();
    fillHoles();
    










    allOpen = false;
    m_randomAccessFile.close();
    endHeader = null;
    centralDirectory = null;
    currentLocalFileData = null;
  }
  
  public OutputStream createFile(String filename) throws IllegalArgumentException, IOException
  {
    return createFile(filename, true);
  }
  
  public OutputStream createFile(String filename, boolean compressItIfYouGotIt) throws IllegalArgumentException, IOException {
    shouldCompressCurrent = compressItIfYouGotIt;
    if (m_randomAccessFile != null) {
      if (outputStreamOpen) {
        closeCurrentFile();
      }
      m_currentlyOpenStream = new ByteArrayOutputStream();
      currentFile = filename;
      outputStreamOpen = true;
    }
    else {
      throw new IOException("No zip file currently open");
    }
    
    return m_currentlyOpenStream;
  }
  
  public void keepFile(String filename) throws KeepFileNotSupportedException {
    if (newZip) {
      throw new KeepFileNotSupportedException();
    }
    String fullName = currentDirectory + filename;
    CentralDirectoryHeader toKeep = getHeader(fullName);
    if (toKeep != null) {
      toKeep.setShouldDelete(false);
    }
    else {
      throw new RuntimeException("Could not keep file \"" + fullName + "\"");
    }
  }
  
  public boolean isKeepFileSupported() {
    return !newZip;
  }
  
  public void createDirectory(String pathname) throws IllegalArgumentException, IOException {
    if ((pathname.indexOf('/') != -1) || (pathname.indexOf('\\') != -1)) {
      throw new IllegalArgumentException("pathname cannot contain path separators");
    }
    if (pathname.length() <= 0) {
      throw new IllegalArgumentException("pathname has no length");
    }
  }
  
  public void checkAndUpdateHeader(String filename, byte[] data) throws IOException
  {
    filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
    CentralDirectoryHeader header = getHeader(filename);
    if (header != null) {
      header.setShouldDelete(false);
      if (data != null) {
        lastModTime = timeStamp;
        localHeaderReference.lastModTime = timeStamp;
        
        updateHeader(header, data, false);
      }
    }
    else
    {
      header = new CentralDirectoryHeader();
      localHeaderReference = new LocalHeader();
      header.setCompression(shouldCompressCurrent);
      lastModTime = timeStamp;
      localHeaderReference.lastModTime = timeStamp;
      header.setFileName(filename);
      if (newZip) {
        position = lastEntry;
        relativeOffset = lastEntry;
        localHeaderReference.position = lastEntry;
        updateHeader(header, data, true);
        centralDirectory.add(header);
        lastEntry += header.getLocalHeaderSpace();
      }
      else
      {
        updateHeader(header, data, false);
      }
    }
  }
  
  public void closeCurrentFile() throws IOException {
    String filename = currentDirectory + currentFile;
    
    outputStreamOpen = false;
    checkAndUpdateHeader(filename, m_currentlyOpenStream.toByteArray());
    m_currentlyOpenStream.close();
  }
  
  private boolean doesFileExist(String filename) {
    for (int i = 0; i < centralDirectory.size(); i++) {
      CentralDirectoryHeader cdh = (CentralDirectoryHeader)centralDirectory.get(i);
      if (fileName.equals(currentDirectory + filename)) {
        return true;
      }
    }
    return false;
  }
  
  public Object getKeepKey(String filename) {
    if ((newZip) || (!doesFileExist(filename)))
    {




      return null;
    }
    return ZipFileTreeLoader.getKeepKey(rootFile, currentDirectory, filename);
  }
}
