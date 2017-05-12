package edu.cmu.cs.stage3.pratt.maxkeyframing;









public abstract class BezierKey
  extends Key
{
  private double[] incomingControlComponents;
  







  private double[] outgoingControlComponents;
  







  protected BezierKey(double time, double[] components, double[] incomingControlComponents, double[] outgoingControlComponents)
  {
    setTime(time);
    setValueComponents(components);
    this.incomingControlComponents = incomingControlComponents;
    this.outgoingControlComponents = outgoingControlComponents;
  }
  
  public double[] getIncomingControlComponents() {
    return incomingControlComponents;
  }
  
  public double[] getOutgoingControlComponents() {
    return outgoingControlComponents;
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
    for (int i = 0; i < numComponents; i++) {
      repr.append(incomingControlComponents[i]);
      repr.append(",");
    }
    for (int i = 0; i < numComponents - 1; i++) {
      repr.append(outgoingControlComponents[i]);
      repr.append(",");
    }
    repr.append(outgoingControlComponents[(numComponents - 1)]);
    repr.append("]");
    
    return repr.toString();
  }
}
