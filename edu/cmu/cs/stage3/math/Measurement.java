package edu.cmu.cs.stage3.math;








public class Measurement
  extends Number
{
  private double m_value;
  





  private double m_factor;
  






  public Measurement(double value, double factor)
  {
    m_value = value;
    m_factor = factor;
  }
  
  public byte byteValue() {
    return (byte)(int)doubleValue();
  }
  
  public double doubleValue() {
    return m_value * m_factor;
  }
  
  public float floatValue() {
    return (float)doubleValue();
  }
  
  public int intValue() {
    return (int)doubleValue();
  }
  
  public long longValue() {
    return doubleValue();
  }
  
  public short shortValue() {
    return (short)(int)doubleValue();
  }
}
