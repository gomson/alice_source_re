package edu.cmu.cs.stage3.image.codec;






















































public class StreamSegment
{
  private long startPos = 0L;
  private int segmentLength = 0;
  




  public StreamSegment() {}
  



  public StreamSegment(long startPos, int segmentLength)
  {
    this.startPos = startPos;
    this.segmentLength = segmentLength;
  }
  


  public final long getStartPos()
  {
    return startPos;
  }
  


  public final void setStartPos(long startPos)
  {
    this.startPos = startPos;
  }
  


  public final int getSegmentLength()
  {
    return segmentLength;
  }
  


  public final void setSegmentLength(int segmentLength)
  {
    this.segmentLength = segmentLength;
  }
}
