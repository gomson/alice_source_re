package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;


















public class ObjectArrayPropertyUndoableRedoable
  implements ContextAssignableUndoableRedoable
{
  protected ObjectArrayProperty property;
  protected int changeType;
  protected int oldIndex;
  protected int newIndex;
  protected Object value;
  protected Object context;
  
  public ObjectArrayPropertyUndoableRedoable(ObjectArrayProperty property, int changeType, int oldIndex, int newIndex, Object value)
  {
    this.property = property;
    this.changeType = changeType;
    this.oldIndex = oldIndex;
    this.newIndex = newIndex;
    this.value = value;
  }
  
  public void setContext(Object context) {
    this.context = context;
  }
  
  public void undo() {
    if (changeType == 1) {
      property.remove(value);
    } else if (changeType == 3) {
      property.add(oldIndex, value);
    } else if (changeType == 2) {
      property.shift(newIndex, oldIndex);
    }
  }
  
  public void redo() {
    if (changeType == 1) {
      property.add(newIndex, value);
    } else if (changeType == 3) {
      property.remove(value);
    } else if (changeType == 2) {
      property.shift(oldIndex, newIndex);
    }
  }
  
  public Object getAffectedObject() {
    return property.getOwner();
  }
  
  public Object getContext() {
    return context;
  }
  
  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append("edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable[ ");
    s.append("property=" + property + "; ");
    s.append("changeType=" + changeType + "; ");
    s.append("oldIndex=" + oldIndex + "; ");
    s.append("newIndex=" + newIndex + "; ");
    s.append("value=" + value + "; ");
    if ((context != this) && (!(context instanceof DefaultUndoRedoStack))) {
      s.append("context=" + context + "; ");
    } else {
      s.append("context=" + context.getClass() + "; ");
    }
    s.append(" ]");
    return s.toString();
  }
  
  public String getLogString() {
    String logString = "TIME=<" + System.currentTimeMillis() + "> ";
    String valueString = "";
    
    if ((value instanceof Element)) {
      valueString = ((Element)value).getKey();
    }
    else if (value != null) { valueString = value.toString();
    }
    
    if (changeType == 1) {
      logString = logString + "EVENT=<objectArrayInsert> ELEMENT=<" + valueString + "> " + "OBJECTARRAYPROPERTY=<" + property.getName() + "> " + "PROPERTYOWNERTYPE=<" + property.getOwner().getClass().getName() + "> " + "PROPERTYOWNERNAME=<" + property.getOwner().getKey() + "> " + "NEWINDEX=<" + newIndex + ">";
    } else if (changeType == 3) {
      logString = logString + "EVENT=<objectArrayDelete> ELEMENT=<" + valueString + "> " + "OBJECTARRAYPROPERTY=<" + property.getName() + "> " + "PROPERTYOWNERTYPE=<" + property.getOwner().getClass().getName() + "> " + "PROPERTYOWNERNAME=<" + property.getOwner().getKey() + "> " + "OLDINDEX=<" + oldIndex + ">";
    } else if (changeType == 2) {
      logString = logString + "EVENT=<objectArrayShift> ELEMENT=<" + valueString + "> " + "OBJECTARRAYPROPERTY=<" + property.getName() + "> " + "PROPERTYOWNERTYPE=<" + property.getOwner().getClass().getName() + "> " + "PROPERTYOWNERNAME=<" + property.getOwner().getKey() + "> " + "OLDINDEX=<" + oldIndex + "> " + "NEWINDEX=<" + newIndex + ">";
    }
    
    return logString;
  }
}
