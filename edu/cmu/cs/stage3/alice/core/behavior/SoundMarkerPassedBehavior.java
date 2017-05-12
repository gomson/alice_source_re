package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.media.SoundMarker;
import edu.cmu.cs.stage3.alice.core.media.SoundMarkerListener;
import edu.cmu.cs.stage3.alice.core.property.ElementProperty;


















public class SoundMarkerPassedBehavior
  extends TriggerBehavior
  implements SoundMarkerListener
{
  public SoundMarkerPassedBehavior() {}
  
  public final ElementProperty marker = new ElementProperty(this, "marker", null, SoundMarker.class);
  protected SoundMarker m_marker = null;
  
  public void markerPassed(SoundMarker m) {
    if (m == m_marker)
      trigger(System.currentTimeMillis() * 0.001D);
  }
  
  protected void started(World world, double time) {
    super.started(world, time);
    m_marker = ((SoundMarker)marker.getValue());
    if (m_marker != null) {
      m_marker.addSoundMarkerListener(this);
    }
  }
  
  protected void stopped(World world, double time) {
    super.stopped(world, time);
    if (m_marker != null) {
      m_marker.removeSoundMarkerListener(this);
    }
  }
}
