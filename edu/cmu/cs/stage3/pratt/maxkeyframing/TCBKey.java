package edu.cmu.cs.stage3.pratt.maxkeyframing;







public abstract class TCBKey
  extends Key
{
  private double tension;
  





  private double continuity;
  





  private double bias;
  





  protected TCBKey(double time, double[] components, double tension, double continuity, double bias)
  {
    setTime(time);
    setValueComponents(components);
    this.tension = tension;
    this.continuity = continuity;
    this.bias = bias;
  }
  
  public double getTension() {
    return tension;
  }
  
  public double getContinuity() {
    return continuity;
  }
  
  public double getBias() {
    return bias;
  }
  
  public String toString()
  {
    String className = getClass().getName();
    double[] components = getValueComponents();
    int numComponents = components.length;
    
    StringBuffer repr = new StringBuffer();
    repr.append(className);
    repr.append("[");
    repr.append(getTime());
    repr.append(",");
    for (int i = 0; i < numComponents; i++) {
      repr.append(components[i]);
      repr.append(",");
    }
    repr.append(tension);
    repr.append(",");
    repr.append(continuity);
    repr.append(",");
    repr.append(bias);
    repr.append("]");
    
    return repr.toString();
  }
}
