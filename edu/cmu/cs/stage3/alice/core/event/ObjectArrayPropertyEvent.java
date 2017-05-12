package edu.cmu.cs.stage3.alice.core.event;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import java.util.EventObject;


















public class ObjectArrayPropertyEvent
  extends EventObject
{
  public static final int ITEM_INSERTED = 1;
  public static final int ITEM_SHIFTED = 2;
  public static final int ITEM_REMOVED = 3;
  protected Object m_item;
  protected int m_changeType;
  protected int m_oldIndex;
  protected int m_newIndex;
  
  public ObjectArrayPropertyEvent(ObjectArrayProperty source, Object item, int changeType, int oldIndex, int newIndex)
  {
    super(source);
    m_item = item;
    m_changeType = changeType;
    m_oldIndex = oldIndex;
    m_newIndex = newIndex; }
  /**
   * @deprecated
   */
  public Property getProperty() { return (Property)getSource(); }
  
  public ObjectArrayProperty getObjectArrayProperty() {
    return (ObjectArrayProperty)getSource();
  }
  
  public Object getItem() { return m_item; }
  
  public int getChangeType() {
    return m_changeType;
  }
  
  public int getOldIndex() { return m_oldIndex; }
  
  public int getNewIndex() {
    return m_newIndex;
  }
}
