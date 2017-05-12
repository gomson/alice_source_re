package edu.cmu.cs.stage3.image.codec;






















class SectorStreamSegmentMapper
  implements StreamSegmentMapper
{
  long[] segmentPositions;
  




















  int segmentLength;
  



















  int totalLength;
  



















  int lastSegmentLength;
  




















  public SectorStreamSegmentMapper(long[] segmentPositions, int segmentLength, int totalLength)
  {
    this.segmentPositions = ((long[])segmentPositions.clone());
    this.segmentLength = segmentLength;
    this.totalLength = totalLength;
    lastSegmentLength = 
      (totalLength - (segmentPositions.length - 1) * segmentLength);
  }
  
  public StreamSegment getStreamSegment(long position, int length) {
    int index = (int)(position / segmentLength);
    

    int len = index == segmentPositions.length - 1 ? 
      lastSegmentLength : segmentLength;
    

    position -= index * segmentLength;
    

    len = (int)(len - position);
    if (len > length) {
      len = length;
    }
    return new StreamSegment(segmentPositions[index] + position, len);
  }
  
  public void getStreamSegment(long position, int length, StreamSegment seg)
  {
    int index = (int)(position / segmentLength);
    

    int len = index == segmentPositions.length - 1 ? 
      lastSegmentLength : segmentLength;
    

    position -= index * segmentLength;
    

    len = (int)(len - position);
    if (len > length) {
      len = length;
    }
    
    seg.setStartPos(segmentPositions[index] + position);
    seg.setSegmentLength(len);
  }
}
