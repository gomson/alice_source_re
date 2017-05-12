package edu.cmu.cs.stage3.media.jmfmedia;

import javax.media.Time;

public class Player extends edu.cmu.cs.stage3.media.AbstractPlayer {
  private javax.media.Player m_jmfPlayer;
  private Time m_pendingMediaTime;
  private Float m_pendingVolumeLevel;
  private Float m_pendingRate;
  
  public Player(DataSource dataSource) { super(dataSource);
    try {
      FixedJavaSoundRenderer.usurpControlFromJavaSoundRenderer();
      m_jmfPlayer = javax.media.Manager.createPlayer(dataSource.getJMFDataSource());
      m_jmfPlayer.addControllerListener(new javax.media.ControllerListener() {
        public void controllerUpdate(javax.media.ControllerEvent e) {
          if ((e instanceof javax.media.TransitionEvent)) {
            javax.media.TransitionEvent te = (javax.media.TransitionEvent)e;
            switch (te.getCurrentState()) {
            case 300: 
              if (m_pendingMediaTime != null) {
                m_jmfPlayer.setMediaTime(m_pendingMediaTime);
                m_pendingMediaTime = null;
              }
              if (m_pendingVolumeLevel != null) {
                Player.this.updateVolumeLevel(m_pendingVolumeLevel.floatValue());
                m_pendingVolumeLevel = null;
              }
              if (m_pendingRate != null) {
                Player.this.updateRate(m_pendingRate.floatValue());
                m_pendingRate = null;
              }
              break;
            }
            fireStateChanged();
          }
          if ((e instanceof javax.media.EndOfMediaEvent)) {
            fireEndReached();
          }
          if ((e instanceof javax.media.DurationUpdateEvent)) {
            fireDurationUpdated();
          }
        }
      });
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
  
  public int getState() {
    return m_jmfPlayer.getState();
  }
  
  public double getDuration() {
    Time t = m_jmfPlayer.getDuration();
    if (t != null) {
      long nsec = t.getNanoseconds();
      if (nsec == 9223372036854775806L)
        return NaN.0D;
      if (nsec == 9223372036854775806L) {
        return Double.POSITIVE_INFINITY;
      }
      return nsec * 1.0E-9D;
    }
    
    return NaN.0D;
  }
  
  public double getCurrentTime() { Time t;
    Time t;
    if (m_pendingMediaTime != null) {
      t = m_pendingMediaTime;
    } else {
      t = m_jmfPlayer.getMediaTime();
    }
    if (t != null) {
      long nsec = t.getNanoseconds();
      if (nsec == 9223372036854775806L)
        return NaN.0D;
      if (nsec == 9223372036854775806L) {
        return Double.POSITIVE_INFINITY;
      }
      return nsec * 1.0E-9D;
    }
    
    return NaN.0D;
  }
  
  private boolean isAtLeastRealized() {
    int state = getState();
    return (state != 100) && (state != 200);
  }
  
  public void setCurrentTime(double currentTime) { Time t = new Time(currentTime);
    if (isAtLeastRealized()) {
      m_jmfPlayer.setMediaTime(t);
    } else {
      m_pendingMediaTime = t;
    }
  }
  
  private void updateVolumeLevel(float volumeLevel) {
    javax.media.GainControl gainConrol = m_jmfPlayer.getGainControl();
    if (gainConrol != null) {
      gainConrol.setLevel(volumeLevel);
    }
  }
  
  public void setVolumeLevel(float volumeLevel)
  {
    if (isAtLeastRealized()) {
      updateVolumeLevel(volumeLevel);
    } else {
      m_pendingVolumeLevel = new Float(volumeLevel);
    }
  }
  
  private void updateRate(float rate) {
    float actualRate = m_jmfPlayer.setRate(rate);
  }
  


  public void setRate(float rate)
  {
    if (isAtLeastRealized()) {
      updateRate(rate);
    } else {
      m_pendingRate = new Float(rate);
    }
  }
  
  public void realize() {
    try {
      m_jmfPlayer.realize();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
  
  public void prefetch() {
    try {
      m_jmfPlayer.prefetch();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
  
  public void start() {
    try {
      m_jmfPlayer.start();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
  
  public void stop() {
    try {
      if (m_jmfPlayer.getState() == 600) {
        m_jmfPlayer.stop();
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
