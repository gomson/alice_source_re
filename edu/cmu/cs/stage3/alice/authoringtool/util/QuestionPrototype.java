package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.util.StringObjectPair;





















public class QuestionPrototype
  extends ElementPrototype
{
  public QuestionPrototype(Class questionClass, StringObjectPair[] knownPropertyValues, String[] desiredProperties)
  {
    super(questionClass, knownPropertyValues, desiredProperties);
  }
  
  public Question createNewQuestion() {
    return (Question)super.createNewElement();
  }
  
  public Class getQuestionClass() {
    return super.getElementClass();
  }
}
