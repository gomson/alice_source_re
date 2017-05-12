package edu.cmu.cs.stage3.alice.scenegraph.event;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import java.util.EventObject;
















public class ChildrenEvent
  extends EventObject
{
  public static final int CHILD_ADDED = 1;
  public static final int CHILD_REMOVED = 2;
  private Component m_child;
  private int m_id;
  
  public ChildrenEvent(Container source, int id, Component child)
  {
    super(source);
    m_id = id;
    m_child = child;
  }
  
  public int getID() { return m_id; }
  

  public Component getChild() { return m_child; }
  
  private String getIDText() {
    switch (m_id) {
    case 1: 
      return "CHILD_ADDED";
    case 2: 
      return "CHILD_REMOVED";
    }
    return "UNKNOWN";
  }
  
  public String toString() {
    return getClass().getName() + "[source=" + source + ",id=" + getIDText() + ",child=" + m_child + "]";
  }
}
