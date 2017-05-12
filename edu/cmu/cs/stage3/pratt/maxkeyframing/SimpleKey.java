package edu.cmu.cs.stage3.pratt.maxkeyframing;
























public abstract class SimpleKey
  extends Key
{
  protected SimpleKey(double time, double[] components)
  {
    setTime(time);
    setValueComponents(components);
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
    for (int i = 0; i < numComponents - 1; i++) {
      repr.append(components[i]);
      repr.append(",");
    }
    repr.append(components[(numComponents - 1)]);
    repr.append("]");
    
    return repr.toString();
  }
}
