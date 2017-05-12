package edu.cmu.cs.stage3.pratt.maxkeyframing;






public abstract class Key
{
  protected double[] components;
  




  protected double time;
  




  public Key() {}
  




  public abstract Object createSample(double[] paramArrayOfDouble);
  




  public double[] getValueComponents()
  {
    return components;
  }
  
  public void setValueComponents(double[] components) {
    this.components = components;
  }
  
  public double getTime() {
    return time;
  }
  
  public void setTime(double time) {
    this.time = time;
  }
}
