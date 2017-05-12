package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.UserDefinedResponseProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.util.HashMap;








public class CallToUserDefinedResponsePrototype
  extends ResponsePrototype
{
  protected UserDefinedResponse actualResponse;
  protected RefreshListener refreshListener = new RefreshListener();
  
  public CallToUserDefinedResponsePrototype(UserDefinedResponse actualResponse) {
    super(CallToUserDefinedResponse.class, new StringObjectPair[0], new String[0]);
    this.actualResponse = actualResponse;
  }
  
  protected CallToUserDefinedResponsePrototype(UserDefinedResponse actualResponse, StringObjectPair[] knownPropertyValues, String[] desiredProperties) {
    super(CallToUserDefinedResponse.class, knownPropertyValues, desiredProperties);
    this.actualResponse = actualResponse;
  }
  
  public void startListening() {
    if (actualResponse != null) {
      actualResponse.requiredFormalParameters.addObjectArrayPropertyListener(refreshListener);
      Object[] vars = actualResponse.requiredFormalParameters.getArrayValue();
      desiredProperties = new String[vars.length];
      for (int i = 0; i < vars.length; i++) {
        name.addPropertyListener(refreshListener);
        desiredProperties[i] = name.getStringValue();
      }
    }
  }
  
  public void stopListening() {
    if (actualResponse != null) {
      actualResponse.requiredFormalParameters.removeObjectArrayPropertyListener(refreshListener);
      Object[] vars = actualResponse.requiredFormalParameters.getArrayValue();
      desiredProperties = new String[vars.length];
      for (int i = 0; i < vars.length; i++) {
        name.removePropertyListener(refreshListener);
        desiredProperties[i] = name.getStringValue();
      }
    }
  }
  
  public Element createNewElement() {
    HashMap knownMap = new HashMap();
    for (int i = 0; i < knownPropertyValues.length; i++) {
      knownMap.put(knownPropertyValues[i].getString(), knownPropertyValues[i].getObject());
    }
    
    CallToUserDefinedResponse callToUserDefinedResponse = new CallToUserDefinedResponse();
    userDefinedResponse.set(actualResponse);
    Object[] params = actualResponse.requiredFormalParameters.getArrayValue();
    for (int i = 0; i < params.length; i++) {
      Variable formalParameter = (Variable)params[i];
      Variable actualParameter = new Variable();
      name.set(name.get());
      valueClass.set(valueClass.get());
      if (!knownMap.containsKey(name.get())) {
        value.set(AuthoringToolResources.getDefaultValueForClass((Class)valueClass.get()));
      } else {
        value.set(knownMap.get(name.get()));
      }
      callToUserDefinedResponse.addChild(actualParameter);
      requiredActualParameters.add(actualParameter);
    }
    
    return callToUserDefinedResponse;
  }
  
  public ElementPrototype createCopy(StringObjectPair[] newKnownPropertyValues) {
    return super.createCopy(newKnownPropertyValues);
  }
  
  public UserDefinedResponse getActualResponse() {
    return actualResponse;
  }
  
  public String[] getDesiredProperties() {
    return desiredProperties;
  }
  
  public void calculateDesiredProperties() {
    Object[] params = actualResponse.requiredFormalParameters.getArrayValue();
    desiredProperties = new String[params.length];
    for (int i = 0; i < params.length; i++) {
      desiredProperties[i] = name.getStringValue();
    }
  }
  



  protected ElementPrototype createInstance(Class elementClass, StringObjectPair[] knownPropertyValues, String[] desiredProperties) { return new CallToUserDefinedResponsePrototype(actualResponse, knownPropertyValues, desiredProperties); }
  
  class RefreshListener implements ObjectArrayPropertyListener, PropertyListener {
    RefreshListener() {}
    
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) { try { if (ev.getChangeType() == 1) {
          Variable variable = (Variable)ev.getItem();
          name.addPropertyListener(this);
        } else if (ev.getChangeType() == 3) {
          Variable variable = (Variable)ev.getItem();
          name.removePropertyListener(this);
        }
      }
      catch (Throwable localThrowable) {}
      
      calculateDesiredProperties(); }
    
    public void propertyChanging(PropertyEvent ev) {}
    
    public void propertyChanged(PropertyEvent ev) { calculateDesiredProperties(); }
  }
}
