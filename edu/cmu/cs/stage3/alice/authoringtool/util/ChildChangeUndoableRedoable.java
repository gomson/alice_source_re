package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.UserDefinedResponseProperty;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;










public class ChildChangeUndoableRedoable
  implements ContextAssignableUndoableRedoable
{
  protected Element parent;
  protected Element child;
  protected int changeType;
  protected int oldIndex;
  protected int newIndex;
  protected Object context;
  
  public ChildChangeUndoableRedoable(ChildrenEvent childrenEvent)
  {
    parent = ((Element)childrenEvent.getSource());
    child = childrenEvent.getChild();
    changeType = childrenEvent.getChangeType();
    oldIndex = childrenEvent.getOldIndex();
    newIndex = childrenEvent.getNewIndex();
  }
  
  /**
   * @deprecated
   */
  public ChildChangeUndoableRedoable(ChildrenEvent childrenEvent, Object context) {
    parent = ((Element)childrenEvent.getSource());
    child = childrenEvent.getChild();
    changeType = childrenEvent.getChangeType();
    oldIndex = childrenEvent.getOldIndex();
    newIndex = childrenEvent.getNewIndex();
  }
  
  public void setContext(Object context) {
    this.context = context;
  }
  
  public void undo() {
    if (changeType == 1) {
      child.removeFromParent();
    } else if (changeType == 3) {
      parent.insertChildAt(child, oldIndex);
    } else if (changeType == 2) {
      parent.insertChildAt(child, oldIndex);
    }
  }
  
  public void redo() {
    if (changeType == 1) {
      parent.insertChildAt(child, newIndex);
    } else if (changeType == 3) {
      parent.removeChild(child);
    } else if (changeType == 2) {
      parent.insertChildAt(child, newIndex);
    }
  }
  
  public Object getAffectedObject() {
    return child;
  }
  
  public Object getContext() {
    return context;
  }
  
  public String getLogString() {
    String logString = "TIME=<" + System.currentTimeMillis() + "> ";
    
    if ((child instanceof Response))
    {

      if ((child instanceof CallToUserDefinedResponse)) {
        String responseType = child.getClass().getName();
        UserDefinedResponse udResponse = (UserDefinedResponse)child).userDefinedResponse.get();
        String methodName = "methodCopied";
        if (udResponse != null) {
          methodName = udResponse.getKey();
        }
        
        ElementArrayProperty eaProp = child).requiredActualParameters;
        Element[] elems = eaProp.getElementArrayValue();
        
        String params = "";
        
        for (int i = 0; i < elems.length; i++) {
          if ((elems[i] instanceof Variable)) {
            Variable var = (Variable)elems[i];
            params = params + var.getKey(child).toUpperCase() + "=<" + var.getValue() + "> ";
          }
        }
        

        if (changeType == 1) {
          logString = logString + "EVENT=<insertCallToUserResponse> RESPONSETYPE=<" + responseType + "> " + "RESPONSENAME=<" + methodName + "> " + params + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "NEWINDEX=<" + newIndex + ">";
        } else if (changeType == 3) {
          logString = logString + "EVENT=<deleteCallToUserResponse> RESPONSETYPE=<" + responseType + "> " + "RESPONSENAME=<" + methodName + "> " + params + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + ">";
        } else if (changeType == 2) {
          logString = logString + "EVENT=<shiftCallToUserResponse> RESPONSETYPE=<" + responseType + "> " + "RESPONSENAME=<" + methodName + "> " + params + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + "> " + "NEWINDEX=<" + newIndex + ">";
        }
      }
      else
      {
        String responseType = AuthoringToolResources.getReprForValue(child.getClass());
        
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
        responseType = "<" + responseType.trim() + ">";
        
        Response resp = (Response)child;
        
        String[] propNames = AuthoringToolResources.getInitialVisibleProperties(child.getClass());
        String propValues = "";
        for (int i = 0; i < propNames.length; i++) {
          Property prop = child.getPropertyNamed(propNames[i]);
          propValues = propValues + propNames[i].toUpperCase() + "=<" + AuthoringToolResources.getReprForValue(prop.get()) + "> ";
        }
        
        if (changeType == 1) {
          logString = logString + "EVENT=<insertResponse> RESPONSETYPE=<" + child.getClass().getName() + "> " + "RESPONSENAME=<" + responseType + "> " + propValues + " " + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "NEWINDEX=<" + newIndex + ">";
        } else if (changeType == 3) {
          logString = logString + "EVENT=<deleteResponse> RESPONSETYPE=<" + child.getClass().getName() + "> " + "RESPONSENAME=<" + responseType + "> " + propValues + " " + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + ">";
        } else if (changeType == 2) {
          logString = logString + "EVENT=<shiftResponse> RESPONSETYPE=<" + child.getClass().getName() + "> " + "RESPONSENAME=<" + responseType + "> " + propValues + " " + "PARENTCLASS=<" + parent.getClass().getName() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + "> " + "NEWINDEX=<" + newIndex + ">";
        }
        
      }
      
    }
    else if (changeType == 1) {
      logString = logString + "EVENT=<insertChild> CHILDTYPE=<" + child.getClass().getName() + "> " + "CHILDKEY=<" + child.getKey() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "NEWINDEX=<" + newIndex + ">";
    } else if (changeType == 3) {
      logString = logString + "EVENT=<deleteChild> CHILDTYPE=<" + child.getClass().getName() + "> " + "CHILDKEY=<" + child.getKey() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + ">";
    } else if (changeType == 2) {
      logString = logString + "EVENT=<shiftChild> CHILDTYPE=<" + child.getClass().getName() + "> " + "CHILDKEY=<" + child.getKey() + "> " + "PARENTKEY=<" + parent.getKey() + "> " + "OLDINDEX=<" + oldIndex + "> " + "NEWINDEX=<" + newIndex + ">";
    }
    

    return logString;
  }
}
