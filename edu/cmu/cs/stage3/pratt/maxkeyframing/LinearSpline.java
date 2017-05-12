package edu.cmu.cs.stage3.pratt.maxkeyframing;












public class LinearSpline
  extends Spline
{
  public LinearSpline() {}
  










  public boolean addKey(SimpleKey key)
  {
    return super.addKey(key);
  }
  
  public boolean removeKey(SimpleKey key) {
    return super.removeKey(key);
  }
  
  public Object getSample(double t)
  {
    if (t <= 0.0D) {
      Key key = getFirstKey();
      if (key != null) {
        return key.createSample(key.getValueComponents());
      }
    } else if (t >= getDuration()) {
      Key key = getLastKey();
      if (key != null) {
        return key.createSample(key.getValueComponents());
      }
    } else {
      Key[] boundingKeys = getBoundingKeys(t);
      if (boundingKeys != null) {
        double timeSpan = boundingKeys[1].getTime() - boundingKeys[0].getTime();
        double portion = (t - boundingKeys[0].getTime()) / timeSpan;
        
        double[] prevComponents = boundingKeys[0].getValueComponents();
        double[] nextComponents = boundingKeys[1].getValueComponents();
        
        double[] components = new double[prevComponents.length];
        for (int j = 0; j < prevComponents.length; j++) {
          prevComponents[j] += (nextComponents[j] - prevComponents[j]) * portion;
        }
        
        return boundingKeys[0].createSample(components);
      }
    }
    
    return null;
  }
}
