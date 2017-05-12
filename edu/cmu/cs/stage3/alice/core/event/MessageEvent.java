package edu.cmu.cs.stage3.alice.core.event;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Transformable;
import java.util.EventObject;




















public class MessageEvent
  extends EventObject
{
  private String m_message;
  private Transformable m_fromWho;
  private Transformable m_toWhom;
  private long m_when;
  
  public MessageEvent(Element source, String message, Transformable fromWho, Transformable toWhom, long when)
  {
    super(source);
    m_message = message;
    m_fromWho = fromWho;
    m_toWhom = toWhom;
    m_when = when;
  }
  
  public String getMessage() { return m_message; }
  
  public Transformable getFromWho() {
    return m_fromWho;
  }
  
  public Transformable getToWhom() { return m_toWhom; }
  
  public long getWhen() {
    return m_when;
  }
}
