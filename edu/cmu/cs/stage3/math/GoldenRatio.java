package edu.cmu.cs.stage3.math;










public class GoldenRatio
{
  public GoldenRatio() {}
  








  public static double PHI = 1.6180339887D;
  
  public static int getShorterSideLength(int longerSideLength) { return (int)(longerSideLength / PHI); }
  
  public static int getLongerSideLength(int shorterSideLength) {
    return (int)(shorterSideLength * PHI);
  }
}
