package edu.cmu.cs.stage3.alice.core.question;











public class Not
  extends UnaryBooleanResultingInBooleanQuestion
{
  public Not() {}
  









  protected boolean getValue(boolean a)
  {
    return !a;
  }
}
