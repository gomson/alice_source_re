package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
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
import edu.cmu.cs.stage3.alice.core.property.UserDefinedQuestionProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.util.HashMap;







public class CallToUserDefinedQuestionPrototype
  extends QuestionPrototype
{
  protected UserDefinedQuestion actualQuestion;
  protected RefreshListener refreshListener = new RefreshListener();
  
  public CallToUserDefinedQuestionPrototype(UserDefinedQuestion actualQuestion) {
    super(CallToUserDefinedQuestion.class, new StringObjectPair[0], new String[0]);
    this.actualQuestion = actualQuestion;
  }
  
  public void startListening()
  {
    if (actualQuestion != null) {
      actualQuestion.requiredFormalParameters.addObjectArrayPropertyListener(refreshListener);
      Object[] vars = actualQuestion.requiredFormalParameters.getArrayValue();
      for (int i = 0; i < vars.length; i++) {
        name.addPropertyListener(refreshListener);
      }
    } else {
      AuthoringTool.showErrorDialog("actualQuestion is null", null);
    }
  }
  
  public void stopListening() {
    if (actualQuestion != null) {
      actualQuestion.requiredFormalParameters.removeObjectArrayPropertyListener(refreshListener);
      Object[] vars = actualQuestion.requiredFormalParameters.getArrayValue();
      for (int i = 0; i < vars.length; i++) {
        name.removePropertyListener(refreshListener);
      }
    } else {
      AuthoringTool.showErrorDialog("actualQuestion is null", null);
    }
  }
  
  protected CallToUserDefinedQuestionPrototype(UserDefinedQuestion actualQuestion, StringObjectPair[] knownPropertyValues, String[] desiredProperties) {
    super(CallToUserDefinedQuestion.class, knownPropertyValues, desiredProperties);
    this.actualQuestion = actualQuestion;
  }
  
  public Element createNewElement() {
    HashMap knownMap = new HashMap();
    for (int i = 0; i < knownPropertyValues.length; i++) {
      knownMap.put(knownPropertyValues[i].getString(), knownPropertyValues[i].getObject());
    }
    
    CallToUserDefinedQuestion callToUserDefinedQuestion = new CallToUserDefinedQuestion();
    userDefinedQuestion.set(actualQuestion);
    Object[] params = actualQuestion.requiredFormalParameters.getArrayValue();
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
      callToUserDefinedQuestion.addChild(actualParameter);
      requiredActualParameters.add(actualParameter);
    }
    
    return callToUserDefinedQuestion;
  }
  
  public ElementPrototype createCopy(StringObjectPair[] newKnownPropertyValues) {
    return super.createCopy(newKnownPropertyValues);
  }
  
  public UserDefinedQuestion getActualQuestion() {
    return actualQuestion;
  }
  
  private void calculateDesiredProperties() {
    Object[] params = actualQuestion.requiredFormalParameters.getArrayValue();
    desiredProperties = new String[params.length];
    for (int i = 0; i < params.length; i++) {
      desiredProperties[i] = name.getStringValue();
    }
  }
  



  protected ElementPrototype createInstance(Class elementClass, StringObjectPair[] knownPropertyValues, String[] desiredProperties) { return new CallToUserDefinedQuestionPrototype(actualQuestion, knownPropertyValues, desiredProperties); }
  
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
      
      CallToUserDefinedQuestionPrototype.this.calculateDesiredProperties(); }
    
    public void propertyChanging(PropertyEvent ev) {}
    
    public void propertyChanged(PropertyEvent ev) { CallToUserDefinedQuestionPrototype.this.calculateDesiredProperties(); }
  }
}
