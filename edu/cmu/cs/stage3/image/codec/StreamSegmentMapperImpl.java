package edu.cmu.cs.stage3.image.codec;



















class StreamSegmentMapperImpl
  implements StreamSegmentMapper
{
  private long[] segmentPositions;
  
















  private int[] segmentLengths;
  

















  public StreamSegmentMapperImpl(long[] segmentPositions, int[] segmentLengths)
  {
    this.segmentPositions = ((long[])segmentPositions.clone());
    this.segmentLengths = ((int[])segmentLengths.clone());
  }
  
  public StreamSegment getStreamSegment(long position, int length) {
    int numSegments = segmentLengths.length;
    for (int i = 0; i < numSegments; i++) {
      int len = segmentLengths[i];
      if (position < len) {
        return new StreamSegment(segmentPositions[i] + position, 
          Math.min(len - (int)position, 
          length));
      }
      position -= len;
    }
    
    return null;
  }
  
  public void getStreamSegment(long position, int length, StreamSegment seg)
  {
    int numSegments = segmentLengths.length;
    for (int i = 0; i < numSegments; i++) {
      int len = segmentLengths[i];
      if (position < len) {
        seg.setStartPos(segmentPositions[i] + position);
        seg.setSegmentLength(Math.min(len - (int)position, length));
        return;
      }
      position -= len;
    }
    
    seg.setStartPos(-1L);
    seg.setSegmentLength(-1);
  }
}
