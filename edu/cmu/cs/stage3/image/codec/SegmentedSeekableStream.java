package edu.cmu.cs.stage3.image.codec;

import java.io.IOException;






































































































































































































public class SegmentedSeekableStream
  extends SeekableStream
{
  private SeekableStream stream;
  private StreamSegmentMapper mapper;
  private long pointer = 0L;
  








  private boolean canSeekBackwards;
  








  public SegmentedSeekableStream(SeekableStream stream, StreamSegmentMapper mapper, boolean canSeekBackwards)
  {
    this.stream = stream;
    this.mapper = mapper;
    this.canSeekBackwards = canSeekBackwards;
    
    if ((canSeekBackwards) && (!stream.canSeekBackwards())) {
      throw new IllegalArgumentException(JaiI18N.getString("Source_stream_does_not_support_seeking_backwards_"));
    }
  }
  























  public SegmentedSeekableStream(SeekableStream stream, long[] segmentPositions, int[] segmentLengths, boolean canSeekBackwards)
  {
    this(stream, new StreamSegmentMapperImpl(segmentPositions, segmentLengths), canSeekBackwards);
  }
  
































  public SegmentedSeekableStream(SeekableStream stream, long[] segmentPositions, int segmentLength, int totalLength, boolean canSeekBackwards)
  {
    this(stream, new SectorStreamSegmentMapper(segmentPositions, segmentLength, totalLength), canSeekBackwards);
  }
  






  public long getFilePointer()
  {
    return pointer;
  }
  





  public boolean canSeekBackwards()
  {
    return canSeekBackwards;
  }
  














  public void seek(long pos)
    throws IOException
  {
    if (pos < 0L) {
      throw new IOException();
    }
    pointer = pos;
  }
  
  private StreamSegment streamSegment = new StreamSegment();
  











  public int read()
    throws IOException
  {
    mapper.getStreamSegment(pointer, 1, streamSegment);
    stream.seek(streamSegment.getStartPos());
    
    int val = stream.read();
    pointer += 1L;
    return val;
  }
  















































  public int read(byte[] b, int off, int len)
    throws IOException
  {
    if (b == null) {
      throw new NullPointerException();
    }
    if ((off < 0) || (len < 0) || (off + len > b.length)) {
      throw new IndexOutOfBoundsException();
    }
    if (len == 0) {
      return 0;
    }
    
    mapper.getStreamSegment(pointer, len, streamSegment);
    stream.seek(streamSegment.getStartPos());
    
    int nbytes = stream.read(b, off, streamSegment.getSegmentLength());
    pointer += nbytes;
    return nbytes;
  }
}
