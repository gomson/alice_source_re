package edu.cmu.cs.stage3.alice.core.event;

import edu.cmu.cs.stage3.alice.core.Element;
import java.util.EventObject;



















public class ChildrenEvent
  extends EventObject
{
  public static final int CHILD_INSERTED = 1;
  public static final int CHILD_SHIFTED = 2;
  public static final int CHILD_REMOVED = 3;
  protected Element m_child;
  protected int m_changeType;
  protected int m_oldIndex;
  protected int m_newIndex;
  
  public ChildrenEvent(Element source, Element child, int changeType, int oldIndex, int newIndex)
  {
    super(source);
    m_child = child;
    m_changeType = changeType;
    m_oldIndex = oldIndex;
    m_newIndex = newIndex;
  }
  
  public Element getParent() { return (Element)getSource(); }
  
  public Element getChild() {
    return m_child;
  }
  
  public int getChangeType() { return m_changeType; }
  
  public int getOldIndex() {
    return m_oldIndex;
  }
  
  public int getNewIndex() { return m_newIndex; }
}
