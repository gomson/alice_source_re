package edu.cmu.cs.stage3.alice.core.event;

import edu.cmu.cs.stage3.alice.core.response.SoundResponse;
import java.util.EventObject;


















public class SoundEvent
  extends EventObject
{
  private Object time;
  private Object dataSource;
  private Object duration;
  
  public SoundEvent(SoundResponse source, Object value, Object ds, Object dur)
  {
    super(source);
    time = value;
    dataSource = ds;
    duration = dur;
  }
  
  public SoundResponse getSoundResponse() {
    return (SoundResponse)getSource();
  }
  
  public Object getTime() {
    return time;
  }
  
  public Object getDuration() {
    return duration;
  }
  
  public Object getDataSource() {
    return dataSource;
  }
}
