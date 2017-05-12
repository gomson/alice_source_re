package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.UserDefinedQuestionProperty;




















public class CallToUserDefinedQuestion
  extends Question
{
  public CallToUserDefinedQuestion() {}
  
  public final UserDefinedQuestionProperty userDefinedQuestion = new UserDefinedQuestionProperty(this, "userDefinedQuestion", null);
  public final ElementArrayProperty requiredActualParameters = new ElementArrayProperty(this, "requiredActualParameters", null, [Ledu.cmu.cs.stage3.alice.core.Variable.class);
  public final ElementArrayProperty keywordActualParameters = new ElementArrayProperty(this, "keywordActualParameters", null, [Ledu.cmu.cs.stage3.alice.core.Variable.class);
  
  public Object getValue() {
    UserDefinedQuestion userDefinedQuestionValue = userDefinedQuestion.getUserDefinedQuestionValue();
    Behavior currentBehavior = null;
    World world = getWorld();
    if (world != null) {
      Sandbox sandbox = world.getCurrentSandbox();
      if (sandbox != null) {
        currentBehavior = sandbox.getCurrentBehavior();
      }
    }
    



    if (currentBehavior != null) {
      currentBehavior.pushStack(
        (Variable[])requiredActualParameters.getArrayValue(), 
        (Variable[])keywordActualParameters.getArrayValue(), 
        (Variable[])requiredFormalParameters.getArrayValue(), 
        (Variable[])keywordFormalParameters.getArrayValue(), 
        (Variable[])localVariables.getArrayValue(), 
        true);
      
      Object returnValue = userDefinedQuestionValue.getValue();
      currentBehavior.popStack();
      return returnValue;
    }
    return null;
  }
  
  public Class getValueClass()
  {
    UserDefinedQuestion userDefinedQuestionValue = userDefinedQuestion.getUserDefinedQuestionValue();
    if (userDefinedQuestionValue != null) {
      return userDefinedQuestionValue.getValueClass();
    }
    return null;
  }
}
