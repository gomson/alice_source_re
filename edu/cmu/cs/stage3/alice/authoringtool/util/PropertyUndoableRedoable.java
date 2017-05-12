package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.property.UserDefinedResponseProperty;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;













public class PropertyUndoableRedoable
  implements ContextAssignableUndoableRedoable
{
  protected Property property;
  protected Object oldValue;
  protected Object newValue;
  protected Object context;
  
  /**
   * @deprecated
   */
  public PropertyUndoableRedoable(Property property, Object oldValue, Object newValue, Object context)
  {
    this.property = property;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }
  
  public PropertyUndoableRedoable(Property property, Object oldValue, Object newValue) {
    this.property = property;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }
  
  public void setContext(Object context) {
    this.context = context;
  }
  
  public void undo() {
    property.set(oldValue);
  }
  
  public void redo() {
    property.set(newValue);
  }
  
  public Object getAffectedObject() {
    return property.getOwner();
  }
  
  public Object getContext() {
    return context;
  }
  
  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append("edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable[ ");
    s.append("property=" + property + "; ");
    s.append("oldValue=" + oldValue + "; ");
    s.append("newValue=" + newValue + "; ");
    if ((context != this) && (!(context instanceof DefaultUndoRedoStack))) {
      s.append("context=" + context + "; ");
    } else {
      s.append("context=" + context.getClass() + "; ");
    }
    s.append(" ]");
    return s.toString();
  }
  
  public String getLogString()
  {
    String logString = "TIME=<" + System.currentTimeMillis() + "> ";
    String oldValueString = "null";
    String newValueString = "null";
    String ownerKey = "";
    
    if ((oldValue instanceof Element)) {
      oldValueString = ((Element)oldValue).getKey();
    }
    else if (oldValue != null) { oldValueString = oldValue.toString();
    }
    
    if ((newValue instanceof Element)) {
      newValueString = ((Element)newValue).getKey();
    }
    else if (newValue != null) { newValueString = newValue.toString();
    }
    
    if ((property.getOwner() instanceof CallToUserDefinedResponse)) {
      CallToUserDefinedResponse cudResponse = (CallToUserDefinedResponse)property.getOwner();
      String methodName = "";
      if ((userDefinedResponse.get() instanceof UserDefinedResponse)) {
        UserDefinedResponse udResponse = (UserDefinedResponse)userDefinedResponse.get();
        methodName = udResponse.getKey();
      }
      
      ownerKey = methodName + "(" + property.getOwner().getKey() + ")";
    } else if ((property.getOwner() instanceof Response)) {
      String responseType = AuthoringToolResources.getReprForValue(property.getOwner().getClass());
      
      if (responseType.indexOf("set") != -1) {
        responseType = "set";
      } else {
        if ((responseType.indexOf("<<<") != -1) && (responseType.indexOf(">>>") != -1)) {
          responseType = responseType.substring(responseType.indexOf(">>>") + 3, responseType.length());
        }
        if ((responseType.indexOf("<") != -1) && (responseType.indexOf(">") != -1)) {
          responseType = responseType.substring(0, responseType.indexOf("<"));
        }
      }
      ownerKey = responseType.trim() + "(" + property.getOwner().getKey() + ")";
    } else {
      ownerKey = property.getOwner().getKey();
    }
    
    if (!property.getName().equals("data")) {
      logString = logString + "EVENT=<propertyChange> PROPERTYNAME=<" + property.getName() + "> " + "PROPERTYOWNER=<" + ownerKey + "> " + "OLDVALUE=<" + oldValueString + "> " + "NEWVALUE=<" + newValueString + ">";
      return logString; }
    return null;
  }
}
