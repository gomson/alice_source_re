package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;


















public class IsEmpty
  extends ListBooleanQuestion
{
  public IsEmpty() {}
  
  protected boolean getValue(List listValue)
  {
    return listValue.isEmpty();
  }
}
