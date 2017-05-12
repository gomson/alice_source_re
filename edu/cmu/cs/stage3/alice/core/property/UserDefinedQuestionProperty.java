package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;

public class UserDefinedQuestionProperty extends ElementProperty
{
  public UserDefinedQuestionProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, UserDefinedQuestion defaultValue) {
    super(owner, name, defaultValue, UserDefinedQuestion.class);
  }
  
  public UserDefinedQuestion getUserDefinedQuestionValue() { return (UserDefinedQuestion)getElementValue(); }
  

  protected boolean getValueOfExpression()
  {
    return false;
  }
}
