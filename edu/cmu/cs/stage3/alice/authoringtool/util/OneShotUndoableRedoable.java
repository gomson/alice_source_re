package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Scheduler;
























public class OneShotUndoableRedoable
  implements ContextAssignableUndoableRedoable
{
  protected Object context;
  protected Response redoResponse;
  protected Response undoResponse;
  protected OneShotSimpleBehavior oneShotBehavior;
  protected Scheduler scheduler;
  
  public OneShotUndoableRedoable(Response redoResponse, Response undoResponse, OneShotSimpleBehavior oneShotBehavior, Scheduler scheduler)
  {
    this.redoResponse = redoResponse;
    this.undoResponse = undoResponse;
    this.oneShotBehavior = oneShotBehavior;
    this.scheduler = scheduler;
  }
  


  public OneShotUndoableRedoable(Response redoResponse, Response undoResponse, Property[] affectedProperties, Scheduler scheduler)
  {
    this.redoResponse = redoResponse;
    this.undoResponse = undoResponse;
    oneShotBehavior = new OneShotSimpleBehavior();
    oneShotBehavior.setAffectedProperties(affectedProperties);
    this.scheduler = scheduler;
  }
  
  public void setContext(Object context) {
    this.context = context;
  }
  
  public void undo() {
    oneShotBehavior.stopRunningResponse(AuthoringToolResources.getCurrentTime());
    oneShotBehavior.setResponse(undoResponse);
    oneShotBehavior.start(scheduler);
  }
  
  public void redo() {
    oneShotBehavior.stopRunningResponse(AuthoringToolResources.getCurrentTime());
    oneShotBehavior.setResponse(redoResponse);
    oneShotBehavior.start(scheduler);
  }
  
  public Object getAffectedObject() {
    return this;
  }
  
  public Object getContext() {
    return context;
  }
  
  public Response getRedoResponse() {
    return redoResponse;
  }
  
  public Response getUndoResponse() {
    return undoResponse;
  }
  
  public OneShotSimpleBehavior getOneShotBehavior() {
    return oneShotBehavior;
  }
  
  public String getLogString()
  {
    String logString = "TIME=<" + System.currentTimeMillis() + "> ";
    
    String responseType = AuthoringToolResources.getReprForValue(redoResponse.getClass());
    
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
    
    String[] propNames = AuthoringToolResources.getInitialVisibleProperties(redoResponse.getClass());
    String propValues = "";
    for (int i = 0; i < propNames.length; i++) {
      Property prop = redoResponse.getPropertyNamed(propNames[i]);
      propValues = propValues + propNames[i].toUpperCase() + "=<" + AuthoringToolResources.getReprForValue(prop.get()) + "> ";
    }
    
    logString = logString + "EVENT=<oneshot>  RESPONSE=" + responseType + " " + propValues;
    
    return logString;
  }
}
