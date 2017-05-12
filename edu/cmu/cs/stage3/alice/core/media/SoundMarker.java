package edu.cmu.cs.stage3.alice.core.media;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import java.util.Vector;




















public class SoundMarker
  extends Element
{
  public final NumberProperty time = new NumberProperty(this, "time", new Double(0.0D));
  
  protected Vector soundMarkerListeners = new Vector();
  
  public SoundMarker() {}
  
  public SoundMarker(String n, double t)
  {
    if (n != null)
      name.set(n);
    time.set(new Double(t));
  }
  
  public SoundMarker(double t) {
    time.set(new Double(t));
  }
  
  public void setTime(double t) {
    time.set(new Double(t));
  }
  
  public double getTime() {
    return time.doubleValue();
  }
  
  public void addSoundMarkerListener(SoundMarkerListener sml) {
    if (!soundMarkerListeners.contains(sml))
    {

      soundMarkerListeners.addElement(sml);
    }
  }
  
  public void removeSoundMarkerListener(SoundMarkerListener sml) {
    soundMarkerListeners.removeElement(sml);
  }
}
