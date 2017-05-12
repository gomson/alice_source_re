package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.Sound;
import edu.cmu.cs.stage3.alice.core.event.ExpressionEvent;
import edu.cmu.cs.stage3.alice.core.event.ExpressionListener;
import edu.cmu.cs.stage3.alice.core.event.SoundEvent;
import edu.cmu.cs.stage3.alice.core.event.SoundListener;
import edu.cmu.cs.stage3.alice.core.media.SoundMarker;
import edu.cmu.cs.stage3.alice.core.property.DataSourceProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.SoundProperty;
import edu.cmu.cs.stage3.media.DataSource;
import edu.cmu.cs.stage3.media.Player;
import java.util.Vector;









public class SoundResponse
  extends Response
{
  public final ReferenceFrameProperty subject = new ReferenceFrameProperty(this, "subject", null);
  public final SoundProperty sound = new SoundProperty(this, "sound", null);
  public final ElementProperty fromMarker = new ElementProperty(this, "fromMarker", null, SoundMarker.class);
  public final ElementProperty toMarker = new ElementProperty(this, "toMarker", null, SoundMarker.class);
  public final NumberProperty volumeLevel = new NumberProperty(this, "volumeLevel", new Double(1.0D));
  public final NumberProperty rate = new NumberProperty(this, "rate", new Double(1.0D));
  public final NumberProperty pan = new NumberProperty(this, "pan", new Double(0.0D));
  private Vector soundListeners = new Vector();
  private SoundListener[] soundListenerArray = null;
  
  public SoundResponse() {}
  
  protected Number getDefaultDuration() { return null; }
  
  public void addSoundListener(SoundListener soundListener) {
    if (!soundListeners.contains(soundListener))
    {

      soundListeners.addElement(soundListener);
      soundListenerArray = null;
    }
  }
  
  public void removeSoundListener(SoundListener soundListener) { soundListeners.removeElement(soundListener);
    soundListenerArray = null;
  }
  
  public SoundListener[] getSoundListeners() { if (soundListenerArray == null) {
      soundListenerArray = new SoundListener[soundListeners.size()];
      soundListeners.copyInto(soundListenerArray);
    }
    return soundListenerArray;
  }
  
  private void fireSoundListeners(double time, double duration, DataSource ds) {
    SoundEvent e = new SoundEvent(this, new Double(time), ds, new Double(duration));
    for (int i = 0; i < soundListeners.size(); i++) {
      SoundListener l = (SoundListener)soundListeners.elementAt(i);
      l.SoundStarted(e);
    }
  }
  
  public class RuntimeSoundResponse extends Response.RuntimeResponse { public RuntimeSoundResponse() { super(); }
    private Player m_player = null;
    private Expression m_volumeLevelExpression = null;
    private Expression m_rateExpression = null;
    private Expression m_panExpression = null;
    private ExpressionListener m_volumeLevelExpressionListener = new ExpressionListener() {
      public void expressionChanged(ExpressionEvent e) {
        SoundResponse.RuntimeSoundResponse.this.updateVolumeLevel(e.getExpression());
      }
    };
    private ExpressionListener m_rateExpressionListener = new ExpressionListener() {
      public void expressionChanged(ExpressionEvent e) {
        SoundResponse.RuntimeSoundResponse.this.updateRate(e.getExpression());
      }
    };
    private double m_prevMediaTime;
    private double m_timeout;
    
    private void updateVolumeLevel(Number n)
    {
      if (m_player != null)
        m_player.setVolumeLevel(n.floatValue());
    }
    
    private void updateVolumeLevel(Expression expression) {
      if (expression != null) {
        Object value = expression.getValue();
        if ((value instanceof Number)) {
          updateVolumeLevel((Number)value);
        }
      }
    }
    



    private void updateRate(Number n)
    {
      if (m_player != null)
        m_player.setRate(n.floatValue());
    }
    
    private void updateRate(Expression expression) {
      if (expression != null) {
        Object value = expression.getValue();
        if ((value instanceof Number)) {
          updateRate((Number)value);
        }
      }
    }
    




    public void prologue(double t)
    {
      super.prologue(t);
      Sound soundValue = sound.getSoundValue();
      if (soundValue != null) {
        DataSource dataSourceValue = dataSource.getDataSourceValue();
        if (dataSourceValue != null) {
          m_player = dataSourceValue.acquirePlayer();
          


          SoundResponse.this.fireSoundListeners(t, m_player.getDuration(), dataSourceValue);
          AuthoringTool.pauseSound(m_player);
        }
      }
      



      m_volumeLevelExpression = null;
      Object o = volumeLevel.get();
      if ((o instanceof Expression)) {
        m_volumeLevelExpression = ((Expression)o);
        m_volumeLevelExpression.addExpressionListener(m_volumeLevelExpressionListener);
        updateVolumeLevel(m_volumeLevelExpression);
      } else {
        updateVolumeLevel((Number)o);
      }
      
      m_rateExpression = null;
      o = rate.get();
      if ((o instanceof Expression)) {
        m_rateExpression = ((Expression)o);
        m_rateExpression.addExpressionListener(m_rateExpressionListener);
        updateRate(m_rateExpression);
      } else {
        updateRate((Number)o);
      }
      m_prevMediaTime = 0.0D;
      m_timeout = NaN.0D;
    }
    
    public void epilogue(double t)
    {
      if (m_volumeLevelExpression != null) {
        m_volumeLevelExpression.removeExpressionListener(m_volumeLevelExpressionListener);
      }
      if (m_rateExpression != null) {
        m_rateExpression.removeExpressionListener(m_rateExpressionListener);
      }
      Sound soundValue = sound.getSoundValue();
      if (m_player != null) {
        m_player.stop();
        m_player.setIsAvailable(true);
        m_player = null;
      }
      super.epilogue(t);
    }
    
    public double getTimeRemaining(double t)
    {
      if (m_player != null) {
        double currMediaTime = m_player.getCurrentTime();
        double duration = SoundResponse.this.duration.doubleValue();
        double endMediaTime;
        if (Double.isNaN(duration)) {
          double endMediaTime = m_player.getEndTime();
          if (Double.isNaN(endMediaTime)) {
            endMediaTime = m_player.getDuration();
          }
        } else {
          endMediaTime = duration;
        }
        double mediaTimeRemaining = endMediaTime - currMediaTime;
        double elapsedTime = getTimeElapsed(t);
        if (m_prevMediaTime != currMediaTime) {
          m_timeout = (elapsedTime + mediaTimeRemaining);
          m_prevMediaTime = currMediaTime;
        }
        if (!Double.isNaN(m_timeout))
        {

          if (elapsedTime > m_timeout) {
            mediaTimeRemaining = 0.0D;
          }
        }
        return mediaTimeRemaining;
      }
      return 0.0D;
    }
    

    public double getDuration()
    {
      double dur = super.getDuration();
      if (dur == 0.0D) {
        return NaN.0D;
      }
      return dur;
    }
  }
}
