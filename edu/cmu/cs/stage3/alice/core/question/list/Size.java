package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;


















public class Size
  extends ListNumberQuestion
{
  public Size() {}
  
  public int getValue(List listValue)
  {
    return listValue.size();
  }
}
